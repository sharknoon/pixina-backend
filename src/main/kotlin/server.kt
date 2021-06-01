import api.v1.cart.add
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
            })
        }

        routing {
            route("/api") {
                route("/v1") {
                    post("/cart/{id}/add") {
                        add(this)
                    }
                }
            }
        }
    }.start(wait = true)
}