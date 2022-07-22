package ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import hlv.cute.todo.R
import hlv.cute.todo.databinding.DialogMoreBinding

class MoreDialog(context: Context?, cancelable: Boolean = true) {

    private var dialog: AlertDialog? = null

    private val binding: DialogMoreBinding

    var onClickEdit: (() -> Unit)? = null
    var onClickDetail: (() -> Unit)? = null
    var onClickDelete: (() -> Unit)? = null

    init {
        binding = DialogMoreBinding.inflate(LayoutInflater.from(context), null, false)

        val builder = AlertDialog.Builder(context, R.style.TranslucentDialog).apply {
            setCancelable(cancelable)
            setView(binding.root)
        }

        binding.txtEdit.setOnClickListener {
            if (onClickEdit == null) {
                dismiss()
                return@setOnClickListener
            }
            onClickEdit!!()
        }

        binding.txtDelete.setOnClickListener {
            if (onClickDelete == null) {
                dismiss()
                return@setOnClickListener
            }
            onClickDelete!!()
        }

        binding.txtDetail.setOnClickListener {
            if (onClickDetail == null) {
                dismiss()
                return@setOnClickListener
            }
            onClickDetail!!()
        }

        if (dialog == null)
            dialog = builder.create()
    }

    fun setWithDetail(withDetail: Boolean) {
        if (withDetail) {
            binding.txtDetail.visibility = View.VISIBLE
            binding.line2.visibility = View.VISIBLE
        } else {
            binding.txtDetail.visibility = View.GONE
            binding.line2.visibility = View.GONE
        }
    }

    fun show() = dialog?.show()

    fun dismiss() = dialog?.dismiss()
}