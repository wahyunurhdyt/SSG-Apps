package id.semisama.app.api.repository

import android.content.Context
import id.semisama.app.api.data.*
import id.semisama.app.api.manager.ManagerNetwork
import id.semisama.app.base.BaseRepository
import id.semisama.app.utilily.tempAuth
import id.semisama.app.utilily.tempTokenResetPassword

class RepositoryAuth(
    context: Context,
    private val managerNetwork: ManagerNetwork
): BaseRepository(context) {

    suspend fun postRegister(
        fullName: String?,
        email: String?,
        phoneNumber: String?,
        password: String?
    ): ResponseAuth? {
        val req = RequestRegister(fullName, email, phoneNumber, password)
        return apiRequest { managerNetwork.serviceAuth.postRegister(req) }
    }

    suspend fun postVerificationEmail(): ResponseVerificationEmail? {
        if (!isTokenOk()) return null
        return apiRequest { managerNetwork.serviceAuth.postVerificationEmail() }
    }

    suspend fun postVerifyEmail(
        requestId: String?,
        code: Int?
    ): Unit? {
        if (!isTokenOk()) return null
        val req = RequestVerifyEmail(requestId, code)
        return apiRequest { managerNetwork.serviceAuth.postVerifyEmail(req) }
    }

    suspend fun postLogin(
        email: String?,
        password: String?
    ): ResponseAuth? {
        val req = RequestLogin(email, password)
        return apiRequest { managerNetwork.serviceAuth.postLogin(req) }
    }

    suspend fun postSocialMedia(
        token: String?,
        type: String?
    ): ResponseAuth? {
        val req = RequestSocmed(token, type)
        return apiRequest { managerNetwork.serviceAuth.postSocialMedia(req) }
    }

    suspend fun postLogout(): Unit? {
        val req = RequestRefreshToken(tempAuth?.tokens?.refresh?.token)
        return apiRequest { managerNetwork.serviceAuth.logout(req) }
    }

    suspend fun resetPassword(password: String): Unit? {
        val req = RequestPassword(password)
        return apiRequest { managerNetwork.serviceAuth.resetPassword(req, tempTokenResetPassword) }
    }

    suspend fun forgotPassword(email: String): Unit? {
        val req = RequestEmail(email)
        return apiRequest { managerNetwork.serviceAuth.forgotPassword(req) }
    }
}