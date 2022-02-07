package com.example.vsensei.util

sealed class Resource<T>(val data: T? = null, val errorMessageResId: Int? = null) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(errorMessageResId: Int?, data: T? = null) : Resource<T>(data, errorMessageResId)
}