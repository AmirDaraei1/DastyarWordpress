package ir.wordpressdashboard.ui.post

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ir.wordpressdashboard.model.Post

@Composable
fun PostsListScreen(
    onPostClick: (Post) -> Unit = {},
    refreshTrigger: Int = 0,
    viewModel: PostsViewModel = hiltViewModel()
) {
    val posts = viewModel.posts
    val isLoading = viewModel.isPostsLoading
    val isLoadingMore = viewModel.isLoadingMorePosts
    val hasMore = viewModel.hasMorePosts
    val isOffline = viewModel.isOffline
    val isRefreshing = viewModel.isPostsRefreshing
    val isDeletingPost = viewModel.isDeletingPost
    val deletePostError = viewModel.deletePostError
    val deletePostSuccess = viewModel.deletePostSuccess

    var postToDelete by remember { mutableStateOf<Post?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadPosts() }

    LaunchedEffect(refreshTrigger) {
        if (refreshTrigger > 0) viewModel.refreshPosts()
    }

    LaunchedEffect(deletePostSuccess) {
        if (deletePostSuccess) {
            snackbarHostState.showSnackbar("پست با موفقیت حذف شد")
            viewModel.resetDeleteState()
        }
    }

    LaunchedEffect(deletePostError) {
        if (deletePostError != null) {
            snackbarHostState.showSnackbar(deletePostError)
            viewModel.resetDeleteState()
        }
    }

    // Delete confirmation dialog
    if (postToDelete != null) {
        val post = postToDelete!!
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            title = { Text("حذف پست", fontWeight = FontWeight.Bold) },
            text = { Text("آیا از حذف پست «${post.title}» مطمئن هستید؟ این عمل قابل بازگشت نیست.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePost(post.id)
                        postToDelete = null
                    }
                ) {
                    Text("حذف", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 3 && total > 0
        }
    }
    LaunchedEffect(shouldLoadMore, isLoadingMore) {
        if (shouldLoadMore && !isLoadingMore && hasMore && !isOffline)
            viewModel.loadNextPostsPage()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
            onRefresh = { viewModel.refreshPosts() },
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
                            text = "پست‌ها",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(32.dp)
                        ) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(text = "📭", fontSize = 48.sp)
                                        Text(
                                            text = "پستی یافت نشد",
                                            color = Color(0xFF999999),
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "برای بارگذاری مجدد به پایین بکشید",
                                            color = Color(0xFFBBBBBB),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
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
                            itemsIndexed(posts, key = { _, post -> "post_${post.id}" }) { _, post ->
                                PostCard(
                                    post = post,
                                    onClick = { onPostClick(post) },
                                    onDeleteClick = { postToDelete = post }
                                )
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
        } // end SwipeRefresh

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF323232),
                contentColor = Color.White
            )
        }

        // Loading overlay while deleting
        if (isDeletingPost) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6251A6))
            }
        }
    } // end Box
}

@Composable
fun PostCard(post: Post, onClick: () -> Unit = {}, onDeleteClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "شناسه: ${post.id}",
                        fontSize = 11.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    if (post.status.isNotEmpty()) {
                        val (label, bg, fg) = when (post.status) {
                            "publish" -> Triple("منتشر", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                            "draft" -> Triple("پیش‌نویس", Color(0xFFFFF9C4), Color(0xFFF57F17))
                            "private" -> Triple("خصوصی", Color(0xFFE3F2FD), Color(0xFF1565C0))
                            else -> Triple(post.status, Color(0xFFF5F5F5), Color(0xFF666666))
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = bg
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = fg,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                if (post.date.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(
                        text = post.date.take(10),
                        fontSize = 11.sp,
                        color = Color(0xFFBBBBBB)
                    )
                }
            }

            // Delete button
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف پست",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
