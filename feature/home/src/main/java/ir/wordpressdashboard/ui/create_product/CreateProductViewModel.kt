package ir.wordpressdashboard.ui.create_product

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.model.ProductCategory
import ir.wordpressdashboard.model.ProductTag
import ir.wordpressdashboard.usecase.CreateProductUseCase
import ir.wordpressdashboard.usecase.GetProductTaxonomiesUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val createProductUseCase: CreateProductUseCase,
    private val taxonomiesUseCase: GetProductTaxonomiesUseCase,
) : ViewModel() {

    var isCreating by mutableStateOf(false)
        private set

    var createSuccess by mutableStateOf(false)
        private set

    var createError by mutableStateOf<String?>(null)
        private set

    // ── دسته‌بندی‌ها و تگ‌ها ──────────────────────────────────────────────
    var categories by mutableStateOf<List<ProductCategory>>(emptyList())
        private set

    var tags by mutableStateOf<List<ProductTag>>(emptyList())
        private set

    var isTaxonomiesLoading by mutableStateOf(false)
        private set

    var taxonomiesError by mutableStateOf<String?>(null)
        private set

    var isAddingCategory by mutableStateOf(false)
        private set

    var isAddingTag by mutableStateOf(false)
        private set

    var addCategoryError by mutableStateOf<String?>(null)
        private set

    var addTagError by mutableStateOf<String?>(null)
        private set

    init {
        loadTaxonomies()
    }

    fun loadTaxonomies() {
        viewModelScope.launch {
            isTaxonomiesLoading = true
            taxonomiesError = null
            try {
                categories = taxonomiesUseCase.getCategories()
                tags = taxonomiesUseCase.getTags()
            } catch (e: java.net.ConnectException) {
                taxonomiesError = "اتصال به سرور برقرار نشد. اینترنت یا آدرس سایت را بررسی کنید."
                Log.e("CreateProductVM", "loadTaxonomies error: ${e.message}", e)
            } catch (e: java.net.SocketTimeoutException) {
                taxonomiesError = "زمان اتصال به سرور به پایان رسید. دوباره تلاش کنید."
                Log.e("CreateProductVM", "loadTaxonomies timeout: ${e.message}", e)
            } catch (e: Exception) {
                taxonomiesError = "خطا در بارگذاری: ${e.message}"
                Log.e("CreateProductVM", "loadTaxonomies error: ${e.message}", e)
            } finally {
                isTaxonomiesLoading = false
            }
        }
    }

    fun createProduct(
        context: android.content.Context,
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        imageUris: List<android.net.Uri> = emptyList(),
        wpImageUrls: List<String> = emptyList(),
        categoryIds: List<Int> = emptyList(),
        tagIds: List<Int> = emptyList()
    ) {
        if (isCreating) return
        viewModelScope.launch {
            isCreating = true
            createSuccess = false
            createError = null
            try {
                Log.d("CreateProductVM", "Creating product: ${imageUris.size} local images, ${wpImageUrls.size} WP images, ${categoryIds.size} categories, ${tagIds.size} tags")
                createProductUseCase(
                    context = context,
                    name = name,
                    description = description,
                    price = price,
                    stockStatus = stockStatus,
                    imageUris = imageUris,
                    wpImageUrls = wpImageUrls,
                    categoryIds = categoryIds,
                    tagIds = tagIds
                )
                createSuccess = true
                Log.d("CreateProductVM", "Product created successfully")
            } catch (e: Exception) {
                val msg = e.message ?: "خطای ناشناخته"
                createError = when {
                    e is java.net.ConnectException ->
                        "اتصال به سرور برقرار نشد. اینترنت یا آدرس سایت را بررسی کنید."
                    e is java.net.SocketTimeoutException ->
                        "زمان اتصال به سرور به پایان رسید. دوباره تلاش کنید."
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

    fun createCategory(name: String, onCreated: (ProductCategory) -> Unit) {
        if (isAddingCategory) return
        viewModelScope.launch {
            isAddingCategory = true
            addCategoryError = null
            try {
                val cat = taxonomiesUseCase.createCategory(name.trim())
                categories = categories + cat
                onCreated(cat)
                Log.d("CreateProductVM", "Category created: ${cat.name}")
            } catch (e: Exception) {
                addCategoryError = "خطا در ساخت دسته‌بندی: ${e.message}"
                Log.e("CreateProductVM", "createCategory error: ${e.message}", e)
            } finally {
                isAddingCategory = false
            }
        }
    }

    fun createTag(name: String, onCreated: (ProductTag) -> Unit) {
        if (isAddingTag) return
        viewModelScope.launch {
            isAddingTag = true
            addTagError = null
            try {
                val tag = taxonomiesUseCase.createTag(name.trim())
                tags = tags + tag
                onCreated(tag)
                Log.d("CreateProductVM", "Tag created: ${tag.name}")
            } catch (e: Exception) {
                addTagError = "خطا در ساخت تگ: ${e.message}"
                Log.e("CreateProductVM", "createTag error: ${e.message}", e)
            } finally {
                isAddingTag = false
            }
        }
    }

    fun clearAddErrors() {
        addCategoryError = null
        addTagError = null
    }
}
