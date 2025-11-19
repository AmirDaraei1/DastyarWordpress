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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
        onQRCodeClick = {
            navigateToQRCode()
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
    onQRCodeClick: () -> Unit,
    onNavItemSelected: (Int) -> Unit,
    selectIndex: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Aparat Video Section
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
        ) {
            AparatVideoView(
                videoUrl = "https://www.aparat.com/v/your_video_id", // Replace with actual video ID
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }

        //2. Logo/Image
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFEDE7F6), shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Build, contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFFB39DDB)
            )
        }
        //3. Main Title/Text
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "برای مشاهده فروشگاه ابتدا سایت خود را متصل کنید",
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color(0xFF616161),
            textAlign = TextAlign.Center
        )
        //4. Buttons
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onSecureKeyClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("اتصال با کلید امنیتی", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onQRCodeClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("اتصال با QR Code", color = Color.White)
        }
        //5. Info/Help Section
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "راهنما",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB39DDB),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column {
            Text(
                text = "برای اتصال به سایت نیاز به راهنمایی داری ویدیوی بالا را ببین",
                fontWeight = FontWeight.Bold
            )
        }

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
        onQRCodeClick = {},
        onNavItemSelected = {},
        selectIndex = 1
    )
}