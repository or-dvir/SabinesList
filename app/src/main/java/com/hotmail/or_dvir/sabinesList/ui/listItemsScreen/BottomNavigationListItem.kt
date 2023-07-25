package com.hotmail.or_dvir.sabinesList.ui.listItemsScreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hotmail.or_dvir.sabinesList.R

sealed class BottomNavigationListItem(
    @StringRes val textRes: Int,
    @StringRes val contentDescriptionRes: Int,
    @DrawableRes val iconRes: Int
) {
    object AllItems : BottomNavigationListItem(
        textRes = R.string.bottomNavigation_all,
        contentDescriptionRes = R.string.bottomNavigation_contentDescription_all,
        iconRes = R.drawable.bottom_navigation_all
    )
    object CheckedItems : BottomNavigationListItem(
        textRes = R.string.bottomNavigation_checked,
        contentDescriptionRes = R.string.bottomNavigation_contentDescription_checked,
        iconRes = R.drawable.bottom_navigation_checked
    )
    object UncheckedItems : BottomNavigationListItem(
        textRes = R.string.bottomNavigation_unchecked,
        contentDescriptionRes = R.string.bottomNavigation_contentDescription_unchecked,
        iconRes = R.drawable.bottom_navigation_unchecked
    )
}
