package com.example.signupform

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Registration::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun registrationDao(): RegistrationDao
}
