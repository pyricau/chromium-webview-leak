package com.example.chromiumwebviewleak

import android.app.Application
import leakcanary.LeakCanary

class ExampleApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    LeakCanary.config = LeakCanary.config.copy(retainedVisibleThreshold = 1)
  }
}