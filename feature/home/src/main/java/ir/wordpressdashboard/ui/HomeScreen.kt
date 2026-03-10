package ir.wordpressdashboard.ui

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.wordpressdashboard.model.ProductImage
import ir.wordpressdashboard.model.Products
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeRoute() {
    HomeScreen()
}

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val products = viewModel.products
    val isLoading = viewModel.isLoading

    // Default selected tab is CreateProduct (index 1)
    var selectedTab by remember { mutableStateOf(BottomNavItem.CreateProduct) }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
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
                        isLoading = isLoading
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
    isLoading: Boolean
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

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6251A6))
            }
        } else if (products.isEmpty()) {
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductCard(product = product)
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Products) {
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
            // Thumbnail placeholder (commented out image loading)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📦", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product info (title + price)
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
                    text = if (inStock) "موجود" else "ناموجود",
                    fontSize = 11.sp,
                    color = if (inStock) Color(0xFF4CAF50) else Color(0xFFE53935),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (product.price.isNotEmpty()) {
                    val priceFormatted = formatPrice(product.price)
                    Text(
                        text = "$priceFormatted تومان",
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
