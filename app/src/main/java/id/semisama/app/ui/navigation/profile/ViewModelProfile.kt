package id.semisama.app.ui.navigation.profile

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisama.app.api.data.User
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.ui.person.edit.ActivityEdit
import id.semisama.app.utilily.Coroutines
import id.semisama.app.utilily.launchNewActivity
import id.semisama.app.utilily.tempAddress

class ViewModelProfile(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null

    val user = MutableLiveData<User>()
    val city = MutableLiveData(tempAddress)
    val textInitial = MutableLiveData("")
    val loadingVisibility = MutableLiveData(View.GONE)

    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    @Suppress("unused")
    fun View.onClickedLogout() {
        bridge?.showLogoutDialog()
    }

    fun logoutAccount(){
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                managerRepository.repositoryPerson.unsubscribeFcm()
                managerRepository.repositoryAuth.postLogout()
                bridge?.logout()
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                loadingVisibility.postValue(View.GONE)
            }
        }

    }
    fun View.onClickedEdit(){
        context.launchNewActivity(ActivityEdit::class.java)
    }

    interface Bridge {
        fun logout()
        fun showLogoutDialog()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}