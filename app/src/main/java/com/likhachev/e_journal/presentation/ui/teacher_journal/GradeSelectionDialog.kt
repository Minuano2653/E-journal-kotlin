package com.likhachev.e_journal.presentation.ui.teacher_journal

import android.app.AlertDialog
import android.content.Context
import com.likhachev.e_journal.R

object GradeSelectionDialog {

    private val gradeOptions = arrayOf("2", "3", "4", "5", "Н", "У", "+")

    fun show(
        context: Context,
        currentGrade: String?,
        onGradeSelected: (String) -> Unit
    ) {
        val currentIndex = gradeOptions.indexOf(currentGrade).takeIf { it >= 0 } ?: -1

        AlertDialog.Builder(context, R.style.CustomDatePickerDialogTheme)
            .setTitle("Выберите отметку")
            .setSingleChoiceItems(gradeOptions, currentIndex) { dialog, which ->
                val selectedGrade = gradeOptions[which]
                onGradeSelected(selectedGrade)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}