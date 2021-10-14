package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.DialogMoreBinding;

public class MoreDialog {

    private AlertDialog dialog;

    private OnClickEdit onClickEdit;
    private OnClickDetail onClickDetail;
    private OnClickDelete onClickDelete;

    public MoreDialog(Context context, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);
        DialogMoreBinding binding = DialogMoreBinding.inflate(LayoutInflater.from(context), null, false);
        builder.setView(binding.getRoot());

        binding.txtEdit.setOnClickListener(view -> {
            if (onClickEdit == null) {
                dismiss();
                return;
            }

            onClickEdit.onClick();
        });

        binding.txtDelete.setOnClickListener(view -> {
            if (onClickDelete == null) {
                dismiss();
                return;
            }

            onClickDelete.onClick();
        });

        binding.txtDetail.setOnClickListener(v -> {
            if (onClickDetail == null) {
                dismiss();
                return;
            }

            onClickDetail.onClick();
        });


        if (dialog == null)
            dialog = builder.create();
    }

    public MoreDialog(Context context) {
        this(context, true);
    }

    public void setOnClickEdit(OnClickEdit onClickEdit) {
        this.onClickEdit = onClickEdit;
    }

    public void setOnClickDetail(OnClickDetail onClickDetail) {
        this.onClickDetail = onClickDetail;
    }

    public void setOnClickDelete(OnClickDelete onClickDelete) {
        this.onClickDelete = onClickDelete;
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public interface OnClickEdit {
        void onClick();
    }

    public interface OnClickDetail {
        void onClick();
    }

    public interface OnClickDelete {
        void onClick();

    }

}
