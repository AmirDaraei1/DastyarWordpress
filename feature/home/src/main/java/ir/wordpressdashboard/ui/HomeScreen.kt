package ir.wordpressdashboard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val isLoading = viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn {
            items(products) { product ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = product.price, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen("123")
}
