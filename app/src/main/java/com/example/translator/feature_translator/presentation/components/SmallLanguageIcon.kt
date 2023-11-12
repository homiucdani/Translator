package com.example.translator.feature_translator.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.translator.core.domain.presentation.UiLanguage

@Composable
fun SmallLanguageIcon(
    modifier: Modifier = Modifier,
    language: UiLanguage
) {
    AsyncImage(
        model = language.drawableRes,
        contentDescription = language.language.langName,
        modifier = modifier.size(25.dp)
    )
}