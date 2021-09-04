package ru.andrewkir.developerslifegifclient.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.andrewkir.developerslifegifclient.data.api.PostsApi
import ru.andrewkir.developerslifegifclient.data.api.PostsRepository
import ru.andrewkir.developerslifegifclient.data.model.PostInfo
import ru.andrewkir.developerslifegifclient.utils.ButtonVisibilityHolder
import ru.andrewkir.developerslifegifclient.utils.ResponseWithStatus
import ru.andrewkir.developerslifegifclient.utils.SectionsEnum

class PostsViewModel(private val postsRepository: PostsRepository) : ViewModel() {

    private val mutableLoading: MutableLiveData<Boolean?> = MutableLiveData()
    val loading: LiveData<Boolean?>
        get() = mutableLoading

    private val mutableErrorResponse: MutableLiveData<ResponseWithStatus.OnErrorResponse> =
        MutableLiveData()
    val errorResponse: LiveData<ResponseWithStatus.OnErrorResponse>
        get() = mutableErrorResponse

    private lateinit var section: SectionsEnum
    private val posts = mutableListOf<PostInfo>()
    private var currentPost = 0
    private var postsAmount = 0
    private var page = 0

    //Для каждой из кнопок будем использовать отдельную liveData
    private val mutableBackButtonVisibility: MutableLiveData<Boolean> =
        MutableLiveData()
    val backButtonVisibility: LiveData<Boolean>
        get() = mutableBackButtonVisibility

    private val mutableForwardButtonVisibility: MutableLiveData<Boolean> =
        MutableLiveData()
    val forwardButtonVisibility: LiveData<Boolean>
        get() = mutableForwardButtonVisibility

    private val mutablePostLiveData: MutableLiveData<PostInfo?> = MutableLiveData()
    val postLiveData: LiveData<PostInfo?>
        get() = mutablePostLiveData


    private fun getPosts() {
        viewModelScope.launch {
            mutableLoading.value = true
            when (val result = postsRepository.getPosts(section, page)) {
                is ResponseWithStatus.OnSuccessResponse -> {
                    val listPosts = result.value.result
                    if (listPosts != null && !posts.containsAll(listPosts)
                    ) {
                        posts.addAll(listPosts)
                    }
                    postsAmount = result.value.totalCount ?: 0
                }
                is ResponseWithStatus.OnErrorResponse -> {
                    mutableErrorResponse.value = result
                }
            }
            mutableLoading.value = false
            updatePost()
        }
    }

    fun nextPost() {
        if (currentPost + 1 >= posts.size) {
            page++
            getPosts()
        } else {
            currentPost++
            updatePost()
        }
    }

    fun previousPost() {
        if (currentPost > 0) {
            currentPost--
            updatePost()
        }
    }

    private fun updatePost() {
        if (currentPost < posts.size) mutablePostLiveData.value = posts[currentPost]
        if (posts.size == 0) {
            mutablePostLiveData.value = null
        }

        mutableBackButtonVisibility.value = currentPost != 0
        mutableForwardButtonVisibility.value = currentPost + 1 < postsAmount
    }

    fun init(section: SectionsEnum) {
        if (posts.size == 0) {
            this.section = section
            getPosts()
        }
    }
}