package com.hotmail.or_dvir.sabinesList.ui.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import com.hotmail.or_dvir.sabinesList.R
import com.hotmail.or_dvir.sabinesList.collectAsStateLifecycleAware
import com.hotmail.or_dvir.sabinesList.preferences.ThemePreference
import com.hotmail.or_dvir.sabinesList.ui.NavigationIconBackArrow
import com.hotmail.or_dvir.sabinesList.ui.preferences.PreferencesScreenModel.PreferencesEvent

private typealias OnThemeSelected = (ThemePreference) -> Unit
private typealias OnPreferencesEvent = (PreferencesEvent) -> Unit

private const val PACKAGE_FLAG_NO_EXTRA_INFO = 0

class PreferencesScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<PreferencesScreenModel>()
        val currentTheme by screenModel.userSelectedTheme.collectAsStateLifecycleAware(
            ThemePreference.Default
        )

        PreferencesScreenContent(
            currentTheme = currentTheme,
            onPreferencesEvent = screenModel::onPreferencesEvent
        )
    }

    // needs to be extracted into a separate function in order to appear in previews
    // (voyager library issue)
    @Composable
    private fun PreferencesScreenContent(
        currentTheme: ThemePreference,
        onPreferencesEvent: OnPreferencesEvent
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = { NavigationIconBackArrow() },
                    title = { Text(stringResource(R.string.preferenceScreen_title)) }
                )
            },
        ) { contentPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    ThemeSelectionPreference(
                        currentTheme = currentTheme,
                        onThemeSelected = {
                            onPreferencesEvent(PreferencesEvent.SetTheme(it))
                        }
                    )
                    PreferencesDivider()
                    CreditsPreference()
                }

                FooterCredits()
            }
        }
    }

    @Composable
    private fun FooterCredits() {
        Column(Modifier.fillMaxWidth()) {
            PreferencesDivider()

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MyCredits()

                val context = LocalContext.current
                val versionName = if (LocalInspectionMode.current) {
                    "1.0"
                } else {
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        PACKAGE_FLAG_NO_EXTRA_INFO
                    ).versionName
                }

                PreferenceBodyText("v$versionName")
            }
        }
    }

    @Composable
    private fun PreferencesDivider() = Divider(Modifier.fillMaxWidth())

    @Composable
    private fun CreditsPreference() {
        PreferenceSection(stringResource(R.string.preferenceScreen_sectionTitle_credits)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MyCredits(prefix = "● ")
                PreferenceBodyText(
                    text = stringResource(R.string.credits_appIcon),
                    prefix = "● "
                )
            }
        }
    }

    @Composable
    private fun ThemeSelectionPreference(
        currentTheme: ThemePreference,
        onThemeSelected: OnThemeSelected
    ) {
//        stopped here TIME FOR TESTING!!! first go over all the new tests. then test the real app
//        AND THEME CHANGING!

        PreferenceSection(stringResource(R.string.preferenceScreen_sectionTitle_theme)) {
            Column {
                ThemePreference.entries.forEach { preference ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(preference) }
                    ) {
                        RadioButton(
                            selected = currentTheme == preference,
                            onClick = { onThemeSelected(preference) }
                        )

                        PreferenceBodyText(stringResource(preference.labelRes))
                    }
                }
            }
        }
    }

    @Composable
    private fun PreferenceBodyText(
        text: String,
        prefix: String = ""
    ) {
        Text(
            text = prefix + text,
            style = MaterialTheme.typography.body1
        )
    }

    @Composable
    private fun PreferenceSection(title: String, content: @Composable () -> Unit) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1
            )

            Spacer(Modifier.height(16.dp))
            content()
        }
    }

    @Composable
    private fun MyCredits(prefix: String = "") =
        PreferenceBodyText(
            text = stringResource(R.string.credits_me),
            prefix = prefix
        )

    @Preview(showBackground = true, backgroundColor = 0xffffffff)
    @Composable
    private fun ScreenPreview() = PreferencesScreenContent(
        currentTheme = ThemePreference.LIGHT,
        onPreferencesEvent = { }
    )

    @Preview(showBackground = true, backgroundColor = 0xffffffff)
    @Composable
    private fun FooterCreditsPreview() {
        Column {
            Spacer(Modifier.height(32.dp))
            FooterCredits()
        }
    }

    @Preview(showBackground = true, backgroundColor = 0xffffffff)
    @Composable
    private fun CreditsPreferencePreview() = CreditsPreference()

    @Preview(showBackground = true, backgroundColor = 0xffffffff)
    @Composable
    private fun ThemeSelectionPreferencePreview() {
        ThemeSelectionPreference(
            currentTheme = ThemePreference.DARK,
            onThemeSelected = { }
        )
    }
}
