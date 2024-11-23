package com.example.sensor.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.openai.com/" // ChatGPT API 베이스 URL

    // Retrofit 인스턴스 생성
    val api: ChatGptApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱용
            .build()
            .create(ChatGptApi::class.java) // API 인터페이스 연결
    }
}
