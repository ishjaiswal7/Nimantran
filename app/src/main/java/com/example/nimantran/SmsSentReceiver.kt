package com.example.nimantran

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class SmsSentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (resultCode) {
            RESULT_OK -> {
                makeStatusNotification("SMS sent", context)
            }
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                makeStatusNotification("generic failure", context)
            }
            SmsManager.RESULT_ERROR_NO_SERVICE -> {
                makeStatusNotification("no service", context)
            }
            SmsManager.RESULT_ERROR_NULL_PDU -> {
                makeStatusNotification("null PDU", context)
            }
            SmsManager.RESULT_ERROR_RADIO_OFF -> {
                makeStatusNotification("radio off", context)
            }
        }
    }
}