package com.example.translator.feature_translator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.core.domain.presentation.UiLanguage
import com.example.translator.feature_translator.domain.history.TranslatorHistoryDataSource
import com.example.translator.feature_translator.domain.translate.TranslateException
import com.example.translator.feature_translator.domain.use_case.Translate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val translate: Translate, // use_case
    private val historyDataSource: TranslatorHistoryDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(TranslateState())

    // read only state
    // whenever a value changes into db, this state observes the change and emit a new value to the main state
    val state = combine(
        _state,
        historyDataSource.getHistory() // this is a flow also which observes the db
    ) { state, historyList ->

        if (state.historyList != historyList) {
            state.copy(
                historyList = historyList.mapNotNull { item ->
                    UiHistoryItem(
                        id = item.id ?: return@mapNotNull null,
                        fromText = item.fromText,
                        toText = item.toText,
                        fromLanguage = UiLanguage.byCode(item.fromLanguageCode),
                        toLanguage = UiLanguage.byCode(item.toLanguageCode)
                    )
                }
            )
        } else {
            state
        }
        // after the last collector, this state will remain 5 seconds until it closes, if im collecting data again, then the flow is the same
        // and will emit new values. ex: translating smth, after 2 seconds i decide to translate again, then there are 2 jobs involved
        // first job collects and the flow goes into timeout, when job2 connects then exits the timeout and continues the same flow.
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TranslateState())

    private var translateJob: Job? = null

    fun onEvent(event: TranslateEvent) {
        when (event) {
            is TranslateEvent.ChangeTranslationText -> {
                _state.update {
                    it.copy(
                        fromText = event.text
                    )
                }
            }

            is TranslateEvent.ChooseFromLanguage -> {
                _state.update {
                    it.copy(
                        isChoosingFromLanguage = false,
                        fromLanguage = event.language
                    )
                }
            }

            is TranslateEvent.ChooseToLanguage -> {
                val newState = _state.updateAndGet {
                    it.copy(
                        isChoosingToLanguage = false, // close dropdown menu when we clicked on a lang
                        toLanguage = event.language
                    )
                }
                translate(newState) // if we change from german to french, then we automatically wanna translate
            }

            is TranslateEvent.CloseTranslation -> {
                _state.update {
                    it.copy(
                        isTranslating = false,
                        fromText = "",
                        toText = null
                    )
                }
            }

            is TranslateEvent.EditTranslation -> {
                //only if we have a translated value we can edit
                if (state.value.toText != null) {
                    _state.update {
                        it.copy(
                            isTranslating = false,
                            toText = null
                        )
                    }
                }
            }

            is TranslateEvent.OnErrorSeen -> {
                _state.update {
                    it.copy(error = null)
                }
            }

            is TranslateEvent.OpenFromLanguageDropDown -> {
                _state.update {
                    it.copy(
                        isChoosingFromLanguage = true
                    )
                }
            }

            is TranslateEvent.OpenToLanguageDropDown -> {
                _state.update {
                    it.copy(
                        isChoosingToLanguage = true
                    )
                }
            }

            is TranslateEvent.SelectHistoryItem -> {
                translateJob?.cancel()
                _state.update {
                    it.copy(
                        fromText = event.item.fromText,
                        toText = event.item.toText,
                        fromLanguage = event.item.fromLanguage,
                        toLanguage = event.item.toLanguage,
                        isTranslating = false
                    )
                }
            }

            is TranslateEvent.StopChoosingLanguage -> {
                _state.update {
                    it.copy(
                        isChoosingToLanguage = false,
                        isChoosingFromLanguage = false
                    )
                }
            }

            is TranslateEvent.SubmitVoiceResult -> {
                _state.update {
                    it.copy(
                        fromText = event.result ?: it.fromText,
                        isTranslating = if (event.result != null) false else it.isTranslating,
                        toText = if (event.result != null) null else it.toText
                    )
                }
            }

            is TranslateEvent.SwapLanguages -> {
                _state.update {
                    it.copy(
                        fromLanguage = it.toLanguage,
                        toLanguage = it.fromLanguage,
                        fromText = it.toText ?: "",
                        toText = if (it.toText != null) it.fromText else null
                    )
                }
            }

            is TranslateEvent.Translate -> {
                translate(state.value)
            }

            else -> Unit
        }
    }


    // we pass the state as a parameter cuz ".update fun" is async and it could read old info when we update a state inside onEvent
    private fun translate(state: TranslateState) {
        if (state.isTranslating || state.fromText.isBlank()) {
            return
        }
        translateJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isTranslating = true
                )
            }

            val result = translate(
                fromLanguage = state.fromLanguage.language,
                fromText = state.fromText,
                toLanguage = state.toLanguage.language
            )

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isTranslating = false,
                        toText = result.getOrNull() ?: ""
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isTranslating = false,
                        error = result.exceptionOrNull() as TranslateException
                    )
                }
            }
        }
    }
}