package ir.wordpressdashboard.ui.product

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.common.NetworkMonitor
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.usecase.DeleteProductUseCase
import ir.wordpressdashboard.usecase.GetProductUseCase
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProducts: GetProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    var products by mutableStateOf<List<Products>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    var hasMoreProducts by mutableStateOf(true)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var isOffline by mutableStateOf(false)
        private set

    val isShowingCachedData: Boolean
        get() = isOffline && products.isNotEmpty()

    var deleteError by mutableStateOf<String?>(null)
        private set

    private var isLoaded = false
    private var currentPage = 1

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val wasOffline = isOffline
                isOffline = !online
                if (wasOffline && online && !isLoaded) loadProducts(force = true)
            }
        }
    }

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
                if (result.isNotEmpty()) isOffline = false
                Log.d("ProductsVM", "loaded page 1: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("ProductsVM", "Network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductsVM", "Error: ${e.message}")
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
                Log.d("ProductsVM", "loaded page $nextPage: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("ProductsVM", "loadNextPage network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductsVM", "loadNextPage error: ${e.message}")
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun refreshProducts() {
        if (isRefreshing || isLoading) return
        viewModelScope.launch {
            isRefreshing = true
            try {
                val result = getProducts(page = 1, perPage = PAGE_SIZE)
                products = result
                currentPage = 1
                hasMoreProducts = result.size >= PAGE_SIZE
                isLoaded = true
                if (isOffline) isOffline = false
                Log.d("ProductsVM", "refreshProducts: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("ProductsVM", "refreshProducts network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProductsVM", "refreshProducts error: ${e.message}")
            } finally {
                isRefreshing = false
            }
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            try {
                deleteProductUseCase(id)
                products = products.filter { it.id != id }
                Log.d("ProductsVM", "Product $id deleted successfully")
            } catch (e: Exception) {
                deleteError = "خطا در حذف محصول: ${e.message}"
                Log.e("ProductsVM", "deleteProduct error: ${e.message}", e)
            }
        }
    }

    fun updateProductInList(updated: Products) {
        products = products.map { if (it.id == updated.id) updated else it }
        Log.d("ProductsVM", "Product ${updated.id} updated in list: stock=${updated.stock_status}")
    }

    fun resetDeleteError() {
        deleteError = null
    }
}
