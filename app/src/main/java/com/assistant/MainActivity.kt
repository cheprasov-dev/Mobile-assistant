package com.assistant

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.assistant.ui.theme.AssistantTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "Grate init MainActivity")
        val batteryCheckRequest = PeriodicWorkRequestBuilder<BatteryCheckWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "batteryCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            batteryCheckRequest
        )
        setContent {
            AssistantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    ComposablePermission(permission = "android.permission.RECEIVE_SMS", {}, {})
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun ComposablePermission(
    permission: String,
    onDenied: @Composable (requester: () -> Unit) -> Unit,
    onGranted: @Composable () -> Unit
) {
    val ctx = LocalContext.current

    var grantState by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                ctx,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    if (grantState) {
        Log.i("Main Activity", "Permission is granted")
        onGranted()
    } else {
        val launcher: ManagedActivityResultLauncher<String, Boolean> =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
                grantState = it
            }
        Log.i("Main Activity", "Permission isn't granted")
        SideEffect {
            launcher.launch(permission)
        }
//        onDenied { launcher.launch(permission) }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AssistantTheme {
        Greeting("Android")
    }
}