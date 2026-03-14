package ir.wordpressdashboard.model

data class Post(
    val id: Int,
    val title: String,
    val content: String = "",
    val excerpt: String = "",
    val status: String = "publish",
    val date: String = ""
)
