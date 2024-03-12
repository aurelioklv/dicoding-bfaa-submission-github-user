package com.aurelioklv.githubuser.data.api

import com.aurelioklv.githubuser.data.response.FollowerFollowingItem
import com.aurelioklv.githubuser.data.response.UserResponse
import com.aurelioklv.githubuser.data.response.UserSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    fun searchUsers(@Query("q") q: String): Call<UserSearchResponse>

    @GET("users/{username}")
    fun getUserDetails(@Path("username") username: String): Call<UserResponse>

    @GET("users/{username}/followers?per_page=100")
    fun getFollowers(@Path("username") username: String): Call<List<FollowerFollowingItem>>

    @GET("users/{username}/following?per_page=100")
    fun getFollowing(@Path("username") username: String): Call<List<FollowerFollowingItem>>
}