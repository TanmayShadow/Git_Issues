package com.example.gitissue

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiCallInterface {

    @GET("issues?state=closed")
    fun getData():Call<List<ClosedIssuesItem>>

    @GET("{issueNumber}?state=closed")
    fun getData(@Path("issueNumber") issueNumber:String):Call<List<ClosedIssuesItem>>
}