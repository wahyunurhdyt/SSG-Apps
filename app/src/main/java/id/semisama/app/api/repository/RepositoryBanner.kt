package id.semisama.app.api.repository

import android.content.Context
import id.semisama.app.api.data.*
import id.semisama.app.api.manager.ManagerNetwork
import id.semisama.app.base.BaseRepository
import id.semisama.app.utilily.tempLocation

class RepositoryBanner(
    context: Context,
    private val managerNetwork: ManagerNetwork
): BaseRepository(context) {

    suspend fun getBanners(): ResponseBanners? {
        return apiRequest { managerNetwork.servieceApi.getBanners() }
    }
}