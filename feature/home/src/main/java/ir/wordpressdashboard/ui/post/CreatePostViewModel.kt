package ir.wordpressdashboard.ui.post

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.usecase.CreatePostUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    var isCreating by mutableStateOf(false)
        private set

    var createSuccess by mutableStateOf(false)
        private set

    var createError by mutableStateOf<String?>(null)
        private set

    var createdPost by mutableStateOf<Post?>(null)
        private set

    fun createPost(title: String, content: String, excerpt: String, status: String) {
        if (isCreating) return
        viewModelScope.launch {
            isCreating = true
            createSuccess = false
            createError = null
            try {
                val result = createPostUseCase(
                    title = title,
                    content = content,
                    excerpt = excerpt,
                    status = status
                )
                createdPost = result
                createSuccess = true
                Log.d("CreatePostVM", "Post created: ${result.id}")
            } catch (e: java.net.ConnectException) {
                createError = "اتصال به سرور برقرار نشد."
                Log.e("CreatePostVM", "createPost connect error: ${e.message}", e)
            } catch (e: Exception) {
                createError = "خطا در ایجاد پست: ${e.message}"
                Log.e("CreatePostVM", "createPost error: ${e.message}", e)
            } finally {
                isCreating = false
            }
        }
    }

    fun resetState() {
        createSuccess = false
        createError = null
        createdPost = null
    }
}
