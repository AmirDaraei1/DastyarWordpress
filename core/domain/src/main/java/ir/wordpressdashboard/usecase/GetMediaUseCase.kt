package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.model.Media
import ir.wordpressdashboard.repository.MediaRepository
import javax.inject.Inject

class GetMediaUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(): List<Media> = repository.getMedia()
}
