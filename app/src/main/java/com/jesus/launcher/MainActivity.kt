package com.jesus.launcher

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var web: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        web.setBackgroundColor(Color.parseColor("#04173a"))
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.webChromeClient = WebChromeClient()
        web.addJavascriptInterface(Bridge(), "Android")
        web.loadUrl("file:///android_asset/index.html")
        setContentView(web)
    }

    // En un launcher, "atrás" solo cierra la lista de apps.
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        web.evaluateJavascript("closeApps()", null)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        web.evaluateJavascript("closeApps()", null)
    }

    inner class Bridge {

        @JavascriptInterface
        fun launch(pkg: String): Boolean {
            val i = packageManager.getLaunchIntentForPackage(pkg) ?: return false
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            runOnUiThread { startActivity(i) }
            return true
        }

        @JavascriptInterface
        fun wifi() {
            val i = Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            runOnUiThread { startActivity(i) }
        }

        @JavascriptInterface
        fun media(keyCode: Int) {
            val am = getSystemService(AUDIO_SERVICE) as AudioManager
            am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        }

        @JavascriptInterface
        fun apps(): String {
            val q = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val arr = JSONArray()
            packageManager.queryIntentActivities(q, 0)
                .filter { it.activityInfo.packageName != packageName }
                .sortedBy { it.loadLabel(packageManager).toString().lowercase() }
                .forEach {
                    arr.put(
                        JSONObject()
                            .put("n", it.loadLabel(packageManager).toString())
                            .put("p", it.activityInfo.packageName)
                    )
                }
            return arr.toString()
        }
    }
}
