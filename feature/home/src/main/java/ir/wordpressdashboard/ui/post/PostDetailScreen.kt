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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.wordpressdashboard.i18n.LocalStrings
import ir.wordpressdashboard.i18n.resolve
import ir.wordpressdashboard.model.Post

@Composable
fun PostDetailScreen(
    post: Post,
    onBack: () -> Unit,
    onPostUpdated: (Post) -> Unit = {},
    viewModel: EditPostViewModel = hiltViewModel()
) {
    val strings = LocalStrings.current
    val purple = Color(0xFF6251A6)
    var isEditMode by remember { mutableStateOf(false) }

    // Editable fields
    var title by remember(post.id) { mutableStateOf(post.title) }
    var content by remember(post.id) {
        mutableStateOf(post.content.replace(Regex("<[^>]+>"), "").trim())
    }
    var status by remember(post.id) { mutableStateOf(post.status) }

    var titleError by remember { mutableStateOf(false) }

    val isUpdating = viewModel.isUpdating
    val updateSuccess = viewModel.updateSuccess
    val updateError = viewModel.updateError
    val updatedPost = viewModel.updatedPost

    BackHandler {
        if (isEditMode) isEditMode = false else onBack()
    }

    // After successful update, go back to detail view
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            updatedPost?.let { onPostUpdated(it) }
            viewModel.resetState()
            isEditMode = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────
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
                    onClick = { if (isEditMode) isEditMode = false else onBack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.back,
                        tint = Color.White
                    )
                }
                Text(
                    text = if (isEditMode) strings.editPost else strings.postDetailsTitle,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 56.dp)
                )
                if (!isEditMode) {
                    IconButton(
                        onClick = { isEditMode = true },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = strings.edit,
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // ── Content ───────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Error banner ──────────────────────────────────────────────
            if (updateError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = updateError ?: "",
                        color = Color(0xFFC62828),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // ── وضعیت (status) ────────────────────────────────────────────
            PostStatusChip(status = status, editMode = isEditMode, onStatusChange = { status = it })

            // ── عنوان ─────────────────────────────────────────────────────
            SectionLabel(strings.resolve("عنوان پست"))
            if (isEditMode) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(strings.postTitlePlaceholderText) },
                    isError = titleError,
                    supportingText = if (titleError) { { Text(strings.postTitleRequiredText) } } else null,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = false,
                    maxLines = 4
                )
            } else {
                InfoCard(text = post.title.ifEmpty { "—" })
            }

            // ── تاریخ ─────────────────────────────────────────────────────
            if (!isEditMode && post.date.isNotEmpty()) {
                SectionLabel(strings.publishDate)
                InfoCard(text = post.date)
            }

            // ── خلاصه ─────────────────────────────────────────────────────
            if (!isEditMode) {
                val excerptClean = post.excerpt.replace(Regex("<[^>]+>"), "").trim()
                if (excerptClean.isNotEmpty()) {
                    SectionLabel(strings.resolve("خلاصه"))
                    InfoCard(text = excerptClean)
                }
            }

            // ── محتوا ─────────────────────────────────────────────────────
            SectionLabel(strings.resolve("محتوا"))
            if (isEditMode) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp),
                    placeholder = { Text(strings.postContentPlaceholderText) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                val contentClean = post.content.replace(Regex("<[^>]+>"), "").trim()
                InfoCard(text = contentClean.ifEmpty { "—" }, minHeight = 80.dp)
            }

            // ── دکمه ذخیره ────────────────────────────────────────────────
            if (isEditMode) {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        if (title.isBlank()) { titleError = true; return@Button }
                        viewModel.updatePost(
                            id = post.id,
                            title = title,
                            content = content,
                            status = status
                        )
                    },
                    enabled = !isUpdating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = purple)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(strings.saveChanges, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Helper composables ────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF555555)
    )
}

@Composable
private fun InfoCard(
    text: String,
    minHeight: androidx.compose.ui.unit.Dp = 0.dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF222222),
            lineHeight = 22.sp,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun PostStatusChip(
    status: String,
    editMode: Boolean,
    onStatusChange: (String) -> Unit
) {
    val strings = LocalStrings.current
    val statuses = listOf(
        "publish" to strings.published,
        "draft" to strings.draft,
        "private" to strings.privateStatus
    )
    val purple = Color(0xFF6251A6)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        statuses.forEach { (value, label) ->
            val selected = status == value
            if (editMode) {
                FilterChip(
                    selected = selected,
                    onClick = { onStatusChange(value) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = purple,
                        selectedLabelColor = Color.White
                    )
                )
            } else if (selected) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when (value) {
                        "publish" -> Color(0xFFE8F5E9)
                        "draft" -> Color(0xFFFFF9C4)
                        else -> Color(0xFFE3F2FD)
                    }
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (value) {
                            "publish" -> Color(0xFF2E7D32)
                            "draft" -> Color(0xFFF57F17)
                            else -> Color(0xFF1565C0)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
