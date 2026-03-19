package ir.wordpressdashboard.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Extended icons (require material-icons-extended dependency) are commented out
// import androidx.compose.material.icons.automirrored.filled.List
// import androidx.compose.material.icons.filled.PhotoLibrary
// import androidx.compose.material.icons.filled.ShoppingCart

enum class BottomNavItem(
    val label: String,
    val icon: ImageVector
) {
    // Order: right to left in RTL → first item = rightmost
    Products(
        label = "محصولات",
        icon = Icons.Filled.Home          // replace with Icons.Filled.ShoppingCart when extended added
    ),
    CreateProduct(
        label = "ایجاد محصول",
        icon = Icons.Filled.Add
    ),
    Media(
        label = "تصاویر",
        icon = Icons.Filled.Settings      // replace with Icons.Filled.PhotoLibrary when extended added
    ),
    Posts(
        label = "پست‌ها",
        icon = Icons.Filled.Menu          // replace with Icons.AutoMirrored.Filled.List when extended added
    )
}
