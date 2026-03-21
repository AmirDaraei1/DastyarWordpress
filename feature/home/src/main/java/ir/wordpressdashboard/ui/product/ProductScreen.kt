package ir.wordpressdashboard.ui.product

import ir.wordpressdashboard.ui.navigation.BottomNavItem
import ir.wordpressdashboard.ui.create_product.CreateProductScreen
import ir.wordpressdashboard.ui.media.MediaListScreen
import ir.wordpressdashboard.ui.post.PostsListScreen
import ir.wordpressdashboard.ui.edit_product.EditProductScreen
import ir.wordpressdashboard.ui.post.CreatePostScreen
import ir.wordpressdashboard.ui.post.PostDetailScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ir.wordpressdashboard.model.ProductImage
import ir.wordpressdashboard.model.Products
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeRoute() {
    HomeScreen()
}

@Composable
fun HomeScreen(viewModel: ProductsViewModel = hiltViewModel()) {
    val products = viewModel.products
    val isLoading = viewModel.isLoading
    val isLoadingMore = viewModel.isLoadingMore
    val hasMore = viewModel.hasMoreProducts
    val isOffline = viewModel.isOffline
    val isShowingCachedData = viewModel.isShowingCachedData
    val isRefreshing = viewModel.isRefreshing

    var selectedTab by remember { mutableStateOf(BottomNavItem.CreateProduct) }
    var selectedProduct by remember { mutableStateOf<Products?>(null) }
    var editingProduct by remember { mutableStateOf<Products?>(null) }
    var productToDelete by remember { mutableStateOf<Products?>(null) }
    var selectedPost by remember { mutableStateOf<ir.wordpressdashboard.model.Post?>(null) }
    var isCreatingPost by remember { mutableStateOf(false) }
    var postRefreshTrigger by remember { mutableStateOf(0) }
    var mediaRefreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    // وقتی تب عوض می‌شه، لیست اون تب رو refresh کن
    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            BottomNavItem.Products -> viewModel.refreshProducts()
            BottomNavItem.Posts -> postRefreshTrigger++
            BottomNavItem.Media -> mediaRefreshTrigger++
            else -> Unit
        }
    }

    // صفحه ویرایش محصول
    if (editingProduct != null) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            EditProductScreen(
                product = editingProduct!!,
                onBack = { editingProduct = null },
                onProductUpdated = { updated ->
                    viewModel.updateProductInList(updated)
                    editingProduct = null
                }
            )
        }
        return
    }

    // صفحه جزئیات محصول
    if (selectedProduct != null) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ProductDetailScreen(
                product = selectedProduct!!,
                onBack = { selectedProduct = null }
            )
        }
        return
    }

    // صفحه جزئیات / ویرایش پست
    if (selectedPost != null) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            PostDetailScreen(
                post = selectedPost!!,
                onBack = { selectedPost = null },
                onPostUpdated = { updated -> selectedPost = updated }
            )
        }
        return
    }

    // صفحه ایجاد پست جدید
    if (isCreatingPost) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            CreatePostScreen(
                onBack = { isCreatingPost = false },
                onPostCreated = {
                    isCreatingPost = false
                    postRefreshTrigger++
                }
            )
        }
        return
    }

    // دیالوگ تأیید حذف
    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = {
                Text(
                    text = "حذف محصول",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "آیا از حذف «${productToDelete!!.name}» مطمئن هستید؟\nاین عمل قابل بازگشت نیست.",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(productToDelete!!.id)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("بله، حذف شود", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { productToDelete = null }) {
                    Text("انصراف")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0),
            bottomBar = {
                AppBottomNavigationBar(
                    selectedItem = selectedTab,
                    onItemSelected = { selectedTab = it }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    BottomNavItem.Products -> HomeScreenContent(
                        products = products,
                        isLoading = isLoading,
                        isLoadingMore = isLoadingMore,
                        hasMore = hasMore,
                        isOffline = isOffline,
                        isShowingCachedData = isShowingCachedData,
                        isRefreshing = isRefreshing,
                        onLoadMore = { viewModel.loadNextPage() },
                        onProductClick = { selectedProduct = it },
                        onRetry = { viewModel.refreshProducts() },
                        onRefresh = { viewModel.refreshProducts() },
                        onDeleteProduct = { product -> productToDelete = product },
                        onEditProduct = { product -> editingProduct = product }
                    )
                    BottomNavItem.CreateProduct -> CreateProductScreen()
                    BottomNavItem.Media -> MediaListScreen(refreshTrigger = mediaRefreshTrigger)
                    BottomNavItem.Posts -> Box(modifier = Modifier.fillMaxSize()) {
                        PostsListScreen(
                            onPostClick = { selectedPost = it },
                            refreshTrigger = postRefreshTrigger
                        )
                        FloatingActionButton(
                            onClick = { isCreatingPost = true },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            containerColor = Color(0xFF6251A6),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "ایجاد پست جدید"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit
) {
    val purple = Color(0xFF6251A6)
    val items = BottomNavItem.entries

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
        windowInsets = WindowInsets(0)
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color(0xFF9E9E9E),
                    selectedTextColor = purple,
                    unselectedTextColor = Color(0xFF9E9E9E),
                    indicatorColor = purple
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    products: List<Products>,
    isLoading: Boolean,
    isLoadingMore: Boolean = false,
    hasMore: Boolean = false,
    isOffline: Boolean = false,
    isShowingCachedData: Boolean = false,
    isRefreshing: Boolean = false,
    onLoadMore: () -> Unit = {},
    onProductClick: (Products) -> Unit = {},
    onRetry: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onDeleteProduct: (Products) -> Unit = {},
    onEditProduct: (Products) -> Unit = {}
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible >= totalItems - 3 && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore && hasMore && !isOffline) {
            onLoadMore()
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = onRefresh,
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
                    text = "محصولات",
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
                    text = if (isShowingCachedData)
                        "بدون اینترنت — نمایش داده‌های ذخیره شده"
                    else
                        "اینترنت در دسترس نیست",
                    fontSize = 13.sp,
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6251A6))
                }
            }
            // آفلاین و هیچ cache‌ای موجود نیست
            isOffline && products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
                            text = "لطفاً اتصال اینترنت خود را بررسی کنید و دوباره تلاش کنید.",
                                    fontSize = 14.sp,
                                    color = Color(0xFF888888),
                                    textAlign = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF6251A6))
                                .clickable { onRetry() }
                                .padding(horizontal = 32.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "تلاش مجدد",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "محصولی یافت نشد",
                        color = Color(0xFF999999),
                        fontSize = 16.sp
                    )
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
                    items(items = products, key = { "product_${it.id}" }) { product ->
                        SwipeActionsRow(
                            onEdit = { onEditProduct(product) },
                            onDelete = { onDeleteProduct(product) }
                        ) {
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product) }
                            )
                        }
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
                    if (!hasMore && products.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "همه محصولات بارگذاری شدند",
                                    color = Color(0xFFBBBBBB),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    } // end SwipeRefresh
}

@Composable
fun ProductCard(
    product: Products,
    onClick: () -> Unit = {}
) {
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
            // Product image — loaded via Coil (auto disk+memory cached)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = product.images.firstOrNull()?.src
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(text = "📦", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                val (statusText, statusColor) = when (product.stock_status) {
                    "instock"     -> "✓ موجود"      to Color(0xFF4CAF50)
                    "onbackorder" -> "⏳ پیش‌فروش"  to Color(0xFFE65100)
                    else          -> "✗ ناموجود"    to Color(0xFFE53935)
                }
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (product.price.isNotEmpty()) {
                    Text(
                        text = "${formatPrice(product.price)} تومان",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6251A6)
                    )
                } else {
                    Text(
                        text = "قیمت: تماس بگیرید",
                        fontSize = 13.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        }
    }
}

fun formatPrice(price: String): String {
    return try {
        val number = price.toBigDecimal().toLong()
        NumberFormat.getNumberInstance(Locale("fa", "IR")).format(number)
    } catch (_: Exception) {
        price
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreenContent(
        products = listOf(
            Products(
                id = 1,
                name = "کفش ورزشی نایک مدل ایر مکس پلاس",
                price = "2500000",
                description = "",
                permalink = "",
                images = listOf(ProductImage(1, "")),
                stock_status = "instock"
            ),
            Products(
                id = 2,
                name = "تیشرت مردانه آدیداس",
                price = "850000",
                description = "",
                permalink = "",
                images = emptyList(),
                stock_status = "outofstock"
            )
        ),
        isLoading = false
    )
}

@Composable
@Preview(showBackground = true, name = "Loading State")
fun HomeScreenLoadingPreview() {
    HomeScreenContent(products = emptyList(), isLoading = true)
}

@Composable
fun SwipeActionsRow(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val actionWidth = 140.dp   // total width of the two buttons revealed
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    Box(modifier = Modifier.fillMaxWidth()) {
        // ── Action buttons (revealed behind the card) ──────────────────
        Row(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit button
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxSize()
                    .background(Color(0xFF1976D2))
                    .clickable {
                        scope.launch {
                            offsetX.animateTo(0f, tween(300))
                        }
                        onEdit()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ویرایش",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("ویرایش", color = Color.White, fontSize = 11.sp)
                }
            }
            // Delete button
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxSize()
                    .background(Color(0xFFE53935))
                    .clickable {
                        scope.launch {
                            offsetX.animateTo(0f, tween(300))
                        }
                        onDelete()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("حذف", color = Color.White, fontSize = 11.sp)
                }
            }
        }

        // ── Draggable card ─────────────────────────────────────────────
        val actionWidthPx = with(LocalDensity.current) { actionWidth.toPx() }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value < -actionWidthPx / 2) {
                                    offsetX.animateTo(-actionWidthPx, tween(300))
                                } else {
                                    offsetX.animateTo(0f, tween(300))
                                }
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newValue = (offsetX.value + dragAmount).coerceIn(-actionWidthPx, 0f)
                                offsetX.snapTo(newValue)
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}
