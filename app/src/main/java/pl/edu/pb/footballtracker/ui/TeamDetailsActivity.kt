package pl.edu.pb.footballtracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import pl.edu.pb.footballtracker.databinding.ActivityTeamDetailsBinding
import pl.edu.pb.footballtracker.loadSvg

class TeamDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamDetailsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val teamName = intent.getStringExtra("TEAM_NAME") ?: "Drużyna"
        val teamBadge = intent.getStringExtra("TEAM_BADGE") ?: ""

        binding.tvDetailName.text = teamName
        binding.ivDetailBadge.loadSvg(teamBadge)

        supportActionBar?.title = teamName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obsługa kliknięcia przycisku GPS
        binding.btnCheckDistance.setOnClickListener {
            calculateDistanceForTeam(teamName)
        }
    }

    private fun calculateDistanceForTeam(name: String) {
        val (lat, lng) = when {
            name.contains("Arsenal") -> 51.5549 to -0.1084           // Emirates Stadium
            name.contains("Aston Villa") -> 52.4824 to -1.8848       // Villa Park
            name.contains("Chelsea") -> 51.4817 to -0.1910           // Stamford Bridge
            name.contains("Everton") -> 53.4389 to -2.9664           // Goodison Park
            name.contains("Fulham") -> 51.4750 to -0.2217            // Craven Cottage
            name.contains("Liverpool") -> 53.4308 to -2.9608         // Anfield
            name.contains("Manchester City") -> 53.4831 to -2.2004   // Etihad Stadium
            name.contains("Manchester United") -> 53.4631 to -2.2913 // Old Trafford
            name.contains("Newcastle") -> 54.9756 to -1.6217         // St James' Park
            name.contains("Sunderland") -> 54.9146 to -1.3882        // Stadium of Light
            name.contains("Tottenham") -> 51.6042 to -0.0662         // Tottenham Hotspur Stadium
            name.contains("Wolverhampton") -> 52.5902 to -2.1304     // Molineux Stadium
            name.contains("Burnley") -> 53.7892 to -2.2302           // Turf Moor
            name.contains("Leeds") -> 53.7778 to -1.5722             // Elland Road
            name.contains("Nottingham") -> 52.9400 to -1.1328        // City Ground
            name.contains("Crystal Palace") -> 51.3983 to -0.0855    // Selhurst Park
            name.contains("Brighton") -> 50.8618 to -0.0837          // Amex Stadium
            name.contains("Brentford") -> 51.4906 to -0.2886         // Gtech Community Stadium
            else -> 52.2394 to 21.0458                               // Domyślnie: Stadion Narodowy (Warszawa)
        }

        checkLocationPermissionAndCalculate(lat, lng)
    }

    private fun checkLocationPermissionAndCalculate(stadiumLat: Double, stadiumLng: Double) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val stadiumLocation = Location("").apply {
                    latitude = stadiumLat
                    longitude = stadiumLng
                }

                val distanceInKm = location.distanceTo(stadiumLocation) / 1000
                Toast.makeText(this, "Do stadionu masz ok. %.2f km".format(distanceInKm), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Ustaw lokalizację w emulatorze!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}