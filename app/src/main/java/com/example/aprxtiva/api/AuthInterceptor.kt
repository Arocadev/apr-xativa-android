package com.example.aprxtiva.api

import android.content.Context
import com.example.aprxtiva.entities.RefreshRequest
import com.example.aprxtiva.utils.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 401 &&
            !originalRequest.url.encodedPath.contains("/auth/login") &&
            !originalRequest.url.encodedPath.contains("/auth/refresh")) {

            response.close()

            val tokenManager = TokenManager(context)
            val refreshToken = runBlocking { tokenManager.refreshToken.first() }

            if (refreshToken == null) {
                runBlocking { tokenManager.cerrarSesion() }
                return response
            }

            return try {
                val refreshResponse = runBlocking {
                    RetrofitClient.getClient().refresh(RefreshRequest(refreshToken))
                }

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val body = refreshResponse.body()!!
                    runBlocking {
                        tokenManager.guardarToken(body.token, body.refreshToken)
                    }

                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer ${body.token}")
                        .build()
                    chain.proceed(newRequest)
                } else {
                    runBlocking { tokenManager.cerrarSesion() }
                    response
                }
            } catch (e: Exception) {
                runBlocking { tokenManager.cerrarSesion() }
                response
            }
        }

        return response
    }
}