package id.semisama.app.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import id.semisama.app.api.manager.ManagerLocation
import id.semisama.app.api.manager.ManagerNetwork
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.api.repository.*
import id.semisama.app.api.service.ServiceApi
import id.semisama.app.api.service.ServiceAuth
import id.semisama.app.api.service.ServicePerson
import id.semisama.app.api.service.ServiceSrv
import id.semisama.app.api.util.Client
import id.semisama.app.api.util.InterceptorHeader
import id.semisama.app.api.util.InterceptorNetworkConnection
import id.semisama.app.ui.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class Application: Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@Application))

        /*
        * PROJECT WIDE
        */
        bind() from singleton {
            InterceptorHeader()
        }
        bind() from singleton {
            InterceptorNetworkConnection(
                instance()
            )
        }
        bind() from singleton {
            ChuckerInterceptor.Builder(instance()).build()
        }
        bind() from singleton {
            Client(
                instance(),
                instance(),
                instance()
            )
        }

        /*
        * SERVICE
        */
        bind() from singleton {
            ServiceAuth(
                instance()
            )
        }
        bind() from singleton {
            ServicePerson(
                instance()
            )
        }
        bind() from singleton {
            ServiceApi(
                instance()
            )
        }
        bind() from singleton {
            ServiceSrv(
                instance()
            )
        }

        /*
        * MANAGER
        */
        bind() from singleton {
            ManagerNetwork(
                instance()
            )
        }
        bind() from singleton {
            ManagerRepository(
                instance()
            )
        }
        bind() from singleton {
            ManagerLocation(
                instance()
            )
        }

        /*
        * REPOSITORY
        */
        bind() from singleton {
            RepositoryAuth(
                instance(),
                instance()
            )
        }
        bind() from singleton {
            RepositoryPerson(
                instance(),
                instance()
            )
        }
        bind() from singleton {
            RepositoryLocation(
                instance(),
                instance()
            )
        }
        bind() from singleton {
            RepositoryRegion(
                instance(),
                instance()
            )
        }
        bind() from singleton {
            RepositoryBanner(
                instance(),
                instance()
            )
        }
        bind() from singleton {
            RepositoryProduct(
                instance(),
                instance()
            )
        }

        /*
        * Factory
        */
        bind() from provider {
            ViewModelFactoryHome(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactoryCategory(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactoryProduct(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactoryDetail(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactorySearch(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactoryLogin(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactoryProfile(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactoryRegister(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactoryVerification(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactoryPassword(
                instance()
            )
        }
        bind() from provider {
            ViewModelFactoryEdit(
                instance()
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getContext(): Context {
            return context
        }

        fun getQuantity(resId: Int, quantity: Int?, vararg format: Int?): String {
            return quantity?.let {
                context.resources.getQuantityString(resId, it, *format)
            } ?: "Wrong Format"
        }

        fun getString(resId: Int): String {
            return context.getString(resId)
        }

        fun getString(resId: Int, vararg format: String?): String {
            return context.getString(resId, *format)
        }

        fun getString(resId: Int, vararg format: Int?): String {
            return context.getString(resId, *format)
        }

        fun getString(resId: Int, vararg format: Long?): String {
            return context.getString(resId, *format)
        }

        fun getStringArray(resId: Int): Array<String> {
            return context.resources.getStringArray(resId)
        }
    }

}