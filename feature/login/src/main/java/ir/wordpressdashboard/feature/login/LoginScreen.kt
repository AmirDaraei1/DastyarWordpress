package ir.wordpressdashboard.feature.login


import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalInspectionMode
import ir.wordpressdashboard.i18n.LocalStrings

@Composable
fun LoginRoute(
    navigateToConSecKeys: () -> Unit,
    navigateToQRCode: () -> Unit
) {
    var selectIndex by remember { mutableStateOf(1) }

    LoginScreen(
        onSecureKeyClick = {
            navigateToConSecKeys()
        },
        onNavItemSelected = { idx ->
            selectIndex = idx
        },
        selectIndex = selectIndex,
    )
}

@Composable
fun AparatVideoView(
    videoUrl: String = "https://www.aparat.com/v/a00ml3n", // Replace with actual Aparat video URL
    modifier: Modifier = Modifier
) {
    val isInPreview = LocalInspectionMode.current

    if (isInPreview) {
        // Show a placeholder in preview mode
        Box(
            modifier = modifier.background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Video Preview\n(WebView not available in preview)",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    } else {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    webViewClient = WebViewClient()

                    // Aparat embed URL format
                    val embedUrl = when {
                        videoUrl.contains("aparat.com/v/") -> {
                            val videoId = videoUrl.substringAfterLast("/")
                            "https://www.aparat.com/video/video/embed/videohash/$videoId/vt/frame"
                        }
                        else -> videoUrl
                    }

                    loadUrl(embedUrl)
                }
            },
            modifier = modifier
        )
    }
}

@Composable
fun LoginScreen(
    onSecureKeyClick: ()-> Unit,
    onNavItemSelected: (Int) -> Unit,
    selectIndex: Int
) {
    val strings = LocalStrings.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6B52E3))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = strings.connectToSite,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 1. Aparat Video Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Color.Black)
        ) {
            AparatVideoView(
                videoUrl = "https://www.aparat.com/v/your_video_id", // Replace with actual video ID
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        //2. Buttons
        Button(
            onClick = onSecureKeyClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B52E3)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp)
        ) {
            Text(
                text = strings.connectWithPassword,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // دکمه QRCode موقتاً غیرفعال شده
        // Button(
        //     onClick = onQRCodeClick,
        //     colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B52E3)),
        //     shape = RoundedCornerShape(12.dp),
        //     modifier = Modifier
        //         .fillMaxWidth()
        //         .padding(horizontal = 24.dp)
        //         .height(56.dp)
        // ) {
        //     Text(
        //         text = "اتصال با QRCODE",
        //         color = Color.White,
        //         fontSize = 16.sp,
        //         fontWeight = FontWeight.Bold
        //     )
        // }

        Spacer(modifier = Modifier.height(40.dp))

        //3. Help Section
        Text(
            text = strings.helpTitle,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A4A4A),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = strings.helpDescription,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

//@Composable
//fun BottomNavigationBar(selectIndex: Int, onItemSelected: (Int) -> Unit) {
//
//    val items = listOf("حساب کاربری","محصولات","تصاویر")
//    Row (
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(bottom = 24.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        items.forEachIndexed { idex, title ->
//            Column (horizontalAlignment = Alignment.CenterHorizontally) {
//                Box (
//                    modifier = Modifier
//                        .size(8.dp)
//                        .background(
//                            if (selectIndex == idex) Color(0xFF512DA8) else Color(0xFFE1BEE7),
//                            CircleShape
//                        )
//                )
//                Text(
//                    text = title,
//                    fontSize = 12.sp,
//                    color = if (selectIndex == idex) Color(0xFF512DA8) else Color(0xFFBDBDBD),
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//            }
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(
        onSecureKeyClick = {},
        onNavItemSelected = {},
        selectIndex = 1
    )
}