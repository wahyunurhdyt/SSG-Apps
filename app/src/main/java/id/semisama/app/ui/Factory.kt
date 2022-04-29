package id.semisama.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.semisama.app.api.manager.ManagerRepository
import id.semisama.app.ui.auth.login.ViewModelLogin
import id.semisama.app.ui.auth.register.ViewModelRegister
import id.semisama.app.ui.auth.verification.ViewModelVerification
import id.semisama.app.ui.navigation.home.ViewModelHome
import id.semisama.app.ui.navigation.profile.ViewModelProfile
import id.semisama.app.ui.person.edit.ViewModelEdit
import id.semisama.app.ui.person.password.ViewModelPassword
import id.semisama.app.ui.product.category.ViewModelCategory
import id.semisama.app.ui.product.detail.ViewModelDetail
import id.semisama.app.ui.product.productssg.ViewModelProduct
import id.semisama.app.ui.product.search.ViewModelSearch

data class ViewModelFactoryHome(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelHome(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryCategory(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelCategory(
            managerRepository
        ) as T
    }
}

data class ViewModelFactorySearch(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelSearch(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryProduct(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelProduct(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryDetail(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelDetail(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryProfile(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelProfile(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryLogin(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelLogin(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryRegister(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelRegister(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryVerification(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelVerification(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryPassword(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelPassword(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryEdit(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelEdit(
            managerRepository
        ) as T
    }
}