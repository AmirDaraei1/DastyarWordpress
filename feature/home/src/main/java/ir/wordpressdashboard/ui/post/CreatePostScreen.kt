package ir.wordpressdashboard.ui.post

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.wordpressdashboard.model.Post

@Composable
fun CreatePostScreen(
    onBack: () -> Unit,
    onPostCreated: (Post) -> Unit = {},
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val purple = Color(0xFF6251A6)

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var excerpt by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("draft") }

    var titleError by remember { mutableStateOf(false) }
    var contentError by remember { mutableStateOf(false) }

    val isCreating = viewModel.isCreating
    val createSuccess = viewModel.createSuccess
    val createError = viewModel.createError
    val createdPost = viewModel.createdPost

    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler { onBack() }

    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            createdPost?.let { onPostCreated(it) }
            viewModel.resetState()
            onBack()
        }
    }

    LaunchedEffect(createError) {
        if (createError != null) {
            snackbarHostState.showSnackbar(createError)
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // ── Top Bar ──────────────────────────────────────────────────
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
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "ایجاد پست جدید",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Form ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Error banner
                if (createError != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = createError,
                            color = Color(0xFFC62828),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // ── وضعیت ──────────────────────────────────────────────
                CreatePostSectionLabel("وضعیت پست")
                CreatePostStatusChip(status = status, onStatusChange = { status = it })

                // ── عنوان ──────────────────────────────────────────────
                CreatePostSectionLabel("عنوان پست *")
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("عنوان پست را وارد کنید") },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text("عنوان نمی‌تواند خالی باشد", color = Color(0xFFC62828)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = false,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        focusedLabelColor = purple,
                        cursorColor = purple
                    )
                )

                // ── خلاصه ──────────────────────────────────────────────
                CreatePostSectionLabel("خلاصه (اختیاری)")
                OutlinedTextField(
                    value = excerpt,
                    onValueChange = { excerpt = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("خلاصه‌ای از پست بنویسید") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        focusedLabelColor = purple,
                        cursorColor = purple
                    )
                )

                // ── محتوا ──────────────────────────────────────────────
                CreatePostSectionLabel("محتوای پست *")
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        contentError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 220.dp),
                    placeholder = { Text("محتوای پست را اینجا بنویسید…") },
                    isError = contentError,
                    supportingText = if (contentError) {
                        { Text("محتوا نمی‌تواند خالی باشد", color = Color(0xFFC62828)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        focusedLabelColor = purple,
                        cursorColor = purple
                    )
                )

                // ── دکمه‌های اقدام ─────────────────────────────────────
                Spacer(Modifier.height(4.dp))

                // دکمه انتشار
                Button(
                    onClick = {
                        var valid = true
                        if (title.isBlank()) { titleError = true; valid = false }
                        if (content.isBlank()) { contentError = true; valid = false }
                        if (!valid) return@Button
                        viewModel.createPost(
                            title = title.trim(),
                            content = content.trim(),
                            excerpt = excerpt.trim(),
                            status = "publish"
                        )
                    },
                    enabled = !isCreating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    if (isCreating && status == "publish") {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                    } else {
                        Text("انتشار", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // دکمه ذخیره به عنوان پیش‌نویس
                OutlinedButton(
                    onClick = {
                        var valid = true
                        if (title.isBlank()) { titleError = true; valid = false }
                        if (!valid) return@OutlinedButton
                        viewModel.createPost(
                            title = title.trim(),
                            content = content.trim(),
                            excerpt = excerpt.trim(),
                            status = "draft"
                        )
                    },
                    enabled = !isCreating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = purple)
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(color = purple, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                    } else {
                        Text("ذخیره به عنوان پیش‌نویس", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        // Loading overlay
        if (isCreating) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = purple)
                        Text("در حال ایجاد پست…", fontSize = 14.sp, color = Color(0xFF444444))
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF323232),
                contentColor = Color.White
            )
        }
    }
}

@Composable
private fun CreatePostSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF555555)
    )
}

@Composable
private fun CreatePostStatusChip(
    status: String,
    onStatusChange: (String) -> Unit
) {
    val purple = Color(0xFF6251A6)
    val statuses = listOf(
        "publish" to "منتشر شده",
        "draft" to "پیش‌نویس",
        "private" to "خصوصی",
        "pending" to "در انتظار بررسی"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        statuses.forEach { (value, label) ->
            val selected = status == value
            FilterChip(
                selected = selected,
                onClick = { onStatusChange(value) },
                label = { Text(label, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = purple,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}
