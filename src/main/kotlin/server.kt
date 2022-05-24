import api.v1.cart.add
import api.v1.progress.getProgress
import api.v1.progress.setProgress
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080) {
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
                        post {
                            setProgress(this)
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}