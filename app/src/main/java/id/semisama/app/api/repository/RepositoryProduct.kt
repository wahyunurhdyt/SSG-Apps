package id.semisama.app.api.repository

import android.content.Context
import id.semisama.app.api.data.*
import id.semisama.app.api.manager.ManagerNetwork
import id.semisama.app.base.BaseRepository
import id.semisama.app.utilily.tempProductId

class RepositoryProduct(
    context: Context,
    private val managerNetwork: ManagerNetwork
): BaseRepository(context) {

    suspend fun getCategories(): ResponseCategories? {
        return apiRequest { managerNetwork.serviceSrv.getCategories() }
    }

    suspend fun getProduct(): ResponseProduct? {
        return apiRequest { managerNetwork.serviceSrv.getProduct() }
    }

    suspend fun getProductsRecommendations(): ResponseProductSRecommendations? {
        return apiRequest { managerNetwork.serviceSrv.getProductsRecommendations() }
    }

    suspend fun getProducts(
        region: String?,
        categories: String?,
        search: String?,
        isSelected: Boolean?,
        page: Int?,
        limit: Int?,
        sortBy: String?,
    ): ResponseProducts? {
        return apiRequest { managerNetwork.serviceSrv.getProducts(region, categories, search,isSelected, page, limit, sortBy) }
    }
}