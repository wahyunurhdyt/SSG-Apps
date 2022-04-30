package id.semisama.app.ui.navigation.home

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import id.semisama.app.api.data.*
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.base.BaseResponse
import id.semisama.app.ui.product.search.ActivitySearch
import id.semisama.app.utilily.*

class ViewModelHome(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null
    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    val banners = MutableLiveData<MutableList<Banner>>()
    val categories = MutableLiveData<MutableList<Category>>()
    val routes = MutableLiveData<ResponseRoute>()
    val driverLocation = MutableLiveData(LatLng(0.0,0.0))
    val product = MutableLiveData<ResponseProducts>()
    val productRecommend = MutableLiveData<ResponseProducts>()
    val city = MutableLiveData(tempAddress)
    private val isChangeLocation = MutableLiveData(false)
    val loadingVisibility = MutableLiveData(View.VISIBLE)
    val connectionErrorVisibility = MutableLiveData(View.GONE)
    val loadingCheckLocationVisibility = MutableLiveData(View.GONE)
    val requestLocationVisibility = MutableLiveData(View.GONE)


    fun View.onClickedSearch(){
        context.launchNewActivity(ActivitySearch::class.java)
    }

    val onClickChangeLocation = View.OnClickListener {
        isChangeLocation.postValue(true)
        getRegencies()
    }
    val onClickedViewAllProduct = View.OnClickListener {
        bridge?.viewAllProduct(true)
    }

    val onClickedViewAllProductRecommend = View.OnClickListener {
        bridge?.viewAllProduct(null)
    }


    val onClickedRequestLocation = View.OnClickListener {
        requestLocation()
    }

    val onClickRefresh = View.OnClickListener {
        bridge?.refreshData()
    }

    private fun requestLocation() {
        Coroutines.main {
            try {
                managerRepository.repositoryRegion.requestLocation()
                bridge?.showSnackbarLong("Terima Kasih Sudah Melakukan Request")
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
        }
    }

    fun checkLocation() {
        loadingCheckLocationVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                val response = managerRepository.repositoryRegion.checkSupportedLocation()?.data
                bridge?.setLocation(true, response!!, null)
                loadingCheckLocationVisibility.postValue(View.GONE)
            } catch (e: ApiException) {
                when (e.code) {
                    BaseResponse.CODE_LOCATION_NOT_SUPPORTED -> {
                        getRegencies()
                    }
                    else -> {
                        bridge?.showSnackbar(e.message)
                        loadingCheckLocationVisibility.postValue(View.GONE)
                    }
                }
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
                loadingCheckLocationVisibility.postValue(View.GONE)
            }
        }
    }

    fun getRoutes() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryRegion.getRoutes()
                routes.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
        }
    }

    fun getRegencies() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryRegion.getRegencies()?.data
                if (isChangeLocation.value!!) {
                    bridge?.changeLocation(response)
                    isChangeLocation.postValue(false)
                } else {
                    bridge?.setLocation(false, null, response)
                }
            }catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                loadingCheckLocationVisibility.postValue(View.GONE)
            }
        }
    }

    fun getBanners() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryBanner.getBanners()?.data
                banners.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
        }
    }

    fun getCategories() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getCategories()?.data
                categories.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
        }
    }

    fun getProductSelected() {
        loadingVisibility.postValue(View.VISIBLE)
        connectionErrorVisibility.postValue(View.GONE)
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getProducts(
                    tempRegion?.id,
                    null,
                    null,
                    true,
                    null,
                    4,
                    null,
                )
                product.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
                connectionErrorVisibility.postValue(View.VISIBLE)
            } finally {
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    fun getProductRecommend() {
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getProducts(
                    tempRegion?.id,
                    null,
                    null,
                    null,
                    null,
                    4,
                    null,
                )
                productRecommend.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    interface Bridge {
        fun setLocation(isSupported: Boolean, data: Region?, regencies: MutableList<Region>?)
        fun changeLocation(regencies: MutableList<Region>?)
        fun viewAllProduct(isSelected: Boolean?)
        fun refreshData()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}