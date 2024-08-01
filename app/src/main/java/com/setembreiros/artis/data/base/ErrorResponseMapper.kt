package com.xeria.gigbro.data.base

import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import retrofit2.HttpException

class ErrorResponseMapper {

    companion object {
        fun map(throwable: HttpException): ErrorResponse? {
            try {
                throwable.response()?.errorBody()?.string()?.let {
                    return Gson().fromJson(it, ErrorResponse::class.java)
                }


            } catch (exception: Exception) {
                return null
            }
            return null
        }
    }
}