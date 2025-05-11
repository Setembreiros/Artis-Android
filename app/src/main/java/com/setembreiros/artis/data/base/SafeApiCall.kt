package com.setembreiros.artis.data.base

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.setembreiros.artis.data.model.EmptyResponse
import com.setembreiros.artis.data.model.WrapperApi
import com.squareup.moshi.JsonEncodingException
import com.setembreiros.artis.domain.base.Resource
import es.xeria.iventxff.domain.base.FailureError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

interface SafeApiCall {
    @OptIn(UnstableApi::class)
    suspend fun <A, D> safeApiCall(mapper: Mapper<A, D>, apiCall: suspend () -> WrapperApi<A>): Resource<D> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiCall.invoke()
                if(!result.error){
                    Log.i("SafeApiCall", result.message)
                    val obj = getWrapper(result)
                    val model = mapper.map(obj)
                    Resource.Success(model)
                }else{
                    Log.e("SafeApiCall", result.message)
                    handleHttpException(0, result.message)
                }
            } catch (throwable: Throwable) {
                handle(throwable)
            }
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun safeApiCall(apiCall: suspend () -> WrapperApi<EmptyResponse?>): Resource<EmptyResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiCall.invoke()
                if(!result.error){
                    Log.i("SafeApiCall", result.message)
                    Resource.Success(EmptyResponse())
                }else{
                    Log.e("SafeApiCall", result.message)
                    handleHttpException(0, result.message)
                }
            } catch (throwable: Throwable) {
                handle(throwable)
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun handle(throwable: Throwable): Resource.Failure {
        Log.e("SafeApiCall", "UnexpectedError")
        return when (throwable) {
            is HttpException -> {
                val errorResponse = ErrorResponseMapper.map(throwable)
                handleHttpException(throwable.code(), errorResponse?.ErrorMessage)
            }
            is JsonEncodingException -> Resource.Failure(FailureError.Mapping)
            is UnknownHostException, is IOException -> Resource.Failure(FailureError.Network)
            else -> Resource.Failure(FailureError.Generic)
        }
    }

    @OptIn(UnstableApi::class)
    private fun handleHttpException(code: Int, message: String? = ""): Resource.Failure {
        message?.let { Log.e("SafeApiCall", it) }
        return when(code) {
            HttpCode.MAPPER_ERROR -> {
                Log.e("SafeApiCall", "Mapping failed")
                Resource.Failure(FailureError.MapperError, "")
            }
            HttpCode.UNAUTHORIZED -> Resource.Failure(FailureError.Unauthorized, message)
            HttpCode.NOT_FOUND -> Resource.Failure(FailureError.NotFound, message)
            HttpCode.BAD_REQUEST -> Resource.Failure(FailureError.BadRequest, message)
            else -> Resource.Failure(FailureError.Generic, message)
        }
    }

    private fun <A>getWrapper(wrapper: WrapperApi<A>): A{
        return wrapper.content as A
    }
}