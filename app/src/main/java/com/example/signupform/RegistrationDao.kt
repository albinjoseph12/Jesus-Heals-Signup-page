package com.example.signupform

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegistrationDao {
    @Insert
    fun insert(registration: Registration)

    @Query("SELECT * FROM registrations")
    fun getAllRegistrations(): List<Registration>

    @Query("DELETE FROM registrations")
    fun deleteAllRegistrations()
}
