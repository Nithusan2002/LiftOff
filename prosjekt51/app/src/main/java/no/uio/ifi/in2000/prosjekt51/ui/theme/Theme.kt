package com.example.compose
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.preference.PreferenceManager
import com.example.ui.theme.AppTypography
import no.uio.ifi.in2000.prosjekt51.ui.theme.backgroundDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.backgroundDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.backgroundDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.backgroundLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.backgroundLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.backgroundLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.errorLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseOnSurfaceDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseOnSurfaceDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseOnSurfaceDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseOnSurfaceLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseOnSurfaceLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseOnSurfaceLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inversePrimaryDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.inversePrimaryDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inversePrimaryDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inversePrimaryLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.inversePrimaryLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inversePrimaryLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseSurfaceDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseSurfaceDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseSurfaceDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseSurfaceLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseSurfaceLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.inverseSurfaceLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBackgroundDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBackgroundDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBackgroundDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBackgroundLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBackgroundLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBackgroundLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onErrorLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onPrimaryLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSecondaryLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceVariantDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceVariantDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceVariantDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceVariantLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceVariantLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onSurfaceVariantLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.onTertiaryLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineVariantDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineVariantDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineVariantDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineVariantLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineVariantLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.outlineVariantLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.primaryLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.scrimDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.scrimDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.scrimDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.scrimLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.scrimLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.scrimLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.secondaryLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceBrightDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceBrightDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceBrightDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceBrightLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceBrightLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceBrightLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighestDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighestDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighestDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighestLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighestLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerHighestLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowestDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowestDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowestDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowestLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowestLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceContainerLowestLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceDimDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceDimDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceDimDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceDimLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceDimLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceDimLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceVariantDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceVariantDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceVariantDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceVariantLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceVariantLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.surfaceVariantLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryContainerDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryContainerDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryContainerDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryContainerLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryContainerLightMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryDark
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryDarkHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryDarkMediumContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryLightHighContrast
import no.uio.ifi.in2000.prosjekt51.ui.theme.tertiaryLightMediumContrast

@Immutable
data class ExtendedColorScheme(
    val goodConditions: ColorFamily,
    val edgeConditions: ColorFamily,
    val badConditions: ColorFamily,
)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

val extendedLight = ExtendedColorScheme(
  goodConditions = ColorFamily(
  goodConditionsLight,
  onGoodConditionsLight,
  goodConditionsContainerLight,
  onGoodConditionsContainerLight,
  ),
  edgeConditions = ColorFamily(
  edgeConditionsLight,
  onEdgeConditionsLight,
  edgeConditionsContainerLight,
  onEdgeConditionsContainerLight,
  ),
  badConditions = ColorFamily(
  badConditionsLight,
  onBadConditionsLight,
  badConditionsContainerLight,
  onBadConditionsContainerLight,
  ),
)

val extendedDark = ExtendedColorScheme(
  goodConditions = ColorFamily(
  goodConditionsDark,
  onGoodConditionsDark,
  goodConditionsContainerDark,
  onGoodConditionsContainerDark,
  ),
  edgeConditions = ColorFamily(
  edgeConditionsDark,
  onEdgeConditionsDark,
  edgeConditionsContainerDark,
  onEdgeConditionsContainerDark,
  ),
  badConditions = ColorFamily(
  badConditionsDark,
  onBadConditionsDark,
  badConditionsContainerDark,
  onBadConditionsContainerDark,
  ),
)

val extendedLightMediumContrast = ExtendedColorScheme(
  goodConditions = ColorFamily(
  goodConditionsLightMediumContrast,
  onGoodConditionsLightMediumContrast,
  goodConditionsContainerLightMediumContrast,
  onGoodConditionsContainerLightMediumContrast,
  ),
  edgeConditions = ColorFamily(
  edgeConditionsLightMediumContrast,
  onEdgeConditionsLightMediumContrast,
  edgeConditionsContainerLightMediumContrast,
  onEdgeConditionsContainerLightMediumContrast,
  ),
  badConditions = ColorFamily(
  badConditionsLightMediumContrast,
  onBadConditionsLightMediumContrast,
  badConditionsContainerLightMediumContrast,
  onBadConditionsContainerLightMediumContrast,
  ),
)

val extendedLightHighContrast = ExtendedColorScheme(
  goodConditions = ColorFamily(
  goodConditionsLightHighContrast,
  onGoodConditionsLightHighContrast,
  goodConditionsContainerLightHighContrast,
  onGoodConditionsContainerLightHighContrast,
  ),
  edgeConditions = ColorFamily(
  edgeConditionsLightHighContrast,
  onEdgeConditionsLightHighContrast,
  edgeConditionsContainerLightHighContrast,
  onEdgeConditionsContainerLightHighContrast,
  ),
  badConditions = ColorFamily(
  badConditionsLightHighContrast,
  onBadConditionsLightHighContrast,
  badConditionsContainerLightHighContrast,
  onBadConditionsContainerLightHighContrast,
  ),
)

val extendedDarkMediumContrast = ExtendedColorScheme(
  goodConditions = ColorFamily(
  goodConditionsDarkMediumContrast,
  onGoodConditionsDarkMediumContrast,
  goodConditionsContainerDarkMediumContrast,
  onGoodConditionsContainerDarkMediumContrast,
  ),
  edgeConditions = ColorFamily(
  edgeConditionsDarkMediumContrast,
  onEdgeConditionsDarkMediumContrast,
  edgeConditionsContainerDarkMediumContrast,
  onEdgeConditionsContainerDarkMediumContrast,
  ),
  badConditions = ColorFamily(
  badConditionsDarkMediumContrast,
  onBadConditionsDarkMediumContrast,
  badConditionsContainerDarkMediumContrast,
  onBadConditionsContainerDarkMediumContrast,
  ),
)

val extendedDarkHighContrast = ExtendedColorScheme(
  goodConditions = ColorFamily(
  goodConditionsDarkHighContrast,
  onGoodConditionsDarkHighContrast,
  goodConditionsContainerDarkHighContrast,
  onGoodConditionsContainerDarkHighContrast,
  ),
  edgeConditions = ColorFamily(
  edgeConditionsDarkHighContrast,
  onEdgeConditionsDarkHighContrast,
  edgeConditionsContainerDarkHighContrast,
  onEdgeConditionsContainerDarkHighContrast,
  ),
  badConditions = ColorFamily(
  badConditionsDarkHighContrast,
  onBadConditionsDarkHighContrast,
  badConditionsContainerDarkHighContrast,
  onBadConditionsContainerDarkHighContrast,
  ),
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)


@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val isDarkTheme = ThemeManager.getThemeState(context)

    val colorScheme = if (isDarkTheme.value) darkScheme else lightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

@Composable
fun AppThemeDeprecated(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      
      darkTheme -> darkScheme
      else -> lightScheme
  }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.primary.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}


object ThemeManager {
    private const val THEME_PREF = "theme_preference"
    private const val THEME_KEY = "theme_key"
    var isDarkTheme = mutableStateOf(false)

    fun getThemeState(context: Context): MutableState<Boolean> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        isDarkTheme.value = preferences.getBoolean(THEME_KEY, false) // false by default, meaning light theme
        return isDarkTheme
    }

    fun saveTheme(context: Context, isDark: Boolean) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putBoolean(THEME_KEY, isDark)
        editor.apply()
        isDarkTheme.value = isDark
    }
}
