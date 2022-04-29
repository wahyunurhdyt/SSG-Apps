package id.semisama.app.api.data

import id.semisama.app.base.BaseResponse

data class ResponseAuth(
    var data: DataAuth
): BaseResponse()

data class ResponseVerificationEmail(
    var data: VerificationEmail
): BaseResponse()

data class ResponseUser(
    var data: User
): BaseResponse()

data class ResponseToken(
    var data: Tokens
): BaseResponse()

data class ResponseSupportedRegion(
    var data: Region
): BaseResponse()

data class ResponseRegencies(
    var data: MutableList<Region>
): BaseResponse()

data class ResponseBanners(
    var data: MutableList<Banner>
): BaseResponse()

data class ResponseProducts(
    var data: DataProduct
): BaseResponse()

data class ResponseProduct(
    var data: Product
): BaseResponse()

data class ResponseProductSRecommendations(
    var data: MutableList<Product>
): BaseResponse()

data class ResponseCategories(
    var data: MutableList<Category>
): BaseResponse()

data class ResponseRoute(
    var data: MutableList<Route>
): BaseResponse()