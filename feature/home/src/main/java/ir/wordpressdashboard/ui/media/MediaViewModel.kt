package ir.wordpressdashboard.ui.media

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.common.NetworkMonitor
import ir.wordpressdashboard.model.Media
import ir.wordpressdashboard.usecase.DeleteMediaUseCase
import ir.wordpressdashboard.usecase.GetMediaUseCase
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val getMedia: GetMediaUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE_MEDIA = 20
    }

    var mediaList by mutableStateOf<List<Media>>(emptyList())
        private set

    var isMediaLoading by mutableStateOf(false)
        private set

    var isLoadingMoreMedia by mutableStateOf(false)
        private set

    var hasMoreMedia by mutableStateOf(true)
        private set

    var isMediaRefreshing by mutableStateOf(false)
        private set

    var isOffline by mutableStateOf(false)
        private set

    var isDeletingMedia by mutableStateOf(false)
        private set

    var deleteMediaError by mutableStateOf<String?>(null)
        private set

    private var currentMediaPage = 1
    private var isMediaLoaded = false

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val wasOffline = isOffline
                isOffline = !online
                if (wasOffline && online && !isMediaLoaded) loadMedia(force = true)
            }
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
                Log.d("MediaVM", "loadMedia page 1: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("MediaVM", "loadMedia offline: ${e.message}")
            } catch (e: Exception) {
                Log.e("MediaVM", "loadMedia error: ${e.message}")
            } finally {
                isMediaLoading = false
            }
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
                Log.d("MediaVM", "loadMedia page $nextPage: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("MediaVM", "loadNextMedia offline: ${e.message}")
            } catch (e: Exception) {
                Log.e("MediaVM", "loadNextMedia error: ${e.message}")
            } finally {
                isLoadingMoreMedia = false
            }
        }
    }

    fun refreshMedia() {
        if (isMediaRefreshing || isMediaLoading) return
        viewModelScope.launch {
            isMediaRefreshing = true
            try {
                val result = getMedia(page = 1, perPage = PAGE_SIZE_MEDIA)
                mediaList = result
                currentMediaPage = 1
                hasMoreMedia = result.size >= PAGE_SIZE_MEDIA
                isMediaLoaded = true
                if (isOffline) isOffline = false
                Log.d("MediaVM", "refreshMedia: ${result.size} items")
            } catch (e: IOException) {
                isOffline = true
                Log.e("MediaVM", "refreshMedia offline: ${e.message}")
            } catch (e: Exception) {
                Log.e("MediaVM", "refreshMedia error: ${e.message}")
            } finally {
                isMediaRefreshing = false
            }
        }
    }

    fun deleteMedia(id: Int) {
        viewModelScope.launch {
            isDeletingMedia = true
            deleteMediaError = null
            try {
                deleteMediaUseCase(id)
                mediaList = mediaList.filter { it.id != id }
                Log.d("MediaVM", "Media $id deleted successfully")
            } catch (e: Exception) {
                deleteMediaError = "خطا در حذف تصویر: ${e.message}"
                Log.e("MediaVM", "deleteMedia error: ${e.message}", e)
            } finally {
                isDeletingMedia = false
            }
        }
    }

    fun resetDeleteMediaError() {
        deleteMediaError = null
    }
}
