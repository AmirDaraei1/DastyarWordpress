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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import ir.wordpressdashboard.i18n.LocalStrings
import kotlinx.coroutines.launch

// New brand palette — matches the app icon
private val Purple = Color(0xFF7C3AED)        // primary (same as the W in the logo)
private val PurpleLight = Color(0xFFA78BFA)   // icon background purple
private val PurpleSoft = Color(0xFFEDE9FE)    // pale tint for surfaces
private val AccentYellow = Color(0xFFFFC93C)  // the sparkle
private val DotInactive = Color(0xFFD6CCF5)

data class IntroPageData(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val iconTint: Color
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
    val strings = LocalStrings.current
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val introPages = listOf(
        IntroPageData(
            icon = Icons.Default.ShoppingCart,
            title = strings.introPage1Title,
            description = strings.introPage1Desc,
            iconTint = Purple,
        ),
        IntroPageData(
            icon = Icons.Default.Create,
            title = strings.introPage2Title,
            description = strings.introPage2Desc,
            iconTint = PurpleLight,
        ),
        IntroPageData(
            icon = Icons.Default.Star,
            title = strings.introPage3Title,
            description = strings.introPage3Desc,
            iconTint = AccentYellow,
        ),
    )

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
                    Brush.horizontalGradient(listOf(PurpleLight, Purple))
                )
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = strings.introHeaderTitle,
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
                            color = if (pagerState.currentPage == index) Purple else DotInactive,
                            shape = CircleShape
                        )
                )
            }
        }

        // Navigation Buttons — always LTR: Skip(left) | Next(right)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
                    Text(text = strings.introSkip, color = Purple, fontSize = 15.sp)
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
                    text = if (pagerState.currentPage == introPages.size - 1) strings.introStart else strings.introNext,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        } // end CompositionLocalProvider
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
                .background(PurpleSoft, shape = CircleShape),
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