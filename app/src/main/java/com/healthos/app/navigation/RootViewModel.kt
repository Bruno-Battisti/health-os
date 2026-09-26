package com.healthos.app.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.usecase.ObserveOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    observeOnboardingCompletedUseCase: ObserveOnboardingCompletedUseCase,
) : ViewModel() {

    val onboardingCompleted: StateFlow<Boolean?> = observeOnboardingCompletedUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
