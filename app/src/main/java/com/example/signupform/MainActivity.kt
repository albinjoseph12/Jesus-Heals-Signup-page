package com.example.signupform

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var zipCodeEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var exportButton: Button
    private lateinit var database: AppDatabase

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        zipCodeEditText = findViewById(R.id.zipCodeEditText)
        submitButton = findViewById(R.id.submitButton)
        exportButton = findViewById(R.id.exportButton)

        // Initialize database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "registration_database"
        ).build()

        submitButton.setOnClickListener {
            saveRegistration()
        }

        exportButton.setOnClickListener {
            if (allPermissionsGranted()) {
                exportToExcel()
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
    }

    private fun saveRegistration() {
        if (!validateInputs()) {
            return
        }

        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val zipCode = zipCodeEditText.text.toString()

        val registration = Registration(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            zipCode = zipCode
        )

        GlobalScope.launch(Dispatchers.IO) {
            database.registrationDao().insert(registration)
        }

        // Clear input fields after saving
        clearInputFields()
    }

    private fun clearInputFields() {
        firstNameEditText.text.clear()
        lastNameEditText.text.clear()
        emailEditText.text.clear()
        phoneEditText.text.clear()
        zipCodeEditText.text.clear()
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (firstNameEditText.text.isEmpty()) {
            firstNameEditText.error = "First Name is required"
            isValid = false
        }

        if (lastNameEditText.text.isEmpty()) {
            lastNameEditText.error = "Last Name is required"
            isValid = false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.text).matches()) {
            emailEditText.error = "Invalid Email Address"
            isValid = false
        }

        if (!android.util.Patterns.PHONE.matcher(phoneEditText.text).matches() || phoneEditText.text.length != 10) {
            phoneEditText.error = "Invalid Phone Number"
            isValid = false
        }

        if (zipCodeEditText.text.length != 5) {
            zipCodeEditText.error = "Invalid ZIP Code"
            isValid = false
        }

        return isValid
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                exportToExcel()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exportToExcel() {
        GlobalScope.launch(Dispatchers.IO) {
            val registrations = database.registrationDao().getAllRegistrations()
            val excelFile = ExcelExporter(this@MainActivity).exportToExcel(registrations)
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Data exported to ${excelFile.absolutePath}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
