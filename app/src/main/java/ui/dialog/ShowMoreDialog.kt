package ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.aghajari.rlottie.AXrLottie
import com.aghajari.rlottie.AXrLottieDrawable
import hlv.cute.todo.R
import hlv.cute.todo.databinding.DialogShowMoreBinding

class ShowMoreDialog(private val context: Context, cancelable: Boolean = true) {
    private var binding: DialogShowMoreBinding =
        DialogShowMoreBinding.inflate(LayoutInflater.from(context), null, false)

    private var dialog: AlertDialog? = null

    var onClickDelete: (() -> Unit)? = null
    var onClickEdit: (() -> Unit)? = null
    var onClickDone: (() -> Unit)? = null
    var onClickShare: ((view: View) -> Unit)? = null
    var onClickDetail: (() -> Unit)? = null

    var onShowDialog: (() -> Unit)? = null
    var onDismissDialog: (() -> Unit)? = null


    init {
        val builder = AlertDialog.Builder(context, R.style.TranslucentDialog).apply {
            setCancelable(cancelable)
            setView(binding.root)
        }

        AXrLottie.init(context)

        binding.aImgClose.setOnClickListener { dismiss() }

        binding.aImgDelete.setOnClickListener { onClickDelete?.invoke() }
        binding.aImgEdit.setOnClickListener { onClickEdit?.invoke() }
        binding.aImgDone.setOnClickListener { onClickDone?.invoke() }
        binding.aImgShare.setOnClickListener { onClickShare?.invoke(it) }
        binding.aImgDetail.setOnClickListener { onClickDetail?.invoke() }

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

    fun isDone(isDone: Boolean) {
        binding.aImgDone.visibility = if (isDone) View.GONE else View.VISIBLE
    }

    fun showConfetti() {
        binding.aImgDone.visibility = View.GONE
        binding.confetti.visibility = View.VISIBLE

        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels

        binding.confetti.lottieDrawable =
            AXrLottieDrawable
                .fromAssets(
                    context,
                    "congratulations_confetti_lottie.json"
                ) //from assets
                .setSize(width, height)
                .setOnLottieLoaderListener(object :
                    AXrLottieDrawable.OnLottieLoaderListener {
                    override fun onLoaded(drawable: AXrLottieDrawable?) {
                        binding.confetti.postOnAnimationDelayed({
                            binding.confetti.visibility = View.GONE
                            dismiss()
                        }, 3_000L)
                    }

                    override fun onError(
                        drawable: AXrLottieDrawable?,
                        error: Throwable?
                    ) {
                    }
                })
                .build()

        binding.confetti.playAnimation()
    }

}