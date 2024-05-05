package api.v1.umbrelwidgets

import api.v1.progress.progressData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlin.math.floor

suspend fun getProgressWidget(context: PipelineContext<Unit, ApplicationCall>) {
    val progressWidget = ProgressWidget(
        text = floor((progressData.finished.size.toDouble() / 500.0) * 100).toInt().toString() + "%",
        subtext = progressData.finished.size.toString() + " Platten",
        progressLabel = (500 - progressData.finished.size).toString() + " verbleibende Platten",
        progress = (progressData.finished.size.toDouble() / 500.0)
    )
    context.call.respond(HttpStatusCode.OK, progressWidget)
}

suspend fun getProgressAllWidget(context: PipelineContext<Unit, ApplicationCall>) {
    val progressWidgetAll = ProgressWidgetAll(
        listOf(
            ProgressWidgetAllItem(
                "Fertig", progressData.finished.size.toString(), "Platten"
            ), ProgressWidgetAllItem(
                "In Arbeit", progressData.inProgress.size.toString(), "Platten"
            ), ProgressWidgetAllItem(
                "Reserviert", progressData.reserved.size.toString(), "Platten"
            ), ProgressWidgetAllItem(
                "Frei",
                (progressData.availableInStock.size + progressData.availableOutOfStock.size).toString(),
                "Platten"
            )
        )
    )
    context.call.respond(HttpStatusCode.OK, progressWidgetAll)
}