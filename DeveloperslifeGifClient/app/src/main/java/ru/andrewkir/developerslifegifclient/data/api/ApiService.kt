package ru.andrewkir.developerslifegifclient.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import ru.andrewkir.developerslifegifclient.data.model.PostInfo

interface ApiService {
    @GET("{section}/{page}?json=true")
    suspend fun getPosts(@Path("section") section: String, @Path("page") page: Int): List<PostInfo>
}