package ir.wordpressdashboard.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.usecase.CreateProductUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val createProductUseCase: CreateProductUseCase,
) : ViewModel() {

    var isCreating by mutableStateOf(false)
        private set

    var createSuccess by mutableStateOf(false)
        private set

    var createError by mutableStateOf<String?>(null)
        private set

    fun createProduct(
        context: android.content.Context,
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        imageUris: List<android.net.Uri> = emptyList(),
        wpImageUrls: List<String> = emptyList()
    ) {
        if (isCreating) return
        viewModelScope.launch {
            isCreating = true
            createSuccess = false
            createError = null
            try {
                Log.d("CreateProductVM", "Creating product: ${imageUris.size} local images, ${wpImageUrls.size} WP images")
                createProductUseCase(
                    context = context,
                    name = name,
                    description = description,
                    price = price,
                    stockStatus = stockStatus,
                    imageUris = imageUris,
                    wpImageUrls = wpImageUrls
                )
                createSuccess = true
                Log.d("CreateProductVM", "Product created successfully")
            } catch (e: Exception) {
                val msg = e.message ?: "خطای ناشناخته"
                createError = when {
                    msg.contains("upload", ignoreCase = true) ||
                    msg.contains("آپلود") -> "خطا در آپلود تصویر: $msg"
                    else -> "خطا در ایجاد محصول: $msg"
                }
                Log.e("CreateProductVM", "createProduct error: $msg", e)
            } finally {
                isCreating = false
            }
        }
    }

    fun resetCreateState() {
        createSuccess = false
        createError = null
    }
}
