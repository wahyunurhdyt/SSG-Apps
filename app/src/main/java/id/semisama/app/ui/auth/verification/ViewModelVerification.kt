package id.semisama.app.ui.auth.verification

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import id.semisama.app.R
import id.semisama.app.api.data.User
import id.semisama.app.api.data.VerificationEmail
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.base.Application
import id.semisama.app.base.BaseResponse
import id.semisama.app.base.BaseResponse.Companion.CODE_FAILED_TOO_MANY_REQUEST_VERIFICATION_EMAIL
import id.semisama.app.utilily.Coroutines
import id.semisama.app.utilily.toast

class ViewModelVerification(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge){
        this.bridge = bridge
    }

    val title = MutableLiveData(Application.getString(R.string.labelVerification))
    val verificationEmail = MutableLiveData<VerificationEmail>()
    val user = MutableLiveData<User>()
    val code = MutableLiveData<String>()
    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }

    private var isLoading = false
    val isButtonEnabled = MutableLiveData(false)
    val loadingVisibility = MutableLiveData(View.GONE)

    init {
        requestVerification()
    }

    fun requestVerification(){
        isLoading = false
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                verificationEmail.postValue(managerRepository.repositoryAuth.postVerificationEmail()?.data)
            } catch (e: ApiException) {
                when (e.code) {
                    CODE_FAILED_TOO_MANY_REQUEST_VERIFICATION_EMAIL -> {
                        Application.getContext().toast(Application.getString(R.string.labelTryToManyRequest))
                        bridge?.onClickedUpButton()
                    }
                    else -> bridge?.showSnackbar(e.message)
                }
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                isLoading = false
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    fun checkButton() {
        val codeOk = isCodeOk()
        if (!isLoading && isButtonEnabled.value != codeOk) isButtonEnabled.postValue(codeOk)
    }

    private fun isCodeOk(): Boolean {
        return code.value?.length == 6
    }

    @Suppress("unused")
    fun View.onClickedVerification() {
        isLoading = false
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                managerRepository.repositoryAuth.postVerifyEmail(verificationEmail.value?.requestId, code.value?.toInt())
                getUser()
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

    private fun getUser(){
        isLoading = false
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                user.postValue(managerRepository.repositoryPerson.getUser()?.data)
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

    interface Bridge {
        fun onClickedUpButton()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}