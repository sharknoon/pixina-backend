package api.v1.cart

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

suspend fun add(context: PipelineContext<Unit, ApplicationCall>) {
    val id = context.call.parameters["id"]
    if (id.isNullOrBlank()) {
        context.call.respond(HttpStatusCode.BadRequest, "missing cart id")
        return
    }
    val articles: List<Article> = try {
        context.call.receive()
    } catch (e: Exception) {
        context.call.respond(HttpStatusCode.BadRequest, "malformed request: $e")
        return
    }

    if (articles.isEmpty()) {
        context.call.respond(HttpStatusCode.OK)
        return
    }

    callPixelhobbyShop(id, articles)
        .onSuccess {
            context.call.respond(HttpStatusCode.OK)
        }
        .onFailure {
            context.call.respond(HttpStatusCode.InternalServerError, it.toString())
        }
}

private val jsonConfig = Json { encodeDefaults = true }

private fun callPixelhobbyShop(cartId: String, articles: List<Article>): Result<Unit> {
    return runBlocking {
        val client = HttpClient(CIO)
        articles.forEach {
            val json = jsonConfig.encodeToString(Article.serializer(), it)
            try {
                withContext(Dispatchers.Default) {
                    client.post("https://pixelhobby-shop.de/cart/add.js") {
                        headers {
                            append(HttpHeaders.Cookie, "cart=$cartId")
                            append(HttpHeaders.ContentType, "application/json")
                            append(HttpHeaders.Host, "pixelhobby-shop.de")
                            append(HttpHeaders.UserAgent, "PixinaBackend")
                        }
                        setBody(json)
                    }
                }
            } catch (e: Exception) {
                return@runBlocking Result.failure(e)
            }
        }
        Result.success(Unit)
    }
}
