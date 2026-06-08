package com.example.foodypet

import android.app.Application
import com.example.foodypet.network.RetrofitClient

class FoodyPetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}