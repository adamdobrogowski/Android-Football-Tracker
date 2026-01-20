package pl.edu.pb.footballtracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.edu.pb.footballtracker.databinding.ActivityTeamDetailsBinding
import pl.edu.pb.footballtracker.loadSvg

class TeamDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val teamName = intent.getStringExtra("TEAM_NAME") ?: "Drużyna"
        val teamBadge = intent.getStringExtra("TEAM_BADGE") ?: ""

        binding.tvDetailName.text = teamName
        binding.ivDetailBadge.loadSvg(teamBadge)

        supportActionBar?.title = teamName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}