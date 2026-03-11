package ir.wordpressdashboard.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import ir.wordpressdashboard.model.Media

@Composable
fun MediaListScreen(viewModel: ProductViewModel = hiltViewModel()) {
    val mediaList = viewModel.mediaList
    val isLoading = viewModel.isMediaLoading
    val isLoadingMore = viewModel.isLoadingMoreMedia
    val hasMore = viewModel.hasMoreMedia
    val isOffline = viewModel.isOffline

    // ایندکس عکس انتخاب‌شده برای نمایش detail (-1 = هیچ‌کدام)
    var selectedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) { viewModel.loadMedia() }

    // نمایش صفحه detail
    if (selectedIndex >= 0) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            MediaDetailScreen(
                mediaList = mediaList,
                initialIndex = selectedIndex,
                onBack = { selectedIndex = -1 }
            )
        }
        return
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
                .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "تصاویر",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
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
                    text = if (mediaList.isNotEmpty()) "بدون اینترنت — نمایش داده‌های ذخیره شده"
                    else "اینترنت در دسترس نیست",
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
                        Text("اینترنت در دسترس نیست", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Text("لطفاً اتصال اینترنت خود را بررسی کنید.", fontSize = 14.sp, color = Color(0xFF888888), textAlign = TextAlign.Center)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF6251A6))
                                .clickable { viewModel.loadMedia(force = true) }
                                .padding(horizontal = 32.dp, vertical = 12.dp)
                        ) {
                            Text("تلاش مجدد", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            mediaList.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("تصویری یافت نشد", color = Color(0xFF999999), fontSize = 16.sp)
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
                            onClick = { selectedIndex = index }
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
                                Text("همه تصاویر بارگذاری شدند", color = Color(0xFFBBBBBB), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MediaGridItem(
    media: Media,
    onClick: () -> Unit = {}
) {
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
            }
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                Text(
                    text = media.title.ifEmpty { "بدون عنوان" },
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
