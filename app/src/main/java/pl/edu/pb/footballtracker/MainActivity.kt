package pl.edu.pb.footballtracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
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
    private val CHANNEL_ID = "football_tracker_channel"

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Snackbar.make(binding.root, "Powiadomienia są wyłączone.", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        db = AppDatabase.getDatabase(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        createNotificationChannel()
        setupRecyclerView()
        observeFavorites()
        checkNotificationPermission()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadTeams()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                teamAdapter.filter(newText ?: "")
                return true
            }
        })
        return true
    }

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
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

            if (acceleration > 10.0) {
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
        Snackbar.make(binding.root, "Wykryto potrząśnięcie! Otwieram ulubione.", Snackbar.LENGTH_SHORT).show()
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
                    putExtra("TEAM_NAME", team.name ?: "Drużyna")
                    putExtra("TEAM_BADGE", team.badgeUrl ?: "")
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
            val favoriteEntity = FavoriteTeam(
                id = team.id,
                name = team.name ?: "Nieznana drużyna",
                badgeUrl = team.badgeUrl ?: ""
            )

            if (isFav) {
                dao.delete(favoriteEntity)
                Snackbar.make(binding.root, getString(R.string.removed_from_favorites), Snackbar.LENGTH_SHORT).show()
            } else {
                dao.insert(favoriteEntity)
                showNotification(team.name ?: "Drużyna")
                Snackbar.make(binding.root, getString(R.string.added_to_favorites), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Football Tracker Channel"
            val descriptionText = "Powiadomienia o ulubionych drużynach"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(teamName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle("Football Tracker")
            .setContentText("Dodano $teamName do ulubionych!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(teamName.hashCode(), builder.build())
    }

    private fun loadTeams() {
        lifecycleScope.launch {
            try {
                val token = BuildConfig.FOOTBALL_API_TOKEN
                val response = RetrofitInstance.api.getTeams("PL", token)
                val teams = response.teams ?: emptyList<NetworkTeam>()

                teamAdapter.updateTeams(teams)
                Log.d("API_TEST", "Załadowano ${teams.size} drużyn.")
            } catch (e: Exception) {
                Log.e("API_TEST", "Błąd: ${e.message}")
                Snackbar.make(binding.root, "Błąd pobierania danych", Snackbar.LENGTH_LONG).show()
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