package ir.wordpressdashboard.feature.qrcode

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun QRCodeRoute(navigateToEnterShopAddress: () -> Unit) {
    QRCodeScreen(onNextClick = navigateToEnterShopAddress)
}

@Composable
fun QRCodeScreen(onNextClick: () -> Unit) {
    val stepInactive = Color(0xFFD9BFFF)
    val stepActive = Color(0xFF6251A6)
    val stepBackground = Color(0xFFF3EAFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4F5))
    ) {
        // Top App Bar
        Box(
            Modifier
                .fillMaxWidth()
                .background(stepBackground)
                .height(56.dp),
        ) {
            // Progress Bar (center)
            Box(
                Modifier.align(Alignment.Center)
            ) {
                Box(
                    Modifier
                        .width(80.dp)
                        .height(6.dp)
                        .background(
                            color = stepActive,
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "بعدی",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(28.dp),
                tint = stepInactive,
            )
        }

        Spacer(Modifier.height(24.dp))

        // Step Dots
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                Modifier
                    .size(28.dp)
                    .background(stepActive, CircleShape)
            )
            Box(
                Modifier
                    .size(28.dp)
                    .background(stepActive, CircleShape)
            )
            Box(
                Modifier
                    .size(28.dp)
                    .background(stepInactive, CircleShape)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Title
        Text(
            text = "اسکن QR Code",
            color = stepActive,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        // QR Code Scanner Placeholder
        Box(
            Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .height(300.dp)
                .background(stepBackground, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // QR Code Scanner will be implemented here
                Box(
                    Modifier
                        .size(200.dp)
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "QR Code Scanner\n(To be implemented)",
                        textAlign = TextAlign.Center,
                        color = stepActive,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "دوربین را به سمت QR Code نگه دارید",
                    color = stepActive,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Next Button (Skip for now)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, end = 24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Button(
                onClick = onNextClick,
                colors = ButtonDefaults.buttonColors(containerColor = stepActive),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(130.dp)
                    .height(48.dp)
            ) {
                Text(text = "رد کردن", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQRCodeScreen() {
    QRCodeScreen(onNextClick = {})
}

