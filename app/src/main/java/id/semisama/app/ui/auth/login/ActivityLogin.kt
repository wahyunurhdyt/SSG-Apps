package id.semisama.app.ui.auth.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import id.semisama.app.R
import id.semisama.app.base.BaseActivity
import id.semisama.app.databinding.ActivityLoginBinding
import id.semisama.app.ui.ViewModelFactoryLogin
import id.semisama.app.utilily.*
import id.semisama.app.utilily.Constant.authTemps
import id.semisama.app.utilily.Constant.currentActivity
import id.semisama.app.utilily.Constant.login
import id.semisama.app.utilily.Constant.registration
import org.kodein.di.generic.instance

class ActivityLogin : BaseActivity(), ViewModelLogin.Bridge {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: ViewModelLogin
    private val factory: ViewModelFactoryLogin by instance()
    private var googleSignInClient: GoogleSignInClient? = null


    private val startForLoginGoogleResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        if (resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                viewModel.loginSocialMedia(account?.idToken, "google")
            } catch (e: ApiException) {
                Log.e("GOOGLE_SIGN_IN","failed code=" + e.statusCode)
                //FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentActivity = login
        initBinding()
        initObserver()
        initView()
        initGoogleSignIn()
    }


    private fun initBinding(){

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this, factory).get(ViewModelLogin::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            dataAuth.observe(owner, {
                cache.set(authTemps, it)
                viewModel.requestFcmToken()
            })
        }
        viewModel.email.observe(this, {
            viewModel.checkButton()
        })
        viewModel.password.observe(this,  {
            viewModel.checkButton()
        })
    }


    private fun initView() {
//        binding.tvTryApp.makeLinks(this,
//            Pair(getString(R.string.labelLoginAsGuest), View.OnClickListener {
//                toast(getString(R.string.labelLoginAsGuest))
//            }),
//        )

        binding.loginGoogle.setOnClickListener {
            googleSignInClient?.signOut()
            val intent = googleSignInClient?.signInIntent
            startForLoginGoogleResult.launch(intent)
        }
    }

    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(getString(R.string.web_client_id))
            .requestServerAuthCode(getString(R.string.web_client_id))
            .requestScopes(Scope(Scopes.PROFILE), Scope(Scopes.EMAIL))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun next() {
        onBackPressed()
    }


    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }

    override fun onResume() {
        super.onResume()
        if (currentActivity == registration){
            onBackPressed()
        }
    }
}