package com.example.demoottmobile.presentation.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.demoottmobile.R
import com.example.demoottmobile.databinding.DialogGlobalBinding

object GlobalDialog {

    fun show(
        context: Context,
        title: String,
        message: String,
        positiveText: String = context.getString(R.string.ok),
        negativeText: String? = null,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null,
        cancelable: Boolean = true
    ): Dialog {
        val dialog = Dialog(context, R.style.OttDialog)
        val binding = DialogGlobalBinding.inflate(LayoutInflater.from(context))

        binding.tvDialogTitle.text = title
        binding.tvDialogMessage.text = message
        binding.btnPositive.text = positiveText

        binding.btnPositive.setOnClickListener {
            onPositive?.invoke()
            dialog.dismiss()
        }

        if (negativeText != null) {
            binding.btnNegative.visibility = View.VISIBLE
            binding.btnNegative.text = negativeText
            binding.btnNegative.setOnClickListener {
                onNegative?.invoke()
                dialog.dismiss()
            }
        }

        dialog.setContentView(binding.root)
        dialog.setCancelable(cancelable)
        dialog.show()
        return dialog
    }
}
