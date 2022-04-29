package id.semisama.app.api.service

import id.semisama.app.BuildConfig
import id.semisama.app.api.data.*
import id.semisama.app.api.util.Client
import id.semisama.app.base.BaseResponse
import id.semisama.app.utilily.tempRegion
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ServiceApi {

    @GET("$version/regions")
    suspend fun getRegencies(): Response<ResponseRegencies>

    @GET("${ServiceAuth.version}/regions/location")
    suspend fun checkSupportedLocation(
        @Query("lat") lat: Double?,
        @Query("lng") lng: Double?
    ): Response<ResponseSupportedRegion>

    @GET("${ServiceAuth.version}/banners")
    suspend fun getBanners(
        @Query("type") type: String = "app",
        @Query("isActive") isActive: Boolean = true
    ): Response<ResponseBanners>

    @GET("$version/regions/{id}/sell-routes")
    suspend fun getRoutes(
        @Path("id") id: String = tempRegion?.id!!
    ): Response<ResponseRoute>

    @POST("$version/request-locations")
    suspend fun createRequestLocation(
        @Body request: RequestLocation
    ): Response<Unit>

    companion object {
        const val version = "v1"
        operator fun invoke(client: Client): ServiceApi {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL_API)
                .client(client.provideClient())
                .build()
                .create(ServiceApi::class.java)
        }
    }
}