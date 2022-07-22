package ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import hlv.cute.todo.R
import hlv.cute.todo.databinding.DialogGlobalMenuBinding

class GlobalMenuDialog(context: Context?, cancelable: Boolean = true) {

    private var dialog: AlertDialog? = null

    var onClickCategories: (() -> Unit)? = null
    var onClickDeleteAll: (() -> Unit)? = null
    var onClickDeleteAllDone: (() -> Unit)? = null

    init {
        val binding = DialogGlobalMenuBinding.inflate(LayoutInflater.from(context), null, false)

        val builder = AlertDialog.Builder(context, R.style.TranslucentDialog).apply {
            setCancelable(cancelable)
            setView(binding.root)
        }


        binding.txtCategories.setOnClickListener {
            if (onClickCategories == null) {
                if (dialog != null) dismiss()
                return@setOnClickListener
            }

            onClickCategories!!()
        }

        binding.txtDeleteAll.setOnClickListener {
            if (onClickDeleteAll == null) {
                dismiss()
                return@setOnClickListener
            }

            onClickDeleteAll!!()
        }

        binding.txtDeleteAllDone.setOnClickListener {
            if (onClickDeleteAllDone == null) {
                dismiss()
                return@setOnClickListener
            }

            onClickDeleteAllDone!!()
        }


        if (dialog == null)
            dialog = builder.create()
    }

    fun show() = dialog?.show()
    fun dismiss() = dialog?.dismiss()

}