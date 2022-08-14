package ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import hlv.cute.todo.R
import hlv.cute.todo.databinding.DialogShowMoreBinding

class ShowMoreDialog(context: Context?, cancelable: Boolean = true) {
    private var binding: DialogShowMoreBinding
    private var dialog: AlertDialog? = null

    var onClickOpen: (() -> Unit)? = null
    var onShowDialog: (() -> Unit)? = null
    var onDismissDialog: (() -> Unit)? = null


    init {
        binding = DialogShowMoreBinding.inflate(LayoutInflater.from(context), null, false)

        val builder = AlertDialog.Builder(context, R.style.TranslucentDialog).apply {
            setCancelable(cancelable)
            setView(binding.root)
        }


        binding.txtClose.setOnClickListener { dismiss() }

        binding.txtOpenDetail.setOnClickListener { onClickOpen?.invoke() }

        if (dialog == null)
            dialog = builder.create()

        dialog?.window?.setDimAmount(0f)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation


        dialog?.setOnShowListener { onShowDialog?.invoke() }
        dialog?.setOnDismissListener { onDismissDialog?.invoke() }
    }

    fun show() = dialog?.show()

    fun dismiss() = dialog?.dismiss()

    fun setMessage(message: String?) {
        if (dialog != null)
            binding.txtTodo.text = message
    }

}