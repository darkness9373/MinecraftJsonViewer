package org.darkness.jsonuiview.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.darkness.jsonuiview.ui.formlist.FormListScreen
import org.darkness.jsonuiview.ui.main.MainScreen
import org.darkness.jsonuiview.ui.settings.SettingsScreen
import org.darkness.jsonuiview.ui.testform.TestFormScreen
import org.darkness.jsonuiview.ui.viewer.FormViewerScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Main.route,
    onThemeChange: (org.darkness.jsonuiview.util.PreferencesManager.ThemeMode) -> Unit
) {
    val themeMode by remember { mutableStateOf(org.darkness.jsonuiview.util.PreferencesManager.ThemeMode.System) }
    
    NavHost(navController, startDestination) {
        composable(Screen.Main.route) {
            MainScreen(onFileSelected = { uri ->
                navController.navigate(Screen.FormList.route.replace("{fileUri}", uri))
            })
        }
        
        composable(
            route = Screen.FormList.route,
            arguments = listOf(androidx.navigation.navArgument("fileUri") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val fileUri = backStackEntry.getString()!!
            FormListScreen(
                fileUri = fileUri,
                onFormClick = { form ->
                    navController.navigate(Screen.FormViewer.route.replace("{formId}", form.id))
                },
                onTestFormClick = { form ->
                    navController.navigate(Screen.TestForm.route.replace("{formId}", form.id))
                }
            )
        }
        
        composable(
            route = Screen.FormViewer.route,
            arguments = listOf(androidx.navigation.navArgument("formId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val formId = backStackEntry.getString()!!
            FormViewerScreen(
                formId = formId,
                onTestClick = { form ->
                    navController.navigate(Screen.TestForm.route.replace("{formId}", form.id))
                }
            )
        }
        
        composable(
            route = Screen.TestForm.route,
            arguments = listOf(androidx.navigation.navArgument("formId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val formId = backStackEntry.getString()!!
            TestFormScreen(formId = formId)
        }
        
        composable(Screen.TestFormNew.route) {
            TestFormScreen(formId = null)
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(onThemeChange = onThemeChange)
        }
    }
}