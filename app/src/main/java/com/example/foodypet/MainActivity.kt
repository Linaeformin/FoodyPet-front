package com.example.foodypet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.foodypet.home.fragment.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContentView(R.layout.activity_main)

        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.statusBars())

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // 처음 실행될 때 기본 프래그먼트
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.nav_mypage -> {
                    replaceFragment(MypageFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}