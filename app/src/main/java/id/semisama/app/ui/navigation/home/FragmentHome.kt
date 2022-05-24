package id.semisama.app.ui.navigation.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.sip.SipAudioCall
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import id.semisama.app.BuildConfig
import id.semisama.app.R
import id.semisama.app.adapter.Adapter
import id.semisama.app.adapter.ViewPagerAdapter
import id.semisama.app.api.data.*
import id.semisama.app.base.Application
import id.semisama.app.base.BaseFragment
import id.semisama.app.databinding.FragmentHomeBinding
import id.semisama.app.ui.ViewModelFactoryHome
import id.semisama.app.ui.product.category.FragmentCategory
import id.semisama.app.ui.product.detail.ActivityDetail
import id.semisama.app.ui.product.productssg.FragmentProduct
import id.semisama.app.utilily.*
import id.semisama.app.utilily.Constant.baseUrlImageBanner
import id.semisama.app.utilily.Constant.baseUrlImageCategories
import id.semisama.app.utilily.Constant.baseUrlImageProducts
import id.semisama.app.utilily.Constant.currentActivity
import id.semisama.app.utilily.Constant.getIndonesiaLatLng
import id.semisama.app.utilily.Constant.home
import id.semisama.app.utilily.Constant.regionTemps
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import org.kodein.di.generic.instance


class FragmentHome : BaseFragment(), OnMapReadyCallback, ViewModelHome.Bridge {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: ViewModelHome
    private val factory: ViewModelFactoryHome by instance()
    private lateinit var adapterProduct: Adapter<Product>
    private lateinit var adapterRecommend: Adapter<Product>
    private lateinit var adapterCategory: Adapter<Category>
    private lateinit var adapterBanner: ViewPagerAdapter<Banner>
    private var currentBanner = 0

    private lateinit var socket: Socket

    private var map: GoogleMap? = null

    override fun onResume() {
        super.onResume()
        fetchLocation()
        currentActivity = home
        if (::binding.isInitialized) {
            binding.mvMain.onResume()
            checkLocation("onResume")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        viewModel = ViewModelProvider(this, factory).get(ViewModelHome::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.mvMain.onCreate(savedInstanceState)
        binding.mvMain.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initRefresh()
        if (!tempFragmenHasLoadedData) {
            loadData()
            tempFragmenHasLoadedData = true
        }
    }

    private fun loadData() {
        viewModel.getBanners()
        viewModel.getCategories()
    }

    private fun initRefresh() {
        binding.srlLayout.setOnRefreshListener {
            loadData()
            binding.srlLayout.isRefreshing = false
        }
    }

    private fun initObserver() {
        viewModel.apply {
            banners.observe(viewLifecycleOwner) {
                showBanner(it)
                updateBannerIndicator(it)
            }
            product.observe(viewLifecycleOwner) {
                showProducts(it.data.results)
            }
            productRecommend.observe(viewLifecycleOwner) {
                showProductRecommend(it.data.results)
            }
            categories.observe(viewLifecycleOwner) {
                showCategory(it)
            }
            driverLocation.observe(viewLifecycleOwner) {
                if (viewModel.routes.value != null) {
                    loadRoute(it, viewModel.routes.value?.data!!)
                }
            }
            routes.observe(viewLifecycleOwner) {
                loadRoute(viewModel.driverLocation.value!!, it.data)
            }
        }
    }

    private fun showBanner(data: MutableList<Banner>) {
        this.adapterBanner = ViewPagerAdapter(
            requireContext(),
            R.layout.item_banner,
            listOf()
        ) { itemView, item, _ ->
            val image = itemView.findViewById<ImageView>(R.id.ivBanner)
            image.loadImageFromLink(baseUrlImageBanner + item.image)
        }
        binding.vpBanner.adapter = this.adapterBanner
        binding.vpBanner.apply {
            adapter = adapterBanner
            setPadding(
                context.getPx(0),
                context.getPx(0),
                context.getPx(0),
                context.getPx(0))
            clipToPadding = false
            pageMargin = context.getPx(0)
        }
        binding.vpBanner.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                currentBanner = position
                updateBannerIndicator(data)
            }
        })
        this.adapterBanner.data = data
    }

    private fun updateBannerIndicator(data: MutableList<Banner>) {
        if (binding.llContainerBannerIndicator.childCount > 0) {
            binding.llContainerBannerIndicator.removeAllViews()
        }

        for (i in data.indices) {
            val ivIndicator = ImageView(context)
            if (i == currentBanner) {
                ivIndicator.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(),
                        R.drawable.banner_active))
            } else {
                ivIndicator.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(),
                        R.drawable.banner_inactive))
            }

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(requireContext().getPx(1), 0, requireContext().getPx(1), 0)
            binding.llContainerBannerIndicator.addView(ivIndicator, layoutParams)
        }
    }

    private fun showCategory(data: MutableList<Category>) {
        adapterCategory = Adapter(R.layout.item_category, mutableListOf(),
            { itemView, item ->
                val name = itemView.findViewById<TextView>(R.id.tvName)
                val image = itemView.findViewById<ImageView>(R.id.ivCategory)
                name.text = item.name
                image.loadImageFromLink(baseUrlImageCategories + item.image)

            },
            { _, item ->
                tempCategoryId = item.id
                tempCategoryName = item.name
                val fragment = FragmentCategory()
                val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fMain, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            })
        binding.rvCategory.adapter = adapterCategory
        adapterCategory.data = data
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
                context?.launchNewActivity(ActivityDetail::class.java)
            })
        binding.rvProducts.adapter = adapterProduct
        adapterProduct.data = data
    }

    @SuppressLint("SetTextI18n")
    private fun showProductRecommend(data: MutableList<Product>) {
        adapterRecommend = Adapter(R.layout.item_product, mutableListOf(),
            { itemView, item ->
                val name = itemView.findViewById<TextView>(R.id.tvName)
                val price = itemView.findViewById<TextView>(R.id.tvPrice)
                val unit = itemView.findViewById<TextView>(R.id.tvUnit)
                val image = itemView.findViewById<ImageView>(R.id.ivProduct)
                name.text = item.name
                price.text = item.getPriceFormat()
                unit.text = item.getUnitDescription()
                image.loadImageFromLink(baseUrlImageProducts + item.image)

            },
            { _, item ->
                tempProductId = item.id
                context?.launchNewActivity(ActivityDetail::class.java)
            })
        binding.rvRecommends.adapter = adapterRecommend
        adapterRecommend.data = data
    }

    private fun checkLocation(from: String) {
        Log.e("checkLocation", "from = $from")
        val region = cache.get(regionTemps)
        if (region.isNullOrEmpty()) {
            Log.e("checkLocation", "0")
            viewModel.loadingCheckLocationVisibility.postValue(View.VISIBLE)
            if (tempAddress == Application.getString(R.string.labelFailedLoadingLocation)) {
                Log.e("checkLocation", "1")
                viewModel.getRegencies()
            } else {
                Log.e("checkLocation", "2")
                viewModel.checkLocation()
            }
        } else {
            Log.e("checkLocation", "3")
            if (stateCheckLocation) {
                Log.e("checkLocation", "4")
                Log.e("checkLocation", "tempRegion?.isSupported = ${tempRegion?.isSupported}")
                Log.e("checkLocation", "tempRegion != null => ${tempRegion != null}")
                Log.e("checkLocation", "tempAuth != null => ${tempAuth != null}")
                if (tempRegion != null) receivedMessageFromSocket().also { Log.e("checkLocation", "5") }
                if (tempRegion?.isSupported == true && tempAuth != null) {
                    Log.e("checkLocation", "6")
                    viewModel.requestLocationVisibility.postValue(View.VISIBLE)
                    viewModel.getRoutes()
                } else {
                    Log.e("checkLocation", "7")
                    viewModel.requestLocationVisibility.postValue(View.GONE)
                }
                viewModel.loadingCheckLocationVisibility.postValue(View.GONE)
                viewModel.getProductSelected()
                viewModel.getProductRecommend()
            } else {
                Log.e("checkLocation", "8")
                stateCheckLocation = true
                viewModel.checkLocation()
            }
        }
    }

    private fun receivedMessageFromSocket() {
        socket = IO.socket(
            BuildConfig.BASE_URL_SOCKET,
            IO.Options().apply {
                reconnection = true
                forceNew = true
                extraHeaders = mutableMapOf(Pair("X-Api-Key", listOf(BuildConfig.API_KEY)))
            }
        )

        val region = SocketRegion(tempRegion?.id)

        socket.on("region_${tempRegion?.id}", onNewMessage).emit("receiveDriverLocation", Gson().toJson(region))
        socket.connect()
            .on(Socket.EVENT_CONNECT) {
                Log.d("SOCKET_STATUS", "Socket Connected")
            }
            .on(Socket.EVENT_DISCONNECT) {
                Log.d("SOCKET_STATUS", "Not Connect")
            }
            .on(Socket.EVENT_CONNECT_ERROR) {
                Log.d("SOCKET_STATUS", "Error Connect = ${Gson().toJson(it)}")
            }

    }

    private val onNewMessage: Emitter.Listener = object : SipAudioCall.Listener(),
        Emitter.Listener {
        override fun call(vararg args: Any) {
            requireActivity().runOnUiThread(Runnable {
                val data = args[0]
                try {
                    val json = JSONObject(data.toString())
                    val message = Gson().fromJson(json.toString(), ListenSocket::class.java)
                    val lat = message.location.coordinates!![1]
                    val lng = message.location.coordinates!![0]
                    val driverLocation = LatLng(lat, lng)
                    if (viewModel.routes.value != null) {
                        viewModel.driverLocation.postValue(driverLocation)
                    }
                    println("location driver : $lat, $lng")
                } catch (e: JSONException) {
                    return@Runnable
                }


            })
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }

    private fun loadRoute(driverLocation: LatLng, data: MutableList<Route>) {
        map?.clear()

        //DriverMarker
        val driverMarker = ((ContextCompat.getDrawable(
            Application.getContext(),
            R.drawable.driver
        )) as BitmapDrawable).bitmap
        val driverIcon = Bitmap.createScaledBitmap(driverMarker, 96, 96, false)
        map?.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(driverIcon))
                .position(driverLocation)
                .title("Posisi Driver")
        )

        val builder = LatLngBounds.Builder()
        builder.include(driverLocation)
        val size = data.size

        val bitmap = ((ContextCompat.getDrawable(
            Application.getContext(),
            R.drawable.marker
        )) as BitmapDrawable).bitmap

        for (i in 0 until size) {
            val lat = data[i].location?.coordinates!![1]
            val lng = data[i].location?.coordinates!![0]
            val position = LatLng(lat, lng)
            val name = data[i].name
            val markerIcon = Bitmap.createScaledBitmap(bitmap, 72, 72, false)

            if (size > 1 && i < (size-1)) {
                addPolyline(position, LatLng(data[i+1].location?.coordinates!![1], data[i+1].location?.coordinates!![0]))
            }

            builder.include(position)
            map?.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon))
                    .snippet((i+1).toString())
                    .position(position)
                    .title(name)
            )
        }
        when (size) {
            0 -> {
                val location = getIndonesiaLatLng()
                map?.addMarker(
                    MarkerOptions()
                        .position(location)
                )
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
            }
            1 -> {
                val lat = data[0].location?.coordinates!![1]
                val lng = data[0].location?.coordinates!![0]
                val location = LatLng(lat, lng)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
            }
            else -> {
                val bounds = builder.build()
                val padding = Application.getContext().getPx(24) // offset from edges of the map in pixels
                val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                map?.moveCamera(cu)
            }
        }

    }

    private fun addPolyline(start: LatLng, end: LatLng) {
        map?.addPolyline(PolylineOptions().add(start, end).width(5f).color(Color.BLACK).geodesic(true))
    }

    override fun setLocation(isSupported: Boolean, data: Region?, regencies: MutableList<Region>?) {
        customDialog(requireActivity(), R.layout.dialog_set_location
        ) { itemView, dialog  ->
            dialog.setCanceledOnTouchOutside(false)
            val appName = SpannableString(
                " ${getString(R.string.apps_name)}.").also {
                it.recolorAndBold(requireContext(), R.color.black_70)
            }
            val tvInfo = itemView.findViewById<TextView>(R.id.tvInfo)
            val tvChooseLocation = itemView.findViewById<TextView>(R.id.tvChooseLocation)
            val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
            val textInputLayout = itemView.findViewById<TextInputLayout>(R.id.textInputLayout)
            val chooseLocation = itemView.findViewById<AutoCompleteTextView>(R.id.chooseLocation)
            val btnConfirm = itemView.findViewById<Button>(R.id.btnConfirm)

            if (isSupported) {
                tvInfo.text = Application.getStringArray(R.array.labelInfoAvailabelLocation)[0]
                tvInfo.append(appName)
                tvChooseLocation.text = Application.getStringArray(R.array.labelInfoAvailabelLocation)[1]
                tvLocation.text = data?.name
                textInputLayout.visibility = View.GONE
                btnConfirm.isEnabled = true
                cache.set(regionTemps, Region(data?.name, data?.id, isSupported))
                if (tempAuth != null) {
                    viewModel.requestLocationVisibility.postValue(View.VISIBLE)
                } else {
                    viewModel.requestLocationVisibility.postValue(View.GONE)
                }
            } else {
                tvInfo.text = Application.getStringArray(R.array.labelInfoUnavailabelLocation)[0]
                tvInfo.append(appName)
                tvChooseLocation.text = Application.getStringArray(R.array.labelInfoUnavailabelLocation)[1]
                tvLocation.visibility = View.GONE

                val location = ArrayList<String?>()
                for (i in 0 until regencies!!.size) {
                    location.add(regencies[i].name)
                }

                val adapter: ArrayAdapter<String?> = ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown,
                    location
                )

                chooseLocation.setAdapter(adapter)
                chooseLocation.threshold = 1
                chooseLocation.onItemClickListener = AdapterView.OnItemClickListener {
                        _, _, _, _ ->
                    val d = regencies.find {
                        it.name == chooseLocation.text.toString()
                    }
                    cache.set(regionTemps, Region(d?.name, d?.id, isSupported))
                    btnConfirm.isEnabled = true
                }

                chooseLocation.doOnTextChanged { _, _, _, _ ->
                    btnConfirm.isEnabled = false
                }

            }
            btnConfirm.setOnClickListener {
                viewModel.city.postValue(tempAddress)
                viewModel.getProductSelected()
                viewModel.getProductRecommend()

                viewModel.getRoutes()
                receivedMessageFromSocket()
                dialog.dismiss()
            }
        }
    }

    override fun changeLocation(regencies: MutableList<Region>?) {
        customDialog(requireActivity(), R.layout.dialog_change_location
        ) { itemView, dialog  ->
            val chooseLocation = itemView.findViewById<AutoCompleteTextView>(R.id.changeLocation)
            val btnConfirm = itemView.findViewById<Button>(R.id.btnConfirmChange)
            val tvUseRealLocation = itemView.findViewById<TextView>(R.id.tvUseRealLocation)

            tvUseRealLocation.setOnClickListener {
                fetchLocation()
                viewModel.checkLocation()
            }

            val location = ArrayList<String?>()
            for (i in 0 until regencies!!.size) {
                location.add(regencies[i].name)
            }

            val adapter: ArrayAdapter<String?> = ArrayAdapter(
                requireContext(),
                R.layout.dropdown,
                location
            )

            chooseLocation.setAdapter(adapter)
            chooseLocation.threshold = 1
            chooseLocation.onItemClickListener = AdapterView.OnItemClickListener{
                    _, _, _, _ ->
                val data = regencies.find {
                    it.name == chooseLocation.text.toString()
                }
                cache.set(regionTemps, Region(data?.name, data?.id, false))
                btnConfirm.isEnabled = true
            }

            chooseLocation.doOnTextChanged { _, _, _, _ ->
                btnConfirm.isEnabled = false
            }

            btnConfirm.setOnClickListener {
                viewModel.city.postValue(tempAddress)
                checkLocation("btnConfirm")
                dialog.dismiss()
            }
        }
    }

    override fun viewAllProduct(isSelected: Boolean?) {
        tempSelectedProduct = isSelected
        val fragment = FragmentProduct()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fMain, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun refreshData() {
        loadData()
    }


    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }
}