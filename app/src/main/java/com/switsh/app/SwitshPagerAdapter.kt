package com.switsh.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SwitshPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> DashboardFragment()
        1 -> CardFragment()
        2 -> TransferFragment()
        3 -> BudgetFragment()
        4 -> SupportFragment()
        else -> ProfileFragment()
    }
}
