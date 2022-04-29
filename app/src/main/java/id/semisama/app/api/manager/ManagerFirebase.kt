package id.semisama.app.api.manager

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import id.semisama.app.BuildConfig
import id.semisama.app.api.repository.RepositoryPerson
import id.semisama.app.api.util.ApiException
import id.semisama.app.api.util.ConnectionException
import id.semisama.app.utilily.Coroutines
import id.semisama.app.utilily.tempAuth
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ManagerFirebase: FirebaseMessagingService(), KodeinAware {
    override val kodein by kodein()

    private val repositoryPerson: RepositoryPerson by instance()

    companion object {
        private const val TOPIC_ALL_DEVICES = BuildConfig.TOPIC_ALL_DEVICES
        private const val TOPIC_ANDROID_DEVICES = BuildConfig.TOPIC_ANDROID_DEVICES

        fun subscribeToAllDevices() {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ALL_DEVICES)
        }

        fun unsubscribeFromAllDevices() {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ALL_DEVICES)
        }

        fun subscribeToAndroidDevices() {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ANDROID_DEVICES)
        }

        fun unsubscribeFromAndroidDevices() {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ANDROID_DEVICES)
        }

        fun deleteToken() {
            FirebaseMessaging.getInstance().deleteToken()
        }
    }

    override fun onNewToken(token: String) {
        Log.d("onNewToken", "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(fcmToken: String) {
        Coroutines.main {
            if (tempAuth != null) {
                try {
                    repositoryPerson.subscribeFcm(fcmToken)
                } catch (e: ApiException) {
                    Log.d("sendTokenToServer", "${e.message}")
                } catch (e: ConnectionException) {
                    Log.d("sendTokenToServer", "${e.message}")
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        //check messages
        Log.d("onMessageReceived", "From: ${remoteMessage.from}")

        // Check if message contains a data payload, you can get the payload here and add as an intent to your activity
        remoteMessage.data.let {
            Log.d("onMessageReceived", "Message data: " + remoteMessage.data)
            //get the data
        }

        // Check if message contains a notification payload, send notification
        remoteMessage.notification?.let {
            Log.d("onMessageReceived", "Message Title: ${it.title}")
            Log.d("onMessageReceived", "Message Body: ${it.body}")
            Log.d("onMessageReceived", "Message Image: ${it.imageUrl}")
        }
    }
}