package com.hotmail.or_dvir.sabinesList.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hotmail.or_dvir.sabinesList.R

internal typealias OnMenuItemClicked = (item: MenuItemInfo) -> Unit

sealed class MenuItemInfo(
    @DrawableRes val iconRes: Int,
    @StringRes val label: Int
) {
    object Search : MenuItemInfo(iconRes = R.drawable.ic_search, label = R.string.menuItem_search)

    object UncheckAll :
        MenuItemInfo(iconRes = R.drawable.ic_uncheck_all, label = R.string.menuItem_uncheckAll)

    object Share : MenuItemInfo(iconRes = R.drawable.ic_share, label = R.string.menuItem_share)

    object Preferences :
        MenuItemInfo(iconRes = R.drawable.ic_preferences, label = R.string.menuItem_preferences)
}