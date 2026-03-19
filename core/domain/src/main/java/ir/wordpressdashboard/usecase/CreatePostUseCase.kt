package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(
        title: String,
        content: String,
        excerpt: String = "",
        status: String = "draft"
    ): Post = repository.createPost(
        title = title,
        content = content,
        excerpt = excerpt,
        status = status
    )
}
