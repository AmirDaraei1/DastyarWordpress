package ir.wordpressdashboard.feature.introduction

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

private val Purple = Color(0xFF6251A6)
private val LightPurple = Color(0xFFEDE7F6)

data class IntroPageData(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val iconTint: Color
)

private val introPages = listOf(
    IntroPageData(
        icon = Icons.Default.ShoppingCart,
        title = "مدیریت محصولات",
        description = "به راحتی محصولات فروشگاه WooCommerce خود را مشاهده، ایجاد، ویرایش و حذف کنید. قیمت، موجودی، تصاویر و دسته‌بندی را مستقیماً از گوشی مدیریت کنید.",
        iconTint = Color(0xFF6251A6)
    ),
    IntroPageData(
        icon = Icons.Default.Create,
        title = "مدیریت پست‌ها",
        description = "پست‌های وردپرس سایت خود را بخوانید، بنویسید و ویرایش کنید. محتوای سایت را در هر جا و هر زمان به‌روز نگه دارید.",
        iconTint = Color(0xFF3F51B5)
    ),
    IntroPageData(
        icon = Icons.Default.Star,
        title = "مدیریت رسانه‌ها",
        description = "تصاویر و فایل‌های کتابخانه رسانه سایت را مشاهده و آپلود کنید. عکس‌ها را مستقیماً از گوشی به سایت اضافه کنید.",
        iconTint = Color(0xFF009688)
    )
)

@Composable
fun IntroductionRoute(navigateToLogin: () -> Unit) {
    IntroductionScreen {
        navigateToLogin()
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IntroductionScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF4E54C8), Color(0xFF8F94FB)))
                )
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "دستیار وردپرس",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Pager
        HorizontalPager(
            count = introPages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            IntroPage(introPages[page])
        }

        // Dot Indicator
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(introPages.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                        .background(
                            color = if (pagerState.currentPage == index) Purple else Color(0xFFBDBDBD),
                            shape = CircleShape
                        )
                )
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalArrangement = if (pagerState.currentPage < introPages.size - 1)
                Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage < introPages.size - 1) {
                TextButton(onClick = { onFinish() }) {
                    Text(text = "رد کردن", color = Purple, fontSize = 15.sp)
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage == introPages.size - 1) {
                            onFinish()
                        } else {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .then(
                        if (pagerState.currentPage == introPages.size - 1)
                            Modifier.fillMaxWidth() else Modifier
                    )
                    .height(48.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == introPages.size - 1) "شروع کنید" else "بعدی ←",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun IntroPage(data: IntroPageData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(LightPurple, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = data.title,
                tint = data.iconTint,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = data.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Purple,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = data.description,
            fontSize = 15.sp,
            color = Color(0xFF555555),
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIntroScreen() {
    IntroductionScreen(onFinish = {})
}