package id.yukngoding.explore_firebase

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.yukngoding.explore_firebase.ui.theme.Explore_FirebaseTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var auth: FirebaseAuth

    private lateinit var signInClient: SignInClient

    private fun setupFirebase() {
        // Configure Google Sign In
        signInClient = Identity.getSignInClient(this)

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFirebase()

        setContent {
            MainScreen(
                name = auth.currentUser?.displayName.toString(),
                email = auth.currentUser?.email.toString(),
                onClickSignOut = {
                    signOut()
                }
            )
        }
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        signInClient.signOut().addOnCompleteListener(this) {
            startActivity(
                Intent(this, SignInActivity::class.java)
            )
        }
    }
}

@Composable
fun MainScreen(
    name: String,
    email: String,
    onClickSignOut: () -> Unit
) {
    Explore_FirebaseTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    "Welcome $name",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    email,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color(0xFFADB5BD)
                )

                // Crashlytics
                Button(
                    modifier = Modifier
                        .padding(top = 36.dp)
                        .fillMaxWidth(),
                    onClick = {
                        // Force a crash
                        throw RuntimeException("Test Crash")
                    }) {
                    Text(text = "Firebase Crashlytics, test crash")
                }
                // Auth
                Button(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    onClick = {
                        onClickSignOut()
                    }) {
                    Text(text = "Firebase Auth, Sign Out")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Explore_FirebaseTheme {
        MainScreen(
            name = "Ikhwan Koto",
            email = "ikhwan220397@gmail.com",
            onClickSignOut = {}
        )
    }
}