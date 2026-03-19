package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.repository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(id: Int) = repository.deletePost(id)
}
