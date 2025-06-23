package ir.wordpressdashboard.feature.siteraddress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EnterSiteAddress(
    onSiteAddressEntered: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val stepInactive = Color(0xFFD9BFFF)
    val stepActive = Color(0xFF6251A6)
    val stepBackground = Color(0xFFF3EAFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4F5))
            .height(56.dp)
    ) {
        //Top App Bar
        Box(
            Modifier.fillMaxWidth()
                .background(stepBackground)

        ) {

        }





    }



}