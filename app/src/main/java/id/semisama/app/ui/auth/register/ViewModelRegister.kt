package id.semisama.app.ui.auth.register

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisama.app.R
import id.semisama.app.api.data.DataAuth
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.base.Application
import id.semisama.app.utilily.Coroutines
import id.semisama.app.utilily.isValidPassword
import id.semisama.app.utilily.isValidPhoneNumber

class ViewModelRegister(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge){
        this.bridge = bridge
    }

    val dataAuth = MutableLiveData<DataAuth>()
    val title = MutableLiveData(Application.getString(R.string.labelRegister))
    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }

    val name = MutableLiveData<String>()
    val nameError = MutableLiveData<String?>()
    val phone = MutableLiveData<String>()
    val phoneError = MutableLiveData<String?>()
    val email = MutableLiveData<String>()
    val emailError = MutableLiveData<String?>()
    val password = MutableLiveData<String>()
    val passwordError = MutableLiveData<String?>()
    val passwordConfirmation = MutableLiveData<String>()
    val passwordConfirmationError = MutableLiveData<String?>()
    private val limitLength = MutableLiveData(10)
    val maxLength = MutableLiveData(14)


    private var isLoading = false
    val isButtonEnabled = MutableLiveData(false)
    val loadingVisibility = MutableLiveData(View.GONE)

    fun checkButton() {
        val nameOk = isNameOk()
        val phoneOk = isPhoneOk()
        val emailOk = isEmailOk()
        val passwordOk = isPasswordOk()
        val passwordConfirmationOk = isPasswordConfirmation()
        val enabled = nameOk && phoneOk && emailOk && passwordOk && passwordConfirmationOk
        if (!isLoading && isButtonEnabled.value != enabled) isButtonEnabled.postValue(enabled)
    }

    private fun isNameOk(): Boolean {
        return if (name.value.isNullOrEmpty()) {
            val error = Application.getString(R.string.labelNameError)
            if (nameError.value != error) nameError.postValue(error)
            false
        } else {
            if (nameError.value != null) nameError.postValue(null)
            true
        }
    }

    private fun isPhoneOk(): Boolean {
        return if (!phone.value.isNullOrEmpty()) {
            if (!isValidPhoneNumber(phone.value!!)) {
                val error = Application.getStringArray(R.array.labelPhoneError)[0]
                var seconChar= "1"
                if (phone.value!!.length >= 2){
                    seconChar = phone.value!![1].toString()
                }
                val newPhoneInternational = "${phone.value!![0]}$seconChar"
                if (newPhoneInternational == "62"){
                    limitLength.postValue(11)
                    maxLength.postValue(15)
                    if (phone.value!!.length >= 5){
                        if (phoneError.value != error) phoneError.postValue(error)
                    }
                }else{
                    limitLength.postValue(10)
                    maxLength.postValue(14)
                    if (phone.value!!.length >= 4){
                        if (phoneError.value != error) phoneError.postValue(error)
                    }
                }
                false
            } else {
                if ((phone.value?.length ?: 0) < limitLength.value!! ) {
                    val error = Application.getStringArray(R.array.labelPhoneError)[1]
                    if (phoneError.value != error) phoneError.postValue(error)
                    false
                } else {
                    if (phoneError.value != null) phoneError.postValue(null)
                    true
                }
            }
        } else {
            if (phoneError.value != null) phoneError.postValue(null)
            false
        }
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
            if (!isValidPassword(password.value)) {
                val error = Application.getStringArray(R.array.labelPasswordError)[0]
                if (passwordError.value != error) passwordError.postValue(error)
                false
            } else {
                if ((password.value?.length ?: 0) < 8) {
                    val error = Application.getStringArray(R.array.labelPasswordError)[1]
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
                    val error = Application.getStringArray(R.array.labelPasswordError)[2]
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
    fun View.onClickedRegister() {
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        isButtonEnabled.postValue(false)
        Coroutines.main {
            try {
                dataAuth.postValue(managerRepository.repositoryAuth.postRegister(name.value, email.value, phone.value, password.value)?.data)
            } catch (e: ApiException) {
                when {
                    e.message?.contains("email") == true -> {
                        emailError.postValue(e.message)
                    }
                    e.message?.contains("phone") == true -> {
                        phoneError.postValue(e.message)
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