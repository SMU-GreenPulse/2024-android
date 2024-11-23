package com.example.sensor.network

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object OpenAiTtsService {
    private const val API_URL = "https://api.openai.com/v1/audio/speech"
    private const val API_KEY = "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"

    fun getTtsAudioBytes(text: String): ByteArray {
        val client = OkHttpClient()

        val json = JSONObject().apply {
            put("model", "tts-1")
            put("input", text)
            put("voice", "echo")
        }

        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("TTS API 요청 실패: ${response.code} ${response.message}")
            }

            return response.body?.bytes() ?: throw Exception("TTS API 응답이 비어 있습니다.")
        }
    }
}