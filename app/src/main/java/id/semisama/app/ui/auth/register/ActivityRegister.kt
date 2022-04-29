package id.semisama.app.ui.auth.register

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisama.app.R
import id.semisama.app.base.BaseActivity
import id.semisama.app.databinding.ActivityRegisterBinding
import id.semisama.app.ui.ViewModelFactoryRegister
import id.semisama.app.utilily.*
import id.semisama.app.utilily.Constant.currentActivity
import id.semisama.app.utilily.Constant.registration
import org.kodein.di.generic.instance


class ActivityRegister : BaseActivity(), ViewModelRegister.Bridge {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: ViewModelRegister
    private val factory: ViewModelFactoryRegister by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
        initView()
    }

    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this, factory).get(ViewModelRegister::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            dataAuth.observe(owner, {
                currentActivity = registration
                cache.set(Constant.authTemps, it)
                finish()
            })
        }

        viewModel.name.observe(this, {
            viewModel.checkButton()
        })
        viewModel.phone.observe(this, {
            viewModel.checkButton()
        })
        viewModel.email.observe(this, {
            viewModel.checkButton()
        })
        viewModel.password.observe(this,  {
            viewModel.checkButton()
        })
        viewModel.passwordConfirmation.observe(this, {
            viewModel.checkButton()
        })
    }

    private fun initView() {
        binding.tvTermAndCondition.makeLinks(this,
            Pair(getString(R.string.labelTemrCondition), View.OnClickListener {
                toast(getString(R.string.labelTemrCondition))
            }),
            Pair(getString(R.string.labelPrivacyPolicy), View.OnClickListener {
                toast(getString(R.string.labelPrivacyPolicy))
            })
        )

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
}