package com.likhachev.e_journal.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.likhachev.e_journal.R
import com.likhachev.e_journal.databinding.FragmentTeacherTabsBinding
import com.likhachev.e_journal.domain.model.TeacherGroup
import com.likhachev.e_journal.domain.model.TeacherLesson
import com.likhachev.e_journal.presentation.viewmodel.TeacherTabsViewModel
import com.likhachev.e_journal.utils.DateChangeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherTabsFragment: Fragment(), DateChangeListener {
    private var _binding: FragmentTeacherTabsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val viewModel: TeacherTabsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_teacher_fragment_bottom) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavView.setupWithNavController(navController)

        observeViewModel()

        setupBackButton()

        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.teacherScheduleFragment -> {
                    viewModel.setTitle("Расписание на")
                    showBottomNavigation()
                    hideBackButton()
                }
                R.id.teacherGroupsFragment -> {
                    viewModel.setTitle("Ваши")
                    showBottomNavigation()
                    hideBackButton()
                }
                R.id.teacherJournalFragment -> {
                    val lesson = arguments?.getParcelable<TeacherGroup>("teacherGroup")
                    val groupName = lesson?.groupName ?: ""
                    viewModel.setJournalTitle(groupName)
                    hideBottomNavigation()
                    showBackButton()
                }
            }
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun showBottomNavigation() {
        binding.bottomNavView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigation() {
        binding.bottomNavView.visibility = View.GONE
    }

    private fun showBackButton() {
        binding.backButton.visibility = View.VISIBLE
        binding.moreButton.visibility = View.GONE
    }

    private fun hideBackButton() {
        binding.backButton.visibility = View.GONE
        binding.moreButton.visibility = View.VISIBLE
    }

    private fun observeViewModel() {
        viewModel.title.observe(viewLifecycleOwner) { title ->
            binding.firstTextView.text = title
            if (title == "Ваши") {
                binding.lastTextView.text = "классы"
            }
        }

        viewModel.subtitle.observe(viewLifecycleOwner) { subtitle ->
            if (subtitle.isNotEmpty()) {
                binding.lastTextView.text = subtitle
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateChanged(newDate: String) {
        binding.lastTextView.text = newDate
    }
}