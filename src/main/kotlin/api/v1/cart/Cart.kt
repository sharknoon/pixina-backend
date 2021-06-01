package api.v1.cart

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
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
            context.call.respond(HttpStatusCode.InternalServerError, it)
        }
}

private suspend fun callPixelhobbyShop(cartId: String, articles: List<Article>): Result<Unit> {
    val client = HttpClient(CIO)
    articles.forEach {
        val json = Json { encodeDefaults = true }.encodeToString(Article.serializer(), it)
        try {
            client.post<String>("https://pixelhobby-shop.de/cart/add.js") {
                headers {
                    append(HttpHeaders.Cookie, "cart=$cartId")
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Host, "pixelhobby-shop.de")
                    append(HttpHeaders.UserAgent, "PixinaBackend")
                }
                body = json
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    return Result.success(Unit)
}
