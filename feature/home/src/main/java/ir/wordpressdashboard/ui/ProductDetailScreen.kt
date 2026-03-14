package ir.wordpressdashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ir.wordpressdashboard.model.Products

@Composable
fun ProductDetailScreen(
    product: Products,
    onBack: () -> Unit
) {
    val purple = Color(0xFF6251A6)
    val inStock = product.stock_status == "instock"

    // Handle system back button
    BackHandler { onBack() }

    val pagerState = rememberPagerState(pageCount = { product.images.size.coerceAtLeast(1) })
    var selectedThumbnail by remember { mutableIntStateOf(0) }

    // ── Collapsible image height ──────────────────────────────────────────
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val density = LocalDensity.current
    val maxImageHeightDp = screenWidthDp.toFloat()   // full square
    val minImageHeightDp = 180f                       // collapsed

    var imageHeightDp by remember { mutableFloatStateOf(maxImageHeightDp) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            // ── Scroll UP (finger moves up, delta.y < 0) → collapse image FIRST
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val deltaDp = with(density) { available.y.toDp().value }
                // Only consume when scrolling UP and image is still collapsible
                if (deltaDp < 0f && imageHeightDp > minImageHeightDp) {
                    val newHeight = (imageHeightDp + deltaDp).coerceAtLeast(minImageHeightDp)
                    val consumedDp = newHeight - imageHeightDp
                    imageHeightDp = newHeight
                    val consumedPx = with(density) { consumedDp.dp.toPx() }
                    return Offset(0f, consumedPx)
                }
                return Offset.Zero
            }

            // ── Scroll DOWN (finger moves down, delta.y > 0) → expand image
            //    only AFTER LazyColumn can't scroll up anymore (consumed == 0)
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val deltaDp = with(density) { available.y.toDp().value }
                // available.y > 0 means LazyColumn had leftover (it's at top already)
                if (deltaDp > 0f && imageHeightDp < maxImageHeightDp) {
                    val newHeight = (imageHeightDp + deltaDp).coerceAtMost(maxImageHeightDp)
                    val consumedDp = newHeight - imageHeightDp
                    imageHeightDp = newHeight
                    val consumedPx = with(density) { consumedDp.dp.toPx() }
                    return Offset(0f, consumedPx)
                }
                return Offset.Zero
            }
        }
    }

    // Sync thumbnail indicator when user swipes pager
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedThumbnail = page
        }
    }
    // Sync pager when thumbnail is tapped
    LaunchedEffect(selectedThumbnail) {
        if (selectedThumbnail != pagerState.currentPage) {
            pagerState.animateScrollToPage(selectedThumbnail)
        }
    }

    // Show thumbnails & dots only when image is not fully collapsed
    val showThumbnailStrip by remember {
        derivedStateOf { imageHeightDp > minImageHeightDp + 20f }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .nestedScroll(nestedScrollConnection)
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(purple)
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White
                    )
                }
                Text(
                    text = product.name,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 56.dp)
                )
            }
        }

        // ── Collapsible image gallery ─────────────────────────────────────
        if (product.images.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeightDp.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color.White)
            ) {
                // key() prevents HorizontalPager recomposing on every height-change frame
                // which was causing ImageReader buffer overflow
                key(product.id) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = product.images[page].src,
                            contentDescription = product.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    }
                }
            }

            if (product.images.size > 1 && showThumbnailStrip) {
                // Dot indicators
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    product.images.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (index == selectedThumbnail) 10.dp else 7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == selectedThumbnail) purple else Color(0xFFCCCCCC)
                                )
                        )
                    }
                }

                // Thumbnail strip
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(product.images) { index, image ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (index == selectedThumbnail)
                                        purple.copy(alpha = 0.25f)
                                    else Color.White
                                )
                                .clickable { selectedThumbnail = index }
                        ) {
                            AsyncImage(
                                model = image.src,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Scrollable product info ───────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // No images placeholder
            if (product.images.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📦", fontSize = 64.sp)
                    }
                }
            }

            // Product name
            item {
                Text(
                    text = product.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Price + Stock row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (inStock) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (inStock) "✓ موجود در انبار" else "✗ ناموجود",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (inStock) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                    if (product.price.isNotEmpty()) {
                        Text(
                            text = "${formatPrice(product.price)} تومان",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = purple
                        )
                    } else {
                        Text(
                            text = "قیمت: تماس بگیرید",
                            fontSize = 14.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }

            // Description
            if (product.description.isNotBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "توضیحات محصول",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF222222)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFEEEEEE)
                        )
                        val cleanDescription = product.description
                            .replace(Regex("<[^>]+>"), "")
                            .trim()
                        Text(
                            text = cleanDescription,
                            fontSize = 14.sp,
                            color = Color(0xFF555555),
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
