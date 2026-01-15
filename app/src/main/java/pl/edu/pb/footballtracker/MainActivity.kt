package pl.edu.pb.footballtracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import pl.edu.pb.footballtracker.adapter.TeamAdapter
import pl.edu.pb.footballtracker.databinding.ActivityMainBinding
import pl.edu.pb.footballtracker.di.RetrofitInstance
import pl.edu.pb.footballtracker.model.NetworkTeam
import pl.edu.pb.footballtracker.ui.TeamDetailsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadTeams()
    }

    private fun setupRecyclerView() {
        teamAdapter = TeamAdapter(emptyList()) { team ->
            val intent = Intent(this, TeamDetailsActivity::class.java).apply {
                putExtra("TEAM_ID", team.id)
                putExtra("TEAM_NAME", team.name)
                putExtra("TEAM_BADGE", team.badgeUrl)
            }
            startActivity(intent)
        }

        binding.rvTeams.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
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
        .components {
            add(SvgDecoder.Factory())
        }
        .build()

    val request = ImageRequest.Builder(this.context)
        .data(url)
        .target(this)
        .crossfade(true)
        .build()

    imageLoader.enqueue(request)
}