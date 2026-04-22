package com.kosherlabs.koshertext.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.fossify.commons.extensions.notificationManager
import org.fossify.commons.helpers.ensureBackgroundThread
import com.kosherlabs.koshertext.extensions.conversationsDB
import com.kosherlabs.koshertext.extensions.markThreadMessagesRead
import com.kosherlabs.koshertext.helpers.MARK_AS_READ
import com.kosherlabs.koshertext.helpers.THREAD_ID
import com.kosherlabs.koshertext.helpers.refreshConversations

class MarkAsReadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            MARK_AS_READ -> {
                val threadId = intent.getLongExtra(THREAD_ID, 0L)
                context.notificationManager.cancel(threadId.hashCode())
                ensureBackgroundThread {
                    context.markThreadMessagesRead(threadId)
                    context.conversationsDB.markRead(threadId)
                    refreshConversations()
                }
            }
        }
    }
}
