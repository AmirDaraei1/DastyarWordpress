package ir.wordpressdashboard.repository

import android.content.Context
import android.net.Uri

interface MediaUploadRepository {
    suspend fun uploadImage(context: Context, uri: Uri): String  // returns sourceUrl
}
