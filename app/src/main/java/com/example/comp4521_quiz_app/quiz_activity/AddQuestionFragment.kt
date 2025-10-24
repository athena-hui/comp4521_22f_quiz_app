package com.example.comp4521_quiz_app.quiz_activity

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentAddQuestionBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


//Add question fragment
class AddQuestionFragment : Fragment() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var photoUrl: MutableLiveData<String?> = MutableLiveData()
    private lateinit var filePath: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoUrl.value = "No file is selected"
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.data
                    if (data != null) {
                        val cursor =
                            context?.contentResolver?.query(data, null, null, null, null)
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                val columnIndex =
                                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                                photoUrl.value = cursor.getString(columnIndex)
                                filePath = data
                            } else {
                                photoUrl.value = "No file is selected"
                            }
                        }
                        //photoUrl.value = Uri.getPath(activity.applicationContext(),data)
                    } else {
                        photoUrl.value = "No file is selected"
                    }
                } else {
                    photoUrl.value = "No file is selected"
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil.inflate<FragmentAddQuestionBinding>(
            inflater,
            R.layout.fragment_add_question,
            container,
            false
        )
        val view = binding.root
        val questionCategorySpinner = binding.QuestionCategorySpinner
        val correctOptionSpinner = binding.CorrectOptionSpinner
        val questionArrayAdapter = context?.let {
            ArrayAdapter.createFromResource(
                it, R.array.quiz_type,
                android.R.layout.simple_spinner_dropdown_item
//                    .layout.support_simple_spinner_dropdown_item
            )
        }
        questionCategorySpinner.adapter = questionArrayAdapter
        val optionArrayAdapter = context?.let {
            ArrayAdapter.createFromResource(
                it, R.array.correct_option,
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        correctOptionSpinner.adapter = optionArrayAdapter
        photoUrl.observe(viewLifecycleOwner) {
            binding.filePathTextView.text = it
        }
        binding.SelectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }
        var photoLink: String
        binding.SubmitButton.setOnClickListener {
            val db = Firebase.firestore
            val questionText = binding.QuestionTextField.text.toString()
            val option1 = binding.Option1Field.text.toString()
            val option2 = binding.Option2Field.text.toString()
            val option3 = binding.Option3Field.text.toString()
            val option4 = binding.Option4Field.text.toString()
            if (option1 == "" || option2 == "" || option3 == "" || option4 == "" || questionText == "") {
                Snackbar.make(view, "All field must be inputted", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (photoUrl.value != "No file is selected") {
                var internet = true
                CoroutineScope(Dispatchers.Default).launch {
                    internet = withContext(Dispatchers.Default) {
                        quizUtil.isInternetAvailable()
                    }
                    if (!internet) {
                        Snackbar.make(view, "Internet is required when adding a quesiton with photo", Snackbar.LENGTH_LONG).show()
                    } else {
                        val storageReference = Firebase.storage.reference
                        val questionImageRef =
                            storageReference.child("QuestionImages/" + UUID.randomUUID().toString())
                        val uploadTask = questionImageRef.putFile(filePath)
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            questionImageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                photoLink = downloadUri.toString()
                                Log.d("Upload Photo", "Link: $photoLink")
                                if (photoLink != "") {
                                    val question = hashMapOf(
                                        "question" to questionText,
                                        "option1" to option1,
                                        "option2" to option2,
                                        "option3" to option3,
                                        "option4" to option4,
                                        "answer" to correctOptionSpinner.selectedItem.toString(),
                                        "category" to questionCategorySpinner.selectedItem.toString(),
                                        "photoLink" to photoLink
                                    )
                                    if (option1!="Option 1 Testing"){
                                        db.collection("QuestionSet")
                                            .add(question)
                                            .addOnSuccessListener { documentReference ->
                                                Log.d(
                                                    ContentValues.TAG,
                                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                                )
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w(ContentValues.TAG, "Error adding document", e)
                                            }
                                    }
                                    Snackbar.make(view, "Completed", Snackbar.LENGTH_LONG).show()
                                    view.findNavController()
                                        .navigate(R.id.action_addQuestionFragment_to_quizDashboardFragment)
                                }
                            } else {
                                photoLink = ""

                            }
                        }
                        uploadTask.addOnFailureListener {
                            Log.e("Upload Photo", it.toString())
                        }
                    }
                }


            } else {
                val question = hashMapOf(
                    "question" to questionText,
                    "option1" to option1,
                    "option2" to option2,
                    "option3" to option3,
                    "option4" to option4,
                    "answer" to correctOptionSpinner.selectedItem.toString(),
                    "category" to questionCategorySpinner.selectedItem.toString()
                )

                //Add a new document with a generated ID
                if (option1!="Option 1 Testing"){
                    db.collection("QuestionSet")
                        .add(question)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                ContentValues.TAG,
                                "DocumentSnapshot added with ID: ${documentReference.id}"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding document", e)
                        }
                }

                Snackbar.make(view, "Completed", Snackbar.LENGTH_LONG).show()
                view.findNavController()
                    .navigate(R.id.action_addQuestionFragment_to_quizDashboardFragment)
            }


        }
        binding.BackButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_addQuestionFragment_to_quizDashboardFragment)
        }
        return view
    }
}