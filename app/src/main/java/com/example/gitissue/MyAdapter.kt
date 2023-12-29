package com.example.gitissue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(var context: Context,var list:List<ClosedIssuesItem>) : RecyclerView.Adapter<MyAdapter.ViewHolder>()
{

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
    {
        var title = v.findViewById<TextView>(R.id.issue_title)
        var createDate = v.findViewById<TextView>(R.id.issue_created_date)
        var closedDate = v.findViewById<TextView>(R.id.issue_closed_date)
        var userImage = v.findViewById<ImageView>(R.id.issue_user_image)
        var userName = v.findViewById<TextView>(R.id.issue_username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.issue_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(list[position].user.avatar_url).into(holder.userImage)
        holder.title.text = list[position].title

        var createdDate = list[position].created_at.take(10)
        holder.createDate.text = "Created: "+createdDate

        var closedDate = list[position].closed_at.take(10)
        holder.closedDate.text = "Closed: "+closedDate

        holder.userName.text = list[position].user.login
    }

    fun changeList(newList: List<ClosedIssuesItem>) {
        list = newList
        notifyDataSetChanged()
    }

}