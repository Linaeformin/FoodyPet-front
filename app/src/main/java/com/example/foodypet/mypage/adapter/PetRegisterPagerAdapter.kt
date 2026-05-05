package com.example.foodypet.mypage.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.foodypet.mypage.fragment.PetRegisterStep1Fragment
import com.example.foodypet.mypage.fragment.PetRegisterStep2Fragment
import com.example.foodypet.mypage.fragment.PetRegisterStep3Fragment
import com.example.foodypet.mypage.fragment.PetRegisterStep4Fragment

class PetRegisterPagerAdapter(
    fragment: Fragment,
    private val mode: String
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PetRegisterStep1Fragment.newInstance(mode)
            1 -> PetRegisterStep2Fragment.newInstance(mode)
            2 -> PetRegisterStep3Fragment.newInstance(mode)
            3 -> PetRegisterStep4Fragment.newInstance(mode)
            else -> throw IllegalArgumentException("Invalid step position: $position")
        }
    }
}