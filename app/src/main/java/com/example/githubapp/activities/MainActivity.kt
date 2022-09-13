package com.example.githubapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.example.githubapp.app.*
import com.example.githubapp.app.Constants.APP_SHARED_PREFERENCES
import com.example.githubapp.app.Constants.KEY_GITHUB_USER
import com.example.githubapp.app.Constants.KEY_LANGUAGE
import com.example.githubapp.app.Constants.KEY_PERSON_NAME
import com.example.githubapp.app.Constants.KEY_QUERY_TYPE
import com.example.githubapp.app.Constants.KEY_REPO_SEARCH
import com.example.githubapp.app.Constants.SEARCH_BY_REPO
import com.example.githubapp.app.Constants.SEARCH_BY_USER
import com.example.githubapp.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

    }

    /** Save app username in SharedPreferences */
    fun saveName(view: View){
        if(isNotEmpty(binding.etName, binding.inputLayoutName)){
            val personName = binding.etName.text.toString() // get data

            // SharedPreferences
            val sp = getSharedPreferences(APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString(KEY_PERSON_NAME, personName)
            editor.apply()
        }
    }

    /** Search github repositories after passing data to [DisplayActivity] */
    fun listRepositories(view: View){
        if(isNotEmpty(binding.etRepoName, binding.inputLayoutRepoName)){
            val queryRepo: String = binding.etRepoName.text.toString()
            val queryLanguage: String = binding.etLanguage.text.toString()

            val intent = Intent(this, DisplayActivity::class.java)

            intent.putExtra(KEY_QUERY_TYPE, SEARCH_BY_REPO) // type of query we want within the DisplayActivity
            intent.putExtra(KEY_REPO_SEARCH, queryRepo)
            intent.putExtra(KEY_LANGUAGE, queryLanguage)

            /**
             * startActivity is used to start a new activity, which will be
             * placed at the top of the activity stack
             */
            startActivity(intent)
        }

    }

    /** Search repo of a particular github user after passing data to [DisplayActivity] */
    fun listUserRepositories(view: View){
        if(isNotEmpty(binding.etGithubUser, binding.inputLayoutGithubUser)){
            val githubUser = binding.etGithubUser.text.toString()

            val intent = Intent(this, DisplayActivity::class.java)

            intent.putExtra(KEY_QUERY_TYPE, SEARCH_BY_USER) // type of query we want within the DisplayActivity
            intent.putExtra(KEY_GITHUB_USER, githubUser)

            startActivity(intent)
        }

    }

    /**
     * Validate the EditText fields
     */
    private fun isNotEmpty(editText: EditText, textInputLayout: TextInputLayout): Boolean{
        return if(editText.text.toString().isEmpty()){
            textInputLayout.error = "Cannot be blank"
            false
        }else{
            textInputLayout.isErrorEnabled = false
            true
        }
    }

    companion object{
        private val TAG = MainActivity::class.java.simpleName
    }
}