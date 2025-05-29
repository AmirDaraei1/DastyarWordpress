package ir.wordpressdashboard.feature.network

import android.content.Context
import ir.wordpressdashboard.feature.offlinApi.Products
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class MockProductApiService(
    private val context: Context
):ProductApiService {
    override suspend fun getProduct() : Product = withContext(Dispatchers.IO) {
                val jsonString = context.assets.open("mock-product.json").bufferedReader().use { it.readText() }
                Json { ignoreUnknownKeys = true }.decodeFromString<Products>(jsonString)
    }
}