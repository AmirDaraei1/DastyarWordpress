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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import ir.wordpressdashboard.model.ProductImage
import ir.wordpressdashboard.model.Products
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeRoute() {
    HomeScreen()
}

@Composable
fun HomeScreen(viewModel: ProductViewModel = hiltViewModel()) {
    val products = viewModel.products
    val isLoading = viewModel.isLoading
    val isLoadingMore = viewModel.isLoadingMore
    val hasMore = viewModel.hasMoreProducts
    val isOffline = viewModel.isOffline
    val isShowingCachedData = viewModel.isShowingCachedData

    var selectedTab by remember { mutableStateOf(BottomNavItem.CreateProduct) }
    var selectedProduct by remember { mutableStateOf<Products?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    if (selectedProduct != null) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ProductDetailScreen(
                product = selectedProduct!!,
                onBack = { selectedProduct = null }
            )
        }
        return
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
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
                        onLoadMore = { viewModel.loadNextPage() },
                        onProductClick = { selectedProduct = it },
                        onRetry = { viewModel.refreshProducts() }
                    )
                    BottomNavItem.CreateProduct -> CreateProductScreen()
                    BottomNavItem.Media -> MediaListScreen()
                    BottomNavItem.Posts -> PostsListScreen()
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
        tonalElevation = 8.dp
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

@Composable
fun HomeScreenContent(
    products: List<Products>,
    isLoading: Boolean,
    isLoadingMore: Boolean = false,
    hasMore: Boolean = false,
    isOffline: Boolean = false,
    isShowingCachedData: Boolean = false,
    onLoadMore: () -> Unit = {},
    onProductClick: (Products) -> Unit = {},
    onRetry: () -> Unit = {}
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
                text = "محصولات",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── بنر آفلاین ────────────────────────────────────────────────────
        androidx.compose.animation.AnimatedVisibility(visible = isOffline) {
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
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) }
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

                val inStock = product.stock_status == "instock"
                Text(
                    text = if (inStock) "✓ موجود" else "✗ ناموجود",
                    fontSize = 11.sp,
                    color = if (inStock) Color(0xFF4CAF50) else Color(0xFFE53935),
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
