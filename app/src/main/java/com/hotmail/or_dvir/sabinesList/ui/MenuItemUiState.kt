package com.hotmail.or_dvir.sabinesList.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hotmail.or_dvir.sabinesList.R

internal typealias OnMenuItemClicked = (item: MenuItemUiState) -> Unit

internal sealed class MenuItemUiState(
    @DrawableRes val iconRes: Int,
    @StringRes val label: Int,
    val isEnabled: Boolean = true
) {
    data class Search(val enabled: Boolean = true) :
        MenuItemUiState(
            iconRes = R.drawable.ic_search,
            label = R.string.menuItem_search,
            isEnabled = enabled
        )

    data class UncheckAll(val enabled: Boolean = true) :
        MenuItemUiState(
            iconRes = R.drawable.ic_uncheck_all,
            label = R.string.menuItem_uncheckAll,
            isEnabled = enabled
        )

    data class Share(val enabled: Boolean = true) :
        MenuItemUiState(
            iconRes = R.drawable.ic_share,
            label = R.string.menuItem_share,
            isEnabled = enabled
        )

    object Preferences :
        MenuItemUiState(iconRes = R.drawable.ic_preferences, label = R.string.menuItem_preferences)
}
