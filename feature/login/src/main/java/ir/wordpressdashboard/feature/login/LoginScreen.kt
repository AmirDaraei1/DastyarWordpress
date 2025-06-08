package ir.wordpressdashboard.feature.login


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginRoute(navigateToHome: (userId: String) -> Unit) {
    LoginScreen {
//        navigateToHome("123")
    }
}

@Composable
fun LoginScreen(onClick: () -> Unit) {
    Column() {
        Image(
            painter = painterResource(id = R.drawable.txt_bottom),
            contentDescription = "",
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier.size(50.dp),
//            contentAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Welcome to Wordpress Dashboard",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF673AB7)
            )

        }
        Spacer(modifier = Modifier.height(32.dp))


    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen {}
}