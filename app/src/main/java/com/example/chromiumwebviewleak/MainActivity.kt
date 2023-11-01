package com.example.chromiumwebviewleak

import android.R.style
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chromiumwebviewleak.ui.theme.ChromiumWebviewLeakTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ChromiumWebviewLeakTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Greeting("Android")
        }
      }
    }

    // TODO Try with alert dialog.
    val dialog = Dialog(this, style.Theme_Black_NoTitleBar)
    val contentView = View.inflate(this, R.layout.browser, null)


    val webView = contentView.findViewById<WebView>(R.id.webview)
    val progressBar = contentView.findViewById<ProgressBar>(R.id.web_progress_bar)

    webView.webViewClient = SlowLoadingWebviewClient()
    webView.webChromeClient = object : WebChromeClient() {
      override fun onProgressChanged(
        view: WebView,
        newProgress: Int
      ) {
        progressBar.progress = newProgress
      }
    }
    webView.settings.javaScriptEnabled = true
    webView.settings.domStorageEnabled = true
    webView.isVerticalScrollBarEnabled = false
    webView.loadUrl("https://squareup.com/us/en/legal/general/ua")
    dialog.setContentView(contentView)
    dialog.setCanceledOnTouchOutside(true)
    dialog.show()
  }
}

class SlowLoadingWebviewClient : WebViewClient() {
  override fun shouldInterceptRequest(
    view: WebView,
    request: WebResourceRequest
  ): WebResourceResponse? {
    if (request.url.toString().endsWith(".js")) {
      Thread.sleep(10_000)
    }
    return null
  }
}

@Composable
fun Greeting(
  name: String,
  modifier: Modifier = Modifier
) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ChromiumWebviewLeakTheme {
    Greeting("Android")
  }
}