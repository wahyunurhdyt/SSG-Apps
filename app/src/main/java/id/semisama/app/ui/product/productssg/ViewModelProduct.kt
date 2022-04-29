package id.semisama.app.ui.product.productssg

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisama.app.R
import id.semisama.app.api.data.ResponseProducts
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.base.Application
import id.semisama.app.ui.product.search.ActivitySearch
import id.semisama.app.utilily.Coroutines
import id.semisama.app.utilily.launchNewActivity
import id.semisama.app.utilily.tempRegion
import id.semisama.app.utilily.tempSelectedProduct

class ViewModelProduct(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null
    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    val title = MutableLiveData(if (tempSelectedProduct == null){
        Application.getString(R.string.labelSsgChoiceProduct)
    }else{
        Application.getString(R.string.labelRecommendProduct)
    })
    val page = MutableLiveData<Int>().apply { value = 1 }
    val product = MutableLiveData<ResponseProducts>()
    val loadingVisibility = MutableLiveData(View.GONE)
    val progressVisibility = MutableLiveData(View.GONE)
    val connectionErrorVisibility = MutableLiveData(View.GONE)
    val onClickRefresh = View.OnClickListener {
        bridge?.refreshData()
    }

    init{
        getProduct(page.value!!)
    }

    fun View.onClickedSearch(){
        context.launchNewActivity(ActivitySearch::class.java)
    }

    fun getProduct(page: Int) {
        connectionErrorVisibility.postValue(View.GONE)
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
                    null,
                    tempSelectedProduct,
                    page,
                    6,
                    null,

                )
                product.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                connectionErrorVisibility.postValue(View.VISIBLE)
            }
            finally {
                loadingVisibility.postValue(View.GONE)
                progressVisibility.postValue(View.GONE)
            }
        }
    }

    interface Bridge {
        fun refreshData()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}