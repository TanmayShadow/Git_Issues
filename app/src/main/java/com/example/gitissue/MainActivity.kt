package com.example.gitissue

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private lateinit var scrollView:HorizontalScrollView
    private lateinit var filterLinearLayout:LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView:SearchView
    private lateinit var filterButton:Button

    private lateinit var myAdapter:MyAdapter

    private var BASE_URL = "https://api.github.com/repos/binaryshrey/Awesome-Android-Open-Source-Projects/"
    lateinit var data:List<ClosedIssuesItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        var intent=Intent()
        var githubUsername = intent.getStringExtra("githubUsername")
        var repository = intent.getStringExtra("repository")
        var default = intent.getBooleanExtra("default",false)
        if(!default)
            BASE_URL = "https://api.github.com/repos/"+githubUsername+"/"+repository+"/"


        scrollView = findViewById<HorizontalScrollView>(R.id.scroll_view)
        filterLinearLayout = findViewById<LinearLayout>(R.id.filter_linear_layout)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchView = findViewById<SearchView>(R.id.search_view)
        filterButton = findViewById<Button>(R.id.issue_number)
        displayData()

        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText==null)
                {
                    myAdapter.changeList(data)
                    return true
                }
                else if(filterButton.id == R.id.issue_number)
                {
                    var newList = data.filter { it.number.toString().contains(newText.trim()) }
                    if(newList.isNotEmpty())
                        myAdapter.changeList(newList)
                    else
                        searchByIssueNumber(newText.toString())
                }
                else if(filterButton.id == R.id.author)
                {
                    var newList = data.filter { it.user.login.contains(newText, ignoreCase = true) }
                    myAdapter.changeList(newList)
                }

                return true
            }

        })
    }

    private fun searchByIssueNumber(number: String)
    {
        BASE_URL = "https://api.github.com/repos/binaryshrey/Awesome-Android-Open-Source-Projects/issues/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiCallInterface::class.java)

        val retroData = retrofit.getData(number)
        retroData.enqueue(object : Callback<List<ClosedIssuesItem>>{
            override fun onResponse(call: Call<List<ClosedIssuesItem>>, response: Response<List<ClosedIssuesItem>>)
            {
                var tempdata = response.body()!!
                if(response.body() == null)
                {
                    Toast.makeText(applicationContext,"Unable to fetch data",Toast.LENGTH_SHORT).show()
                    return
                }
                myAdapter = MyAdapter(baseContext,tempdata)
                recyclerView.adapter = myAdapter
            }

            override fun onFailure(call: Call<List<ClosedIssuesItem>>, t: Throwable)
            {
                Log.d("RetrofitError","Error while fetching data from api")
            }

        })
    }

    private fun displayData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiCallInterface::class.java)

        val retroData = retrofit.getData()
        retroData.enqueue(object : Callback<List<ClosedIssuesItem>>{
            override fun onResponse(call: Call<List<ClosedIssuesItem>>, response: Response<List<ClosedIssuesItem>>)
            {
                if(!response.isSuccessful)
                {
                    Toast.makeText(applicationContext,"Unable to fetch data",Toast.LENGTH_SHORT).show()
                    return
                }
                data = response.body()!!
                myAdapter = MyAdapter(baseContext,data)
                recyclerView.adapter = myAdapter
            }

            override fun onFailure(call: Call<List<ClosedIssuesItem>>, t: Throwable)
            {
                Log.d("RetrofitError","Error while fetching data from api")
            }

        })

    }


    fun showFilterOptions(v: View)
    {
        var dpValue = 40

        val showFilterAnimation = if(!filterLinearLayout.isVisible)
        {
            dpValue=45
            filterLinearLayout.isVisible = true
            AnimationUtils.loadAnimation(this,R.anim.drop_down)
        }
        else
        {
            dpValue=1
            AnimationUtils.loadAnimation(this,R.anim.drop_up)

        }
        val density = resources.displayMetrics.density
        val pixelValue = (dpValue * density).toInt()
        if(dpValue!=1)
        {
            scrollView.layoutParams.height=pixelValue
            scrollView.requestLayout()
            filterLinearLayout.startAnimation(showFilterAnimation)
        }
        else
        {

            showFilterAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Code to run when the animation starts
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Code to run when the animation ends
                    filterLinearLayout.isVisible = false
                    scrollView.layoutParams.height=pixelValue
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Code to run when the animation repeats
                }
            })
            filterLinearLayout.startAnimation(showFilterAnimation)

        }

    }


    fun setFilter(v:View)
    {
        if(filterButton.id == R.id.sort_created_date && v.id != R.id.sort_created_date
            && filterButton.tag == "dark_ascending")
        {
            filterButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ascending,0)
            filterButton.tag = "ascending"
        }
        else if(filterButton.id == R.id.sort_created_date && v.id != R.id.sort_created_date
            && filterButton.tag == "dark_descending")
        {
            filterButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.descending,0)
            filterButton.tag = "descending"
        }
        else if(filterButton.id == R.id.sort_closed_date && v.id != R.id.sort_closed_date
            && filterButton.tag == "dark_ascending")
        {
            filterButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ascending,0)
            filterButton.tag = "ascending"
        }
        else if(filterButton.id == R.id.sort_closed_date && v.id != R.id.sort_closed_date
            && filterButton.tag == "dark_descending")
        {
            filterButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.descending,0)
            filterButton.tag = "descending"
        }





        filterButton.setTextColor(ContextCompat.getColor(this,R.color.light_text_color))
        filterButton.setBackgroundResource(R.drawable.rounded_button)
        filterButton = findViewById<Button>(v.id)
        filterButton.setTextColor(ContextCompat.getColor(this,R.color.dark_text_color))
        filterButton.setBackgroundResource(R.drawable.dark_rounded_button)
        when(v.id)
        {
            R.id.sort_closed_date-> {
                if((filterButton.tag == "descending" || filterButton.tag == "dark_descending"))
                {
                    filterButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.dark_ascending,0)
                    filterButton.tag = "dark_ascending"
                    sortByClosedDate()
                }
                else if((filterButton.tag == "ascending" || filterButton.tag == "dark_ascending"))
                {
                    filterButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.dark_descending,0)
                    filterButton.tag = "dark_descending"
                    sortByClosedDateDesc()
                }

            }
            R.id.sort_created_date-> {
                if( (filterButton.tag == "descending" || filterButton.tag == "dark_descending") )
                {
                    filterButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.dark_ascending,0)
                    filterButton.tag = "dark_ascending"
                    sortByCreatedDate()
                }
                else if((filterButton.tag == "ascending" || filterButton.tag == "dark_ascending"))
                {
                    filterButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.dark_descending,0)
                    filterButton.tag = "dark_descending"
                    sortByCreatedDateDesc()
                }

            }
            R.id.author->{
                searchView.queryHint="Author Name"
                searchView.inputType = android.text.InputType.TYPE_CLASS_TEXT
            }
            R.id.issue_number->{
                searchView.queryHint="Issue Number"
                searchView.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            }
        }
    }

    private fun sortByCreatedDateDesc() {
        val sortedIssues = data.sortedByDescending { it.created_at }
        myAdapter = MyAdapter(baseContext,sortedIssues)
        recyclerView.adapter = myAdapter
    }

    private fun sortByClosedDateDesc() {
        val sortedIssues = data.sortedByDescending { it.closed_at }
        myAdapter = MyAdapter(baseContext,sortedIssues)
        recyclerView.adapter = myAdapter
    }

    fun sortByClosedDate()
    {
        val sortedIssues = data.sortedBy { it.closed_at }
        myAdapter = MyAdapter(baseContext,sortedIssues)
        recyclerView.adapter = myAdapter
    }

    fun sortByCreatedDate()
    {
        val sortedIssues = data.sortedBy { it.created_at }
        myAdapter = MyAdapter(baseContext,sortedIssues)
        recyclerView.adapter = myAdapter
    }

    fun getBack(view: View)
    {
        val i = Intent(this,FirstActivity::class.java)
        startActivity(i)
        finish()
    }
}