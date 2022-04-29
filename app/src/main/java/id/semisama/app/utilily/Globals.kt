package id.semisama.app.utilily

import id.semisama.app.R
import id.semisama.app.api.data.DataAuth
import id.semisama.app.api.data.Location
import id.semisama.app.api.data.Region
import id.semisama.app.base.Application

var tempAuth: DataAuth? = null
var tempLocation: Location? = null
var tempSelectedProduct: Boolean? = null
var tempAddress = Application.getString(R.string.labelFailedLoadingLocation)
var tempRegion: Region? = null
var tempCategoryId: String? = null
var tempProductId: String? = null
var tempCategoryName: String? = null
var tempFragmenHasLoadedData: Boolean = false
var tempTokenResetPassword = ""

var tempID: String = ""
var tempDeviceID: String = ""



