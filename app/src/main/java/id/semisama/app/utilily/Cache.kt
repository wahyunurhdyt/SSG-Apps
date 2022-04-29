package id.semisama.app.utilily

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import id.semisama.app.api.data.DataAuth
import id.semisama.app.api.data.Region
import id.semisama.app.utilily.Constant.authTemps
import id.semisama.app.utilily.Constant.path
import id.semisama.app.utilily.Constant.regionTemps

// Custom cache
class Cache {
    private var sharePref: SharedPreferences? = null

    fun start(context: Context) {
        this.sharePref = context.getSharedPreferences(path, Context.MODE_PRIVATE)
    }

    fun destroy() { this.sharePref = null }

    fun <T> set(name: String, value: T) {
        if (name == authTemps){
            tempAuth = value as DataAuth
        }else if(name == regionTemps){
            val data = value as Region
            tempAddress = data.name!!
            tempRegion = data
        }
        this.sharePref?.let {
            with(it.edit()) {
                putString(name, Gson().toJson(value))
                Log.d("CACHE UTIL", name)
                apply()
            }
        }
    }

    fun delete(name: String) {
        if (name == authTemps){
            tempAuth = null
        }
        this.sharePref?.let {
            with(it.edit()) {
                putString(name, null)
                Log.d("CACHE UTIL", name)
                apply()
            }
        }
    }

    fun clear() {
        this.sharePref?.edit()?.clear()?.apply()
    }

    fun get(name: String): String? {
        if (sharePref != null) return sharePref!!.getString(name, null)
        return null
    }
}