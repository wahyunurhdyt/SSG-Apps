package id.semisama.app.ui.product.search
import android.view.View
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisama.app.R
import id.semisama.app.api.data.ResponseProducts
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.base.Application
import id.semisama.app.utilily.Coroutines
import id.semisama.app.utilily.tempRegion
import java.util.*

class ViewModelSearch(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null
    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    private var timer = Timer()
    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }
    val onSearchTextChanged = TextViewBindingAdapter.OnTextChanged { s, _, _, _ ->
        timer.cancel()
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val text = s.toString()
                search.postValue(if(text.length < 2) null else text)
            }
        }, 1000L)
    }
    val search = MutableLiveData<String>()
    val product = MutableLiveData<ResponseProducts>()
    val loadingVisibility = MutableLiveData(View.GONE)
    val progressVisibility = MutableLiveData(View.GONE)
    val totalResults = MutableLiveData("")
    val textEmpty = MutableLiveData(Application.getString(R.string.labelSearchKeyword))
    val emptyVisibility = MutableLiveData(View.VISIBLE)
    val searchEnabled = MutableLiveData(true)


    fun getProduct(search: String, page: Int) {
        if (page == 1){
            loadingVisibility.postValue(View.VISIBLE)
        }else{
            progressVisibility.postValue(View.VISIBLE)
        }
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getProducts(
                    tempRegion?.id,
                    null,
                    search,
                    null,
                    page,
                    6,
                    null,

                )
                product.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
            finally {
                loadingVisibility.postValue(View.GONE)
                progressVisibility.postValue(View.GONE)
            }
        }
    }

    interface Bridge {
        fun onClickedUpButton()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}