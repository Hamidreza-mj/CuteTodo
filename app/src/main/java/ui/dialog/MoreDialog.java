package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.DialogMoreBinding;
import model.Todo;

public class MoreDialog {

    private AlertDialog dialog;

    private OnClickDelete onClickDelete;
    private OnClickEdit onClickEdit;

    public MoreDialog(Context context, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);
        DialogMoreBinding binding = DialogMoreBinding.inflate(LayoutInflater.from(context), null, false);
        builder.setView(binding.getRoot());

        TextView txtDelete = binding.txtDelete;
        txtDelete.setOnClickListener(view -> {
            if (onClickDelete == null) {
                dismiss();
                return;
            }

            onClickDelete.onClick();
        });

        TextView txtEdit = binding.txtEdit;
        txtEdit.setOnClickListener(view -> {
            if (onClickEdit == null) {
                dismiss();
                return;
            }

            onClickEdit.onClick();
        });

        if (dialog == null)
            dialog = builder.create();
    }

    public MoreDialog(Context context) {
        this(context, true);
    }

    public void setOnClickDelete(OnClickDelete onClickDelete) {
        this.onClickDelete = onClickDelete;
    }

    public void setOnClickEdit(OnClickEdit onClickEdit) {
        this.onClickEdit = onClickEdit;
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public interface OnClickDelete {
        void onClick();
    }

    public interface OnClickEdit {
        void onClick();
    }

}
