package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.repository.MediaRepository
import javax.inject.Inject

class DeleteMediaUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(id: Int) = repository.deleteMedia(id)
}
