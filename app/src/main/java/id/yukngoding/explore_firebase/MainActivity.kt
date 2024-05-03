package id.yukngoding.explore_firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import id.yukngoding.explore_firebase.ui.theme.Explore_FirebaseTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    // FirebaseAuth needs
    private lateinit var auth: FirebaseAuth
    private lateinit var signInClient: SignInClient

    // FirebaseCloudMessaging needs
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                name = auth.currentUser?.displayName.toString(),
                email = auth.currentUser?.email.toString(),
                onClickFCMLogToken = {
                    fcmLogToken()
                },
                onClickSignOut = {
                    signOut()
                }
            )
        }

        setupFirebase()
        setupFirebaseCloudMessaging()
        askNotificationPermission()
    }

    private fun setupFirebase() {
        // Configure Google Sign In
        signInClient = Identity.getSignInClient(this)

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    private fun setupFirebaseCloudMessaging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun fcmLogToken() {
        Firebase.messaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = "FCM registration token: $token"
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            },
        )
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
    onClickFCMLogToken: () -> Unit,
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
                // Cloud Messaging
                Button(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    onClick = {
                        onClickFCMLogToken()
                    }) {
                    Text(text = "Firebase Cloud Messaging, Log token")
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
            onClickFCMLogToken = {},
            onClickSignOut = {}
        )
    }
}