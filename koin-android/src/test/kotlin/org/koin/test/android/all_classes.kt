package org.koin.test.android

import android.app.Application

/**
 * Some tests classes
 */

class AndroidComponent(val application: Application)

class OtherService(val androidComponent: AndroidComponent, val url : String){
    companion object{
        val URL = "URL"
    }
}