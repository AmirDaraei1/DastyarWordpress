package ir.wordpressdashboard.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.common.NetworkMonitor
import ir.wordpressdashboard.model.Media
import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.usecase.CreateProductUseCase
import ir.wordpressdashboard.usecase.GetMediaUseCase
import ir.wordpressdashboard.usecase.GetPostsUseCase
import ir.wordpressdashboard.usecase.GetProductUseCase
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProducts: GetProductUseCase,
    private val getPosts: GetPostsUseCase,
    private val getMedia: GetMediaUseCase,
    private val networkMonitor: NetworkMonitor,
    private val createProductUseCase: CreateProductUseCase
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
        private const val PAGE_SIZE_MEDIA = 20
    }

    var products by mutableStateOf<List<Products>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    var hasMoreProducts by mutableStateOf(true)
        private set

    // true = آفلاین، false = آنلاین
    var isOffline by mutableStateOf(false)
        private set

    // وقتی آفلاین است و داده‌ای از cache موجود است
    val isShowingCachedData: Boolean
        get() = isOffline && products.isNotEmpty()

    // آیا صفحه اول بارگذاری شده
    private var isLoaded = false
    private var currentPage = 1

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set
    var isPostsLoading by mutableStateOf(false)
        private set
    var isLoadingMorePosts by mutableStateOf(false)
        private set
    var hasMorePosts by mutableStateOf(true)
        private set
    private var currentPostsPage = 1
    private var isPostsLoaded = false

    var mediaList by mutableStateOf<List<Media>>(emptyList())
        private set
    var isMediaLoading by mutableStateOf(false)
        private set
    var isLoadingMoreMedia by mutableStateOf(false)
        private set
    var hasMoreMedia by mutableStateOf(true)
        private set
    private var currentMediaPage = 1
    private var isMediaLoaded = false

    init {
        // مانیتور کردن وضعیت اینترنت
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val wasOffline = isOffline
                isOffline = !online
                // وقتی اینترنت برگشت و هنوز لود نشده، دوباره تلاش کن
                if (wasOffline && online) {
                    if (!isLoaded) loadProducts(force = true)
                    if (!isPostsLoaded) loadPosts(force = true)
                    if (!isMediaLoaded) loadMedia(force = true)
                }
            }
        }
    }

    // force=true یعنی حتی اگر قبلاً لود شده، دوباره از شبکه بخواه
    fun loadProducts(force: Boolean = false) {
        if (isLoaded && !force) return
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            try {
                val result = getProducts(page = 1, perPage = PAGE_SIZE)
                products = result
                currentPage = 1
                hasMoreProducts = result.size >= PAGE_SIZE
                isLoaded = true
                // اگر داده گرفتیم → آنلاین هستیم
                if (result.isNotEmpty()) isOffline = false
                Log.d("ProductVM", "loaded page 1: ${result.size} items (offline=$isOffline)")
            } catch (e: IOException) {
                isOffline = true
                Log.e("ProductVM", "Network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductVM", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun loadNextPage() {
        if (isLoadingMore || !hasMoreProducts) return
        viewModelScope.launch {
            isLoadingMore = true
            try {
                val nextPage = currentPage + 1
                val result = getProducts(page = nextPage, perPage = PAGE_SIZE)
                val existingIds = products.map { it.id }.toHashSet()
                products = products + result.filter { it.id !in existingIds }
                currentPage = nextPage
                hasMoreProducts = result.size >= PAGE_SIZE
                if (isOffline) isOffline = false
                Log.d("ProductVM", "loaded page $nextPage: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("ProductVM", "loadNextPage network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductVM", "loadNextPage error: ${e.message}")
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun refreshProducts() {
        isLoaded = false
        products = emptyList()
        currentPage = 1
        hasMoreProducts = true
        loadProducts(force = true)
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
                Log.d("ProductVM", "loadPosts page 1: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("ProductVM", "loadPosts network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductVM", "loadPosts error: ${e.message}")
            } finally { isPostsLoading = false }
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
                Log.d("ProductVM", "loadPosts page $nextPage: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("ProductVM", "loadNextPosts network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductVM", "loadNextPosts error: ${e.message}")
            } finally { isLoadingMorePosts = false }
        }
    }

    fun loadMedia(force: Boolean = false) {
        if (isMediaLoaded && !force) return
        if (isMediaLoading) return
        viewModelScope.launch {
            isMediaLoading = true
            try {
                val result = getMedia(page = 1, perPage = PAGE_SIZE_MEDIA)
                mediaList = result
                currentMediaPage = 1
                hasMoreMedia = result.size >= PAGE_SIZE_MEDIA
                isMediaLoaded = true
                Log.d("ProductVM", "loadMedia page 1: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("ProductVM", "loadMedia offline: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductVM", "loadMedia error: ${e.message}")
            } finally { isMediaLoading = false }
        }
    }

    fun loadNextMediaPage() {
        if (isLoadingMoreMedia || !hasMoreMedia) return
        viewModelScope.launch {
            isLoadingMoreMedia = true
            try {
                val nextPage = currentMediaPage + 1
                val result = getMedia(page = nextPage, perPage = PAGE_SIZE_MEDIA)
                val existingIds = mediaList.map { it.id }.toHashSet()
                mediaList = mediaList + result.filter { it.id !in existingIds }
                currentMediaPage = nextPage
                hasMoreMedia = result.size >= PAGE_SIZE_MEDIA
                Log.d("ProductVM", "loadMedia page $nextPage: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("ProductVM", "loadNextMedia offline: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductVM", "loadNextMedia error: ${e.message}")
            } finally { isLoadingMoreMedia = false }
        }
    }

    // ── Create Product ────────────────────────────────────────────────────
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
                Log.d("ProductVM", "Creating product: ${imageUris.size} local images, ${wpImageUrls.size} WP images")
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
                refreshProducts()
                Log.d("ProductVM", "Product created successfully")
            } catch (e: Exception) {
                val msg = e.message ?: "خطای ناشناخته"
                createError = when {
                    msg.contains("upload", ignoreCase = true) ||
                    msg.contains("آپلود") -> "خطا در آپلود تصویر: $msg"
                    else -> "خطا در ایجاد محصول: $msg"
                }
                Log.e("ProductVM", "createProduct error: $msg", e)
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
