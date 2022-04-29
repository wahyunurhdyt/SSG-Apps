package id.semisama.app.ui.product.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisama.app.R
import id.semisama.app.adapter.Adapter
import id.semisama.app.api.data.Product
import id.semisama.app.base.BaseActivity
import id.semisama.app.databinding.ActivityDetailBinding
import id.semisama.app.ui.ViewModelFactoryDetail
import id.semisama.app.utilily.*
import id.semisama.app.utilily.Constant.baseUrlImageProducts
import org.kodein.di.generic.instance

class ActivityDetail : BaseActivity(), ViewModelDetail.Bridge {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: ViewModelDetail
    private val factory: ViewModelFactoryDetail by instance()
    private lateinit var adapterProduct: Adapter<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
    }

    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        viewModel = ViewModelProvider(this, factory).get(ViewModelDetail::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            productDetail.observe(owner,  {
                binding.ivProduct.loadImageFromLink(baseUrlImageProducts + it.data.image)
            })
            productsRecommendations.observe(owner, {
                showProducts(it.data)
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showProducts(data: MutableList<Product>) {
        adapterProduct = Adapter(R.layout.item_product, mutableListOf(),
            { itemView, item ->
                val name = itemView.findViewById<TextView>(R.id.tvName)
                val price = itemView.findViewById<TextView>(R.id.tvPrice)
                val unit = itemView.findViewById<TextView>(R.id.tvUnit)
                val image = itemView.findViewById<ImageView>(R.id.ivProduct)
                name.text = item.name
                price.text = item.getPriceFormat()
                unit.text = item.getUnitDescription()
                image.loadImageFromLink(baseUrlImageProducts+item.image)

            },
            { _, item ->
                tempProductId = item.id
                launchNewActivity(ActivityDetail::class.java)
            })
        binding.rvProducts.adapter = adapterProduct
        adapterProduct.data = data
    }


    override fun onClickedUpButton() {
        onBackPressed()
    }

    override fun refreshData() {
        viewModel.getProduct()
    }

    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.productDetail.value?.data?.id != null){
            tempProductId = viewModel.productDetail.value?.data?.id
        }
    }
}