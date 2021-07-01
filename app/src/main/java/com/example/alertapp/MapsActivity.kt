package com.example.alertapp

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.alert_dialog.*
import org.json.JSONObject
import kotlinx.coroutines.*
import org.json.JSONArray
import java.util.*
import kotlin.reflect.typeOf


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    //private var dialog: Dialog? = null
    private var mapView: View? = null
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var mainHandler: Handler
    private lateinit var secondHandler: Handler
    //marker informations
    var lastData: Array<alerts>? = null
    private var markerList:ArrayList<Marker> = ArrayList()
    private var testMarkerList:ArrayList<Marker> = ArrayList()
    //private var currentMarkerList:ArrayList<Marker> = ArrayList()
    private var queue: RequestQueue
        get() = Volley.newRequestQueue(this)
        set(value) = TODO()
    val url = "http://192.168.1.30:4000/"
    val gson = Gson()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = (supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment).also {
            mapView = it.getView()
            it.getMapAsync(this)
        }
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.setInterval(1500)
        locationRequest.setFastestInterval((1500))
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        //defining locationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                var latlng = LatLng(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                }
            }

        // Handler for data polling
        mainHandler = Handler(Looper.getMainLooper())
        // Handler fro removing expired alerts
        secondHandler = Handler(Looper.getMainLooper())
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Setting the map style
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))

        // permission check and request
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("String", "geolocation permission missing! ( TODO )")
            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions,0)
            return
        }
        else{
            Log.i("String", "geolocation enabled! ( OK )")
            mMap.isMyLocationEnabled = true
        }
        // Zoom settings
        mMap.setMinZoomPreference(16.0f)
        mMap.setMaxZoomPreference(20.0f)

        // Other settings
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = true

        // Changing the MyLocationButton position
        val locationButton= (mapView?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)

        // Position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
        rlp.setMargins(0,0,30,30);

        mMap.setOnMapLongClickListener(GoogleMap.OnMapLongClickListener(){
            OnMapLongClick(it)
        })

        //focus on the user location
        CenterOnUserLocation()
    }

    override fun onStart() {
        super.onStart()
        // Starting the position tracker
        StartLocationUpdate()
        // Starting the data update functions
        secondHandler.post(updateExpiredMarkers)
        mainHandler.post(updateAlerts)

    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateAlerts)
        secondHandler.removeCallbacks(updateExpiredMarkers)
        StopLocationUpdate()
    }


    fun StartLocationUpdate(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun StopLocationUpdate(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocation.removeLocationUpdates(locationCallback)
    }

    // LongClick on Map
    fun OnMapLongClick(latLng: LatLng){
        //Log.i("String","longClick: $latLng")

        // Creating the Dialog window from a custom layout
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.alert_dialog)
        dialog.window?.setBackgroundDrawableResource(R.color.TRANSPARENT)
        dialog.setCancelable(true)
        dialog.show()

        var submit_button = dialog.findViewById<Button>(R.id.button_submit)
        var alert_description:EditText = dialog.findViewById<EditText>(R.id.alert_description)
        var radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
        var alert_level_selected = 1
        radioGroup.setOnCheckedChangeListener{group, checkedID ->
            if(checkedID == R.id.rbAlert1) alert_level_selected = 1
            if(checkedID == R.id.rbAlert2) alert_level_selected = 2
            else alert_level_selected = 3
        }
        // Submit button listener
        submit_button.setOnClickListener(View.OnClickListener(){

            var alert_object = JSONObject("""{"alertLevel":"${alert_level_selected}","text":"${alert_description.text}","location":{"coordinates":[${latLng.latitude},${latLng.longitude}],"type":"Point"}}""")
            val stringRequest = JsonObjectRequest(Request.Method.POST, url+"addAlertToApi", alert_object,
                {
                        Log.i("String", "\"add request is valid!")
                        // Get your json response and convert it to whatever you want.
                    },
                {
                    Log.i("String", "Add didn't work!\nerror: ${it.toString()}")
                    // Error
                })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
        dialog.dismiss()
        })
    }

    // Click on setting button
    fun openSettings(view: View) {}

    fun CenterOnUserLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        else{
            fusedLocation.lastLocation.addOnSuccessListener { location : Location? ->
                var latlng = LatLng(
                    location!!.latitude,
                    location!!.longitude
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
            }
        }
    }

    // For updating alerts data
    private val updateAlerts = object : Runnable {
        override fun run() {
            //QUERY
            getAlerts()
            drawMarkers()
            mainHandler.postDelayed(this, 3000)
        }
    }
    // For clearing expired alerts
    private val updateExpiredMarkers = object : Runnable {
        override fun run() {
            mMap.clear()
            getAlerts()
            secondHandler.postDelayed(this, 180000)
        }
    }

    // Draws the alerts on the map (markers with custom icons)
    private fun drawMarkers(){
        var currentMarkers:ArrayList<Marker> = ArrayList()
        var tempMarker: Marker? = null
        var found = false
        if(lastData != null){
            for (i in lastData!!) {
                    var latlng = LatLng(i.location.coordinates[0], i.location.coordinates[1])
                    when (i.alertLevel) {
                        2 ->{
                            tempMarker = mMap.addMarker(
                                MarkerOptions()
                                    .position(latlng)
                                    .title(i.text)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.alert_2))
                            )
                            currentMarkers.add(tempMarker)
                            if(markerList.isEmpty()) {
                                markerList.add(tempMarker)
                                found = true
                            }
                            else{
                                for (e in markerList){
                                    if(tempMarker.position == e.position){
                                        //tempMarker.remove()
                                        found = true
                                        break
                                    }
                                    else{
                                        continue
                                    }
                                }
                            }
                        }
                        3 ->{
                            tempMarker = mMap.addMarker(
                                MarkerOptions()
                                    .position(latlng)
                                    .title(i.text)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.alert_3))
                            )
                            currentMarkers.add(tempMarker)
                            if(markerList.isEmpty()) {
                                markerList.add(tempMarker)
                                found = true
                            }
                            else{
                                for (e in markerList){
                                    if(tempMarker.position == e.position){
                                        //tempMarker.remove()
                                        found = true
                                        break
                                    }
                                    else{
                                        continue
                                    }
                                }
                            }
                        }
                        1 -> {
                            tempMarker = mMap.addMarker(
                                MarkerOptions()
                                    .position(latlng)
                                    .title(i.text)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.alert_1))
                            )
                            currentMarkers.add(tempMarker)
                            if(markerList.isEmpty()) {
                                markerList.add(tempMarker)
                                found = true
                            }
                            else{
                                for (e in markerList){
                                    if(tempMarker.position == e.position){
                                        //tempMarker.remove()
                                        found = true
                                        break
                                    }
                                    else{
                                        continue
                                    }
                                }
                            }
                            if(!found) {
                                //Log.i("String","found = $found  ==> merkerlistaAdd")
                                markerList.add(tempMarker)
                            }
                            found = false
                        }
                    }
            }
        }
    }

    // Query with support of Geolocation
    private fun getAlerts(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        else {
            fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
                var latlng = LatLng(
                    location!!.latitude,
                    location!!.longitude
                )
                val stringRequest = JsonArrayRequest(
                    Request.Method.GET, url + "getAlert?coords=${latlng.latitude}&coords=${latlng.longitude}", null,
                    { response ->
                        //parse test
                        var data = gson.fromJson(response.toString(), Array<alerts>::class.java)
                        lastData = data
                    },
                    { response ->
                        Log.i("String", "That didn't work!")
                    }
                )
                // Add the request to the RequestQueue.
                queue.add(stringRequest)
            }
        }
    }
}