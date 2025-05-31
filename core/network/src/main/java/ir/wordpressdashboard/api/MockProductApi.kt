package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.model.ProductImage

class MockProductApi :ProductApi{
    override suspend fun getProduct(): List<Products> {
        return listOf(
            Products(1,
                "laptop1",
                "30000",
                "Nadarad",
                "https://digikhune.com/products1", images = listOf(
                    ProductImage(1,"https://example.com/laptop2picture")
                ),
                stock_status = "in-stock"
            ),
        )
    }
}