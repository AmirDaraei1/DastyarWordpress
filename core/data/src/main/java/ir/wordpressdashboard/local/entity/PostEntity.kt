package ir.wordpressdashboard.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String = "",
    val excerpt: String = "",
    val status: String = "publish",
    val date: String = "",
    val page: Int = 1
)
