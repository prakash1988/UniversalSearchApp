
package com.example.universalsearchapp.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalsearchapp.adapter.UserAdapter
import com.example.universalsearchapp.data.User
import com.example.universalsearchapp.databinding.SearchListBinding
import com.example.universalsearchapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class SearchUserList : Fragment(){

    private val userviewmodel : UserViewModel by viewModels()
    val RecordAudioRequestCode =1;


    var users : ArrayList<User>? = ArrayList()
    var binding : SearchListBinding? = null

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    @Inject
    lateinit var userAdapter : UserAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = SearchListBinding.inflate(inflater)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mLayoutManager = LinearLayoutManager(activity)
        binding?.userList?.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()

            adapter = userAdapter
        }


        userviewmodel.allFavoriteUser.observe(viewLifecycleOwner, Observer {data->
            users?.clear()
            users?.addAll(data)
            users?.let { userAdapter.updateList(it) }
            userAdapter?.notifyDataSetChanged()

        })

        binding?.editSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                filter(charSequence.toString())

            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding?.imgSearch?.setOnClickListener {
            if(activity?.applicationContext?.let { it1 -> ContextCompat.checkSelfPermission(it1, Manifest.permission.RECORD_AUDIO) } != PackageManager.PERMISSION_GRANTED){
                checkPermission();
            }else {
                launchVoiceScreen();
            }

        }



    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    RecordAudioRequestCode
                )
            }
        }
    }




    private fun launchVoiceScreen() {
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Language model defines the purpose, there are special models for other use cases, like search.
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        // Adding an extra language, you can use any language from the Locale class.
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        // Text that shows up on the Speech input prompt.
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")
        try {
            // Start the intent for a result, and pass in our request code.
            startActivityForResult(sttIntent, REQUEST_CODE_STT)
        } catch (e: ActivityNotFoundException) {
            // Handling error when the service is not available.
            e.printStackTrace()
            Toast.makeText(activity, "Your device does not support STT.", Toast.LENGTH_LONG).show()
        }
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<User> = ArrayList()
        if (TextUtils.isEmpty(text)){
            users?.let { userAdapter.updateList(it) }
            userAdapter.notifyDataSetChanged()
        }else {
            for (item in userAdapter.userList) {
                if (item.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                }
            }
            userAdapter.updateList(filteredList)
            userAdapter.notifyDataSetChanged()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.isNotEmpty()){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(activity,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Handle the result for our request code.
            REQUEST_CODE_STT -> {
                // Safety checks to ensure data is available.
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Retrieve the result array.
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    // Ensure result array is not null or empty to avoid errors.
                    if (!result.isNullOrEmpty()) {
                        // Recognized text is in the first position.
                        val recognizedText = result[0]
                        // Do what you want with the recognized text.
                        binding?.editSearch?.setText(recognizedText)
                        filter(recognizedText)
                    }
                }
            }
        }
    }

}