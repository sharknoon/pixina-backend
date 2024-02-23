package api.v1.progress

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

private var progressData: ProgressData = ProgressData()
private var logger = Logger.getLogger("Pixina Backend")

val period: Long = System.getenv("PIXINA_EXCEL_POLL_INTERVAL_MS")?.toLongOrNull() ?: (1000 * 60 * 60 * 24)
@Suppress("unused")
val timer = fixedRateTimer(period = period) {
    logger.info("Updating progress from Excel file")
    progressData = readExcelFile() ?: ProgressData()
}

suspend fun getProgress(context: PipelineContext<Unit, ApplicationCall>) {
    context.call.respond(HttpStatusCode.OK, progressData)
}

enum class Status(val cellname: String) {
    AVAILABLE_OUT_OF_STOCK("Frei (nicht lagernd)"), AVAILABLE_IN_STOCK("Frei (lagernd)"), RESERVED(
        "Reserviert"
    ),
    IN_PROGRESS("In Arbeit"), FINISHED("Fertig")
}

private fun readExcelFile(): ProgressData? {
    try {
        val urlEnv = System.getenv("PIXINA_EXCEL_POLL_URL")
        if (urlEnv == null) {
            logger.severe("Environment variable 'PIXINA_EXCEL_POLL_URL' is missing")
            exitProcess(1)
        }
        val url = URI.create(urlEnv).toURL()
        val pkg = OPCPackage.open(url.openStream())
        val wb = XSSFWorkbook(pkg)
        val sheet: XSSFSheet = wb.getSheet("Übersicht")

        val progress = mutableMapOf<Status, MutableSet<Int>>()

        for ((index, row) in sheet.withIndex()) {
            if (index == 0) continue

            val number = row.getCell(0).numericCellValue.toInt()
            val statusString = row.getCell(2).stringCellValue
            val status = Status.values().find { it.cellname == statusString }
                ?: throw Exception("Unknown plate status $statusString")

            progress.getOrPut(status) { mutableSetOf() }.add(number)
        }

        return ProgressData(
            progress[Status.FINISHED]?.toSet() ?: setOf(),
            progress[Status.IN_PROGRESS]?.toSet() ?: setOf(),
            progress[Status.RESERVED]?.toSet() ?: setOf(),
            progress[Status.AVAILABLE_IN_STOCK]?.toSet() ?: setOf(),
            progress[Status.AVAILABLE_OUT_OF_STOCK]?.toSet() ?: setOf(),
        )
    } catch (e: Exception) {
        logger.log(Level.SEVERE, "Error parsing Excel", e)
    }
    return null
}