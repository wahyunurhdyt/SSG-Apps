package id.semisama.app.api.repository

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import id.semisama.app.R
import id.semisama.app.api.data.Location
import id.semisama.app.api.manager.ManagerLocation
import id.semisama.app.api.util.SafeApiRequest
import id.semisama.app.base.Application
import id.semisama.app.utilily.*

class RepositoryLocation(
    context: Context,
    private val managerLocation: ManagerLocation
):  SafeApiRequest(context){

    fun fetchLocation() {
        managerLocation.getUpdatedLocation{ location -> onLocationReceived(location)}
    }

    private fun onLocationReceived(location: android.location.Location?) = Coroutines.io {
        location?.let { newLocation ->
            saveLocation(
                Location(
                    newLocation.latitude,
                    newLocation.longitude,
                    newLocation.altitude,
                    newLocation.accuracy,
                    newLocation.bearing,
                    newLocation.speed,
                )
            )
        }
    }

    private fun saveLocation(location: Location) {
        tempLocation = location
    }
}