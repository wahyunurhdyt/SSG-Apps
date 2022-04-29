package id.semisama.app.api.data

data class RequestSocmed(
    var token: String?,
    var authType: String?
)

data class RequestLogin(
    var email: String?,
    var password: String?
)

data class RequestVerifyEmail(
    var requestId: String?,
    var code: Int?
)

data class RequestRegister(
    var fullName: String?,
    var email: String?,
    var phoneNumber: String?,
    var password: String?
)

data class RequestRefreshToken(
    var refreshToken: String?
)

data class RequestPassword(
    var password: String?
)

data class RequestEmail(
    var email: String?
)

data class RequestChangePassword(
    var oldPassword: String?,
    var newPassword: String?
)

data class RequestPatch(
    var fullName: String?,
    var email: String?,
    var phoneNumber: String?,
    var image: String?
)

data class RequestSubscribeFcm(
    var token: String?,
    var device: String?
)

data class RequestLocation(
    var region: String?,
    var location: UserLocation?
)
