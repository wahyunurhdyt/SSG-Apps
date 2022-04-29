package id.semisama.app.api.service

import id.semisama.app.BuildConfig
import id.semisama.app.api.data.*
import id.semisama.app.api.util.Client
import id.semisama.app.base.BaseResponse
import id.semisama.app.utilily.tempProductId
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ServiceSrv {

    @GET("$version/categories")
    suspend fun getCategories(): Response<ResponseCategories>

    @GET("$version/products/{id}")
    suspend fun getProduct(
        @Path("id") id: String = tempProductId!!
    ): Response<ResponseProduct>


    @GET("$version/products/{id}/recommendations")
    suspend fun getProductsRecommendations(
        @Path("id") id: String = tempProductId!!
    ): Response<ResponseProductSRecommendations>

    @GET("${ServiceAuth.version}/products")
    suspend fun getProducts(
        @Query("region") region: String?,
        @Query("categories") categories: String?,
        @Query("search") search: String?,
        @Query("isSelected") isSelected: Boolean?,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("sortBy") sortBy: String?
    ): Response<ResponseProducts>


    companion object {
        const val version = "v1"
        operator fun invoke(client: Client): ServiceSrv {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL_SRV)
                .client(client.provideClient())
                .build()
                .create(ServiceSrv::class.java)
        }
    }
}