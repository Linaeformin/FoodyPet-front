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

    val step1Fragment = PetRegisterStep1Fragment.newInstance(mode)
    val step2Fragment = PetRegisterStep2Fragment.newInstance(mode)
    val step3Fragment = PetRegisterStep3Fragment.newInstance(mode)
    val step4Fragment = PetRegisterStep4Fragment.newInstance(mode)

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> step1Fragment
            1 -> step2Fragment
            2 -> step3Fragment
            3 -> step4Fragment
            else -> throw IllegalArgumentException("Invalid step position: $position")
        }
    }
}