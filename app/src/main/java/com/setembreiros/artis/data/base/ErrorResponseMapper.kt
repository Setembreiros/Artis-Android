package com.setembreiros.artis.data.base

import com.google.gson.Gson
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