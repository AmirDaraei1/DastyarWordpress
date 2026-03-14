package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.repository.PostRepository
import javax.inject.Inject

class UpdatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(
        id: Int,
        title: String,
        content: String,
        status: String
    ): Post = repository.updatePost(id = id, title = title, content = content, status = status)
}
