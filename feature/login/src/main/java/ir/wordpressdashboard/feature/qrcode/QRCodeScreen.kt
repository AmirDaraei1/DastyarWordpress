package ir.wordpressdashboard.feature.qrcode

import android.widget.Toast
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun QRCodeRoute(navigateToEnterShopAddress: () -> Unit) {
    QRCodeScreen(
        viewModel = hiltViewModel(),
        onNextClick = navigateToEnterShopAddress
    )
}

@Composable
fun QRCodeScreen(
    viewModel: QRCodeViewModel,
    onNextClick: () -> Unit
) {
    val scanState by viewModel.scanState.collectAsState()
    val stepActive = Color(0xFF6251A6)
    val context = LocalContext.current

    // Auto-navigate after successful scan
    LaunchedEffect(scanState) {
        when (val state = scanState) {
            is QRScanState.Success -> {
                Toast.makeText(context, "✅ اطلاعات سایت ذخیره شد", Toast.LENGTH_SHORT).show()
                onNextClick()
            }
            is QRScanState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4F5))
    ) {
        Spacer(Modifier.height(24.dp))

        Spacer(Modifier.height(32.dp))

        // Title
        Text(
            text = "اسکن QR Code",
            color = stepActive,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        // QR Code Scanner
        Box(
            Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .height(400.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Camera Preview with Scanner Frame
                Box(
                    Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(4.dp, stepActive, RoundedCornerShape(20.dp))
                ) {
                    QRCodeScanner(
                        modifier = Modifier.fillMaxSize(),
                        onQRCodeScanned = { code ->
                            viewModel.onQRCodeScanned(code)
                        }
                    )

                    // Scanner Frame Overlay
                    Box(
                        Modifier
                            .size(220.dp)
                            .align(Alignment.Center)
                            .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    )
                }

                Spacer(Modifier.height(16.dp))

                when (val state = scanState) {
                    is QRScanState.Success -> {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (state.credentials.baseUrl.isEmpty())
                                    "✅ کلیدها ذخیره شدند!\nاکنون آدرس سایت را وارد کنید"
                                else
                                    "✅ اطلاعات سایت ذخیره شد!",
                                color = Color(0xFF4CAF50),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(12.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.Start
                            ) {
                                if (state.credentials.baseUrl.isNotEmpty()) {
                                    Text(
                                        text = "آدرس سایت:",
                                        color = stepActive,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = state.credentials.baseUrl,
                                        color = Color(0xFF333333),
                                        fontSize = 12.sp
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                                Text(
                                    text = "Consumer Key:",
                                    color = stepActive,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = state.credentials.consumerKey.take(12) + "••••••••",
                                    color = Color(0xFF333333),
                                    fontSize = 12.sp
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Secret Key:",
                                    color = stepActive,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = state.credentials.secretKey.take(12) + "••••••••",
                                    color = Color(0xFF333333),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = "دوربین را به سمت QR Code نگه دارید",
                            color = stepActive,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

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
                Text(
                    text = if (scanState is QRScanState.Success) "بعدی" else "رد کردن",
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQRCodeScreen() {
    // Preview without ViewModel
}
