package com.example.tvremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tvremote.network.SignalingClient
import com.example.tvremote.ui.theme.TVRemoteAppTheme
import com.example.tvremote.webrtc.WebRTCClient
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TVRemoteAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val signalingClient = remember { SignalingClient() }
    val webRTCClient = remember { WebRTCClient(context, signalingClient) }

    NavHost(navController = navController, startDestination = "role_selection") {
        composable("role_selection") {
            RoleSelectionScreen(navController)
        }
        composable("helper_mode") {
            RemoteControlScreen(signalingClient)
        }
        composable("helpee_mode") {
            CameraScreen(signalingClient, webRTCClient)
        }
    }
}

@Composable
fun RoleSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to TV Remote Assistance",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("helper_mode") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("I'm Helping", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("helpee_mode") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("I Need Help", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun RemoteControlScreen(signalingClient: SignalingClient) {
    // TODO: Connect to signaling server
    // signalingClient.connect()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Power Button
        Button(onClick = { signalingClient.send("power") }) {
            Icon(Icons.Default.PowerSettingsNew, contentDescription = "Power")
        }

        // Volume and Channel Rockers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Volume
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { signalingClient.send("volume_up") }) {
                    Icon(Icons.Default.Add, contentDescription = "Volume Up")
                }
                Text("VOL")
                Button(onClick = { signalingClient.send("volume_down") }) {
                    Icon(Icons.Default.Remove, contentDescription = "Volume Down")
                }
            }
            // Channel
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { signalingClient.send("channel_up") }) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Channel Up")
                }
                Text("CH")
                Button(onClick = { signalingClient.send("channel_down") }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Channel Down")
                }
            }
        }

        // D-Pad
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { signalingClient.send("dpad_up") }) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up")
            }
            Row {
                Button(onClick = { signalingClient.send("dpad_left") }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left")
                }
                Spacer(modifier = Modifier.width(80.dp))
                Button(onClick = { signalingClient.send("dpad_right") }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right")
                }
            }
            Button(onClick = { signalingClient.send("dpad_down") }) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down")
            }
        }
        
        // OK Button
        Button(onClick = { signalingClient.send("ok") }) {
            Text("OK")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(signalingClient: SignalingClient, webRTCClient: WebRTCClient) {
    // TODO: Connect to signaling server
    // signalingClient.connect()
    // webRTCClient.createPeerConnection()

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        CameraPreview()
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Camera permission is required to show the TV screen.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
fun CameraPreview() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    ) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
            } catch (exc: Exception) {
                // Handle exceptions
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    TVRemoteAppTheme {
        val navController = rememberNavController()
        RoleSelectionScreen(navController)
    }
}
