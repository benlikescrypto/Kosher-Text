package com.kosherlabs.koshertext.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.fossify.commons.extensions.notificationManager
import org.fossify.commons.helpers.ensureBackgroundThread
import com.kosherlabs.koshertext.extensions.deleteMessage
import com.kosherlabs.koshertext.extensions.updateLastConversationMessage
import com.kosherlabs.koshertext.helpers.IS_MMS
import com.kosherlabs.koshertext.helpers.MESSAGE_ID
import com.kosherlabs.koshertext.helpers.THREAD_ID
import com.kosherlabs.koshertext.helpers.refreshConversations
import com.kosherlabs.koshertext.helpers.refreshMessages

class DeleteSmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val threadId = intent.getLongExtra(THREAD_ID, 0L)
        val messageId = intent.getLongExtra(MESSAGE_ID, 0L)
        val isMms = intent.getBooleanExtra(IS_MMS, false)
        context.notificationManager.cancel(threadId.hashCode())
        ensureBackgroundThread {
            context.deleteMessage(messageId, isMms)
            context.updateLastConversationMessage(threadId)
            refreshMessages()
            refreshConversations()
        }
    }
}
