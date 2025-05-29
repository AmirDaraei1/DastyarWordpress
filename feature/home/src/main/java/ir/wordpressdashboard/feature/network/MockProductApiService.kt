package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.Product
import javax.naming.Context

class MockProductApiService(
    private val context:Context
) {
    override suspend fun getProduct() : Product =withCon



}