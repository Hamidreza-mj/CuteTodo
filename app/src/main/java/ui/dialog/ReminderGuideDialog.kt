package ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import hlv.cute.todo.R
import hlv.cute.todo.databinding.DialogReminderGuideBinding

class ReminderGuideDialog(context: Context?, cancelable: Boolean = true) {
    private var binding: DialogReminderGuideBinding
    private var dialog: AlertDialog? = null

    init {
        binding = DialogReminderGuideBinding.inflate(LayoutInflater.from(context), null, false)

        val builder = AlertDialog.Builder(context, R.style.TranslucentDialog).apply {
            setCancelable(cancelable)
            setView(binding.root)
        }


        binding.txtOk.setOnClickListener { dismiss() }
        if (dialog == null)
            dialog = builder.create()
    }

    fun show() = dialog?.show()

    fun dismiss() = dialog?.dismiss()

    fun setTitle(title: String?) {
        if (dialog != null)
            binding.txtTitle.text = title
    }

    fun setMessage(message: String?) {
        if (dialog != null)
            binding.txtMessage.text = message
    }

}