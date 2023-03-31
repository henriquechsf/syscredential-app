package com.github.henriquechsf.syscredentialapp.ui.validation

import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import reactivecircus.flowbinding.android.widget.textChanges

class FormFieldText(
    scope: CoroutineScope,
    private val textInputLayout: TextInputLayout,
    private val textInputEditText: TextInputEditText,
    private val validation: suspend (String?) -> String? = { null }
) : FormField<String>() {

    var isEnable: Boolean
        get() = textInputLayout.isEnabled
        set(value) {
            textInputLayout.isEnabled = value
        }

    var isVisible: Boolean
        get() = textInputLayout.isVisible
        set(value) {
            textInputLayout.isVisible = value
        }

    var value: String?
        get() = stateInternal.value
        set(value) {
            textInputEditText.setText(value)
        }

    init {
        textInputEditText.textChanges().onEach { text ->
            clearError()
            stateInternal.update { text.toString() }
        }.launchIn(scope)
    }

    override fun clearError() {
        if (textInputLayout.error != null) {
            textInputLayout.error = null
            textInputLayout.isErrorEnabled = false
        }
    }

    override fun clearFocus() {
        textInputEditText.clearFocus()
    }

    override fun disable() {
        isEnable = false
    }

    override fun enable() {
        isEnable = true
    }

    override suspend fun validate(focusIfError: Boolean): Boolean {
        if (!isVisible) {
            return true
        }

        val errorValue = try {
            validation(stateInternal.value)
        } catch (error: Throwable) {
            error.message
        }

        val result = errorValue == null
        if (result) {
            clearError()
        } else {
            textInputLayout.error = errorValue
            if (focusIfError) {
                textInputEditText.requestFocus()
            }
        }

        isValidInternal.update { result }
        return result
    }
}