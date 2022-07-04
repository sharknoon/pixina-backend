package api.v1.progress

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.net.URL
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

private var progressData: ProgressData = ProgressData()

val period: Long = System.getenv("PIXINA_EXCEL_POLL_INTERVAL_MS")?.toLongOrNull() ?: (1000 * 60 * 60 * 24)
val timer = fixedRateTimer(period = period) {
    print("Updating progress from Excel file... ")
    progressData = readExcelFile() ?: ProgressData()
    println("Done")
}

suspend fun getProgress(context: PipelineContext<Unit, ApplicationCall>) {
    context.call.respond(HttpStatusCode.OK, progressData)
}

private fun readExcelFile(): ProgressData? {
    try {
        val url = URL(System.getenv("PIXINA_EXCEL_POLL_URL")) ?: exitProcess(1)
        val pkg = OPCPackage.open(url.openStream())
        val wb = XSSFWorkbook(pkg)
        val sheet: XSSFSheet = wb.getSheet("Status")

        val finishedRow = sheet.getRow(5)
        val finished = finishedRow.getCell(1).numericCellValue
        val inProgressRow = sheet.getRow(4)
        val inProgress = inProgressRow.getCell(1).numericCellValue
        val reservedRow = sheet.getRow(3)
        val reserved = reservedRow.getCell(1).numericCellValue

        return ProgressData(
            finished.toInt(),
            inProgress.toInt(),
            reserved.toInt(),
            500 - (finished + inProgress + reserved).toInt()
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}