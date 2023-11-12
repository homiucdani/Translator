package com.example.translator.feature_translator.presentation

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.translator.R
import com.example.translator.feature_translator.domain.translate.TranslateException
import com.example.translator.feature_translator.presentation.components.LanguageDropDownMenu
import com.example.translator.feature_translator.presentation.components.SwapLanguagesButton
import com.example.translator.feature_translator.presentation.components.TranslateHistoryItem
import com.example.translator.feature_translator.presentation.components.TranslateTextField
import com.example.translator.feature_translator.presentation.components.rememberTextToSpeech
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TranslateScreen(
    state: TranslateState,
    onEvent: (TranslateEvent) -> Unit // this replaces the viewModel cuz its much easier for testing
) {

    val context = LocalContext.current
    val tts = rememberTextToSpeech()

    LaunchedEffect(key1 = state.error) {
        val message = when (state.error) {
            is TranslateException.ServiceUnavailable -> {
                context.getString(R.string.error_service_unavailable)
            }

            is TranslateException.ClientError -> {
                context.getString(R.string.client_error)
            }

            is TranslateException.ServerError -> {
                context.getString(R.string.server_error)
            }

            is TranslateException.UnknownError -> {
                context.getString(R.string.unknown_error)
            }

            else -> null
        }

        message?.let {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_LONG
            ).show()
            onEvent(TranslateEvent.OnErrorSeen) // very important to clear the error, otherwise this launched effect is a infinite loop
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(TranslateEvent.RecordAudio)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(75.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.mic),
                    contentDescription = stringResource(
                        id = R.string.record_audio
                    )
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LanguageDropDownMenu(
                        language = state.fromLanguage,
                        isOpen = state.isChoosingFromLanguage,
                        onClick = {
                            onEvent(TranslateEvent.OpenFromLanguageDropDown)
                        },
                        onDismiss = {
                            onEvent(TranslateEvent.StopChoosingLanguage)
                        },
                        onSelectLanguage = {
                            onEvent(TranslateEvent.ChooseFromLanguage(it))
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    SwapLanguagesButton(
                        onClick = {
                            onEvent(TranslateEvent.SwapLanguages)
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    LanguageDropDownMenu(
                        language = state.toLanguage,
                        isOpen = state.isChoosingToLanguage,
                        onClick = {
                            onEvent(TranslateEvent.OpenToLanguageDropDown)
                        },
                        onDismiss = {
                            onEvent(TranslateEvent.StopChoosingLanguage)
                        },
                        onSelectLanguage = {
                            onEvent(TranslateEvent.ChooseToLanguage(it))
                        }
                    )
                }
            }

            item {
                val clipboardManager = LocalClipboardManager.current
                val keyboardController = LocalSoftwareKeyboardController.current

                TranslateTextField(
                    fromText = state.fromText,
                    toText = state.toText,
                    isTranslating = state.isTranslating,
                    fromLanguage = state.fromLanguage,
                    toLanguage = state.toLanguage,
                    onTranslateClick = {
                        keyboardController?.hide()
                        onEvent(TranslateEvent.Translate)
                    },
                    onTextChange = {
                        onEvent(TranslateEvent.ChangeTranslationText(it))
                    },
                    onCopyClick = { copiedText ->
                        clipboardManager.setText(
                            buildAnnotatedString {
                                append(copiedText)
                            }
                        )
                        Toast.makeText(
                            context,
                            R.string.copy_clipboard,
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onCloseClick = {
                        onEvent(TranslateEvent.CloseTranslation)
                    },
                    onSpeakerClick = {
                        tts.language = state.toLanguage.toLocale()
                        tts.speak(
                            state.toText,
                            TextToSpeech.QUEUE_FLUSH,// restart with a new tts
                            null,
                            null
                        )
                    },
                    onTextFieldClick = {
                        onEvent(TranslateEvent.EditTranslation)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                if (state.historyList.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.history),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            items(state.historyList) { item ->
                TranslateHistoryItem(
                    item = item,
                    onClick = {
                        onEvent(TranslateEvent.SelectHistoryItem(item))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}