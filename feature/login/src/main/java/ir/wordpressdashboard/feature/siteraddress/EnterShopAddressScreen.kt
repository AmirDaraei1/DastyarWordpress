package ir.wordpressdashboard.feature.siteraddress

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun EnterShopAddressRoute(navigateToEnterShopAddress: () -> Unit) {
    var address = ""
    EnterShopAddressScreen(
        address = address,
        onSiteAddressEntered = { address = it },
        onNextClick = navigateToEnterShopAddress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterShopAddressScreen(
    address: String = "",
    onSiteAddressEntered: (String) -> Unit,
    onNextClick: () -> Unit
) {
    val primaryPurple = Color(0xFF5850A6)
    val lightGray = Color(0xFFE0E0E0)
    val backgroundColor = Color(0xFFFAFAFA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Top App Bar with Title
//        Box(
//            Modifier
//                .fillMaxWidth()
//                .background(primaryPurple)
//                .height(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "دستیار وردپرس",
//                color = Color.White,
//                style = MaterialTheme.typography.headlineMedium
//            )
//        }

        // Content Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Input Field Label
            Text(
                text = "آدرس سایت",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Right
            )

            // Input Box
            TextField(
                value = address,
                onValueChange = onSiteAddressEntered,
                placeholder = {
                    Text(
                        "https://www.example.com",
                        color = Color.Gray
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightGray,
                    unfocusedContainerColor = lightGray,
                    disabledContainerColor = lightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Helper Text
//            Text(
//                text = "آدرس سایت با این فرمت وارد شود",
//                color = Color.Gray,
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.fillMaxWidth()
//            )
        }

        // Enter Button
        Button(
            onClick = onNextClick,
            colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .height(56.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "ورود",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ورود",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEnterShopAddressScreen() {
    EnterShopAddressScreen(
        address = "",
        onSiteAddressEntered = {},
        onNextClick = {}
    )
}