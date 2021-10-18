package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.DialogDeleteBinding;

public class DeleteDialog {

    private AlertDialog dialog;

    private final TextView txtTitle;
    private final TextView txtMessage;

    private OnClickDelete onClickDelete;

    public DeleteDialog(Context context, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);
        hlv.cute.todo.databinding.DialogDeleteBinding binding = DialogDeleteBinding.inflate(LayoutInflater.from(context), null, false);
        builder.setView(binding.getRoot());

        txtTitle = binding.txtTitle;
        txtMessage = binding.txtMessage;
        TextView txtCancel = binding.txtCancel;
        TextView txtDelete = binding.txtDelete;

        txtCancel.setOnClickListener(view -> dismiss());
        txtDelete.setOnClickListener(view -> {
            if (onClickDelete == null) {
                dismiss();
                return;
            }

            onClickDelete.onClick();
        });

        if (dialog == null)
            dialog = builder.create();
    }

    public DeleteDialog(Context context) {
        this(context, true);
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

    public void setTitle(String title) {
        if (dialog != null)
            txtTitle.setText(title);
    }

    public void setMessage(String message) {
        if (dialog != null)
            txtMessage.setText(message);
    }

    public interface OnClickDelete {
        void onClick();
    }

}
