# What's this?

This repository contains a very simple app that reproduces a critical memory leak in Chromium Webview, which as of November 1st 2023 still reproducible when building webview from the latest Chromium master. 

Here's a sample activity that reproduces the leak:

```kotlin
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
```

When detaching the webview (which in this example happens when the activity is destroyed), a native reference to an object that references AwContents is preventing presenting the WebView instance from being garbage collected.

# Reproducing

Install the demo app on a device with a recent version of Chromium Webview. I just did it on a Pixel 7 running Android 13, on November 1st 2023 the repro still works.

```
./gradlew installDebug
```

* Launch the *Chromium Webview leak* app. You should see a webview load with Square's General Terms of Service webpage.
* Click on the **RECREATE ACTIVITY TO LEAK WEBVIEW** button. After 5 seconds, you should see a giant toast with the LeakCanary logo that says *LeakCanary is dumping memory to investigate leaks.*
* You can see the analysis progress in a notification, or in logcat (`adb logcat | grep LeakCanary`).
* Eventually you'll see a notification with the result: **Found 1 retained objects - Tap for more details**.

Here's the resulting leak trace (`a6.c` and `AwContents.c` might be different as those are obfuscated and unique per Chromium webview build):

```
┬───
│ GC Root: Global variable in native code
│
├─ WV.a6 instance
│    Leaking: UNKNOWN
│    Retaining 189.7 kB in 1093 objects
│    ↓ a6.c
│         ~
├─ org.chromium.android_webview.AwContents instance
│    Leaking: UNKNOWN
│    Retaining 189.7 kB in 1092 objects
│    e instance of WV.gd, wrapping activity com.example.chromiumwebviewleak.MainActivity with mDestroyed = true
│    ↓ AwContents.c
│                 ~
├─ android.webkit.WebView instance
│    Leaking: YES (View.mContext references a destroyed activity)
│    Retaining 3.4 kB in 42 objects
│    View not part of a window view hierarchy
│    View.mAttachInfo is null (view detached)
│    View.mID = R.id.webview
│    View.mWindowAttachCount = 1
│    mContext instance of com.example.chromiumwebviewleak.MainActivity with mDestroyed = true
│    ↓ View.mContext
╰→ com.example.chromiumwebviewleak.MainActivity instance
     Leaking: YES (ObjectWatcher was watching this because com.example.chromiumwebviewleak.MainActivity received
     Activity#onDestroy() callback and Activity#mDestroyed is true)
     Retaining 22.8 kB in 513 objects
     key = 452744ae-3c83-4d8b-9f4b-3e568c236790
     watchDurationMillis = 5619
     retainedDurationMillis = 615
     mApplication instance of com.example.chromiumwebviewleak.ExampleApplication
     mBase instance of android.app.ContextImpl
```