package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.repository.PostRepository
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(page: Int = 1, perPage: Int = 10): List<Post> =
        repository.getPosts(page = page, perPage = perPage)
}
