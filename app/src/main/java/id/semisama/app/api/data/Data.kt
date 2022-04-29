package id.semisama.app.api.data

import com.google.gson.annotations.SerializedName

//Auth
data class DataAuth(
    var user: User?,
    var tokens: Tokens?
)

data class VerificationEmail(
    var requestId: String?,
    var remainingRetry: Int?
)

data class User(
    var image: String?,
    var role: String?,
    var isVerifiedEmail: Boolean?,
    var isVerifiedPhoneNumber: Boolean?,
    var isSetPassword: Boolean?,
    var provider: String?,
    var fullName: String?,
    var email: String?,
    var phoneNumber: String?,
    var id: String?
)

data class Tokens(
    var access: Token?,
    var refresh: Token?,
    var type: String?
)

data class Token(
    var token: String?,
    var expires: Long?
)

//Location
class Location(
    var latitude: Double? = null,
    var longitude: Double? = null,
    var altitude: Double? = null,
    var accuracy: Float? = null,
    var bearing: Float? = null,
    var speed: Float? = null
)


data class Route(
    var location: UserLocation?,
    var name: String?,
    var id: String?
)

data class UserLocation(
    var type: String?,
    var coordinates: ArrayList<Double>?
)

//Region
class Region(
    var name: String? = null,
    var id: String? = null,
    var isSupported: Boolean? = false
)

//Socket
class SocketRegion(
    var region: String? = null
)

class ListenSocket(
    var location: UserLocation
)


//Product
class DataProduct(
    var results: MutableList<Product>,
    var page: Int,
    var limit: Int,
    var totalPages: Int,
    var totalResults: Int
)
