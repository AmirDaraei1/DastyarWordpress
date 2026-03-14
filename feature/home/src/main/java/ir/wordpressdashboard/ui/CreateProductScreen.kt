package ir.wordpressdashboard.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateProductScreen(
    viewModel: CreateProductViewModel = hiltViewModel(),
    mediaViewModel: MediaViewModel = hiltViewModel()
) {
    val purple = Color(0xFF6251A6)
    val isCreating = viewModel.isCreating
    val createSuccess = viewModel.createSuccess
    val createError = viewModel.createError
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stockStatus by remember { mutableStateOf("instock") }
    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

    // لیست عکس‌های انتخاب‌شده از گالری/دوربین (برای آپلود)
    val selectedImages = remember { mutableStateListOf<Uri>() }

    // لیست URL های انتخاب‌شده از تصاویر وردپرس (آپلود نمی‌شوند)
    val selectedWpUrls = remember { mutableStateListOf<String>() }

    // bottom sheet
    var showImageSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // نمایش picker تصاویر وردپرس
    var showWpMediaPicker by remember { mutableStateOf(false) }

    // فایل موقت برای عکس دوربین
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // وضعیت حذف پس‌زمینه
    var isRemovingBg by remember { mutableStateOf(false) }

    // Permission دوربین
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    // Launcher گالری — چند عکس
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages.addAll(uris.filter { it !in selectedImages })
    }

    // Launcher دوربین
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val uri = cameraImageUri ?: return@rememberLauncherForActivityResult
            selectedImages.add(uri)
            // TODO: حذف پس‌زمینه - فعلاً غیرفعال
//            scope.launch {
//                isRemovingBg = true
//                try {
//                    val bitmap = BackgroundRemover.removeBackground(context, uri)
//                    val resultUri = BackgroundRemover.saveBitmapToCache(context, bitmap)
//                    selectedImages.add(resultUri)
//                } catch (e: Exception) {
//                    selectedImages.add(uri)
//                } finally {
//                    isRemovingBg = false
//                }
//            }
        }
    }

    // موفق شد → فرم را پاک کن
    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            name = ""; description = ""; price = ""; stockStatus = "instock"
            nameError = false; priceError = false
            selectedImages.clear()
            selectedWpUrls.clear()
            viewModel.resetCreateState()
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
                .background(purple)
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "افزودن محصول",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // پیام موفقیت
            if (createSuccess) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9)).padding(16.dp)
                ) {
                    Text("✅ محصول با موفقیت ایجاد شد", color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }

            // پیام خطا
            if (createError != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFEBEE)).padding(16.dp)
                ) {
                    Text("❌ $createError", color = Color(0xFFC62828),
                        fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }
            }

            // ── تصاویر محصول ──────────────────────────────────────────────
            FormCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تصاویر محصول", fontSize = 13.sp, color = Color(0xFF666666), fontWeight = FontWeight.Medium)
                    if (isRemovingBg) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CircularProgressIndicator(
                                color = purple,
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp
                            )
                            Text("حذف پس‌زمینه...", fontSize = 11.sp, color = purple)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // دکمه افزودن
                    item {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0EDF8))
                                .border(1.5.dp, purple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable(enabled = !isRemovingBg) { showImageSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📷", fontSize = 26.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("افزودن", fontSize = 11.sp, color = purple, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    // placeholder در حال حذف پس‌زمینه
                    if (isRemovingBg) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF5F5F5))
                                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = purple,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.5.dp
                                    )
                                    Text("پردازش...", fontSize = 10.sp, color = Color(0xFF888888))
                                }
                            }
                        }
                    }
                    // عکس‌های انتخاب‌شده از گالری/دوربین
                    itemsIndexed(selectedImages) { index, uri ->
                        Box(modifier = Modifier.size(90.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(12.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-6).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFC62828))
                                    .clickable { selectedImages.removeAt(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✕", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    // عکس‌های انتخاب‌شده از تصاویر وردپرس
                    itemsIndexed(selectedWpUrls) { index, url ->
                        Box(modifier = Modifier.size(90.dp)) {
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                    .border(1.5.dp, Color(0xFF6251A6).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            )
                            // نشان‌دهنده وردپرس
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF6251A6).copy(alpha = 0.8f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("WP", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-6).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFC62828))
                                    .clickable { selectedWpUrls.removeAt(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✕", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                val totalImages = selectedImages.size + selectedWpUrls.size
                if (totalImages > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$totalImages تصویر انتخاب شده",
                        fontSize = 11.sp, color = Color(0xFF888888))
                }
            }

            // ── نام محصول ──────────────────────────────────────────────────
            FormCard {
                Text("نام محصول *", fontSize = 13.sp, color = Color(0xFF666666), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false; viewModel.resetCreateState() },
                    placeholder = { Text("نام محصول را وارد کنید") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple, unfocusedBorderColor = Color(0xFFDDDDDD),
                        errorBorderColor = Color(0xFFC62828)
                    )
                )
                if (nameError) Text("نام محصول الزامی است", color = Color(0xFFC62828), fontSize = 11.sp)
            }

            // ── قیمت ──────────────────────────────────────────────────────
            FormCard {
                Text("قیمت (ریال) *", fontSize = 13.sp, color = Color(0xFF666666), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { input ->
                        price = input.filter { it.isDigit() }
                        priceError = false; viewModel.resetCreateState()
                    },
                    placeholder = { Text("مثال: 150,000") },
                    isError = priceError, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PriceVisualTransformation(),
                    suffix = { Text("ریال", color = Color(0xFF888888), fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple, unfocusedBorderColor = Color(0xFFDDDDDD),
                        errorBorderColor = Color(0xFFC62828)
                    )
                )
                if (priceError) Text("قیمت الزامی است", color = Color(0xFFC62828), fontSize = 11.sp)
            }

            // ── وضعیت موجودی ──────────────────────────────────────────────
            FormCard {
                Text("وضعیت موجودی", fontSize = 13.sp, color = Color(0xFF666666), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StockChip("موجود", stockStatus == "instock", Color(0xFF2E7D32), Color(0xFFE8F5E9)) { stockStatus = "instock" }
                    StockChip("ناموجود", stockStatus == "outofstock", Color(0xFFC62828), Color(0xFFFFEBEE)) { stockStatus = "outofstock" }
                }
            }

            // ── توضیحات ────────────────────────────────────────────────────
            FormCard {
                Text("توضیحات محصول", fontSize = 13.sp, color = Color(0xFF666666), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; viewModel.resetCreateState() },
                    placeholder = { Text("توضیحات محصول را وارد کنید (اختیاری)") },
                    minLines = 4, maxLines = 8,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple, unfocusedBorderColor = Color(0xFFDDDDDD)
                    )
                )
            }

            // ── دکمه ثبت ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isCreating) Color(0xFFB0A8D6) else purple)
                    .clickable(enabled = !isCreating) {
                        var valid = true
                        if (name.isBlank()) { nameError = true; valid = false }
                        if (price.isBlank()) { priceError = true; valid = false }
                        if (valid) {
                            viewModel.createProduct(
                                context = context,
                                name = name.trim(),
                                description = description.trim(),
                                price = price.trim(),
                                stockStatus = stockStatus,
                                imageUris = selectedImages.toList(),
                                wpImageUrls = selectedWpUrls.toList()
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isCreating) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text(
                            text = if (selectedImages.isNotEmpty()) "در حال آپلود تصویر و ثبت..."
                                   else "در حال ارسال...",
                            color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text("ثبت محصول", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = "افزودن تصویر",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )

                // گزینه دوربین
                ImageSourceOption(
                    emoji = "📸",
                    title = "عکاسی با دوربین",
                    subtitle = "یک عکس جدید بگیرید",
                    onClick = {
                        scope.launch { sheetState.hide(); showImageSheet = false }
                        if (cameraPermission.status.isGranted) {
                            val uri = createCameraUri(context)
                            cameraImageUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermission.launchPermissionRequest()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFF0F0F0)))

                // گزینه گالری
                ImageSourceOption(
                    emoji = "🖼️",
                    title = "انتخاب از گالری",
                    subtitle = "چند عکس را یکجا انتخاب کنید",
                    onClick = {
                        scope.launch { sheetState.hide(); showImageSheet = false }
                        galleryLauncher.launch("image/*")
                    }
                )

                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFF0F0F0)))

                // گزینه تصاویر وردپرس
                ImageSourceOption(
                    emoji = "🌐",
                    title = "انتخاب از تصاویر وردپرس",
                    subtitle = "از کتابخانه رسانه سایت انتخاب کنید",
                    onClick = {
                        scope.launch { sheetState.hide(); showImageSheet = false }
                        showWpMediaPicker = true
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    // ── WordPress Media Picker ─────────────────────────────────────────────
    if (showWpMediaPicker) {
        WpMediaPickerSheet(
            viewModel = mediaViewModel,
            onImagesSelected = { urls: List<String> ->
                urls.forEach { url: String ->
                    if (url !in selectedWpUrls) selectedWpUrls.add(url)
                }
                showWpMediaPicker = false
            },
            onDismiss = { showWpMediaPicker = false }
        )
    }
}

// ── ساخت URI موقت برای عکس دوربین ────────────────────────────────────────
private fun createCameraUri(context: Context): Uri {
    val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

@Composable
private fun ImageSourceOption(emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0EDF8)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp)
        }
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF222222))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF999999))
        }
    }
}

/** هر ۳ رقم از راست یک کاما می‌گذارد: 1500000 → 1,500,000 */
private class PriceVisualTransformation : VisualTransformation {
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
                var originalIndex = 0; var transformedIndex = 0
                while (transformedIndex < offset && originalIndex < original.length) {
                    if (formatted[transformedIndex] == ',') transformedIndex++
                    else { originalIndex++; transformedIndex++ }
                }
                return originalIndex
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

@Composable
private fun FormCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color.White).padding(16.dp)
    ) { content() }
}

@Composable
private fun StockChip(label: String, selected: Boolean, selectedColor: Color, selectedBg: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) selectedBg else Color(0xFFF5F5F5))
            .border(1.5.dp, if (selected) selectedColor else Color(0xFFDDDDDD), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) selectedColor else Color(0xFF888888),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
    }
}
