package ir.wordpressdashboard.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val sourceUrl: String,
    val mediaType: String,
    val mimeType: String,
    val page: Int = 1
)
