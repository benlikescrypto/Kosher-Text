package com.kosherlabs.koshertext.interfaces

import androidx.room.Dao
import androidx.room.Query
import com.kosherlabs.koshertext.models.MessageAttachment

@Dao
interface MessageAttachmentsDao {
    @Query("SELECT * FROM message_attachments")
    fun getAll(): List<MessageAttachment>
}
