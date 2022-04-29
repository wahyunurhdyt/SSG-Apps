package id.semisama.app.api.service

import id.semisama.app.BuildConfig
import id.semisama.app.api.data.*
import id.semisama.app.api.util.Client
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ServiceAuth {

    @POST("$version/register")
    suspend fun postRegister(
        @Body request: RequestRegister
    ): Response<ResponseAuth>

    @POST("$version/verification-email")
    suspend fun postVerificationEmail(): Response<ResponseVerificationEmail>

    @POST("$version/verify-email")
    suspend fun postVerifyEmail(
        @Body request: RequestVerifyEmail
    ): Response<Unit>

    @POST("$version/social-media")
    suspend fun postSocialMedia(
        @Body request: RequestSocmed
    ): Response<ResponseAuth>

    @POST("$version/login")
    suspend fun postLogin(
        @Body request: RequestLogin
    ): Response<ResponseAuth>

    @HTTP(method = "DELETE", path = "$version/logout", hasBody = true)
    suspend fun logout(
        @Body request: RequestRefreshToken
    ): Response<Unit>

    @POST("$version/reset-password")
    suspend fun resetPassword(
        @Body request: RequestPassword,
        @Query("token") token: String
    ): Response<Unit>

    @POST("$version/forgot-password")
    suspend fun forgotPassword(
        @Body request: RequestEmail
    ): Response<Unit>

    @POST("$version/refresh-tokens")
    suspend fun refreshToken(
        @Body request: RequestRefreshToken
    ): Response<ResponseToken>

    companion object {
        const val version = "v1"
        operator fun invoke(client: Client): ServiceAuth {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL_AUTH)
                .client(client.provideClient())
                .build()
                .create(ServiceAuth::class.java)
        }
    }
}