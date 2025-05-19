package com.likhachev.e_journal.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.likhachev.e_journal.R
import com.likhachev.e_journal.SessionManager
import com.likhachev.e_journal.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Запускаем анимации
        startAnimations()

        // Запускаем проверку авторизации и навигацию через определенное время
        startNavigationTimer()
    }

    private fun startAnimations() {
        // Анимация для заголовка
        val titleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.title_animation)
        binding.tittleTextView.startAnimation(titleAnimation)

        // Анимация для изображения
        val imageAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.image_animation)
        binding.imageCardView.startAnimation(imageAnimation)

        // Анимация для названия приложения
        val appNameAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.app_name_animation)
        binding.bottomTextView.startAnimation(appNameAnimation)
    }

    private fun startNavigationTimer() {
        lifecycleScope.launch {
            delay(2100)
            navigateToDestination()
        }
    }

    private fun navigateToDestination() {
        val navController = findNavController()
        val token = sessionManager.getToken()
        val userRole = sessionManager.getRole()

        when {
            token.isNullOrEmpty() || userRole == SessionManager.ROLE_UNDEFINED -> {
                navController.navigate(R.id.action_splashScreenFragment_to_loginFragment)
            }
            userRole == SessionManager.ROLE_STUDENT -> {
                navController.navigate(R.id.action_splashScreenFragment_to_studentTabsFragment)
            }
            userRole == SessionManager.ROLE_TEACHER -> {
                navController.navigate(R.id.action_splashScreenFragment_to_teacherTabsFragment)
            }
            userRole == SessionManager.ROLE_ADMINISTRATOR -> {
                navController.navigate(R.id.action_splashScreenFragment_to_adminTabsFragment)
            }
            else -> {
                navController.navigate(R.id.action_splashScreenFragment_to_loginFragment)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}