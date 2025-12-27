package pl.edu.pb.footballtracker.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import pl.edu.pb.footballtracker.R
import pl.edu.pb.footballtracker.databinding.FragmentMatchDetailsBinding

class MatchDetailsFragment : Fragment(R.layout.fragment_match_details) {
    private var _binding: FragmentMatchDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchDetailsBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}