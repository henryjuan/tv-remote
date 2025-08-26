# TV Remote Assistance App

This is an Android application that allows a user to remotely control another person's TV. It is designed to help non-technical users with TV troubleshooting.

## Features

*   **Remote Assistance:** One user can act as a "helper" and another as a "helpee".
*   **Remote Control UI:** The helper has a full remote control interface on their screen.
*   **Camera Streaming:** The helpee's phone streams the TV screen to the helper using the camera.
*   **Real-time Communication:** The app uses WebRTC for video streaming and WebSockets for sending commands.

## Project Structure

The project is structured as a standard Android application with the following key components:

*   `app/src/main/java/com/example/tvremote/MainActivity.kt`: The main activity of the application, which contains the Jetpack Compose UI.
*   `app/src/main/java/com/example/tvremote/network/SignalingClient.kt`: A client for handling WebSocket communication for signaling.
*   `app/src/main/java/com/example/tvremote/webrtc/WebRTCClient.kt`: A client for managing the WebRTC peer-to-peer connection.

## How to Build

1.  Clone the repository: `git clone https://github.com/henryjuan/tv-remote.git`
2.  Open the project in Android Studio.
3.  Build the project.

*(Note: The networking part is not fully implemented yet. A signaling server is required for the app to function.)*
