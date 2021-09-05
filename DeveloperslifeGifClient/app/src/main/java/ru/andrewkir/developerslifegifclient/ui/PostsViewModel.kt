package ru.andrewkir.developerslifegifclient.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.andrewkir.developerslifegifclient.data.api.PostsRepository
import ru.andrewkir.developerslifegifclient.data.model.PostInfo
import ru.andrewkir.developerslifegifclient.utils.ResponseWithStatus
import ru.andrewkir.developerslifegifclient.utils.SectionsEnum

class PostsViewModel(private val postsRepository: PostsRepository) : ViewModel() {

    private val mutableLoading: MutableLiveData<Boolean?> = MutableLiveData()
    val loading: LiveData<Boolean?>
        get() = mutableLoading

    private val mutableErrorResponse: MutableLiveData<ResponseWithStatus.OnErrorResponse?> =
        MutableLiveData()
    val errorResponse: LiveData<ResponseWithStatus.OnErrorResponse?>
        get() = mutableErrorResponse

    private lateinit var currentSection: SectionsEnum
    private val displayedPosts = mutableListOf<PostInfo>()
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


    fun getPosts() {
        when (currentSection) {
            SectionsEnum.random -> {
                viewModelScope.launch {
                    mutableLoading.value = true
                    when (val result = postsRepository.getRandomPost()) {
                        is ResponseWithStatus.OnSuccessResponse -> {
                            mutableLoading.value = false
                            displayedPosts.add(result.value)
                            updatePostUI()
                            mutableErrorResponse.value = null
                        }
                        is ResponseWithStatus.OnErrorResponse -> {
                            //TODO CHECK ERRORS
                            mutableLoading.value = false
                            mutableErrorResponse.value = result
                        }
                    }
                }
            }
            else -> {
                viewModelScope.launch {
                    mutableLoading.value = true
                    when (val result = postsRepository.getPosts(currentSection, page)) {
                        is ResponseWithStatus.OnSuccessResponse -> {
                            mutableLoading.value = false
                            val listPosts = result.value.result
                            if (listPosts != null && !displayedPosts.containsAll(listPosts)
                            ) {
                                displayedPosts.addAll(listPosts)
                            }
                            postsAmount = result.value.totalCount ?: 0
                            updatePostUI()
                            mutableErrorResponse.value = null
                        }
                        is ResponseWithStatus.OnErrorResponse -> {
                            //TODO CHECK ERRORS
                            mutableLoading.value = false
                            mutableErrorResponse.value = result
                        }
                    }
                }
            }
        }
    }



    fun nextPost() {
        currentPost++
        if (currentPost < postsAmount || currentSection == SectionsEnum.random) {
            if (currentPost >= displayedPosts.size) {
                page++
                getPosts()
            } else {
                updatePostUI()
            }
        } else {
            currentPost--
        }
    }

    fun previousPost() {
        if (currentPost > 0) {
            currentPost--
            updatePostUI()
        }
    }

    private fun updatePostUI() {
        if (currentPost < displayedPosts.size) mutablePostLiveData.value = displayedPosts[currentPost]
        if (displayedPosts.size == 0) {
            mutablePostLiveData.value = null
        }

        mutableBackButtonVisibility.value = currentPost != 0
        mutableForwardButtonVisibility.value =
            currentPost + 1 < postsAmount || currentSection == SectionsEnum.random //Если категория "случайные", то кнопка всегда активна
    }

    fun init(section: SectionsEnum) {
        if (displayedPosts.size == 0) {
            this.currentSection = section
            getPosts()
        }
    }
}