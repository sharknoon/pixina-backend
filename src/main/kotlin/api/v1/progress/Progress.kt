package api.v1.progress

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private val jsonConfig = Json { encodeDefaults = true }
private val path: Path = Paths.get("").toAbsolutePath().resolve("data").resolve("progress.json")

private var progressData: ProgressData = load()

suspend fun getProgress(context: PipelineContext<Unit, ApplicationCall>) {
    context.call.respond(HttpStatusCode.OK, progressData)
}

suspend fun setProgress(context: PipelineContext<Unit, ApplicationCall>) {
    progressData = try {
        context.call.receive()
    } catch (e: Exception) {
        context.call.respond(HttpStatusCode.BadRequest, "malformed request: $e")
        return
    }

    save()
    context.call.respond(HttpStatusCode.OK)
}

private fun load(): ProgressData {
    return if (!Files.exists(path)) {
        val initialProgressData = ProgressData()
        val json = jsonConfig.encodeToString(ProgressData.serializer(), initialProgressData)
        Files.createDirectories(path.parent)
        Files.writeString(path, json)
        initialProgressData
    } else {
        val json = Files.readString(path)
        jsonConfig.decodeFromString(ProgressData.serializer(), json)
    }
}

private fun save() {
    val json = jsonConfig.encodeToString(ProgressData.serializer(), progressData)
    Files.writeString(path, json)
}