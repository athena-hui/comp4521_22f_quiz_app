package com.example.comp4521_quiz_app.main_activity.viewModel

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.InetAddress

class MainActivityViewModel: ViewModel() {
    private val storage = Firebase.storage
    private val imageRef: StorageReference = storage.reference.child("profile pictures")
    private val dbCollectionPath = "users"

    /**
     * This function is used to check the user's email input during registration.
     * The email input is not valid if...
     *  ... the input is empty
     *  ... the input is not in a email format (xxx@xxx.xxx)
     */
    fun isEmailValid(email: String):Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * This function is used to check the user's userName input
     * and password input during registration.
     * The userName and password input is not valid if...
     * ... the userName is empty
     * ... the password is empty
     */
    fun isUserNameAndPasswordValid(userName: String, password:String):Boolean {
        return userName.isNotEmpty() && password.isNotEmpty()
    }

    fun isInternetAvailable():Boolean {
        return try {
            val ipAdd = InetAddress.getByName("google.com")
            !ipAdd.equals("")
        } catch (e: Exception) {
            Log.i("***","Network exception: $e")
            false
        }
    }

    suspend fun isEmailOccupied(
        db:FirebaseFirestore,
        emailStringResource:String,
        email: String,
        userId: String?
    ):Boolean {
        val result = CompletableDeferred<Boolean>()
        val accountSameEmail = db.collection(dbCollectionPath)
            .whereEqualTo(emailStringResource, email)
            .get().await()

        if (accountSameEmail.isEmpty) {
            Log.i("***", "Email is not occupied, it can be used")
            result.complete(false)
        } else if (accountSameEmail.documents.size >= 2) {
            Log.i("***", "Email \"$email\" is occupied!")
            result.complete(true)
        } else {
            if (accountSameEmail.documents[0].id == userId) {
                Log.i("***", "Email is owned by you, it can be used")
                result.complete(false)
            } else {
                Log.i("***", "Email \"$email\" is occupied!")
                result.complete(true)
            }
        }

        return result.await()
    }

    suspend fun isUserNameOccupied(
        db:FirebaseFirestore,
        userNameStringResource:String,
        userName: String,
        userId: String?
    ):Boolean {
        val result = CompletableDeferred<Boolean>()
        val accountSameUserName = db.collection(dbCollectionPath)
            .whereEqualTo(userNameStringResource, userName)
            .get().await()

        if (accountSameUserName.isEmpty) {
            Log.i("***", "User name is not duplicated, it can be used")
            result.complete(false)
        } else if (accountSameUserName.documents.size >= 2) {
            Log.i("***", "User name \"$userName\" is duplicated!")
            result.complete(true)
        } else {
            if (accountSameUserName.documents[0].id == userId) {
                Log.i("***", "User name is owned by you, it can be used")
                result.complete(false)
            } else {
                Log.i("***", "User name \"$userName\" is duplicated!")
                result.complete(true)
            }
        }

        return result.await()
    }

    suspend fun registerAccount(
        db: FirebaseFirestore,
        sharedPref: SharedPreferences?,
        emailStringResource: String,
        userNameStringResource: String,
        passwordStringResource: String,
        userIdStringResource: String,
        firstSeenStringResource: String,
        email: String,
        userName: String,
        password: String,
    ):Boolean {
        //save email, user name, password on cloud fireStore
        val account = hashMapOf(
            emailStringResource to email,
            userNameStringResource to userName,
            passwordStringResource to password,
        )

        val result = CompletableDeferred<Boolean>()
        db.collection(dbCollectionPath)
        .add(account)
        .addOnSuccessListener { documentReference ->
            Log.i("***", "DocumentSnapshot added with ID: ${documentReference.id}")
            Log.i("***", "email = $email")
            Log.i("***", "user_name = $userName")
            Log.i("***", "password = $password")
            Log.i("***", "profile pic saved successfully!")
            Log.i("***", "user_id = ${documentReference.id}")
            with (sharedPref!!.edit()) {
                // save email, user name, password, user id, profile pic on disk using share preferences
                putString(emailStringResource, email)
                putString(userNameStringResource, userName)
                putString(passwordStringResource, password)
                putString(userIdStringResource, documentReference.id)
                putBoolean(firstSeenStringResource, false)
                apply()
            }
            result.complete(true)
        }
        .addOnFailureListener { e ->
            Log.i("***", "Error adding document", e)
            result.complete(false)
        }

        return result.await()
    }

    suspend fun onlineLogin(
        activity: FragmentActivity,
        db: FirebaseFirestore,
        sharedPref: SharedPreferences?,
        emailStringResource: String,
        userNameStringResource: String,
        passwordStringResource: String,
        userIdStringResource: String,
        firstSeenStringResource: String,
        userName: String,
        password: String
    ):Boolean {
        val result = CompletableDeferred<Boolean>()

        val queryResult = db.collection(dbCollectionPath)
            .whereEqualTo(userNameStringResource, userName)
            .whereEqualTo(passwordStringResource, password)
            .get().await()

        // unsuccessful login in
        if (queryResult.documents.size != 1)
            result.complete(false)

        // successful login in
        val accountInfo = queryResult.documents[0]
        val accountEmail = accountInfo.get(emailStringResource)
        val accountUserName = accountInfo.get(userNameStringResource)
        val accountPassword = accountInfo.get(passwordStringResource)
        val accountUserId = accountInfo.id
        Log.i("***", "Online login in success!")
        Log.i("***", "email = $accountEmail")
        Log.i("***", "userName = $accountUserName")
        Log.i("***", "password = $accountPassword")
        Log.i("***", "userId = $accountUserId")
        with (sharedPref!!.edit()) {
            putString(emailStringResource, accountEmail as String)
            putString(userNameStringResource, accountUserName as String)
            putString(passwordStringResource, accountPassword as String)
            putString(userIdStringResource, accountUserId )
            putBoolean(firstSeenStringResource, false)
            apply()
        }

        if (!downloadProfilePicFromCloudAndSaveToInternalStorage(
                activity,
                accountUserId,
            ))
            result.complete(false)
        else {
            result.complete(true)
        }

        return result.await()
    }

    fun offlineLogin(
        sharedPref: SharedPreferences?,
        passwordStringResource: String,
        password: String
    ):Boolean {
        return if (sharedPref != null) {
            sharedPref.getString(passwordStringResource, "") == password
        } else {
            false
        }
    }

    fun signOut(
        activity: FragmentActivity,
        sharedPref: SharedPreferences?,
        emailStringResource: String,
        userNameStringResource: String,
        passwordStringResource: String,
        userIdStringResource: String,
        firstSeenStringResource: String,
    ) {
        val userId = sharedPref?.getString(userIdStringResource, "Error")!!
        with (sharedPref.edit()) {
            remove(emailStringResource)
            remove(userNameStringResource)
            remove(passwordStringResource)
            remove(userIdStringResource)
            putBoolean(firstSeenStringResource, true)
            apply()
        }
        val profilePic = activity.filesDir.listFiles { _, name ->
            name.contains(userId)
        }
        if (profilePic != null) {
            for (file in profilePic) {
                file.delete()
            }
        }
    }

    suspend fun isAccountInfoCorrect(
        db: FirebaseFirestore,
        emailStringResource: String,
        userNameStringResource: String,
        userIdStringResource: String,
        email: String,
        userName: String
    ):Map<String, Any> {
        val queryResult = db.collection(dbCollectionPath)
            .whereEqualTo(userNameStringResource, userName)
            .whereEqualTo(emailStringResource, email)
            .get().await()

        val result = mutableMapOf<String, Any>()
        // account info correct
        return if (queryResult.documents.size == 1) {
            result["isAccountInfoCorrect"] = true
            result[userIdStringResource] = queryResult.documents[0].id
            result
        } else {
            // account info incorrect
            result["isAccountInfoCorrect"] = false
            result
        }
    }

    suspend fun changePasswordInCloud(
        db: FirebaseFirestore,
        passwordStringResource: String,
        password: String,
        userId: String
    ):Boolean {
        val result = CompletableDeferred<Boolean>()

        db.collection(dbCollectionPath)
            .document(userId)
            .update(
                passwordStringResource, password
            ).addOnSuccessListener {
                Log.i("***","Password change in cloud success!")
                result.complete(true)
            }.addOnFailureListener{
                Log.i("***","Password change in cloud fail!")
                result.complete(false)
            }

        return result.await()
    }

    fun changePasswordInLocal(
        sharedPref: SharedPreferences,
        passwordStringResource: String,
        password: String
    ) {
        with (sharedPref.edit()) {
            putString(passwordStringResource, password)
            apply()
        }
    }

    suspend fun updateAccountInfo(
        db: FirebaseFirestore,
        sharedPref: SharedPreferences?,
        emailStringResource: String,
        userNameStringResource: String,
        userIdStringResource: String,
        email: String,
        userName: String,
    ):Boolean {
        //update account info on cloud fireStore
        val result = CompletableDeferred<Boolean>()
        val userId = sharedPref?.getString(userIdStringResource, "Error!")!!

        db.collection(dbCollectionPath)
            .document(userId)
            .update(
                emailStringResource, email,
                userNameStringResource, userName
            )
            .addOnSuccessListener {
                with (sharedPref.edit()) {
                    // save new email and user name on disk using share preferences
                    putString(emailStringResource, email)
                    putString(userNameStringResource, userName)
                    apply()
                }
                result.complete(true)
            }
            .addOnFailureListener { e ->
                Log.i("***", "Error editing document", e)
                result.complete(false)
            }

        return result.await()
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val bStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream)
        return bStream.toByteArray()
    }

    fun saveProfilePicToInternalStorage(
        activity: FragmentActivity,
        userId: String,
        profilePicByteArray: ByteArray
    ):Boolean {
        val fileName = "$userId.bmp"
        return try {
            activity.openFileOutput(fileName, Context.MODE_PRIVATE).use { stream ->
                stream.write(profilePicByteArray)
                stream.flush()
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    suspend fun saveProfilePicToCloud (
        userId: String,
        profilePicByteArray: ByteArray
    ): Boolean {
        val result = CompletableDeferred<Boolean>()

        val fileName = "$userId.bmp"
        val uploadTask = imageRef.child(fileName).putBytes(profilePicByteArray)
        uploadTask.
        addOnFailureListener {
            result.complete(false)
        }.addOnSuccessListener { taskSnapshot ->
            result.complete(true)
        }.await()

        return result.await()
    }

    private suspend fun downloadProfilePicFromCloudAndSaveToInternalStorage (
        activity: FragmentActivity,
        userId: String,
    ): Boolean {
        val result = CompletableDeferred<Boolean>()

        val fileName = "$userId.bmp"
        val filePath = File( activity.filesDir.path + "/" + fileName)
        imageRef.child(fileName).getFile(filePath)
            .addOnSuccessListener {
                result.complete(true)
            }
            .addOnFailureListener {
                result.complete(false)
                Log.i("***", "$it")
            }.await()

        return result.await()
    }

    suspend fun loadProfilePicFromInternalStorage(
        activity: FragmentActivity,
        userId: String
    ): Bitmap {
        return withContext(Dispatchers.IO){
            val profilePic = activity.filesDir.listFiles { dir, name ->
                name.contains(userId)
            }
            val profilePicByte = profilePic[0].readBytes()
            val bmp = BitmapFactory.decodeByteArray(profilePicByte, 0, profilePicByte.size)
            bmp
        }
    }
}