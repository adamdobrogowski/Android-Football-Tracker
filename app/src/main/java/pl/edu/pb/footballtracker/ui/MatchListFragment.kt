package pl.edu.pb.footballtracker.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import pl.edu.pb.footballtracker.R
import pl.edu.pb.footballtracker.adapter.MatchAdapter
import pl.edu.pb.footballtracker.databinding.FragmentMatchListBinding

class MatchListFragment : Fragment(R.layout.fragment_match_list) {
    private var _binding: FragmentMatchListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchViewModel by viewModels()
    private lateinit var matchAdapter: MatchAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchListBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        matchAdapter = MatchAdapter(emptyList())
        binding.recyclerViewMatches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = matchAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.matches.observe(viewLifecycleOwner) { matchList ->
            matchAdapter.updateMatches(matchList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Ponów") { viewModel.refreshMatches() }
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}