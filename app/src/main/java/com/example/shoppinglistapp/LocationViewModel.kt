package com.example.shoppinglistapp

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for holding the user’s **current geographic location**
 * (latitude & longitude) and the **human‑readable address** that we get back
 * when we reverse‑geocode those coordinates.
 *
 * Because these properties are exposed as `State`, every time we assign a new
 * value Compose will automatically *recompose* the parts of the UI that read
 * them – no manual observe/notify calls needed.
 */
class LocationViewModel : ViewModel() {

    // ─────────────────────────── LOCATION ────────────────────────────────

    /**
     * Backing field that actually stores the latest [LocationData].
     * It is **mutable** but *private* so **only** the ViewModel can change it.
     */
    private val _location = mutableStateOf<LocationData?>(null)

    /**
     * Public **read‑only** view that the UI layer observes. Whenever we set a
     * new value on `_location`, this `State` object notifies its observers so
     * the screen updates automatically.
     */
    val location: State<LocationData?> = _location


    // ─────────────────────────── ADDRESS ─────────────────────────────────

    /**
     * Backing field that holds the list of addresses returned by the Google
     * Geocoding API. Multiple results are possible (e.g. street level,
     * city level, country level), so we store them in a list.
     */
    private val _address = mutableStateOf(listOf<GeocodingResult>())

    /**
     * Public **read‑only** view of the address list so the UI can present it.
     */
    val address: State<List<GeocodingResult>> = _address


    // ───────────────────────── PUBLIC API ────────────────────────────────

    /**
     * Update the stored location. Typically called by a location provider
     * (e.g. FusedLocationProviderClient) whenever new GPS data is received.
     */
    fun updateLocation(newLocation: LocationData) {
        _location.value = newLocation
    }

    /**
     * Reverse‑geocode the given coordinates by calling Google’s Geocoding
     * REST endpoint. The heavy network work happens inside a coroutine tied
     * to `viewModelScope`, so it respects the ViewModel’s lifecycle.
     *
     * @param latlng A string formatted as "lat,lng" (e.g. "37.4219983,-122.084").
     *
     *  **Security note:** Never hard‑code API keys in production code. Move
     * them to a secure location (BuildConfig, encrypted storage, remote
     * config, etc.) so they don’t ship inside the APK.
     */
    fun fetchAddress(latlng: String) {
        try {
            // Launch a coroutine; if the ViewModel is cleared, it cancels automatically.
            viewModelScope.launch {
                // Perform the network request off the main thread.
                val result = RetrofitClient.create().getAddressFromCoordinates(
                    latlng,
                    "AIzaSyD7cbh_baEdfEABmpgOhmE15EbxuF8OB5g" // TODO: Replace with your own key
                )
                // Update the state; UI will recompose when this line runs.
                _address.value = result.results
            }
        } catch (e: Exception) {
            // Log the exception so it shows up in Logcat during debugging.
            Log.d("LocationViewModel", "${e.cause} ${e.message}")
        }
    }
}
