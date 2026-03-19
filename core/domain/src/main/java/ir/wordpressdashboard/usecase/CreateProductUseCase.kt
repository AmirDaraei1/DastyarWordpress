package ir.wordpressdashboard.usecase

import android.content.Context
import android.net.Uri
import android.util.Log
import ir.wordpressdashboard.model.ProductCategory
import ir.wordpressdashboard.model.ProductTag
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.repository.MediaUploadRepository
import ir.wordpressdashboard.repository.ProductRepository
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val mediaUploadRepository: MediaUploadRepository
) {
    suspend operator fun invoke(
        context: Context,
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        imageUris: List<Uri> = emptyList(),
        wpImageUrls: List<String> = emptyList(),
        categoryIds: List<Int> = emptyList(),
        tagIds: List<Int> = emptyList()
    ): Products {

        // ① آپلود عکس‌های لوکال (گالری/دوربین)
        val uploadedPairs = mutableListOf<Pair<Int, String>>()  // (mediaId, url)
        imageUris.forEachIndexed { index, uri ->
            Log.d("CreateProduct", "Uploading image ${index + 1}/${imageUris.size}: $uri")
            val pair = mediaUploadRepository.uploadImage(context, uri)
            Log.d("CreateProduct", "Image ${index + 1} uploaded: ${pair.second}  id=${pair.first}")
            uploadedPairs.add(pair)
        }

        val uploadedIds  = uploadedPairs.map { it.first }
        val uploadedSrcs = uploadedPairs.map { it.second }

        // ② اضافه کردن URL های وردپرس (نیازی به آپلود ندارند)
        val allImageSrcs = uploadedSrcs + wpImageUrls
        Log.d("CreateProduct", "Total images: ${allImageSrcs.size} (${uploadedSrcs.size} uploaded + ${wpImageUrls.size} WP)")

        // ③ ساخت محصول — عکس‌های آپلودشده با id، بقیه با src
        val product = repository.createProduct(
            name = name,
            description = description,
            price = price,
            stockStatus = stockStatus,
            imageUris = wpImageUrls,        // فقط WP URL ها با src
            imageIds  = uploadedIds,          // عکس‌های آپلودشده با id
            categoryIds = categoryIds,
            tagIds = tagIds
        )
        Log.d("CreateProduct", "Product created: id=${product.id}, images=${product.images.size}")
        return product
    }

    suspend fun uploadImage(context: Context, uri: Uri): Pair<Int, String> =
        mediaUploadRepository.uploadImage(context, uri)
}

class GetProductTaxonomiesUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend fun getCategories(): List<ProductCategory> = repository.getCategories()
    suspend fun getTags(): List<ProductTag> = repository.getTags()
    suspend fun createCategory(name: String): ProductCategory = repository.createCategory(name)
    suspend fun createTag(name: String): ProductTag = repository.createTag(name)
}
