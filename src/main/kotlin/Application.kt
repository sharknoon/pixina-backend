import api.v1.cart.add
import api.v1.progress.getProgress
import api.v1.umbrelwidgets.getProgressAllWidget
import api.v1.umbrelwidgets.getProgressWidget
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
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
                route("/umbrelwidgets") {
                    get("/progress") {
                        getProgressWidget(this)
                    }
                    get("/progress-all") {
                        getProgressAllWidget(this)
                    }
                }
            }
        }
    }
}