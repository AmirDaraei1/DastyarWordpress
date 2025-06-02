package ir.wordpressdashboard.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import ir.wordpressdashboard.feature.viewmodel.ProductViewModel

@Composable
fun HomeRoute(userId: String) {
    HomeScreen(userId = userId)
}

@Composable
fun HomeScreen(userId: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Welcome to Home Screen\nUser ID: $userId",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
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
