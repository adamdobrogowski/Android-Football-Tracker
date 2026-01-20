package pl.edu.pb.footballtracker

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.edu.pb.footballtracker.adapter.TeamAdapter
import pl.edu.pb.footballtracker.data.local.AppDatabase
import pl.edu.pb.footballtracker.databinding.ActivityMainBinding
import pl.edu.pb.footballtracker.di.RetrofitInstance
import pl.edu.pb.footballtracker.model.FavoriteTeam
import pl.edu.pb.footballtracker.model.NetworkTeam
import pl.edu.pb.footballtracker.ui.FavoriteTeamsActivity
import pl.edu.pb.footballtracker.ui.TeamDetailsActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var teamAdapter: TeamAdapter
    private lateinit var db: AppDatabase

    private lateinit var sensorManager: SensorManager
    private var lastShakeTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        setupRecyclerView()
        observeFavorites()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadTeams()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

            if (acceleration > 12.0) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > 2000) {
                    lastShakeTime = currentTime
                    openFavorites()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun openFavorites() {
        Toast.makeText(this, "Wykryto potrząśnięcie! Otwieram ulubione.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, FavoriteTeamsActivity::class.java)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        teamAdapter = TeamAdapter(
            teams = emptyList(),
            onFavoriteClick = { team -> toggleFavorite(team) },
            onItemClick = { team ->
                val intent = Intent(this, TeamDetailsActivity::class.java).apply {
                    putExtra("TEAM_ID", team.id)
                    putExtra("TEAM_NAME", team.name)
                    putExtra("TEAM_BADGE", team.badgeUrl)
                }
                startActivity(intent)
            }
        )

        binding.rvTeams.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeFavorites() {
        lifecycleScope.launch {
            db.favoriteTeamDao().getAllFavorites().collectLatest { favorites ->
                val favoriteIds = favorites.map { it.id }.toSet()
                teamAdapter.updateFavorites(favoriteIds)
            }
        }
    }

    private fun toggleFavorite(team: NetworkTeam) {
        lifecycleScope.launch {
            val dao = db.favoriteTeamDao()
            val isFav = dao.isFavorite(team.id)
            val favoriteEntity = FavoriteTeam(team.id, team.name, team.badgeUrl)

            if (isFav) {
                dao.delete(favoriteEntity)
            } else {
                dao.insert(favoriteEntity)
            }
        }
    }

    private fun loadTeams() {
        lifecycleScope.launch {
            try {
                val token = BuildConfig.FOOTBALL_API_TOKEN
                val response = RetrofitInstance.api.getTeams("PL", token)
                val teams = response.teams ?: emptyList<NetworkTeam>()

                teamAdapter.updateTeams(teams)
                Log.d("API_TEST", "Sukces! Załadowano ${teams.size} drużyn.")
            } catch (e: Exception) {
                Log.e("API_TEST", "Błąd: ${e.message}")
            }
        }
    }
}

fun android.widget.ImageView.loadSvg(url: String) {
    val imageLoader = ImageLoader.Builder(this.context)
        .components { add(SvgDecoder.Factory()) }
        .build()

    val request = ImageRequest.Builder(this.context)
        .data(url)
        .target(this)
        .crossfade(true)
        .build()

    imageLoader.enqueue(request)
}