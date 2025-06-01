// En app/src/androidTest/java/com/example/test/ui/screens/InitialScreenTest.kt
package com.example.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.test.ui.auth.AuthViewModel
import com.example.test.ui.theme.TestTheme

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.onNodeWithContentDescription

import com.example.test.data.local.AppDatabase
import com.example.test.data.repository.UserRepository
import com.example.test.ui.screens.InitialScreen


import org.junit.Assert.*


@RunWith(AndroidJUnit4::class)
class InitialScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    private var navigateToRegistrationCalled = false
    private var navigateToLoginCalled = false
    private var googleLoginSuccessCalled = false


    private lateinit var fakeAuthViewModel: AuthViewModel



    fun setUp() {
        navigateToRegistrationCalled = false
        navigateToLoginCalled = false
        googleLoginSuccessCalled = false


        composeTestRule.setContent {

            val context = LocalContext.current

            fakeAuthViewModel = AuthViewModel(UserRepository(AppDatabase.getDatabase(context).userDao()))

            TestTheme { // O el nombre de tu tema
                InitialScreen(
                    authViewModel = fakeAuthViewModel, // Pasamos el ViewModel
                    onNavigateToRegistration = { navigateToRegistrationCalled = true },
                    onNavigateToLogin = { navigateToLoginCalled = true },
                    onGoogleLoginSuccess = {
                        googleLoginSuccessCalled = true
                    } // La lambda que recibe userId y role
                )
            }
        }
    }

    @Test
    fun initialScreen_displaysAllButtons() {

        composeTestRule.onNodeWithText("Iniciar sesión").assertIsDisplayed()

        composeTestRule.onNodeWithText("Ya soy fan/pro").assertIsDisplayed()


        composeTestRule.onNodeWithText("Registrarme").assertIsDisplayed()

        composeTestRule.onNodeWithText("Deseo ser fan/pro").assertIsDisplayed()

        composeTestRule.onNodeWithText("Iniciar sesión con Google").assertIsDisplayed()
    }

    @Test
    fun initialScreen_navigateToLoginButton_callsLambda() {

        composeTestRule.onNodeWithText("Iniciar sesión").performClick()
        assertTrue(navigateToLoginCalled)
        assertFalse(navigateToRegistrationCalled)
        assertFalse(googleLoginSuccessCalled)
    }

    @Test
    fun initialScreen_navigateToRegistrationButton_callsLambda() {

        composeTestRule.onNodeWithText("Registrarme").performClick()

        assertTrue(navigateToRegistrationCalled)
        assertFalse(navigateToLoginCalled)
        assertFalse(googleLoginSuccessCalled)
    }

    @Test
    fun initialScreen_googleSignInButton_isClickable() {
        composeTestRule.onNodeWithText("Iniciar sesión con Google").assertIsDisplayed()
        composeTestRule.onNodeWithText("Iniciar sesión con Google").performClick()
    }
    @Test
    fun initialScreen_displaysBannerImage() {
        composeTestRule.onNodeWithContentDescription("Banner GGDROP Be fan. Be pro.").assertIsDisplayed()
    }
}