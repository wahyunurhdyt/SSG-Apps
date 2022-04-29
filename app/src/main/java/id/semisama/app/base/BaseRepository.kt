package id.semisama.app.base

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import id.semisama.app.R
import id.semisama.app.api.data.DataAuth
import id.semisama.app.api.data.RequestRefreshToken
import id.semisama.app.api.manager.ManagerFirebase
import id.semisama.app.api.manager.ManagerNetwork
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.api.util.SafeApiRequest
import id.semisama.app.ui.auth.login.ActivityLogin
import id.semisama.app.utilily.*
import id.semisama.app.utilily.Constant.authTemps
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.util.*

abstract class BaseRepository(context: Context): SafeApiRequest(context), KodeinAware {

    override val kodein by closestKodein { context }

    private val managerNetwork: ManagerNetwork by instance()

    val cache = Cache()

    protected suspend fun isTokenOk(): Boolean {

        this.cache.start(context)
        var boolean = true
        val context = Application.getContext()
        val currentTime = Date().time
        val shouldResetToken = tempAuth != null && currentTime > (tempAuth!!.tokens?.access?.expires ?: 0)

        if (shouldResetToken) {
            try {
                val req = RequestRefreshToken(
                    tempAuth?.tokens?.refresh?.token
                )
                val response = apiRequestBlocking {
                    managerNetwork.serviceAuth.refreshToken(req)
                }
                boolean = if (response?.isSuccess() == true) {
                    val user = tempAuth?.user
                    cache.set(authTemps, DataAuth(user, response.data))
                    true
                } else false
            } catch (e: ApiException) {
                context.toast(e.message)
                Log.e("isTokenOk","${e.message}")
                if (BaseResponse.CODE_REFRESH_TOKEN_INVALID == e.code) { logout(context) }
                boolean = false
            } catch (e: ConnectionException) {
                boolean = false
                context.toast(e.message)
                Log.e("isTokenOk","${e.message}")
            }
        }

        return boolean
    }

    private suspend fun logout(context: Context) {
        ManagerFirebase.deleteToken()
        try {
            managerNetwork.servicePerson.unsubscribeFcm()
        } catch (e: ApiException) {
            Log.e("Logout UnSub","${e.message}")
        } catch (e: ConnectionException) {
            Log.e("logout UnSub","${e.message}")
        }

        try {
            val req = RequestRefreshToken(
                tempAuth?.tokens?.refresh?.token
            )
            managerNetwork.serviceAuth.logout(req)
            if (tempAuth?.user?.provider == "google"){
                FirebaseAuth.getInstance().signOut()
            }
            cache.delete(authTemps)
            context.toast(Application.getString(R.string.labelExpiresSesion))
            context.launchNewActivity(ActivityLogin::class.java)
        } catch (e: ApiException) {
            context.toast(e.message)
        } catch (e: ConnectionException) {
            context.toast(e.message)
        }
    }
}