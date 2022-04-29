package id.semisama.app.api.util

import java.io.IOException

class ApiException(val code: Int?, message: String?): IOException(message)
class ConnectionException(message: String): IOException(message)