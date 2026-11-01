package com.jesus.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

// Abre el launcher unos segundos despues de encender la radio,
// para quedar por encima del launcher de fabrica.
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pending = goAsync()
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val i = Intent(context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            } catch (e: Exception) {
            }
            pending.finish()
        }, 6000)
    }
}
