package mx.com.getic.encuestasoxxo.utils.excel

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import mx.com.getic.encuestasoxxo.data.remote.dto.PromedioPreguntaDto
import mx.com.getic.encuestasoxxo.data.remote.dto.RespuestaFilaDto
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ExcelGenerator {

    fun generarReporteCompleto(
        context: Context,
        respuestas: List<RespuestaFilaDto>,
        statsAtis: List<PromedioPreguntaDto>,
        statsTiendas: List<PromedioPreguntaDto>,
        statsPfs: List<PromedioPreguntaDto>
    ): Uri? {
        val workbook = XSSFWorkbook()

        // --- ESTILOS ---
        val headerFont = workbook.createFont()
        headerFont.bold = true
        headerFont.color = IndexedColors.WHITE.index
        headerFont.fontHeightInPoints = 12

        val headerStyle = workbook.createCellStyle()
        headerStyle.fillForegroundColor = IndexedColors.RED.index
        headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        headerStyle.setFont(headerFont)
        headerStyle.alignment = HorizontalAlignment.CENTER
        headerStyle.verticalAlignment = VerticalAlignment.CENTER
        headerStyle.borderBottom = BorderStyle.THIN
        headerStyle.borderTop = BorderStyle.THIN
        headerStyle.borderLeft = BorderStyle.THIN
        headerStyle.borderRight = BorderStyle.THIN

        // --- HOJA 1: DETALLE DE ENCUESTAS ---
        val sheetDetalle = workbook.createSheet("Detalle de Encuestas")
        val headersDetalle = listOf("Folio", "Fecha", "Tienda (CR)", "Nombre Tienda", "ATI Asignado", "PFS (Usuario)", "Pregunta", "Calificación", "Comentario")
        val rowH = sheetDetalle.createRow(0)
        headersDetalle.forEachIndexed { i, t ->
            val cell = rowH.createCell(i)
            cell.setCellValue(t)
            cell.cellStyle = headerStyle
        }

        respuestas.forEachIndexed { i, r ->
            val row = sheetDetalle.createRow(i + 1)
            row.createCell(0).setCellValue(r.encuesta_id)
            row.createCell(1).setCellValue(r.fecha_creacion_local)
            row.createCell(2).setCellValue(r.tienda_codigo)
            row.createCell(3).setCellValue(r.tienda)
            row.createCell(4).setCellValue(r.ati_nombre ?: "N/A")
            row.createCell(5).setCellValue(r.ati_nombre ?: "N/A")
            row.createCell(6).setCellValue(r.pregunta)
            row.createCell(7).setCellValue(r.calificacion.toDouble())
            row.createCell(8).setCellValue(r.comentario ?: "")
        }
        for (i in headersDetalle.indices) sheetDetalle.autoSizeColumn(i)

        // --- HOJA 2: KPIs POR ATI ---
        crearHojaResumen(workbook, "Resumen por ATI", "ATI", statsAtis, headerStyle)

        // --- HOJA 3: KPIs POR TIENDA ---
        crearHojaResumen(workbook, "Resumen por Tienda", "Tienda", statsTiendas, headerStyle)

        // --- HOJA 4: DESEMPEÑO PFS ---
        crearHojaResumen(workbook, "Desempeño PFS", "Técnico (PFS)", statsPfs, headerStyle)

        // Guardar
        val fileName = "Reporte_Integral_PulsoTI_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.xlsx"
        val file = File(context.cacheDir, fileName)
        
        return try {
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun crearHojaResumen(
        wb: XSSFWorkbook, 
        nombre: String, 
        labelEntidad: String,
        datos: List<PromedioPreguntaDto>,
        style: CellStyle
    ) {
        val sheet = wb.createSheet(nombre)
        val headers = listOf(labelEntidad, "Promedio (1-10)", "Total Evaluaciones", "Nivel")
        val rowH = sheet.createRow(0)
        headers.forEachIndexed { i, t ->
            val cell = rowH.createCell(i)
            cell.setCellValue(t)
            cell.setCellStyle(style)
        }

        datos.forEachIndexed { i, d ->
            val row = sheet.createRow(i + 1)
            row.createCell(0).setCellValue(d.pregunta_texto)
            row.createCell(1).setCellValue(d.promedio)
            row.createCell(2).setCellValue(d.total_encuestas.toDouble())
            val nivel = when {
                d.promedio >= 9.0 -> "PROMOTOR"
                d.promedio >= 7.0 -> "PASIVO"
                else -> "DETRACTOR"
            }
            row.createCell(3).setCellValue(nivel)
        }
        for (i in headers.indices) sheet.autoSizeColumn(i)
    }
}
