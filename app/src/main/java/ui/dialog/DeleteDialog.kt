package ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import hlv.cute.todo.R
import hlv.cute.todo.databinding.DialogDeleteBinding

class DeleteDialog(context: Context?, cancelable: Boolean = true) {

    private val binding: DialogDeleteBinding

    private var dialog: AlertDialog? = null
    var onClickDelete: (() -> Unit)? = null

    init {
        binding = DialogDeleteBinding.inflate(LayoutInflater.from(context), null, false)

        val builder = AlertDialog.Builder(context, R.style.TranslucentDialog).apply {
            setCancelable(cancelable)
            setView(binding.root)
        }

        binding.txtCancel.setOnClickListener { dismiss() }

        binding.txtDelete.setOnClickListener {
            if (onClickDelete == null) {
                dismiss()
                return@setOnClickListener
            }

            onClickDelete!!()
        }

        if (dialog == null)
            dialog = builder.create()

        //dialog?.window?.setDimAmount(0f)
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