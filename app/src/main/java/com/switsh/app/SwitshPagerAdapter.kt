package com.switsh.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SwitshPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 8

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> DashboardFragment()
        1 -> CardFragment()
        2 -> PaiementFragment()
        3 -> VirementFragment()
        4 -> CashInOutFragment()
        5 -> BudgetFragment()
        6 -> SupportFragment()
        else -> ProfileFragment()
    }
}
