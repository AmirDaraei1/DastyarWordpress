package ir.wordpressdashboard.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.common.NetworkMonitor
import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.usecase.GetPostsUseCase
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getPosts: GetPostsUseCase,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    var isPostsLoading by mutableStateOf(false)
        private set

    var isLoadingMorePosts by mutableStateOf(false)
        private set

    var hasMorePosts by mutableStateOf(true)
        private set

    var isPostsRefreshing by mutableStateOf(false)
        private set

    var isOffline by mutableStateOf(false)
        private set

    private var currentPostsPage = 1
    private var isPostsLoaded = false

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val wasOffline = isOffline
                isOffline = !online
                if (wasOffline && online && !isPostsLoaded) loadPosts(force = true)
            }
        }
    }

    fun loadPosts(force: Boolean = false) {
        if (isPostsLoaded && !force) return
        if (isPostsLoading) return
        viewModelScope.launch {
            isPostsLoading = true
            try {
                val result = getPosts(page = 1, perPage = PAGE_SIZE)
                posts = result
                currentPostsPage = 1
                hasMorePosts = result.size >= PAGE_SIZE
                isPostsLoaded = true
                if (result.isNotEmpty() && isOffline) isOffline = false
                Log.d("PostsVM", "loadPosts page 1: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("PostsVM", "loadPosts network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("PostsVM", "loadPosts error: ${e.message}")
            } finally {
                isPostsLoading = false
            }
        }
    }

    fun loadNextPostsPage() {
        if (isLoadingMorePosts || !hasMorePosts) return
        viewModelScope.launch {
            isLoadingMorePosts = true
            try {
                val nextPage = currentPostsPage + 1
                val result = getPosts(page = nextPage, perPage = PAGE_SIZE)
                val existingIds = posts.map { it.id }.toHashSet()
                posts = posts + result.filter { it.id !in existingIds }
                currentPostsPage = nextPage
                hasMorePosts = result.size >= PAGE_SIZE
                if (isOffline) isOffline = false
                Log.d("PostsVM", "loadPosts page $nextPage: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("PostsVM", "loadNextPosts network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("PostsVM", "loadNextPosts error: ${e.message}")
            } finally {
                isLoadingMorePosts = false
            }
        }
    }

    fun refreshPosts() {
        if (isPostsRefreshing || isPostsLoading) return
        viewModelScope.launch {
            isPostsRefreshing = true
            try {
                val result = getPosts(page = 1, perPage = PAGE_SIZE)
                posts = result
                currentPostsPage = 1
                hasMorePosts = result.size >= PAGE_SIZE
                isPostsLoaded = true
                if (isOffline) isOffline = false
                Log.d("PostsVM", "refreshPosts: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("PostsVM", "refreshPosts network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("PostsVM", "refreshPosts error: ${e.message}")
            } finally {
                isPostsRefreshing = false
            }
        }
    }
}
