package ir.wordpressdashboard.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ir.wordpressdashboard.i18n.AppStrings

enum class BottomNavItem(
    val icon: ImageVector,
) {
    Products(
        icon = Icons.Filled.Home,
    ),
    CreateProduct(
        icon = Icons.Filled.Add,
    ),
    Media(
        icon = Icons.Filled.Settings,
    ),
    Posts(
        icon = Icons.Filled.Menu,
    ),
}

fun BottomNavItem.label(strings: AppStrings): String = when (this) {
    BottomNavItem.Products -> strings.navProducts
    BottomNavItem.CreateProduct -> strings.addProduct
    BottomNavItem.Media -> strings.navMedia
    BottomNavItem.Posts -> strings.navPosts
}
