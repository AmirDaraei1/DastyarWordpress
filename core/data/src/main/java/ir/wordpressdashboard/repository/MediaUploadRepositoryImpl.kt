package ir.wordpressdashboard.repository

import android.content.Context
import android.net.Uri
import ir.wordpressdashboard.datasource.MediaUploadDataSource
import javax.inject.Inject

class MediaUploadRepositoryImpl @Inject constructor(
    private val mediaUploadDataSource: MediaUploadDataSource
) : MediaUploadRepository {
    override suspend fun uploadImage(context: Context, uri: Uri): Pair<Int, String> =
        mediaUploadDataSource.uploadMedia(context, uri)
}
