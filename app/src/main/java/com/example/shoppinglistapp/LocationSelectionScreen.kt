package com.example.shoppinglistapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationSelectionScreen(
    location: LocationData,
    onLocationSelected: (LocationData) -> Unit
) {
    // Holds the current user-selected location on the map (initially set to the passed-in location)
    val userLocation = remember {
        mutableStateOf(LatLng(location.latitude, location.longitude))
    }

    // Defines and remembers the camera position state for the Google Map,
    // initially centered on the userLocation with a zoom level of 10
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value, 10f)
    }

    // Main layout container
    Column(modifier = Modifier.fillMaxSize()) {

        // Google Map view that takes up most of the screen
        GoogleMap(
            modifier = Modifier
                .weight(1f) // Makes the map take up available vertical space
                .padding(top = 16.dp), // Adds padding at the top
            cameraPositionState = cameraPositionState, // Controls the camera position
            onMapClick = {
                // Updates the userLocation when the map is clicked
                userLocation.value = it
            }
        ) {
            // Places a marker on the map at the current user-selected location
            Marker(state = MarkerState(position = userLocation.value))
        }

        // Variable to hold the newly selected location before passing it back
        var newLocation: LocationData

        // Button to confirm the location selection
        Button(onClick = {
            // Creates a new LocationData object from the selected location
            newLocation = LocationData(userLocation.value.latitude, userLocation.value.longitude)
            // Passes the selected location back to the parent composable
            onLocationSelected(newLocation)
        }) {
            Text("Set Location") // Button label
        }
    }
}
