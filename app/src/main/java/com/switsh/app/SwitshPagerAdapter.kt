package com.switsh.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SwitshPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 9

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> OnboardingFragment()
        1 -> DashboardFragment()
        2 -> CardFragment()
        3 -> PaiementFragment()
        4 -> VirementFragment()
        5 -> CashInOutFragment()
        6 -> BudgetFragment()
        7 -> SupportFragment()
        else -> ProfileFragment()
    }
}
