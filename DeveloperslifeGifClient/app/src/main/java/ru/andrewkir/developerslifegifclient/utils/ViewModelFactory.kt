package ru.andrewkir.developerslifegifclient.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.andrewkir.developerslifegifclient.data.api.ApiService
import ru.andrewkir.developerslifegifclient.data.api.PostsApi
import ru.andrewkir.developerslifegifclient.data.api.PostsRepository
import ru.andrewkir.developerslifegifclient.ui.PostsViewModel

class ViewModelFactory(private val postsApi: PostsApi) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsViewModel::class.java)) return PostsViewModel(
            PostsRepository(postsApi)
        ) as T
        else throw IllegalArgumentException("ViewModel was not defined")
    }
}