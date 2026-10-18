package org.darkness.jsonuiview.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object FormList : Screen("form_list/{fileUri}")
    object FormViewer : Screen("viewer/{formId}")
    object TestForm : Screen("test_form/{formId}")
    object TestFormNew : Screen("test_form_new")
    object Settings : Screen("settings")
}