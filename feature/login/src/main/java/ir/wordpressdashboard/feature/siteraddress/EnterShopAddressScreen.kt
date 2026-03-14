package ir.wordpressdashboard.feature.siteraddress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ShopCredentials(
    val address: String,
    val wpUsername: String,
    val wpAppPassword: String
)

@Composable
fun EnterShopAddressRoute(navigateToHome: (ShopCredentials) -> Unit) {
    var address      by remember { mutableStateOf("") }
    var wpUsername   by remember { mutableStateOf("") }
    var wpAppPassword by remember { mutableStateOf("") }

    EnterShopAddressScreen(
        address       = address,
        wpUsername    = wpUsername,
        wpAppPassword = wpAppPassword,
        onSiteAddressEntered  = { address = it },
        onWpUsernameChanged   = { wpUsername = it },
        onWpAppPasswordChanged = { wpAppPassword = it },
        onNextClick = {
            navigateToHome(ShopCredentials(address, wpUsername, wpAppPassword))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterShopAddressScreen(
    address: String = "",
    wpUsername: String = "",
    wpAppPassword: String = "",
    onSiteAddressEntered: (String) -> Unit,
    onWpUsernameChanged: (String) -> Unit = {},
    onWpAppPasswordChanged: (String) -> Unit = {},
    onNextClick: () -> Unit
) {
    val primaryPurple = Color(0xFF5850A6)
    val lightGray     = Color(0xFFE0E0E0)
    val backgroundColor = Color(0xFFFAFAFA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // اسکرول‌پذیر چون فیلدها بیشتر شدند
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── آدرس سایت ────────────────────────────────────────────────
            Text(
                text = "آدرس سایت",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
            TextField(
                value = address,
                onValueChange = onSiteAddressEntered,
                placeholder = { Text("https://www.example.com", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = lightGray,
                    unfocusedContainerColor = lightGray,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // ── نام کاربری وردپرس ─────────────────────────────────────────
            Text(
                text = "نام کاربری وردپرس",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
            TextField(
                value = wpUsername,
                onValueChange = onWpUsernameChanged,
                placeholder = { Text("admin", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = lightGray,
                    unfocusedContainerColor = lightGray,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // ── رمز عبور برنامه ───────────────────────────────────────────
            Text(
                text = "رمز عبور برنامه (Application Password)",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
            TextField(
                value = wpAppPassword,
                onValueChange = onWpAppPasswordChanged,
                placeholder = { Text("xxxx xxxx xxxx xxxx xxxx xxxx", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = lightGray,
                    unfocusedContainerColor = lightGray,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Text(
                text = "از مسیر پروفایل کاربری وردپرس → رمزهای عبور برنامه بسازید",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        }

        // ── دکمه ورود ─────────────────────────────────────────────────────
        Button(
            onClick = onNextClick,
            colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .height(56.dp),
            enabled = address.isNotBlank() && wpUsername.isNotBlank() && wpAppPassword.isNotBlank()
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
        address       = "",
        wpUsername    = "",
        wpAppPassword = "",
        onSiteAddressEntered   = {},
        onWpUsernameChanged    = {},
        onWpAppPasswordChanged = {},
        onNextClick = {}
    )
}