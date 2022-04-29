package id.semisama.app.ui.navigation

import android.os.Bundle
import android.os.Handler
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import id.semisama.app.BuildConfig
import id.semisama.app.R
import id.semisama.app.base.BaseActivity
import id.semisama.app.utilily.Constant.currentActivity
import id.semisama.app.utilily.Constant.resetPassword
import id.semisama.app.utilily.tempTokenResetPassword
import id.semisama.app.utilily.toast
import kotlinx.android.synthetic.main.activity_navigation.*

class ActivityNavigation : BaseActivity() {

    private lateinit var navController: NavController
    private var twice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        initBottomNavigation()
        fetchLocation()
    }

    private fun initBottomNavigation() {
        navController = Navigation.findNavController(this, R.id.fMain)
        bnvMain.setupWithNavController(navController)
        bnvMain.setOnNavigationItemReselectedListener {}

        val uri = intent.data
        if (uri != null){
            currentActivity = resetPassword
            tempTokenResetPassword = uri.toString().removeRange(0, BuildConfig.PATH_DEEPLINK_FORGOT_PASSWORD.length)
            bnvMain.selectedItemId = R.id.menuProfile
        }
    }

    override fun onBackPressed() {
        when (fMain.childFragmentManager.backStackEntryCount) {
            0 -> {
                if (twice) {
                    super.onBackPressed()
                    return
                }
                twice = true
                toast(resources.getString(R.string.labelPressOneMore))
                Handler().postDelayed({ twice = false }, 2000)
            }
            else -> super.onBackPressed()
        }
    }
}