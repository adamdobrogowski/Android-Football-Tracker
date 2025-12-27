package pl.edu.pb.footballtracker.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pl.edu.pb.footballtracker.R
import pl.edu.pb.footballtracker.databinding.FragmentMatchListBinding

class MatchListFragment : Fragment(R.layout.fragment_match_list) {
    private var _binding: FragmentMatchListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchListBinding.bind(view)

        binding.recyclerViewMatches.layoutManager = LinearLayoutManager(requireContext())

        viewModel.matches.observe(viewLifecycleOwner) { matchList ->
            binding.recyclerViewMatches.adapter = MatchAdapter(matchList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}