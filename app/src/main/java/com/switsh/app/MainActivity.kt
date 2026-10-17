package com.switsh.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.switsh.app.model.AppState
import com.switsh.app.model.ScreenCatalog
import com.switsh.app.model.ScreenRepository
import com.switsh.app.screens.Navigator
import com.switsh.app.screens.ScreenFragment
import com.switsh.app.databinding.ActivityMainBinding
import com.switsh.clients.OnboardingApiClient

class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding
    override lateinit var appState: AppState
        private set
    override lateinit var catalog: ScreenCatalog
        private set
    override lateinit var onboardingApi: OnboardingApiClient
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appState = AppState(this)
        catalog = ScreenRepository.load(this)
        onboardingApi = OnboardingApiClient(API_BASE_URL, tokenProvider = {
            appState.onboardingAccessToken
                ?.takeIf(String::isNotBlank)
                ?: appState.getOrGenerateOnboardingAccessToken()
        }, allowInsecureHttp = true) // REMOVE BEFORE FLIGHT: remove this opt-in and require HTTPS.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) reset("start-splash")
    }

    override fun open(screenId: String) {
        val screen = catalog.find(screenId) ?: return
        supportFragmentManager.beginTransaction()
            .replace(binding.screenContainer.id, ScreenFragment.forScreen(screen.id, screen.group))
            .addToBackStack(screenId)
            .commit()
    }

    override fun reset(screenId: String) {
        val screen = catalog.find(screenId) ?: return
        supportFragmentManager.popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(binding.screenContainer.id, ScreenFragment.forScreen(screen.id, screen.group))
            .commit()
    }

    override fun onDestroy() {
        onboardingApi.close()
        super.onDestroy()
    }

    private companion object {
        // Android emulators reach the host machine through 10.0.2.2, not device-local localhost.
        const val API_BASE_URL = "http://10.0.2.2:8080"
    }
}