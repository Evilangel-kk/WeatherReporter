package com.example.thirdexperiment

/*
* 设置HTTP返回的接口*/
interface HttpCallbackListener {
    fun onFinish(response: String)
    fun onError(e: Exception)
}