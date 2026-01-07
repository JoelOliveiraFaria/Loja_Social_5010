package com.example.lojasocial.repositories

sealed class ResultWrapper <out T> {

    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error(val message: String? = null, val exception: Throwable? = null) : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()
}