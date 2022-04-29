package id.semisama.app.ui.person.password

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisama.app.R
import id.semisama.app.api.data.User
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.base.Application
import id.semisama.app.base.Application.Companion.getStringArray
import id.semisama.app.base.BaseResponse.Companion.CODE_FAILED_RESET_PASSWORD
import id.semisama.app.base.BaseResponse.Companion.CODE_FAILED_TOO_MANY_REQUEST
import id.semisama.app.base.BaseResponse.Companion.CODE_FAILED_TOO_MANY_REQUEST_FORGOT_PASSWORD
import id.semisama.app.base.BaseResponse.Companion.CODE_OLD_PASSWORD_NOT_MATCH
import id.semisama.app.base.BaseResponse.Companion.CODE_PASSWORD_CANT_BE_SAME
import id.semisama.app.utilily.Constant.createPassword
import id.semisama.app.utilily.Constant.currentActivity
import id.semisama.app.utilily.Constant.editPassword
import id.semisama.app.utilily.Constant.forgotPassword
import id.semisama.app.utilily.Coroutines
import id.semisama.app.utilily.isValidPassword
import id.semisama.app.utilily.toast

class ViewModelPassword(
    private val managerRepository: ManagerRepository
): ViewModel() {
    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge){
        this.bridge = bridge
    }

    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }

    val title = MutableLiveData(
        when (currentActivity) {
            createPassword -> {
                getStringArray(R.array.labelPassword)[0]
            }
            editPassword -> {
                getStringArray(R.array.labelPassword)[1]
            }
            forgotPassword -> {
                getStringArray(R.array.labelPassword)[2]
            }
            else -> {
                getStringArray(R.array.labelPassword)[3]
            }
        }
    )

    val desc = MutableLiveData(
        when (currentActivity) {
            createPassword -> {
                getStringArray(R.array.labelDescPassword)[0]
            }
            editPassword -> {
                getStringArray(R.array.labelDescPassword)[1]
            }
            forgotPassword -> {
                getStringArray(R.array.labelDescPassword)[2]
            }
            else -> {
                getStringArray(R.array.labelDescPassword)[3]
            }
        }
    )

    val oldPasswordVisibility = MutableLiveData(
        if (currentActivity != editPassword){
            View.GONE
        }else{
            View.VISIBLE
        }
    )

    val emailVisibility = MutableLiveData(
        if (currentActivity != forgotPassword){
            View.GONE
        }else{
            View.VISIBLE
        }
    )

    val passwordVisibility = MutableLiveData(
        if (currentActivity == forgotPassword){
            View.GONE
        }else{
            View.VISIBLE
        }
    )

    val user = MutableLiveData<User?>()

    val email = MutableLiveData<String>()
    val emailError = MutableLiveData<String?>()
    val oldPassword = MutableLiveData<String>()
    val oldPasswordError = MutableLiveData<String?>()
    val password = MutableLiveData<String>()
    val passwordError = MutableLiveData<String?>()
    val passwordConfirmation = MutableLiveData<String>()
    val passwordConfirmationError = MutableLiveData<String?>()

    private var isLoading = false
    val isButtonEnabled = MutableLiveData(false)
    val loadingVisibility = MutableLiveData(View.GONE)

    fun checkButton() {
        val emailOk = isEmailOk()
        val oldPasswordOk = isOldPasswordOk()
        val passwordOk = isPasswordOk()
        val passwordConfirmationOk = isPasswordConfirmation()
        val enabled = when (currentActivity) {
            editPassword -> {
                oldPasswordOk && passwordOk && passwordConfirmationOk
            }
            forgotPassword -> {
                emailOk
            }
            else -> {
                passwordOk && passwordConfirmationOk
            }
        }
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

    private fun isOldPasswordOk(): Boolean {
        return if (!oldPassword.value.isNullOrEmpty()) {
            if ((oldPassword.value?.length ?: 0) < 8) {
                val error = getStringArray(R.array.labelPasswordError)[1]
                if (oldPasswordError.value != error) oldPasswordError.postValue(error)
                false
            } else {
                if (oldPasswordError.value != null) oldPasswordError.postValue(null)
                true
            }
        } else {
            if (oldPasswordError.value != null) oldPasswordError.postValue(null)
            false
        }
    }

    private fun isPasswordOk(): Boolean {
        return if (!password.value.isNullOrEmpty()) {
            if (!isValidPassword(password.value)) {
                val error = getStringArray(R.array.labelPasswordError)[0]
                if (passwordError.value != error) passwordError.postValue(error)
                false
            } else {
                if ((password.value?.length ?: 0) < 8) {
                    val error = getStringArray(R.array.labelPasswordError)[1]
                    if (passwordError.value != error) passwordError.postValue(error)
                    false
                } else {
                    if (passwordError.value != null) passwordError.postValue(null)
                    true
                }
            }
        } else {
            if (passwordError.value != null) passwordError.postValue(null)
            false
        }
    }

    private fun isPasswordConfirmation(): Boolean {
        return if (!passwordConfirmation.value.isNullOrEmpty()) {
            when {
                password.value != passwordConfirmation.value -> {
                    val error = getStringArray(R.array.labelPasswordError)[2]
                    if (passwordConfirmationError.value != error) passwordConfirmationError.postValue(error)
                    false
                }
                else -> {
                    if (passwordConfirmationError.value != null) passwordConfirmationError.postValue(null)
                    true
                }
            }
        } else {
            if (passwordConfirmationError.value != null) passwordConfirmationError.postValue(null)
            false
        }
    }

    @Suppress("unused")
    fun View.onClickedConfirm(){
        when (currentActivity) {
            createPassword -> {
                createPassword()
            }
            editPassword -> {
                editPassword()
            }
            forgotPassword -> {
                forgotPassword()
            }
            else -> {
                resetPassword()
            }
        }
    }

    private fun createPassword(){
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        isButtonEnabled.postValue(false)
        Coroutines.main {
            try {
                managerRepository.repositoryPerson.setPassword(password.value!!)
                user.postValue(managerRepository.repositoryPerson.getUser()?.data)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                isLoading = false
                loadingVisibility.postValue(View.GONE)
                isButtonEnabled.postValue(true)
            }
        }
    }

    private fun editPassword(){
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        isButtonEnabled.postValue(false)
        Coroutines.main {
            try {
                managerRepository.repositoryPerson.changePassword(oldPassword.value!!, password.value!!)
//                user.postValue(null)
                Application.getContext().toast(Application.getString(R.string.labelSuccessEditPassword))
                bridge?.onClickedUpButton()
            } catch (e: ApiException) {
                when (e.code) {
                    CODE_OLD_PASSWORD_NOT_MATCH -> {
                        oldPasswordError.postValue(e.message)
                    }
                    CODE_PASSWORD_CANT_BE_SAME -> {
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

    private fun resetPassword(){
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        isButtonEnabled.postValue(false)
        Coroutines.main {
            try {
                managerRepository.repositoryAuth.resetPassword(password.value!!)
                Application.getContext().toast(getStringArray(R.array.labelResetPassword)[0])
                bridge?.onClickedUpButton()
            } catch (e: ApiException) {
                when (e.code) {
                    CODE_FAILED_RESET_PASSWORD -> {
                        Application.getContext().toast(getStringArray(R.array.labelResetPassword)[1])
                        bridge?.onClickedUpButton()
                    }
                    CODE_FAILED_TOO_MANY_REQUEST -> {
                        Application.getContext().toast(getStringArray(R.array.labelResetPassword)[2])
                        bridge?.onClickedUpButton()
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

    private fun forgotPassword(){
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        isButtonEnabled.postValue(false)
        Coroutines.main {
            try {
                managerRepository.repositoryAuth.forgotPassword(email.value!!)
                bridge?.showSnackbarLong(getStringArray(R.array.labelForgotPassword)[0])
            } catch (e: ApiException) {
                when {
                    e.message?.contains("email") == true -> {
                        emailError.postValue(e.message)
                    }
                    e.code == CODE_FAILED_TOO_MANY_REQUEST_FORGOT_PASSWORD -> {
                        Application.getContext().toast(getStringArray(R.array.labelForgotPassword)[1])
                        bridge?.onClickedUpButton()
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

    interface Bridge {
        fun onClickedUpButton()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}