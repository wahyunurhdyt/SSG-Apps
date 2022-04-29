package id.semisama.app.ui.auth.login

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import id.semisama.app.R
import id.semisama.app.api.data.DataAuth
import id.semisama.app.api.manager.ManagerFirebase
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.base.Application
import id.semisama.app.ui.auth.register.ActivityRegister
import id.semisama.app.ui.person.password.ActivityPassword
import id.semisama.app.utilily.Constant.currentActivity
import id.semisama.app.utilily.Constant.forgotPassword
import id.semisama.app.utilily.Coroutines
import id.semisama.app.utilily.launchNewActivity

class ViewModelLogin(
    private val managerRepository: ManagerRepository
): ViewModel() {


    private var bridge: Bridge? = null

    val dataAuth = MutableLiveData<DataAuth>()
    val email = MutableLiveData<String>()
    val emailError = MutableLiveData<String?>()
    val password = MutableLiveData<String>()
    val passwordError = MutableLiveData<String?>()

    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }


    private var isLoading = false
    val isButtonEnabled = MutableLiveData(false)
    val loadingVisibility = MutableLiveData(View.GONE)

    fun checkButton() {
        val emailOk = isEmailOk()
        val passwordOk = isPasswordOk()
        val enabled = emailOk && passwordOk
        if (!isLoading && isButtonEnabled.value != enabled) isButtonEnabled.postValue(enabled)
    }

    private fun isEmailOk(): Boolean {
        return if (!email.value.isNullOrEmpty()) {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.value ?: "").matches()) {
                if (emailError.value != null) emailError.postValue(null)
                true
            } else {
                val error = Application.getString(R.string.labelEmailError)
                if (emailError.value != error) emailError.postValue(error)
                false
            }
        } else {
            if (emailError.value != null) emailError.postValue(null)
            false
        }
    }

    private fun isPasswordOk(): Boolean {
        return if (!password.value.isNullOrEmpty()) {
            if ((password.value?.length ?: 0) < 8) {
                val error = Application.getStringArray(R.array.labelPasswordError)[1]
                if (passwordError.value != error) passwordError.postValue(error)
                false
            } else {
                if (passwordError.value != null) passwordError.postValue(null)
                true
            }
        } else {
            if (passwordError.value != null) passwordError.postValue(null)
            false
        }
    }

    @Suppress("unused")
    fun View.onClickedLogin() {
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        isButtonEnabled.postValue(false)
        Coroutines.main {
            try {
                dataAuth.postValue(managerRepository.repositoryAuth.postLogin(email.value, password.value)?.data)
            } catch (e: ApiException) {
                when {
                    e.message?.contains("email") == true -> {
                        passwordError.postValue(e.message)
                    }
                    else -> bridge?.showSnackbar(e.message)
                }
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                isLoading = false
                loadingVisibility.postValue(View.GONE)
                isButtonEnabled.postValue(true)
            }
        }
    }

    fun loginSocialMedia(token: String?, authType: String?) {
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                dataAuth.postValue(managerRepository.repositoryAuth.postSocialMedia(token, authType)?.data)
            } catch (e: ApiException) {
                FirebaseAuth.getInstance().signOut()
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                isLoading = false
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    fun requestFcmToken(isRetry: Boolean = false) {
        if (isRetry) ManagerFirebase.deleteToken()
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if(it.isSuccessful) { processToken(it.result.toString()) }
            }
        } catch (e: Exception) {
            Log.e("requestFcmToken", "$e")
            if (!isRetry) requestFcmToken(true)
        }
    }

    private fun processToken(token: String) {
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                managerRepository.repositoryPerson.subscribeFcm(token)
                bridge?.next()
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                isLoading = false
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    fun View.onClickedForgotPassword() {
        currentActivity = forgotPassword
        context.launchNewActivity(ActivityPassword::class.java)
    }

    fun View.onClickedRegister() {
        context.launchNewActivity(ActivityRegister::class.java)
    }

    interface Bridge {
        fun next()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}