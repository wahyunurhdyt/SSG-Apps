package id.semisama.app.ui.person.password

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisama.app.R
import id.semisama.app.api.data.DataAuth
import id.semisama.app.base.Application.Companion.getStringArray
import id.semisama.app.base.BaseActivity
import id.semisama.app.databinding.ActivityPasswordBinding
import id.semisama.app.ui.ViewModelFactoryPassword
import id.semisama.app.utilily.*
import id.semisama.app.utilily.Constant.authTemps
import id.semisama.app.utilily.Constant.currentActivity
import id.semisama.app.utilily.Constant.forgotPassword
import id.semisama.app.utilily.Constant.home
import id.semisama.app.utilily.Constant.login
import id.semisama.app.utilily.Constant.resetPassword
import org.kodein.di.generic.instance

class ActivityPassword : BaseActivity(), ViewModelPassword.Bridge {

    private lateinit var binding: ActivityPasswordBinding
    private lateinit var viewModel: ViewModelPassword
    private val factory: ViewModelFactoryPassword by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
    }

    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password)
        viewModel = ViewModelProvider(this, factory).get(ViewModelPassword::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (currentActivity == forgotPassword){
            binding.tvHeader.text = getStringArray(R.array.labelPassword)[4]
        }else{
            binding.tvHeader.text = viewModel.title.value
        }
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            user.observe(owner, {
//                if (it == null){
//                    cache.delete(authTemps)
//                    toast(getString(R.string.labelSuccessEditPassword))
//                    onBackPressed()
//                }else {
//                    val token = tempAuth?.tokens
//                    cache.set(authTemps, DataAuth(it, token))
//                    toast(getString(R.string.labelSuccessCreatePassword))
//                    onBackPressed()
//                }

                val token = tempAuth?.tokens
                cache.set(authTemps, DataAuth(it, token))
                toast(getString(R.string.labelSuccessCreatePassword))
                onBackPressed()
            })
        }
        viewModel.email.observe(this, {
            viewModel.checkButton()
        })
        viewModel.oldPassword.observe(this, {
            viewModel.checkButton()
        })
        viewModel.password.observe(this, {
            viewModel.checkButton()
        })
        viewModel.passwordConfirmation.observe(this, {
            viewModel.checkButton()
        })
    }

    override fun onClickedUpButton() {
        onBackPressed()
    }

    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (currentActivity == resetPassword){
            currentActivity = home
        }else if (currentActivity == forgotPassword){
            currentActivity = login
        }
    }
}