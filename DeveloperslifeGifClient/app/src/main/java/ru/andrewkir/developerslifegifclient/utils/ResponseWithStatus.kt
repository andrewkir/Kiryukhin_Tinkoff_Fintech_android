package ru.andrewkir.developerslifegifclient.utils

import okhttp3.ResponseBody

sealed class ResponseWithStatus<out T> {
    data class OnSuccessResponse<T> (val value: T) : ResponseWithStatus<T>()
    data class OnErrorResponse(
        val isNetworkFailure: Boolean,
        val code: Int?,
        val body: ResponseBody?
    ) : ResponseWithStatus<Nothing>()
}