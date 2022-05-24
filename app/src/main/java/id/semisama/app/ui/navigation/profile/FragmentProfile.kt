package id.semisama.app.ui.navigation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import id.semisama.app.R
import id.semisama.app.adapter.Adapter
import id.semisama.app.api.data.ProfileMenu
import id.semisama.app.api.data.User
import id.semisama.app.api.manager.ManagerFirebase
import id.semisama.app.base.BaseFragment
import id.semisama.app.databinding.FragmentProfileBinding
import id.semisama.app.ui.ViewModelFactoryProfile
import id.semisama.app.ui.auth.login.ActivityLogin
import id.semisama.app.ui.auth.verification.ActivityVerification
import id.semisama.app.ui.person.password.ActivityPassword
import id.semisama.app.utilily.*
import id.semisama.app.utilily.Constant.authTemps
import id.semisama.app.utilily.Constant.baseUrlImageUser
import id.semisama.app.utilily.Constant.createPassword
import id.semisama.app.utilily.Constant.currentActivity
import id.semisama.app.utilily.Constant.editPassword
import id.semisama.app.utilily.Constant.getInitialName
import id.semisama.app.utilily.Constant.getProfileMenu
import id.semisama.app.utilily.Constant.login
import id.semisama.app.utilily.Constant.profile
import id.semisama.app.utilily.Constant.registration
import id.semisama.app.utilily.Constant.resetPassword
import org.kodein.di.generic.instance

class FragmentProfile : BaseFragment(), ViewModelProfile.Bridge {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ViewModelProfile
    private val factory: ViewModelFactoryProfile by instance()
    private lateinit var adapter: Adapter<ProfileMenu>

    override fun onStart() {
        super.onStart()
        if (tempAuth == null) {
            when (currentActivity) {
                login -> {
                    requireActivity().onBackPressed()
                }
                resetPassword -> {
                    requireContext().launchNewActivity(ActivityPassword::class.java)
                }
                else -> {
                    requireContext().launchNewActivity(ActivityLogin::class.java)
                }
            }
        } else {
            binding.loadingView.visibility = View.GONE
            viewModel.user.postValue(tempAuth?.user!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )
        viewModel = ViewModelProvider(this, factory).get(ViewModelProfile::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMenu()
        initObserver()
    }

    private fun showMenu() {
        adapter = Adapter(R.layout.item_menu_profile, mutableListOf(),
            { itemView, item ->
                val title = itemView.findViewById<TextView>(R.id.tvTitleMenu)
                val icon = itemView.findViewById<ImageView>(R.id.iconMenu)
                val required = itemView.findViewById<TextView>(R.id.tvRequiredAction)
                title.text = item.title
                icon.setImageResource(item.icon)
                if (!item.isRequired) {
                    required.text = ""
                }
            },
            { _, item ->
                when (item.title) {
                    resources.getStringArray(R.array.labelProfileMenu)[0] -> {
                        currentActivity = createPassword
                        requireContext().launchNewActivity(ActivityPassword::class.java)
                    }
                    resources.getStringArray(R.array.labelProfileMenu)[1] -> {
                        currentActivity = editPassword
                        requireContext().launchNewActivity(ActivityPassword::class.java)
                    }
                    resources.getStringArray(R.array.labelProfileMenu)[2] -> {
                        requireContext().launchNewActivity(ActivityVerification::class.java)
                    }
                    else -> {
                        requireContext().toast(item.title)
                    }
                }
            })
        binding.rvMenu.adapter = adapter
    }

    private fun initObserver() {
        viewModel.apply {
            user.observe(viewLifecycleOwner) {
                city.postValue(tempAddress)
                if (it.image.isNullOrEmpty()) {
                    textInitial.postValue(getInitialName())
                } else {
                    textInitial.value = ""
                    binding.ivProfilePic.loadImageFromLink(baseUrlImageUser + it.image)
                }
                checkUser(it)
            }
        }
    }

    private fun checkUser(user: User) {
        adapter.data = getProfileMenu()
        if (user.isSetPassword!!) {
            adapter.deletePosition(0)
        } else {
            adapter.deletePosition(1)
        }
        if (user.isVerifiedEmail!!) {
            adapter.deletePosition(1)
        } else {
            if (currentActivity == registration) {
                currentActivity = profile
                requireContext().launchNewActivity(ActivityVerification::class.java)
            }
        }
        currentActivity = profile
    }

    override fun logout() {
        if (tempAuth?.user?.provider == "google") {
            FirebaseAuth.getInstance().signOut()
        }
        ManagerFirebase.deleteToken()
        cache.delete(authTemps)
        requireContext().toast(getString(R.string.labelSuccessLogout))
        onStart()
    }

    override fun showLogoutDialog() {
        customDialog(requireActivity(), R.layout.dialog_logout
        ) { itemView, dialog  ->
            val tvYes = itemView.findViewById<TextView>(R.id.tvYes)
            val tvNo = itemView.findViewById<TextView>(R.id.tvNo)

            tvYes.setOnClickListener {
                viewModel.logoutAccount()
                dialog.dismiss()
            }

            tvNo.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }
}