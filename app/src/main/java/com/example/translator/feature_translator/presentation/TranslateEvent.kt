package com.example.translator.feature_translator.presentation

import com.example.translator.core.domain.presentation.UiLanguage

sealed class TranslateEvent {

    data class ChooseFromLanguage(val language: UiLanguage) : TranslateEvent()
    data class ChooseToLanguage(val language: UiLanguage) : TranslateEvent()

    object StopChoosingLanguage : TranslateEvent() // when we dismiss the dropdowns when we type outside the dropdown

    object SwapLanguages : TranslateEvent()

    data class ChangeTranslationText(val text: String) : TranslateEvent() // text field input

    object Translate : TranslateEvent()

    object OpenFromLanguageDropDown : TranslateEvent()
    object OpenToLanguageDropDown : TranslateEvent()

    object CloseTranslation : TranslateEvent() // this will clear the composables from text

    data class SelectHistoryItem(val item: UiHistoryItem) : TranslateEvent()

    object EditTranslation : TranslateEvent()

    object RecordAudio : TranslateEvent()
    data class SubmitVoiceResult(val result: String?) : TranslateEvent()

    object OnErrorSeen : TranslateEvent()

}
