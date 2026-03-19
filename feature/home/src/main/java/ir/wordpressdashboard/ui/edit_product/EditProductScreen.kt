package ir.wordpressdashboard.ui.edit_product

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.ui.media.MediaViewModel
import ir.wordpressdashboard.ui.media.WpMediaPickerSheet
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditProductScreen(
    product: Products,
    onBack: () -> Unit,
    onProductUpdated: (Products) -> Unit = {},
    viewModel: EditProductViewModel = hiltViewModel(),
    mediaViewModel: MediaViewModel = hiltViewModel()
) {
    val purple = Color(0xFF6251A6)
    val isUpdating = viewModel.isUpdating
    val updateSuccess = viewModel.updateSuccess
    val updateError = viewModel.updateError
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(product.name) }
    var description by remember { mutableStateOf(
        product.description.replace(Regex("<[^>]+>"), "").trim()
    ) }
    var price by remember { mutableStateOf(product.price) }
    var stockStatus by remember { mutableStateOf(product.stock_status) }

    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

    // URLهای تصاویر موجود (از سرور)
    val existingImageUrls = remember { mutableStateListOf<String>().also { it.addAll(product.images.map { img -> img.src }) } }
    // عکس‌های جدید از گالری/دوربین (باید آپلود شوند)
    val newLocalImages = remember { mutableStateListOf<Uri>() }
    // عکس‌های جدید از کتابخانه وردپرس
    val newWpUrls = remember { mutableStateListOf<String>() }

    var showImageSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showWpMediaPicker by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris -> newLocalImages.addAll(uris.filter { it !in newLocalImages }) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraImageUri?.let { newLocalImages.add(it) }
    }

    BackHandler { onBack() }

    // بعد از موفقیت، برگشت
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            viewModel.updatedProduct?.let { onProductUpdated(it) }
            viewModel.resetUpdateState()
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(purple)
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
                }
                Text(
                    text = "ویرایش محصول",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // ── Form ──────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── تصاویر محصول ──────────────────────────────────────────────
            Text("تصاویر محصول", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF444444))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                // دکمه افزودن
                item {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0EDF8))
                            .border(1.5.dp, purple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { showImageSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📷", fontSize = 26.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("افزودن", fontSize = 11.sp, color = purple, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // تصاویر موجود (از سرور)
                itemsIndexed(existingImageUrls) { index, url ->
                    Box(modifier = Modifier.size(90.dp)) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(12.dp))
                        )
                        Box(
                            modifier = Modifier.size(22.dp).align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-6).dp)
                                .clip(CircleShape).background(Color(0xFFC62828))
                                .clickable { existingImageUrls.removeAt(index) },
                            contentAlignment = Alignment.Center
                        ) { Text("✕", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    }
                }

                // عکس‌های جدید از گالری/دوربین
                itemsIndexed(newLocalImages) { index, uri ->
                    Box(modifier = Modifier.size(90.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, Color(0xFF43A047).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        )
                        // نشان جدید
                        Box(
                            modifier = Modifier.align(Alignment.BottomStart).padding(4.dp)
                                .clip(RoundedCornerShape(4.dp)).background(Color(0xFF43A047).copy(alpha = 0.85f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) { Text("جدید", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold) }
                        Box(
                            modifier = Modifier.size(22.dp).align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-6).dp)
                                .clip(CircleShape).background(Color(0xFFC62828))
                                .clickable { newLocalImages.removeAt(index) },
                            contentAlignment = Alignment.Center
                        ) { Text("✕", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    }
                }

                // عکس‌های جدید از وردپرس
                itemsIndexed(newWpUrls) { index, url ->
                    Box(modifier = Modifier.size(90.dp)) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, purple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        )
                        Box(
                            modifier = Modifier.align(Alignment.BottomStart).padding(4.dp)
                                .clip(RoundedCornerShape(4.dp)).background(purple.copy(alpha = 0.8f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) { Text("WP", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold) }
                        Box(
                            modifier = Modifier.size(22.dp).align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-6).dp)
                                .clip(CircleShape).background(Color(0xFFC62828))
                                .clickable { newWpUrls.removeAt(index) },
                            contentAlignment = Alignment.Center
                        ) { Text("✕", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }

            val totalImages = existingImageUrls.size + newLocalImages.size + newWpUrls.size
            if (totalImages > 0) {
                Text("$totalImages تصویر", fontSize = 11.sp, color = Color(0xFF888888))
            }

            // ── نام محصول ─────────────────────────────────────────────────
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                label = { Text("نام محصول") },
                isError = nameError,
                supportingText = if (nameError) {{ Text("نام محصول الزامی است", color = Color.Red) }} else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purple, focusedLabelColor = purple),
                singleLine = true
            )

            // ── قیمت با جداکننده سه‌رقمی ─────────────────────────────────
            OutlinedTextField(
                value = price,
                onValueChange = { price = it.filter { c -> c.isDigit() }; priceError = false },
                label = { Text("قیمت (تومان)") },
                isError = priceError,
                supportingText = if (priceError) {{ Text("قیمت الزامی است", color = Color.Red) }} else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purple, focusedLabelColor = purple),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = EditPriceVisualTransformation(),
                suffix = { Text("تومان", color = Color(0xFF888888), fontSize = 13.sp) }
            )

            // ── توضیحات ───────────────────────────────────────────────────
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("توضیحات محصول") },
                modifier = Modifier.fillMaxWidth().height(130.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = purple, focusedLabelColor = purple),
                maxLines = 6
            )

            // ── وضعیت موجودی ──────────────────────────────────────────────
            Text("وضعیت موجودی", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF444444))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("instock" to "موجود", "outofstock" to "ناموجود", "onbackorder" to "پیش‌فروش")
                    .forEach { (value, label) ->
                        val selected = stockStatus == value
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) purple else Color.White)
                                .border(1.5.dp, if (selected) purple else Color(0xFFCCCCCC), RoundedCornerShape(20.dp))
                                .clickable { stockStatus = value }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Color.White else Color(0xFF555555),
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
            }

            // ── خطا ───────────────────────────────────────────────────────
            AnimatedVisibility(visible = updateError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = updateError ?: "",
                        color = Color(0xFFC62828),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── دکمه ذخیره تغییرات ────────────────────────────────────────
            Button(
                onClick = {
                    var valid = true
                    if (name.isBlank()) { nameError = true; valid = false }
                    if (price.isBlank()) { priceError = true; valid = false }
                    if (valid) {
                        viewModel.updateProduct(
                            id = product.id,
                            name = name.trim(),
                            description = description.trim(),
                            price = price.trim(),
                            stockStatus = stockStatus,
                            newLocalImageUris = newLocalImages.toList(),
                            existingImageUrls = existingImageUrls.toList(),
                            newWpImageUrls = newWpUrls.toList(),
                            context = context
                        )
                    }
                },
                enabled = !isUpdating,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = purple)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("ذخیره تغییرات", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // ── Bottom Sheet انتخاب تصویر ─────────────────────────────────────────
    if (showImageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showImageSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = "افزودن تصویر",
                    fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
                // دوربین
                EditImageSourceOption(emoji = "📸", title = "عکاسی با دوربین", subtitle = "یک عکس جدید بگیرید") {
                    scope.launch { sheetState.hide(); showImageSheet = false }
                    if (cameraPermission.status.isGranted) {
                        val uri = createEditCameraUri(context)
                        cameraImageUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        cameraPermission.launchPermissionRequest()
                    }
                }
                Spacer(Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFF0F0F0)))
                // گالری
                EditImageSourceOption(emoji = "🖼️", title = "انتخاب از گالری", subtitle = "چند عکس را یکجا انتخاب کنید") {
                    scope.launch { sheetState.hide(); showImageSheet = false }
                    galleryLauncher.launch("image/*")
                }
                Spacer(Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFF0F0F0)))
                // تصاویر وردپرس
                EditImageSourceOption(emoji = "🌐", title = "انتخاب از تصاویر وردپرس", subtitle = "از کتابخانه رسانه سایت انتخاب کنید") {
                    scope.launch { sheetState.hide(); showImageSheet = false }
                    showWpMediaPicker = true
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // ── WordPress Media Picker ─────────────────────────────────────────────
    if (showWpMediaPicker) {
        WpMediaPickerSheet(
            viewModel = mediaViewModel,
            onImagesSelected = { urls ->
                urls.forEach { url -> if (url !in newWpUrls) newWpUrls.add(url) }
                showWpMediaPicker = false
            },
            onDismiss = { showWpMediaPicker = false }
        )
    }
}

private fun createEditCameraUri(context: Context): Uri {
    val file = File(context.cacheDir, "edit_camera_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

@Composable
private fun EditImageSourceOption(emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0EDF8)),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 24.sp) }
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF222222))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF999999))
        }
    }
}

/** جداکننده سه‌رقمی: 2500000 → 2,500,000 */
private class EditPriceVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formatted = buildString {
            original.reversed().forEachIndexed { index, char ->
                if (index > 0 && index % 3 == 0) append(',')
                append(char)
            }
        }.reversed()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (original.isEmpty()) return offset
                val commasBefore = (0 until offset.coerceAtMost(original.length))
                    .count { (original.length - 1 - it) % 3 == 0 && it != 0 }
                return (offset + commasBefore).coerceAtMost(formatted.length)
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (formatted.isEmpty()) return offset
                var origIdx = 0; var fmtIdx = 0
                while (fmtIdx < offset && origIdx < original.length) {
                    if (formatted[fmtIdx] == ',') fmtIdx++
                    else { origIdx++; fmtIdx++ }
                }
                return origIdx
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
