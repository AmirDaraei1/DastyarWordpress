package ir.wordpressdashboard.ui.edit_product

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.usecase.CreateProductUseCase
import ir.wordpressdashboard.usecase.UpdateProductUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val updateProductUseCase: UpdateProductUseCase,
    private val createProductUseCase: CreateProductUseCase,
) : ViewModel() {

    var isUpdating by mutableStateOf(false)
        private set

    var updateSuccess by mutableStateOf(false)
        private set

    var updateError by mutableStateOf<String?>(null)
        private set

    var updatedProduct by mutableStateOf<Products?>(null)
        private set

    fun updateProduct(
        id: Int,
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        existingImageUrls: List<String> = emptyList(),
        newLocalImageUris: List<android.net.Uri> = emptyList(),
        newWpImageUrls: List<String> = emptyList(),
        context: android.content.Context? = null
    ) {
        if (isUpdating) return
        viewModelScope.launch {
            isUpdating = true
            updateSuccess = false
            updateError = null
            try {
                val uploadedUrls = mutableListOf<String>()
                if (newLocalImageUris.isNotEmpty() && context != null) {
                    newLocalImageUris.forEachIndexed { index, uri ->
                        Log.d("EditProductVM", "Uploading new image ${index + 1}/${newLocalImageUris.size}")
                        val url = createProductUseCase.uploadImage(context, uri).second
                        uploadedUrls.add(url)
                    }
                }
                val allImageUrls = existingImageUrls + uploadedUrls + newWpImageUrls
                val updated = updateProductUseCase(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    stockStatus = stockStatus,
                    imageUrls = allImageUrls
                )
                updatedProduct = updated
                updateSuccess = true
                Log.d("EditProductVM", "Product $id updated successfully")
            } catch (e: Exception) {
                updateError = "خطا در ویرایش محصول: ${e.message}"
                Log.e("EditProductVM", "updateProduct error: ${e.message}", e)
            } finally {
                isUpdating = false
            }
        }
    }

    fun resetUpdateState() {
        updateSuccess = false
        updateError = null
        updatedProduct = null
    }
}
