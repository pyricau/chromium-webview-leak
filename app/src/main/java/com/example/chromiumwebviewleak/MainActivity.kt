package com.example.chromiumwebviewleak

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity)

    val webView = findViewById<WebView>(R.id.webview)
    webView.loadUrl("https://squareup.com/us/en/legal/general/ua")

    findViewById<View>(R.id.recreate).setOnClickListener {
      recreate()
    }
  }
}
