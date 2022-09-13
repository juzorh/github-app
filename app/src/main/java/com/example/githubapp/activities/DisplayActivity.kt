package com.example.githubapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapp.R
import com.example.githubapp.adapters.DisplayAdapter
import com.example.githubapp.app.*
import com.example.githubapp.app.Constants.APP_SHARED_PREFERENCES
import com.example.githubapp.app.Constants.KEY_GITHUB_USER
import com.example.githubapp.app.Constants.KEY_LANGUAGE
import com.example.githubapp.app.Constants.KEY_PERSON_NAME
import com.example.githubapp.app.Constants.KEY_QUERY_TYPE
import com.example.githubapp.app.Constants.KEY_REPO_SEARCH
import com.example.githubapp.app.Constants.SEARCH_BY_REPO
import com.example.githubapp.databinding.ActivityDisplayBinding
import com.example.githubapp.databinding.HeaderBinding
import com.example.githubapp.models.Repository
import com.example.githubapp.models.SearchResponse
import com.example.githubapp.retrofit.GithubAPIService
import com.example.githubapp.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DisplayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayBinding
    private lateinit var headerBinding: HeaderBinding

    private lateinit var displayAdapter: DisplayAdapter

    private val githubAPIService: GithubAPIService by lazy {
        RetrofitClient.githubAPIService
    }

    private var browsedRepositories: List<Repository> = mutableListOf()

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayBinding.inflate(layoutInflater)
        headerBinding = HeaderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        /**
         * Get the user name from sharedPreferences and put it in the
         * header.xml file
         */
        setAppUsername()

        val drawerLayout = binding.drawerLayout

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerView.layoutManager = layoutManager

        val intent = intent
        if (intent.getIntExtra(KEY_QUERY_TYPE, -1) == SEARCH_BY_REPO) {
            val queryRepo = intent.getStringExtra(KEY_REPO_SEARCH)
            val repoLanguage = intent.getStringExtra(KEY_LANGUAGE)
            fetchRepositories(queryRepo!!, repoLanguage!!)
        } else {
            val githubUser = intent.getStringExtra(KEY_GITHUB_USER)
            githubUser?.let {
                fetchUserRepositories(it)
            }
        }

    }

    private fun setAppUsername() {
        val sp = getSharedPreferences(APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val personName = sp.getString(KEY_PERSON_NAME, "User")

        /**
         * Get the reference to header.xml from the NavigationView in
         * activity_display.xml
         */
        // val headerLayout = binding.navigationView.getHeaderView(0)
        // headerLayout.txvName

        val headerUserText = headerBinding.txvName
        headerUserText.text = personName

        Log.i("GitHub", headerUserText.toString())
    }

    private fun fetchRepositories(queryRepository: String, repoLanguage: String) {
        var queryRepo = queryRepository
        val query = HashMap<String, String>()

        if(repoLanguage.isNotEmpty())
            queryRepo += " language:$repoLanguage"
        query["q"] = queryRepo

        githubAPIService.searchRepositories(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if(response.isSuccessful){
                    Log.i(TAG, "Posts from API $response")

                    response.body()?.items?.let{
                        browsedRepositories = it
                    }

                    if(browsedRepositories.isNotEmpty())
                        setupRecyclerView(browsedRepositories)
                    else
                        Util.showMessage(this@DisplayActivity, "No Items Found")
                }else{
                    Log.i(TAG, "error $response")
                    Util.showErrorMessage(this@DisplayActivity, response.errorBody())
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Util.showMessage(this@DisplayActivity, t.toString())
            }
        })
    }

    private fun fetchUserRepositories(githubUser: String){
        githubAPIService.searchRepositoriesByUser(githubUser).enqueue(object : Callback<List<Repository>>{
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {

                    Log.i(TAG, "posts loaded from API $response")

                    response.body()?.let {
                        browsedRepositories = it
                    }

                    if (browsedRepositories.isNotEmpty()) {
                        setupRecyclerView(browsedRepositories)
                    } else
                    {
                        Util.showMessage(this@DisplayActivity, "No Items Found")
                    }
                } else {
                    Log.i(TAG, "Error $response")
                    Util.showErrorMessage(this@DisplayActivity, response.errorBody())
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Util.showMessage(this@DisplayActivity, t.message)
            }

        })
    }

    private fun setupRecyclerView(items: List<Repository>) {
        displayAdapter = DisplayAdapter(this, items)
        binding.recyclerView.adapter = displayAdapter
    }

    private fun showBrowsedResults(){
        displayAdapter.swap(browsedRepositories)
    }

    /**
     * override the onOptionsItemSelected() function to implement the
     * item click listener callback to open and close the navigation
     * drawer when the icon is clicked
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }else super.onOptionsItemSelected(item)
    }

    companion object{
        private val TAG = DisplayActivity::class.java.simpleName
    }
}