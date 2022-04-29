package id.semisama.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import com.google.firebase.messaging.FirebaseMessaging
import id.semisama.app.api.manager.ManagerFirebase
import id.semisama.app.base.BaseActivity
import id.semisama.app.ui.navigation.ActivityNavigation
import id.semisama.app.utilily.*
import id.semisama.app.utilily.Constant.authTemps
import id.semisama.app.utilily.Constant.boardingTemps
import id.semisama.app.utilily.Constant.firebaseTemps
import id.semisama.app.utilily.Constant.getAuth
import id.semisama.app.utilily.Constant.getRegion
import id.semisama.app.utilily.Constant.regionTemps

@SuppressLint("CustomSplashScreen")
class ActivitySplashScreen : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val auth = cache.get(authTemps)
        val region = cache.get(regionTemps)
        if (!auth.isNullOrEmpty()){
            val data = getAuth(auth)
            tempAuth = data
        }
        if (!region.isNullOrEmpty()){
            val data = getRegion(region)
            tempRegion = data
            tempAddress = data.name!!
        }

        if (cache.get(firebaseTemps) == null){
            ManagerFirebase.subscribeToAllDevices()
            ManagerFirebase.subscribeToAndroidDevices()
            cache.set(firebaseTemps, true)
        }

        Handler().postDelayed({
            startNextActivity()
        }, 1000)
    }

    private fun startNextActivity() {
        val boarding = cache.get(boardingTemps)
        if (isFinishing) return
        if (boarding.isNullOrEmpty()) {
            launchNewActivity(ActivityBoarding::class.java)
            finish()
        } else {
            if (haveLocationPermissions()) {
                fetchLocation()
            }
            launchNewActivity(ActivityNavigation::class.java)
            finish()
        }
    }
}