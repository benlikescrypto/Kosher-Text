package com.kosherlabs.koshertext.interfaces

import androidx.room.Dao
import androidx.room.Query
import com.kosherlabs.koshertext.models.Attachment

@Dao
interface AttachmentsDao {
    @Query("SELECT * FROM attachments")
    fun getAll(): List<Attachment>
}
