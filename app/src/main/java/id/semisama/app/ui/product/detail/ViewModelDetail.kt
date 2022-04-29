package id.semisama.app.ui.product.detail

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisama.app.R
import id.semisama.app.api.data.ResponseProduct
import id.semisama.app.api.data.ResponseProductSRecommendations
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.base.Application
import id.semisama.app.utilily.Coroutines

class ViewModelDetail(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }
    val title = MutableLiveData(Application.getString(R.string.labelTitleDetailProduct))
    val productDetail = MutableLiveData<ResponseProduct>()
    val productsRecommendations = MutableLiveData<ResponseProductSRecommendations>()
    val loadingVisibility = MutableLiveData(View.GONE)
    val connectionErrorVisibility = MutableLiveData(View.GONE)
    val onClickRefresh = View.OnClickListener {
        bridge?.refreshData()
    }

    init{
        getProduct()
    }

    fun getProduct() {
        loadingVisibility.postValue(View.VISIBLE)
        connectionErrorVisibility.postValue(View.GONE)
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getProduct()
                val responseProductSRecommendations = managerRepository.repositoryProduct.getProductsRecommendations()
                productDetail.postValue(response!!)
                productsRecommendations.postValue(responseProductSRecommendations!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                connectionErrorVisibility.postValue(View.VISIBLE)
            }
            finally {
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    interface Bridge {
        fun onClickedUpButton()
        fun refreshData()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}