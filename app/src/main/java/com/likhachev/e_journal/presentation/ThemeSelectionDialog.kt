package com.likhachev.e_journal.presentation

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.likhachev.e_journal.R
import com.likhachev.e_journal.utils.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThemeSelectionDialog : DialogFragment() {

    @Inject
    lateinit var themeManager: ThemeManager

    private var onThemeSelected: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val themes = arrayOf("Светлая", "Темная", "Системная")
        val themeValues = arrayOf(
            ThemeManager.THEME_LIGHT,
            ThemeManager.THEME_DARK,
            ThemeManager.THEME_SYSTEM
        )

        val currentTheme = themeManager.getCurrentTheme()
        val selectedIndex = themeValues.indexOf(currentTheme)

        return AlertDialog.Builder(requireContext(), R.style.CustomDatePickerDialogTheme)
            .setTitle("Выберите тему")
            .setSingleChoiceItems(themes, selectedIndex) { dialog, which ->
                val selectedTheme = themeValues[which]
                themeManager.setTheme(selectedTheme)
                onThemeSelected?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    fun setOnThemeSelectedListener(listener: (() -> Unit)?) {
        onThemeSelected = listener
    }

    companion object {
        const val TAG = "ThemeSelectionDialog"

        fun newInstance(onThemeSelected: (() -> Unit)? = null): ThemeSelectionDialog {
            return ThemeSelectionDialog().apply {
                setOnThemeSelectedListener(onThemeSelected)
            }
        }
    }
}