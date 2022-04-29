package id.semisama.app.api.repository

import android.content.Context
import id.semisama.app.api.data.*
import id.semisama.app.api.manager.ManagerNetwork
import id.semisama.app.base.BaseRepository
import id.semisama.app.base.BaseResponse
import id.semisama.app.utilily.tempLocation
import id.semisama.app.utilily.tempRegion

class RepositoryRegion(
    context: Context,
    private val managerNetwork: ManagerNetwork
): BaseRepository(context) {

    suspend fun checkSupportedLocation(): ResponseSupportedRegion? {
        return apiRequest { managerNetwork.servieceApi.checkSupportedLocation(tempLocation?.latitude, tempLocation?.longitude) }
    }

    suspend fun getRegencies(): ResponseRegencies? {
        return apiRequest { managerNetwork.servieceApi.getRegencies() }
    }

    suspend fun getRoutes(): ResponseRoute? {
        return apiRequest { managerNetwork.servieceApi.getRoutes() }
    }

    suspend fun requestLocation(): Unit? {
        isTokenOk()
        val coordinates: ArrayList<Double> = ArrayList()
        coordinates.add(tempLocation?.longitude!!)
        coordinates.add(tempLocation?.latitude!!)
        val req = RequestLocation(tempRegion?.id, UserLocation("Point", coordinates))
        return apiRequest { managerNetwork.servieceApi.createRequestLocation(req) }
    }
}