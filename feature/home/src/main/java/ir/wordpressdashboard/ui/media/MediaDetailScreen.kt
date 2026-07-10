package ir.wordpressdashboard.ui.media

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ir.wordpressdashboard.i18n.LocalStrings
import ir.wordpressdashboard.model.Media

@Composable
fun MediaDetailScreen(
    mediaList: List<Media>,
    initialIndex: Int = 0,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    BackHandler { onBack() }

    val imageItems = mediaList.filter { it.mimeType.startsWith("image") }
    val startIndex = imageItems.indexOfFirst { it.id == mediaList.getOrNull(initialIndex)?.id }
        .coerceAtLeast(0)

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { imageItems.size.coerceAtLeast(1) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // key(pagerState) → HorizontalPager خودش را recompose نمی‌کند وقتی state عوض می‌شه
        key(Unit) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1   // فقط ۱ صفحه کنار را preload می‌کند
            ) { page ->
                val media = imageItems.getOrNull(page) ?: return@HorizontalPager

                // هر صفحه zoom state مستقل دارد
                var scale by remember(page) { mutableFloatStateOf(1f) }
                var offsetX by remember(page) { mutableFloatStateOf(0f) }
                var offsetY by remember(page) { mutableFloatStateOf(0f) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(page) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(1f, 5f)
                                scale = newScale
                                if (newScale > 1f) {
                                    offsetX += pan.x
                                    offsetY += pan.y
                                } else {
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // بدون animateFloatAsState — مستقیم scale استفاده می‌شود
                    // تا recompose loop و buffer overflow جلوگیری شود
                    AsyncImage(
                        model = media.sourceUrl,
                        contentDescription = media.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            )
                    )
                }
            }
        }

        // Top bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.back,
                        tint = Color.White
                    )
                }

                val current = imageItems.getOrNull(pagerState.currentPage)
                Text(
                    text = current?.title?.ifEmpty { strings.imageCounterTitle(pagerState.currentPage + 1) } ?: "",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 56.dp)
                )

                Text(
                    text = strings.imagePosition(pagerState.currentPage + 1, imageItems.size),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                )
            }
        }
    }
}
