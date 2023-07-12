package com.example.android_nepali_calendar_picker_lib.utils

import android.content.Context
import android.os.LocaleList
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

internal object KeyBoardUtils {
     fun showKeyboard(editText: TextInputEditText,context: Context) {
        editText.imeHintLocales = LocaleList(Locale("ne", "NP"))
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

     fun isSoftKeyboardVisible(context: Context): Boolean {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.isAcceptingText
    }

     fun hideKeyboard(context: Context,view : View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}