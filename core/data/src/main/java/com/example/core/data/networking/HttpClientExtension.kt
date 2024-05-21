package com.example.core.data.networking

import com.example.core.data.BuildConfig
import com.example.core.domain.util.DataError
import io.ktor.client.statement.HttpResponse
import com.example.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

typealias ConstructedRoute = String
fun constructRoute(route: String): ConstructedRoute {
    return when {
        route.contains(BuildConfig.BASE_URL) -> route
        route.startsWith("/") -> BuildConfig.BASE_URL + route
        else -> BuildConfig.BASE_URL + "/$route"
    }
}

suspend inline fun <reified T> handleHttpResponse(response: HttpResponse): Result<T, DataError.Network> {
    return when (response.status.value) {
        in 200..299 -> Result.Success(response.body<T>())
        401 -> Result.Failure(DataError.Network.UNAUTHORIZED)
        408 -> Result.Failure(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Failure(DataError.Network.CONFLICT)
        413 -> Result.Failure(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Failure(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Failure(DataError.Network.SERVER_ERROR)
        else -> Result.Failure(DataError.Network.UNKNOWN)
    }
}

suspend inline fun <reified T> safeHttpCall(execute: () -> HttpResponse): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        e.printStackTrace()
        return Result.Failure(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return Result.Failure(DataError.Network.SERIALIZATION_ERROR)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        return Result.Failure(DataError.Network.UNKNOWN)
    }
    return handleHttpResponse(response)
}

// region HTTP methods
suspend inline fun <reified Response: Any> HttpClient.get(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> = safeHttpCall {
    get {
        url(constructRoute(route))
        queryParameters.forEach {  (key, value) ->
            parameter(key, value)
        }
    }
}

suspend inline fun <reified Request, reified Response: Any> HttpClient.post(
    route: String,
    body: Request
): Result<Response, DataError.Network> = safeHttpCall {
    post {
        url(constructRoute(route))
        setBody(body)
    }
}

suspend inline fun <reified Response: Any> HttpClient.delete(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> = safeHttpCall {
    delete {
        url(constructRoute(route))
        queryParameters.forEach {  (key, value) ->
            parameter(key, value)
        }
    }
}
// endregion