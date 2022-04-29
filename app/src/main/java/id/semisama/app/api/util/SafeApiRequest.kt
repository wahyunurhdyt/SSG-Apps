package id.semisama.app.api.util

import android.content.Context
import android.os.Looper
import android.util.Log
import id.semisama.app.R
import id.semisama.app.base.BaseResponse.Companion.CODE_AUTH_TOKEN_INVALID
import id.semisama.app.utilily.Coroutines
import id.semisama.app.utilily.tempAuth
import id.semisama.app.utilily.toast
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


abstract class SafeApiRequest(val context: Context) {


    suspend fun <T: Any> apiRequest(call: suspend () -> Response<T>): T? {
        val response = call.invoke()
        return checkForError(response)
    }

    fun <T: Any> apiRequestBlocking(call: suspend () -> Response<T>): T? {
        val response = runBlocking { call.invoke() }
        return checkForError(response)
    }

    private fun <T> checkForError(response: Response<T>): T? {
        if (response.isSuccessful) {
            return response.body()
        } else {
            @Suppress("BlockingMethodInNonBlockingContext")
            val error = response.errorBody()?.string()
            var code = response.code()
            var message = ""

            if (code >= 500) {
                message = context.getString(R.string.labelServerDown)
            } else {
                error?.let {
                    try {
                        code = JSONObject(it)
                            .getInt("code")
                        message = JSONObject(it)
                            .getString("message")
                    } catch (e: JSONException) {
                        try {
                            message = it
                        } catch (e: Exception) {
                            code = response.code()
                            message = "Error: ${response.message()}"
                        }
                    }
                }
            }

            if (code == CODE_AUTH_TOKEN_INVALID) {
                Coroutines.io {
                    val token = tempAuth?.tokens?.access?.token
                    if (token != null) {
                        try {
                            Looper.prepare()
                            context.toast(context.getString(R.string.labelTokenInvalid))
                            //Logout
                        } catch (e: ApiException) {
                            Log.e("Logout SAR","${e.message}")
                        } catch (e: ConnectionException) {
                            Log.e("logout SAR","${e.message}")
                        }
                    }
                }
            }

            throw ApiException(code, message)
        }
    }

}