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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import ir.wordpressdashboard.feature.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun IntroductionRoute(navigateToLogin: () -> Unit) {
    IntroductionScreen {
        navigateToLogin()
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IntroductionScreen(
    onFinish: () -> Unit // Callback when user reaches the last screen
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pager
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier.weight(1f) // Take most of the screen
        ) { page ->
            IntroPage(page)
        }

        // Page Indicator
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                        .background(
                            if (pagerState.currentPage == index) Color(0xFF673AB7) else Color.Gray,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = if (pagerState.currentPage < 2) Arrangement.SpaceBetween else Arrangement.End
        ) {
            if (pagerState.currentPage < 2) {
                TextButton( onClick = { onFinish() }) {
                    Text(text = "رد کردن", color = Color(0xFF673AB7))
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage == 2) {
                            onFinish()
                        } else {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Text(text = if (pagerState.currentPage == 2) "اتمام" else "بعدی  > ")
            }
        }
    }
}

@Composable
fun IntroPage(page: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color(0xFFEDE7F6), shape = RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Title ${page + 1}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF673AB7)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Description for page ${page + 1}",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIntroScreen() {
    IntroductionScreen(onFinish = {})
}