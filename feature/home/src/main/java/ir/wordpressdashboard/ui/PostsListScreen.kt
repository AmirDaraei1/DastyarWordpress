package ir.wordpressdashboard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.wordpressdashboard.model.Post

@Composable
fun PostsListScreen(viewModel: ProductViewModel = hiltViewModel()) {
    val posts = viewModel.posts
    val isLoading = viewModel.isPostsLoading
    val isLoadingMore = viewModel.isLoadingMorePosts
    val hasMore = viewModel.hasMorePosts
    val isOffline = viewModel.isOffline

    LaunchedEffect(Unit) { viewModel.loadPosts() }

    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 3 && total > 0
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore && hasMore && !isOffline)
            viewModel.loadNextPostsPage()
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
                text = "پست‌ها",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── بنر آفلاین ────────────────────────────────────────────────────
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
                    text = if (posts.isNotEmpty()) "بدون اینترنت — نمایش داده‌های ذخیره شده"
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
            // آفلاین و هیچ داده‌ای در دیتابیس نیست
            isOffline && posts.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(text = "📵", fontSize = 64.sp)
                        Text(
                            text = "اینترنت در دسترس نیست",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF444444)
                        )
                        Text(
                            text = "لطفاً اتصال اینترنت خود را بررسی کنید.",
                            fontSize = 14.sp,
                            color = Color(0xFF888888),
                            textAlign = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF6251A6))
                                .clickable { viewModel.loadPosts(force = true) }
                                .padding(horizontal = 32.dp, vertical = 12.dp)
                        ) {
                            Text("تلاش مجدد", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            posts.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "پستی یافت نشد", color = Color(0xFF999999), fontSize = 16.sp)
                }
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(posts, key = { "post_${it.id}" }) { post ->
                        PostCard(post = post)
                    }
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
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
                    if (!hasMore && posts.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("همه پست‌ها بارگذاری شدند", color = Color(0xFFBBBBBB), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Icon placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEDE7F6)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📝", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "شناسه: ${post.id}",
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}
