package ir.wordpressdashboard.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ir.wordpressdashboard.feature.login.R
import kotlinx.coroutines.delay

@Composable
fun SplashRoute(navigateToLogin: () -> Unit) {
    SplashScreen {
        navigateToLogin()
    }
}

@Composable
fun SplashScreen(navigateToLogin: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        navigateToLogin()
    }

    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Image(
            painter = painterResource(id = R.drawable.txt_bottom),
            contentDescription = "Bottom Text",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Image(
            painter = painterResource(id = R.drawable.logo_wordpress),
            contentDescription = "Centered Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .size(1000.dp),
            contentScale = ContentScale.Fit
        )
    }
}