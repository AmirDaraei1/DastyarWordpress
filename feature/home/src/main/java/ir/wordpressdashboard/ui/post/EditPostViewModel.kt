package ir.wordpressdashboard.ui.post

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.usecase.UpdatePostUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val updatePostUseCase: UpdatePostUseCase
) : ViewModel() {

    var isUpdating by mutableStateOf(false)
        private set

    var updateSuccess by mutableStateOf(false)
        private set

    var updateError by mutableStateOf<String?>(null)
        private set

    var updatedPost by mutableStateOf<Post?>(null)
        private set

    fun updatePost(id: Int, title: String, content: String, status: String) {
        if (isUpdating) return
        viewModelScope.launch {
            isUpdating = true
            updateSuccess = false
            updateError = null
            try {
                val result = updatePostUseCase(id = id, title = title, content = content, status = status)
                updatedPost = result
                updateSuccess = true
                Log.d("EditPostVM", "Post $id updated successfully")
            } catch (e: Exception) {
                updateError = "خطا در ویرایش پست: ${e.message}"
                Log.e("EditPostVM", "updatePost error: ${e.message}", e)
            } finally {
                isUpdating = false
            }
        }
    }

    fun resetState() {
        updateSuccess = false
        updateError = null
        updatedPost = null
    }
}
