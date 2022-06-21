
import api.v1.cart.add
import api.v1.progress.getProgress
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Host)
        }
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                encodeDefaults = true
            })
        }

        routing {
            route("/api") {
                route("/v1") {
                    post("/cart/{id}/add") {
                        add(this)
                    }
                    route("/progress") {
                        get {
                            getProgress(this)
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}