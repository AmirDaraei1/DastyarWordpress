package ir.wordpressdashboard.feature.keys

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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ConSecKeysRoute(navigateToEnterShopAddress: () -> Unit) {
    var siteAddress by remember { mutableStateOf("") }
    var consumerKey by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("") }

    ConSecKeysScreen(
        siteAddress = siteAddress,
        consumerKey = consumerKey,
        secretKey = secretKey,
        onSiteAddressChanged = { siteAddress = it },
        onConsumerKeyChanged = { consumerKey = it },
        onSecretKeyChanged = { secretKey = it },
        onNextClick = navigateToEnterShopAddress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConSecKeysScreen(
    siteAddress: String = "",
    consumerKey: String = "",
    secretKey: String = "",
    onSiteAddressChanged: (String) -> Unit,
    onConsumerKeyChanged: (String) -> Unit,
    onSecretKeyChanged: (String) -> Unit,
    onNextClick: () -> Unit
) {
    val stepActive = Color(0xFF6251A6)
    val inputBackground = Color(0xFFE8E8E8)
    val textColor = Color(0xFF666666)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top App Bar
        Box(
            Modifier
                .fillMaxWidth()
                .background(stepActive)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "اعتبار سنجی سایت",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(Modifier.height(40.dp))

        // Site Address Input Box
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(inputBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    Text(
                        text = "آدرس سایت",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextField(
                        value = siteAddress,
                        onValueChange = onSiteAddressChanged,
                        placeholder = {
                            Text(
                                text = "آدرس سایت یا این فرمت وارد شود",
                                color = textColor,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
            Text(
                text = "https://www.example.com",
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Consumer Key Input Box
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(inputBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    Text(
                        text = "کلید مصرف کننده",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextField(
                        value = consumerKey,
                        onValueChange = onConsumerKeyChanged,
                        placeholder = { Text("") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
            Text(
                text = "مثال:\nck_ef1b5aa967cgfb90e453be4671e80884dfbe4e16",
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Secret Key Input Box
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(inputBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    Text(
                        text = "رمز مصرف کننده",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextField(
                        value = secretKey,
                        onValueChange = onSecretKeyChanged,
                        placeholder = { Text("") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            }
            Text(
                text = "مثال:\ncs_7c584ec7704c60b47f9017c817d3db3fd81c1182",
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        // Login Button
        Button(
            onClick = onNextClick,
            colors = ButtonDefaults.buttonColors(containerColor = stepActive),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp),
            enabled = siteAddress.isNotBlank() && consumerKey.isNotBlank() && secretKey.isNotBlank()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "ورود",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "ورود", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Bottom Links
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "ویدیویی آموزش تنظیمات",
                color = stepActive,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "دانلود پلاگین",
                color = stepActive,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConSecKeysScreen() {
    ConSecKeysScreen(
        siteAddress = "",
        consumerKey = "",
        secretKey = "",
        onSiteAddressChanged = {},
        onConsumerKeyChanged = {},
        onSecretKeyChanged = {},
        onNextClick = {}
    )
}
