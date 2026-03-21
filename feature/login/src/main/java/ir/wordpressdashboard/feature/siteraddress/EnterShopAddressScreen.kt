package ir.wordpressdashboard.feature.siteraddress

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class ShopCredentials(
    val address: String,
    val wpUsername: String,
    val wpAppPassword: String
)

// ─── مدل هر مرحله راهنما ───────────────────────────────────────────────────
private data class GuideStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val mockContent: @Composable () -> Unit
)

// ─── مراحل راهنما ──────────────────────────────────────────────────────────
@Composable
private fun guideSteps(): List<GuideStep> = listOf(
    GuideStep(
        stepNumber = 1,
        title = "وارد پنل وردپرس شوید",
        description = "به آدرس سایت/wp-admin بروید و وارد حساب کاربری شوید",
        icon = Icons.Default.Person,
        mockContent = { MockWpLoginPanel() }
    ),
    GuideStep(
        stepNumber = 2,
        title = "روی نام کاربری کلیک کنید",
        description = "در بالای صفحه روی نام کاربری یا «پروفایل» کلیک کنید",
        icon = Icons.Default.AccountCircle,
        mockContent = { MockWpAdminBar() }
    ),
    GuideStep(
        stepNumber = 3,
        title = "به بخش Application Passwords بروید",
        description = "در صفحه پروفایل، به پایین اسکرول کنید تا «رمزهای عبور برنامه» را ببینید",
        icon = Icons.Default.Lock,
        mockContent = { MockAppPasswordSection() }
    ),
    GuideStep(
        stepNumber = 4,
        title = "رمز جدید بسازید",
        description = "یک نام وارد کنید (مثلاً «دستیار وردپرس») و روی «افزودن» کلیک کنید",
        icon = Icons.Default.Add,
        mockContent = { MockAddPasswordForm() }
    ),
    GuideStep(
        stepNumber = 5,
        title = "رمز 16 رقمی را کپی کنید",
        description = "رمز نمایش داده شده را کپی کرده و در اینجا وارد کنید",
        icon = Icons.Default.Share,
        mockContent = { MockPasswordResult() }
    )
)

// ─── Mock صفحه لاگین وردپرس ────────────────────────────────────────────────
@Composable
private fun MockWpLoginPanel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F1), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFF0073AA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("W", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(Modifier.height(8.dp))
        MockField("نام کاربری")
        Spacer(Modifier.height(4.dp))
        MockField("رمز عبور", isPassword = true)
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0073AA), RoundedCornerShape(4.dp))
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("ورود به سایت", color = Color.White, fontSize = 11.sp)
        }
    }
}

// ─── Mock نوار ادمین وردپرس ─────────────────────────────────────────────────
@Composable
private fun MockWpAdminBar() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // نوار بالا
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1D2327))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🏠  سایت من", color = Color.White, fontSize = 10.sp)
            // نام کاربری با انیمیشن پالس
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.4f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                label = "alpha"
            )
            Row(
                modifier = Modifier
                    .background(Color(0xFF2271B1), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .alpha(alpha),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AccountCircle, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("مدیر سایت", color = Color.White, fontSize = 10.sp)
            }
        }
        // منوی dropdown
        Column(
            modifier = Modifier
                .align(Alignment.End)
                .width(140.dp)
                .background(Color.White)
                .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
        ) {
            listOf("پروفایل", "ویرایش پروفایل", "خروج").forEachIndexed { i, item ->
                Text(
                    text = item,
                    fontSize = 10.sp,
                    color = if (i == 1) Color(0xFF2271B1) else Color(0xFF1D2327),
                    fontWeight = if (i == 1) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (i == 1) Color(0xFFE8F0FE) else Color.Transparent)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// ─── Mock بخش Application Passwords ────────────────────────────────────────
@Composable
private fun MockAppPasswordSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(6.dp))
            .padding(10.dp)
    ) {
        Text("رمزهای عبور برنامه", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1D2327))
        Text("با رمزهای عبور برنامه، احراز هویت بدون نیاز به رمز اصلی", fontSize = 9.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        // انیمیشن اسکرول
        val infiniteTransition = rememberInfiniteTransition(label = "scroll")
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
            label = "offset"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFFEEEEEE), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(3.dp)
                    .background(Color(0xFF0073AA), RoundedCornerShape(2.dp))
                    .align(Alignment.CenterStart)
                    .padding(start = (offset * 70).dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text("↓ اسکرول کنید تا به این بخش برسید", fontSize = 9.sp, color = Color(0xFF0073AA))
    }
}

// ─── Mock فرم افزودن رمز ────────────────────────────────────────────────────
@Composable
private fun MockAddPasswordForm() {
    var typed by remember { mutableStateOf("") }
    val fullText = "دستیار وردپرس"
    LaunchedEffect(Unit) {
        delay(500)
        fullText.forEach { char ->
            typed += char
            delay(120)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(6.dp))
            .padding(10.dp)
    ) {
        Text("افزودن رمز عبور برنامه جدید", fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF0F0F1), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (typed.isEmpty()) "نام برنامه..." else typed,
                    fontSize = 11.sp,
                    color = if (typed.isEmpty()) Color.Gray else Color(0xFF1D2327)
                )
            }
            Spacer(Modifier.width(6.dp))
            // دکمه افزودن با انیمیشن
            val btnColor by animateColorAsState(
                targetValue = if (typed == fullText) Color(0xFF0073AA) else Color(0xFFCCCCCC),
                animationSpec = tween(300), label = "btn"
            )
            Box(
                modifier = Modifier
                    .background(btnColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("افزودن", color = Color.White, fontSize = 10.sp)
            }
        }
    }
}

// ─── Mock نمایش رمز ساخته شده ───────────────────────────────────────────────
@Composable
private fun MockPasswordResult() {
    val infiniteTransition = rememberInfiniteTransition(label = "copy")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "alpha"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEBF5FB), RoundedCornerShape(8.dp))
            .border(2.dp, Color(0xFF0073AA), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, null, tint = Color(0xFF27AE60), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("رمز عبور ساخته شد!", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF27AE60))
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFF0073AA), RoundedCornerShape(6.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "AbCd EfGh IjKl MnOp",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D2327),
                letterSpacing = 1.sp
            )
            Icon(
                Icons.Default.Share,
                null,
                tint = Color(0xFF0073AA),
                modifier = Modifier.size(16.dp).alpha(alpha)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "⚠️ این رمز فقط یک‌بار نمایش داده می‌شود!",
            fontSize = 9.sp,
            color = Color(0xFFE74C3C),
            fontWeight = FontWeight.Bold
        )
    }
}

// ─── فیلد ساختگی ────────────────────────────────────────────────────────────
@Composable
private fun MockField(label: String, isPassword: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFF8C8F94), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = if (isPassword) "••••••••" else label,
            color = Color.Gray,
            fontSize = 10.sp
        )
    }
}

// ─── ویجت انیمیشن راهنما (اصلی) ───────────────────────────────────────────
@Composable
fun AppPasswordGuideWidget() {
    val steps = guideSteps()
    var currentStep by remember { mutableIntStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }

    // auto-play وقتی باز است
    LaunchedEffect(isExpanded, currentStep) {
        if (isExpanded) {
            delay(3500)
            currentStep = (currentStep + 1) % steps.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F4FF), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF6251A6).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        // هدر کلیک‌پذیر
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF6251A6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "چطور رمز 16 رقمی بسازم؟",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF6251A6)
                    )
                    Text(
                        "راهنمای گام به گام",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF6251A6),
                modifier = Modifier.size(20.dp)
            )
        }

        // محتوای انیمیشن
        if (isExpanded) {
            Column(modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {

                // نوار مراحل
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentStep) 10.dp else 7.dp)
                                .background(
                                    if (index <= currentStep) Color(0xFF6251A6) else Color(0xFFCCBBEE),
                                    CircleShape
                                )
                                .clickable { currentStep = index }
                        )
                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .background(
                                        if (index < currentStep) Color(0xFF6251A6) else Color(0xFFCCBBEE)
                                    )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // محتوای مرحله با انیمیشن slide
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith
                                (slideOutHorizontally { it } + fadeOut())
                    },
                    label = "step"
                ) { step ->
                    val s = steps[step]
                    Column {
                        // شماره و عنوان مرحله
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(Color(0xFF6251A6), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${s.stepNumber}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(s.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF333333))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(s.description, fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        // محتوای بصری mock
                        s.mockContent()
                    }
                }

                Spacer(Modifier.height(10.dp))

                // دکمه‌های ناوبری
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 0) {
                        Text(
                            "◀ قبلی",
                            color = Color(0xFF6251A6),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { currentStep-- }
                        )
                    } else Spacer(Modifier.width(1.dp))

                    Text(
                        "${currentStep + 1} از ${steps.size}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    if (currentStep < steps.size - 1) {
                        Text(
                            "بعدی ▶",
                            color = Color(0xFF6251A6),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { currentStep++ }
                        )
                    } else {
                        Text(
                            "✓ فهمیدم",
                            color = Color(0xFF27AE60),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isExpanded = false }
                        )
                    }
                }
            }
        }
    }
}

// ─── Route و Screen اصلی ────────────────────────────────────────────────────
@Composable
fun EnterShopAddressRoute(navigateToHome: (ShopCredentials) -> Unit) {
    var address       by remember { mutableStateOf("") }
    var wpUsername    by remember { mutableStateOf("") }
    var wpAppPassword by remember { mutableStateOf("") }

    EnterShopAddressScreen(
        address        = address,
        wpUsername     = wpUsername,
        wpAppPassword  = wpAppPassword,
        onSiteAddressEntered   = { address = it },
        onWpUsernameChanged    = { wpUsername = it },
        onWpAppPasswordChanged = { wpAppPassword = it },
        onNextClick = {
            navigateToHome(ShopCredentials(address, wpUsername, wpAppPassword))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterShopAddressScreen(
    address: String = "",
    wpUsername: String = "",
    wpAppPassword: String = "",
    onSiteAddressEntered: (String) -> Unit,
    onWpUsernameChanged: (String) -> Unit = {},
    onWpAppPasswordChanged: (String) -> Unit = {},
    onNextClick: () -> Unit
) {
    val primaryPurple   = Color(0xFF5850A6)
    val lightGray       = Color(0xFFE0E0E0)
    val backgroundColor = Color(0xFFFAFAFA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── ویجت راهنمای انیمیشنی ─────────────────────────────────────
            AppPasswordGuideWidget()

            // ── آدرس سایت ─────────────────────────────────────────────────
            Text("آدرس سایت", color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
            TextField(
                value = address, onValueChange = onSiteAddressEntered,
                placeholder = { Text("https://www.example.com", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightGray, unfocusedContainerColor = lightGray,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp), singleLine = true
            )

            // ── نام کاربری ────────────────────────────────────────────────
            Text("نام کاربری وردپرس", color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
            TextField(
                value = wpUsername, onValueChange = onWpUsernameChanged,
                placeholder = { Text("admin", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightGray, unfocusedContainerColor = lightGray,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp), singleLine = true
            )

            // ── رمز 16 رقمی ───────────────────────────────────────────────
            Text("رمز عبور برنامه (16 رقمی)", color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
            TextField(
                value = wpAppPassword, onValueChange = onWpAppPasswordChanged,
                placeholder = { Text("xxxx xxxx xxxx xxxx xxxx xxxx", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightGray, unfocusedContainerColor = lightGray,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp), singleLine = true
            )
            Text(
                "راهنمای بالا را ببینید تا بدانید چطور رمز بسازید ↑",
                color = Color(0xFF6251A6), fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right
            )
        }

        // ── دکمه ورود ─────────────────────────────────────────────────────
        Button(
            onClick = onNextClick,
            colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp).height(56.dp),
            enabled = address.isNotBlank() && wpUsername.isNotBlank() && wpAppPassword.isNotBlank()
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("ورود", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEnterShopAddressScreen() {
    EnterShopAddressScreen(
        address = "", wpUsername = "", wpAppPassword = "",
        onSiteAddressEntered = {}, onWpUsernameChanged = {},
        onWpAppPasswordChanged = {}, onNextClick = {}
    )
}