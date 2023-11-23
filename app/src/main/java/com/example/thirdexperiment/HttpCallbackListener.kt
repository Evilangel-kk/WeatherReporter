package com.example.thirdexperiment

interface HttpCallbackListener {
    fun onFinish(response: String)
    fun onError(e: Exception)
}