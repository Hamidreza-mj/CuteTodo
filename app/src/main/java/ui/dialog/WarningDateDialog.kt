package ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import hlv.cute.todo.R
import hlv.cute.todo.databinding.DialogWarningDateBinding

class WarningDateDialog(context: Context?, cancelable: Boolean = true) {
    private var binding: DialogWarningDateBinding

    private var dialog: AlertDialog? = null
    var continueClicked: (() -> Unit)? = null

    init {
        binding = DialogWarningDateBinding.inflate(LayoutInflater.from(context), null, false)

        val builder = AlertDialog.Builder(context, R.style.TranslucentDialog).apply {
            setCancelable(cancelable)
            setView(binding.root)
        }


        binding.txtEdit.setOnClickListener { dismiss() }

        binding.txtContinue.setOnClickListener {
            if (continueClicked == null) {
                dismiss()
                return@setOnClickListener
            }

            continueClicked!!()
        }

        if (dialog == null)
            dialog = builder.create()
    }

    fun show() = dialog?.show()

    fun dismiss() = dialog?.dismiss()

    fun setTitle(title: String) {
        if (dialog != null)
            binding.txtTitle.text = title
    }

    fun setMessage(message: String) {
        if (dialog != null)
            binding.txtMessage.text = message
    }

    fun setEditText(text: String) {
        if (dialog != null)
            binding.txtEdit.text = text
    }

}