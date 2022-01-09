package com.hsg.localmarket.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hsg.localmarket.data.ApkList
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface RetrofitService {

    @GET("upload/apklist.json")
    fun getVersion(): Call<ApkList>

    @GET()
    @Streaming
    fun downloadFile(@Url downloadUrl: String): Call<ResponseBody?>?

    companion object {
        var BASE_URL = "http://192.168.0.18:8080" // 주소
        var API : RetrofitService

        init {
            val gson : Gson =   GsonBuilder().setLenient().create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            API = retrofit.create(RetrofitService::class.java)
        }
    }
}