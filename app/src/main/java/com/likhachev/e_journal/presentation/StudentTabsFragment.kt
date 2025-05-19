package com.likhachev.e_journal.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Visibility
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.FragmentStudentTabsBinding
import com.likhachev.e_journal.presentation.viewmodel.LoginViewModel
import com.likhachev.e_journal.presentation.viewmodel.StudentTabsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentTabsFragment: Fragment(), DateChangeListener {
    private var _binding: FragmentStudentTabsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val viewModel: StudentTabsViewModel by viewModels()

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

        observeViewModel()

        // Обновление заголовка при смене фрагмента
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.studentScheduleFragment -> viewModel.setTitle("Расписание на")
                R.id.studentGradesFragment -> viewModel.setTitle("Оценки")
                R.id.studentPerformanceFragment -> viewModel.setTitle("Успеваемость")
            }
        }
    }

    private fun observeViewModel() {
        viewModel.title.observe(viewLifecycleOwner) { title ->
            binding.dateTextView.text = title
            binding.dateValueTextView.visibility = if (title == "Расписание на") View.VISIBLE else View.GONE
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