package com.likhachev.e_journal.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Visibility
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.FragmentStudentTabsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentTabsFragment: Fragment(), DateChangeListener {
    private var _binding: FragmentStudentTabsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_fragment_bottom) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateAppBar(destination)
        }

        // Установка начального состояния
        binding.dateTextView.text = "Расписание на"
        binding.dateValueTextView.visibility = View.VISIBLE
    }

    private fun updateAppBar(destination: NavDestination) {
        when (destination.id) {
            R.id.studentScheduleFragment -> {
                binding.dateTextView.text = "Расписание на"
                binding.dateValueTextView.visibility = View.VISIBLE
            }
            R.id.studentGradesFragment -> {
                binding.dateTextView.text = "Оценки"
                binding.dateValueTextView.visibility = View.GONE
            }
            R.id.studentPerformanceFragment -> {
                binding.dateTextView.text = "Успеваемость"
                binding.dateValueTextView.visibility = View.GONE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateChanged(newDate: String) {
        binding.dateValueTextView.text = newDate
    }
}

interface DateChangeListener {
    fun onDateChanged(newDate: String)
}