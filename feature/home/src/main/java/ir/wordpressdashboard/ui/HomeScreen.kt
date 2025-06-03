package ir.wordpressdashboard.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeRoute(userId: String) {
    HomeScreen(userId = userId)
}

@Composable
fun HomeScreen(
    userId: String,
               viewModel: HomeViewModel = hiltViewModel()
               ) {
    val products = viewModel.products
//    val isLoading = viewModel.loadProducts()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    LazyColumn {
        items(products){ product ->
            Text(product.name)
            Text(product.price)
        }
    }

//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Text(
//            text = "Welcome to Home Screen\nUser ID: $userId",
//            style = MaterialTheme.typography.titleLarge,
//            textAlign = TextAlign.Center
//        )
//    }
}

//@Composable
//fun HomeScreen(viewModel: ProductViewModel= hiltViewModel()) {
//
//    val product by viewModel.product.observeAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.loadProduct()
//    }
//    product?.let {
//        Text(text = it.name)
//    }
//}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen("123")
}
