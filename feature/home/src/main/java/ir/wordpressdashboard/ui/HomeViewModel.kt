package ir.wordpressdashboard.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.model.Media
import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.usecase.GetMediaUseCase
import ir.wordpressdashboard.usecase.GetPostsUseCase
import ir.wordpressdashboard.usecase.GetProductUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProducts: GetProductUseCase,
    private val getPosts: GetPostsUseCase,
    private val getMedia: GetMediaUseCase
) : ViewModel() {

    var products by mutableStateOf<List<Products>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    var isPostsLoading by mutableStateOf(false)
        private set

    var mediaList by mutableStateOf<List<Media>>(emptyList())
        private set

    var isMediaLoading by mutableStateOf(false)
        private set

    fun loadProducts() {
        viewModelScope.launch {
            isLoading = true
            try {
                products = getProducts()
                Log.d("HomeViewModel", "loadProducts: $products")
            } catch (e: Exception) {
                products = emptyList()
                Log.e("HomeViewModel", "${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            isPostsLoading = true
            try {
                posts = getPosts()
                Log.d("HomeViewModel", "loadPosts: $posts")
            } catch (e: Exception) {
                posts = emptyList()
                Log.e("HomeViewModel", "loadPosts error: ${e.message}")
            } finally {
                isPostsLoading = false
            }
        }
    }

    fun loadMedia() {
        viewModelScope.launch {
            isMediaLoading = true
            try {
                mediaList = getMedia()
                Log.d("HomeViewModel", "loadMedia: $mediaList")
            } catch (e: Exception) {
                mediaList = emptyList()
                Log.e("HomeViewModel", "loadMedia error: ${e.message}")
            } finally {
                isMediaLoading = false
            }
        }
    }
}