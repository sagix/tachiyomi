package eu.kanade.presentation.more

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.GetApp
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.Divider
import eu.kanade.presentation.components.PreferenceRow
import eu.kanade.presentation.components.Scaffold
import eu.kanade.presentation.components.ScrollbarLazyColumn
import eu.kanade.presentation.components.SwitchPreference
import eu.kanade.presentation.util.bottomNavPaddingValues
import eu.kanade.presentation.util.plus
import eu.kanade.presentation.util.quantityStringResource
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.more.DownloadQueueState
import eu.kanade.tachiyomi.ui.more.MoreController
import eu.kanade.tachiyomi.ui.more.MorePresenter

@Composable
fun MoreScreen(
    presenter: MorePresenter,
    onClickDownloadQueue: () -> Unit,
    onClickCategories: () -> Unit,
    onClickBackupAndRestore: () -> Unit,
    onClickSettings: () -> Unit,
    onClickAbout: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val downloadQueueState by presenter.downloadQueueState.collectAsState()

    Scaffold(
        topBar = { scrollBehavior ->
            AppBar(
                title = stringResource(R.string.label_more),
                downloadedOnlyMode = presenter.downloadedOnly.value,
                incognitoMode = presenter.incognitoMode.value,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        ScrollbarLazyColumn(
            contentPadding = contentPadding + bottomNavPaddingValues,
        ) {
            item {
                LogoHeader()
            }

            item {
                SwitchPreference(
                    preference = presenter.downloadedOnly,
                    title = stringResource(R.string.label_downloaded_only),
                    subtitle = stringResource(R.string.downloaded_only_summary),
                    painter = rememberVectorPainter(Icons.Outlined.CloudOff),
                )
            }
            item {
                SwitchPreference(
                    preference = presenter.incognitoMode,
                    title = stringResource(R.string.pref_incognito_mode),
                    subtitle = stringResource(R.string.pref_incognito_mode_summary),
                    painter = painterResource(R.drawable.ic_glasses_24dp),
                )
            }

            item { Divider() }

            item {
                PreferenceRow(
                    title = stringResource(R.string.label_download_queue),
                    subtitle = when (downloadQueueState) {
                        DownloadQueueState.Stopped -> null
                        is DownloadQueueState.Paused -> {
                            val pending = (downloadQueueState as DownloadQueueState.Paused).pending
                            if (pending == 0) {
                                stringResource(R.string.paused)
                            } else {
                                "${stringResource(R.string.paused)} • ${
                                quantityStringResource(
                                    R.plurals.download_queue_summary,
                                    pending,
                                    pending,
                                )
                                }"
                            }
                        }
                        is DownloadQueueState.Downloading -> {
                            val pending = (downloadQueueState as DownloadQueueState.Downloading).pending
                            quantityStringResource(R.plurals.download_queue_summary, pending, pending)
                        }
                    },
                    painter = rememberVectorPainter(Icons.Outlined.GetApp),
                    onClick = onClickDownloadQueue,
                )
            }
            item {
                PreferenceRow(
                    title = stringResource(R.string.categories),
                    painter = rememberVectorPainter(Icons.Outlined.Label),
                    onClick = onClickCategories,
                )
            }
            item {
                PreferenceRow(
                    title = stringResource(R.string.label_backup),
                    painter = rememberVectorPainter(Icons.Outlined.SettingsBackupRestore),
                    onClick = onClickBackupAndRestore,
                )
            }

            item { Divider() }

            item {
                PreferenceRow(
                    title = stringResource(R.string.label_settings),
                    painter = rememberVectorPainter(Icons.Outlined.Settings),
                    onClick = onClickSettings,
                )
            }
            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_category_about),
                    painter = rememberVectorPainter(Icons.Outlined.Info),
                    onClick = onClickAbout,
                )
            }
            item {
                PreferenceRow(
                    title = stringResource(R.string.label_help),
                    painter = rememberVectorPainter(Icons.Outlined.HelpOutline),
                    onClick = { uriHandler.openUri(MoreController.URL_HELP) },
                )
            }
        }
    }
}
