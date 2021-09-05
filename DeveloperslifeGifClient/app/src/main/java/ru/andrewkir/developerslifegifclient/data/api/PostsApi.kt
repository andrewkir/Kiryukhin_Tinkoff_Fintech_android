package ru.andrewkir.developerslifegifclient.data.api

import ru.andrewkir.developerslifegifclient.utils.SectionsEnum

class PostsApi(private val apiService: ApiService) {

    suspend fun getPosts(section: SectionsEnum, page: Int = 0) =
        apiService.getPosts(section.name, page)

    suspend fun getRandomPost() = apiService.getRandomPost()
}
