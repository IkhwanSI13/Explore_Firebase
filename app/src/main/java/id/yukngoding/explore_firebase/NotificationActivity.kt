package id.yukngoding.explore_firebase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.yukngoding.explore_firebase.ui.theme.Explore_FirebaseTheme

class NotificationActivity : ComponentActivity() {
    companion object {
        private const val TAG = "NotificationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        var value = "Empty"
        intent.extras?.let {
            value = it.getString(
                MyFirebaseMessagingService.KEY_VALUE, "Default"
            )
        }
        // [END handle_data_extras]

        setContent {
            NotificationScreen(
                value = value
            )
        }
    }

}


@Composable
fun NotificationScreen(
    value: String,
) {
    Explore_FirebaseTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Text(
                "Notification Screen",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(24.dp)
            )
            Box(
                modifier = Modifier.padding(24.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    value,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPreview() {
    Explore_FirebaseTheme {
        NotificationScreen(
            value = "null",
        )
    }
}