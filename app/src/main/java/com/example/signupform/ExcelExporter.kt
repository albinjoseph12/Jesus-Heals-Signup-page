package com.example.signupform

import android.content.Context
import android.os.Environment
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import java.io.File
import java.io.FileOutputStream

class ExcelExporter(private val context: Context) {

    fun exportToExcel(registrations: List<Registration>): File {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Registrations")

        // Create header row
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("First Name")
        headerRow.createCell(1).setCellValue("Last Name")
        headerRow.createCell(2).setCellValue("Email")
        headerRow.createCell(3).setCellValue("Phone")
        headerRow.createCell(4).setCellValue("ZIP Code")

        // Populate data rows
        registrations.forEachIndexed { index, registration ->
            val row: Row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(registration.firstName)
            row.createCell(1).setCellValue(registration.lastName)
            row.createCell(2).setCellValue(registration.email)
            row.createCell(3).setCellValue(registration.phone)
            row.createCell(4).setCellValue(registration.zipCode)
        }

        // Write the workbook to a file in the public Documents directory
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        if (!documentsDir.exists()) {
            documentsDir.mkdirs()
        }
        val file = File(documentsDir, "registrations.xls")
        FileOutputStream(file).use { outputStream ->
            workbook.write(outputStream)
        }

        return file
    }
}
