package id.semisama.app.ui.auth.verification

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisama.app.R
import id.semisama.app.api.data.DataAuth
import id.semisama.app.base.BaseActivity
import id.semisama.app.databinding.ActivityVerificationBinding
import id.semisama.app.ui.ViewModelFactoryVerification
import id.semisama.app.utilily.*
import id.semisama.app.utilily.Constant.authTemps
import org.kodein.di.generic.instance

class ActivityVerification : BaseActivity(), ViewModelVerification.Bridge {

    private lateinit var binding: ActivityVerificationBinding
    private lateinit var viewModel: ViewModelVerification
    private val factory: ViewModelFactoryVerification by instance()

    var second = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
        initView()
        initPinView()
    }

    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification)
        viewModel = ViewModelProvider(this, factory).get(ViewModelVerification::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            verificationEmail.observe(owner, {
                second = 60
                createTimeCountDown()
            })
            user.observe(owner, {
                val token = tempAuth?.tokens
                cache.set(authTemps, DataAuth(it, token))
                onBackPressed()
            })
        }
        viewModel.code.observe(this, {
            viewModel.checkButton()
        })
    }

    private fun initView() {
        val email = SpannableString(
            " ${tempAuth?.user?.email}").also {
            it.recolor(this, R.color.colorPrimary)
        }
        binding.tvSendEmail.append(email)
        binding.tvResendCode.makeLinks(this,
            Pair(getString(R.string.labelResendCode), View.OnClickListener {
                checkRequest()
            })
        )
    }

    private fun checkRequest(){
        if (second == 0){
            if (viewModel.verificationEmail.value != null){
                if (viewModel.verificationEmail.value!!.remainingRetry == 0){
                    viewModel.requestVerification()
                }else{
                    toast(getString(R.string.labelTryToManyRequest))
                }
            }
            viewModel.requestVerification()
        }else{
            toast(getString(R.string.labelWaitAMinute))
        }
    }

    private fun initPinView(){
        binding.pinView.setAnimationEnable(true)
        binding.pinView.addTextChangedListener {
            viewModel.code.postValue(it.toString())
        }
    }

    private fun createTimeCountDown() {
        val thread: Thread = object : Thread() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            // update TextView here!
                            second -= 1
                            binding.tvCountDown.text = "$second ${getString(R.string.labelSecond)}"
                            if (second == 0) interrupt()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }

        thread.start()
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