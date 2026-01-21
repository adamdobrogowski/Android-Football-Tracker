package pl.edu.pb.footballtracker.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import pl.edu.pb.footballtracker.BuildConfig
import pl.edu.pb.footballtracker.R
import pl.edu.pb.footballtracker.databinding.ActivityTeamDetailsBinding
import pl.edu.pb.footballtracker.di.RetrofitInstance
import pl.edu.pb.footballtracker.loadSvg

class TeamDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamDetailsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val teamId = intent.getIntExtra("TEAM_ID", -1)
        val teamName = intent.getStringExtra("TEAM_NAME") ?: "Drużyna"
        val teamBadge = intent.getStringExtra("TEAM_BADGE") ?: ""

        binding.tvDetailName.text = teamName
        binding.ivDetailBadge.loadSvg(teamBadge)

        supportActionBar?.title = teamName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (teamId != -1) {
            loadLastMatch(teamId)
        }

        binding.btnCheckDistance.setOnClickListener {
            calculateDistanceForTeam(teamName)
        }

        binding.btnYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$teamName+highlights"))
            startActivity(intent)
        }
    }

    private fun loadLastMatch(teamId: Int) {
        lifecycleScope.launch {
            try {
                val token = BuildConfig.FOOTBALL_API_TOKEN
                val response = RetrofitInstance.api.getTeamMatches(teamId, token)

                val lastMatch = response.matches?.firstOrNull()

                if (lastMatch != null) {
                    binding.tvLastMatchTeams.text = "${lastMatch.homeTeam.name} vs ${lastMatch.awayTeam.name}"

                    val homeScore = lastMatch.score.fullTime.home ?: 0
                    val awayScore = lastMatch.score.fullTime.away ?: 0

                    binding.tvLastMatchScore.text = "$homeScore : $awayScore"
                } else {
                    binding.tvLastMatchTeams.text = "Brak danych o meczach"
                }
            } catch (e: Exception) {
                Log.e("API_MATCH_ERROR", "Błąd: ${e.message}")
                binding.tvLastMatchTeams.text = "Błąd pobierania wyniku"
            }
        }
    }

    private fun calculateDistanceForTeam(name: String) {
        val (lat, lng) = when {
            name.contains("Arsenal") -> 51.5549 to -0.1084
            name.contains("Aston Villa") -> 52.4824 to -1.8848
            name.contains("Chelsea") -> 51.4817 to -0.1910
            name.contains("Everton") -> 53.4389 to -2.9664
            name.contains("Fulham") -> 51.4750 to -0.2217
            name.contains("Liverpool") -> 53.4308 to -2.9608
            name.contains("Manchester City") -> 53.4831 to -2.2004
            name.contains("Manchester United") -> 53.4631 to -2.2913
            name.contains("Newcastle") -> 54.9756 to -1.6217
            name.contains("Sunderland") -> 54.9146 to -1.3882
            name.contains("Tottenham") -> 51.6042 to -0.0662
            name.contains("Wolverhampton") -> 52.5902 to -2.1304
            name.contains("Burnley") -> 53.7892 to -2.2302
            name.contains("Leeds") -> 53.7778 to -1.5722
            name.contains("Nottingham") -> 52.9400 to -1.1328
            name.contains("Crystal Palace") -> 51.3983 to -0.0855
            name.contains("Brighton") -> 50.8618 to -0.0837
            name.contains("Brentford") -> 51.4906 to -0.2886
            else -> 52.2394 to 21.0458
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
                Snackbar.make(binding.root, "Do stadionu masz ok. %.2f km".format(distanceInKm), Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(binding.root, "Ustaw lokalizację w emulatorze!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}