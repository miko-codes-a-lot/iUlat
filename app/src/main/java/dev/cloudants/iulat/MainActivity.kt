package dev.cloudants.iulat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.cloudants.iulat.lib.services_impl.UserServiceImpl
import dev.cloudants.iulat.lib.utils.main.MainNav
import dev.cloudants.iulat.lib.utils.main.mainGraph
import dev.cloudants.iulat.ui.theme.IUlatTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IUlatTheme {
                val navController = rememberNavController()

//                CompositionLocalProvider(UserState provides userState) {
//                    val startingDestination = getStartDestination(loginViewModel)
                NavHost(navController = navController, startDestination = MainNav) {
//                        introGraph(navController)
                    mainGraph(navController)
                }
            }
        }
    }
}