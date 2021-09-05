package ru.andrewkir.developerslifegifclient.data.api

import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.andrewkir.developerslifegifclient.utils.ResponseWithStatus
import ru.andrewkir.developerslifegifclient.utils.SectionsEnum

class PostsRepository(private val api: PostsApi) {
    suspend fun getPosts(section: SectionsEnum, page: Int = 0) =
        protectedCall { api.getPosts(section, page) }

    suspend fun getRandomPost() = protectedCall { api.getRandomPost() }

    private suspend fun <T> protectedCall(
        api: suspend () -> T
    ): ResponseWithStatus<T> {
        return withContext(Dispatchers.IO) {
            try {
                ResponseWithStatus.OnSuccessResponse(api.invoke())
            } catch (ex: Throwable) {
                when (ex) {
                    is HttpException -> {
                        try {
                            ResponseWithStatus.OnErrorResponse(
                                false,
                                ex.code(),
                                ex.response()?.errorBody()?.string()
                            )
                        } catch (exception: Throwable) {
                            ResponseWithStatus.OnErrorResponse(
                                false,
                                null,
                                "Ошибка на стороне сервера"
                            )
                        }
                    }
                    is JsonSyntaxException -> {
                        ResponseWithStatus.OnErrorResponse(false, null, ex.message)
                    }
                    else -> {
                        ResponseWithStatus.OnErrorResponse(true, null, null)
                    }
                }
            }
        }
    }
}