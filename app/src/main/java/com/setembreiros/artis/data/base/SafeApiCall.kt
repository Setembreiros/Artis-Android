package com.setembreiros.artis.data.base

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
        suspend fun <A, D> safeApiCall(mapper: Mapper<A, D>, apiCall: suspend () -> WrapperApi<A>): Resource<D> {
        return withContext(Dispatchers.IO) {
            try {
                val result = apiCall.invoke()
                if(!result.error){
                    val obj = getWrapper(result)
                    val model = mapper.map(obj)
                    Resource.Success(model)
                }else{
                    handleHttpException(0, result.message)
                }

            } catch (throwable: Throwable) {
                handle(throwable)
            }
        }
    }

    private fun handle(throwable: Throwable): Resource.Failure {
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

    private fun handleHttpException(code: Int, message: String? = ""): Resource.Failure {
        return when(code) {
            HttpCode.MAPPER_ERROR -> Resource.Failure(FailureError.MapperError, "")
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