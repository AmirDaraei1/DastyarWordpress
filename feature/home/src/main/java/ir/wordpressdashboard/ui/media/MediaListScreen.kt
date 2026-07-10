package ir.wordpressdashboard.ui.media

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.wordpressdashboard.i18n.LocalStrings
import ir.wordpressdashboard.i18n.resolve
import ir.wordpressdashboard.model.Media

@Composable
fun MediaListScreen(refreshTrigger: Int = 0, viewModel: MediaViewModel = hiltViewModel()) {
    val strings = LocalStrings.current
    val currentLayoutDirection = LocalLayoutDirection.current
    val mediaList = viewModel.mediaList
    val isLoading = viewModel.isMediaLoading
    val isLoadingMore = viewModel.isLoadingMoreMedia
    val hasMore = viewModel.hasMoreMedia
    val isOffline = viewModel.isOffline
    val isRefreshing = viewModel.isMediaRefreshing

    // ایندکس عکس انتخاب‌شده برای نمایش detail (-1 = هیچ‌کدام)
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var mediaToDelete by remember { mutableStateOf<Media?>(null) }

    LaunchedEffect(Unit) { viewModel.loadMedia() }

    LaunchedEffect(refreshTrigger) {
        if (refreshTrigger > 0) viewModel.refreshMedia()
    }

    // نمایش صفحه detail
    if (selectedIndex >= 0) {
        CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDirection) {
            MediaDetailScreen(
                mediaList = mediaList,
                initialIndex = selectedIndex,
                onBack = { selectedIndex = -1 }
            )
        }
        return
    }

    // دیالوگ تأیید حذف
    if (mediaToDelete != null) {
        AlertDialog(
            onDismissRequest = { mediaToDelete = null },
            title = {
                Text(
                    text = strings.resolve("حذف تصویر"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = strings.deleteImageConfirm(mediaToDelete!!.title.ifEmpty { strings.untitled }),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMedia(mediaToDelete!!.id)
                        mediaToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text(strings.yesDelete, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mediaToDelete = null }) {
                    Text(strings.cancel)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    val gridState = rememberLazyGridState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = gridState.layoutInfo.totalItemsCount
            lastVisible >= total - 4 && total > 0
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore && hasMore && !isOffline) {
            viewModel.loadNextMediaPage()
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = { viewModel.refreshMedia() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6251A6))
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.mediaTitle,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // بنر آفلاین
        AnimatedVisibility(visible = isOffline) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF3E0))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📡", fontSize = 18.sp)
                Text(
                    text = if (mediaList.isNotEmpty()) strings.offlineCachedData else strings.noInternet,
                    fontSize = 13.sp,
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6251A6))
                }
            }
            isOffline && mediaList.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(text = "📵", fontSize = 64.sp)
                        Text(strings.noInternet, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Text(strings.checkInternet, fontSize = 14.sp, color = Color(0xFF888888), textAlign = TextAlign.Center)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF6251A6))
                                .clickable { viewModel.loadMedia(force = true) }
                                .padding(horizontal = 32.dp, vertical = 12.dp)
                        ) {
                            Text(strings.retry, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            mediaList.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(strings.noMediaFound, color = Color(0xFF999999), fontSize = 16.sp)
                }
            }
            else -> {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(mediaList, key = { _, item -> "media_${item.id}" }) { index, media ->
                        MediaGridItem(
                            media = media,
                            onClick = { selectedIndex = index },
                            onDeleteClick = { mediaToDelete = media }
                        )
                    }
                    // loading more indicator
                    if (isLoadingMore) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF6251A6),
                                    modifier = Modifier.size(28.dp),
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                    // end of list
                    if (!hasMore && mediaList.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(strings.allImagesLoaded, color = Color(0xFFBBBBBB), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    } // end Column
    } // end SwipeRefresh
}

@Composable
fun MediaGridItem(
    media: Media,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                if (media.mimeType.startsWith("image")) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(media.sourceUrl)
                            .crossfade(true)
                            .size(300, 300)
                            .build(),
                        contentDescription = media.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = when {
                            media.mimeType.startsWith("video") -> "🎬"
                            media.mimeType.startsWith("audio") -> "🎵"
                            else -> "📎"
                        },
                        fontSize = 36.sp
                    )
                }

                // دکمه حذف — گوشه بالا سمت چپ
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xCCE53935))
                        .clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = strings.delete,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                Text(
                    text = media.title.ifEmpty { strings.untitled },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = media.mimeType,
                    fontSize = 10.sp,
                    color = Color(0xFF9E9E9E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
