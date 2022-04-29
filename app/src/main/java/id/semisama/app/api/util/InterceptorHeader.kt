package id.semisama.app.api.util

import id.semisama.app.BuildConfig
import id.semisama.app.utilily.tempAuth
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class InterceptorHeader: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        request = request.newBuilder()
            .addHeader("X-Api-Key", BuildConfig.API_KEY)
            .addHeader("Authorization", "Bearer ${tempAuth?.tokens?.access?.token}")
            .build()

        return chain.proceed(request)
    }

}