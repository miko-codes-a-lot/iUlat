package dev.cloudants.iulat.lib.components.print

import android.content.Context
import android.os.Environment
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import dev.cloudants.iulat.lib.components.context.PrintableRow
import java.io.File
import java.io.FileOutputStream

fun exportDynamicPDF(
    context: Context,
    title: String,
    headers: List<String>,
    data: List<PrintableRow>,
    onFinish: (File) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val file = createFile(context)
        val document = Document(PageSize.A4.rotate())
        val writer = PdfWriter.getInstance(document, FileOutputStream(file))

        document.open()

        val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f)
        val titleParagraph = Paragraph(title, titleFont)
        titleParagraph.alignment = Element.ALIGN_CENTER
        document.add(titleParagraph)
        addLineSpace(document, 1)

        val table = createTable(headers.size, FloatArray(headers.size) { 1f })

        headers.forEach { table.addCell(createCell(it, fixedHeight = 40f)) }

        data.forEachIndexed { index, row ->

            table.addCell(createCell((index + 1).toString(), 35f))

            row.getColumns().forEach { value ->
                table.addCell(createCell(value, 35f))
            }
        }

        document.add(table)
        addPageNumbers(document, writer)
        document.close()

        onFinish(file)
    } catch (e: Exception) {
        onError(e)
    }
}

private fun createFile(context: Context): File {
    val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Reports")
    if (!dir.exists()) dir.mkdirs()

    val fileName = "report_${System.currentTimeMillis()}.pdf"
    return File(dir, fileName)
}

private fun addLineSpace(document: Document, count: Int) {
    repeat(count) {
        document.add(Paragraph(" "))
    }
}

private fun addPageNumbers(document: Document, writer: PdfWriter) {
    val cb = writer.directContent
    val pageSize = document.pageSize
    val text = "Page ${writer.pageNumber}"
    val font = FontFactory.getFont(FontFactory.HELVETICA, 10f)

    ColumnText.showTextAligned(
        cb,
        Element.ALIGN_CENTER,
        Paragraph(text, font),
        (pageSize.right + pageSize.left) / 2,
        pageSize.bottom + 20,
        0f
    )
}

private fun createCell(
    value: Any,
    fixedHeight: Float = 30f,
    alignment: Int = Element.ALIGN_CENTER
): PdfPCell {

    return when (value) {

        is Image -> {
            value.scaleToFit(80f, 80f)
            val cell = PdfPCell(value, true)
            cell.fixedHeight = fixedHeight
            cell.horizontalAlignment = alignment
            cell.verticalAlignment = Element.ALIGN_MIDDLE
            cell
        }

        else -> {
            val cell = PdfPCell(Paragraph(value.toString()))
            cell.fixedHeight = fixedHeight
            cell.horizontalAlignment = alignment
            cell.verticalAlignment = Element.ALIGN_MIDDLE
            cell
        }
    }
}


private fun createTable(columns: Int, widths: FloatArray): PdfPTable {
    val table = PdfPTable(columns)
    table.widthPercentage = 100f
    table.setWidths(widths)
    return table
}
