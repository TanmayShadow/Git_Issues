package com.example.gitissue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
    }

    fun showIssues(view: View)
    {
        val username = findViewById<EditText>(R.id.github_username)
        val repository = findViewById<EditText>(R.id.github_repository)

        if(username.text.toString().isEmpty() || repository.text.toString().isEmpty())
        {
            var errorText = findViewById<TextView>(R.id.errorText)
            val handler = Handler(Looper.getMainLooper())
            errorText.text = "Please fill all the fields"
            handler.postDelayed({
                errorText.text = ""
            },2000)
            return
        }
        val i = Intent(this,MainActivity::class.java)
        i.putExtra("githubUsername",username.text.toString())
        i.putExtra("repository",repository.text.toString())
        i.putExtra("default",false)
        startActivity(i)
    }
    fun showDefault(view:View)
    {
        val i = Intent(this,MainActivity::class.java)
        i.putExtra("default",true)
        startActivity(i)
    }
}