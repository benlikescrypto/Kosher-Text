package com.kosherlabs.koshertext.receivers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Telephony.Sms
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import org.fossify.commons.extensions.getMyContactsCursor
import org.fossify.commons.helpers.ensureBackgroundThread
import com.kosherlabs.koshertext.extensions.getMessageRecipientAddress
import com.kosherlabs.koshertext.extensions.getNameFromAddress
import com.kosherlabs.koshertext.extensions.getThreadId
import com.kosherlabs.koshertext.extensions.messagesDB
import com.kosherlabs.koshertext.extensions.messagingUtils
import com.kosherlabs.koshertext.extensions.notificationHelper
import com.kosherlabs.koshertext.helpers.refreshConversations
import com.kosherlabs.koshertext.helpers.refreshMessages

/** Handles updating databases and states when a SMS message is sent. */
class SmsStatusSentReceiver : SendStatusReceiver() {

    override fun updateAndroidDatabase(context: Context, intent: Intent, receiverResultCode: Int) {
        val messageUri: Uri? = intent.data
        val resultCode = resultCode
        val messagingUtils = context.messagingUtils

        val type = if (resultCode == Activity.RESULT_OK) {
            Sms.MESSAGE_TYPE_SENT
        } else {
            Sms.MESSAGE_TYPE_FAILED
        }
        messagingUtils.updateSmsMessageSendingStatus(messageUri, type)
        messagingUtils.maybeShowErrorToast(
            resultCode = resultCode,
            errorCode = intent.getIntExtra(EXTRA_ERROR_CODE, NO_ERROR_CODE)
        )
    }

    override fun updateAppDatabase(context: Context, intent: Intent, receiverResultCode: Int) {
        val messageUri = intent.data
        if (messageUri != null) {
            val messageId = messageUri.lastPathSegment?.toLong() ?: 0L
            ensureBackgroundThread {
                val type = if (receiverResultCode == Activity.RESULT_OK) {
                    Sms.MESSAGE_TYPE_SENT
                } else {
                    showSendingFailedNotification(context, messageId)
                    Sms.MESSAGE_TYPE_FAILED
                }

                context.messagesDB.updateType(messageId, type)
                refreshMessages()
                refreshConversations()
            }
        }
    }

    private fun showSendingFailedNotification(context: Context, messageId: Long) {
        Handler(Looper.getMainLooper()).post {
            if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                return@post
            }
            val privateCursor = context.getMyContactsCursor(favoritesOnly = false, withPhoneNumbersOnly = true)
            ensureBackgroundThread {
                val address = context.getMessageRecipientAddress(messageId)
                val threadId = context.getThreadId(address)
                val recipientName = context.getNameFromAddress(address, privateCursor)
                context.notificationHelper.showSendingFailedNotification(recipientName, threadId)
            }
        }
    }
}
