package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.DialogWarningDateBinding;

public class WarningDateDialog {

    private AlertDialog dialog;

    private final TextView txtTitle;
    private final TextView txtMessage;

    private final TextView txtEditDate;

    private ContinueClicked continueClicked;

    public WarningDateDialog(Context context, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);
        DialogWarningDateBinding binding = DialogWarningDateBinding.inflate(LayoutInflater.from(context), null, false);
        builder.setView(binding.getRoot());

        txtTitle = binding.txtTitle;
        txtMessage = binding.txtMessage;
        txtEditDate = binding.txtEdit;

        txtEditDate.setOnClickListener(view -> dismiss());
        binding.txtContinue.setOnClickListener(v -> {
            if (continueClicked == null) {
                dismiss();
                return;
            }

            continueClicked.onClick();
        });

        if (dialog == null)
            dialog = builder.create();
    }

    public WarningDateDialog(Context context) {
        this(context, true);
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

    public void setEditText(String text) {
        if (dialog != null && txtEditDate != null)
            txtEditDate.setText(text);
    }

    public void setContinueClicked(ContinueClicked continueClicked) {
        this.continueClicked = continueClicked;
    }

    public interface ContinueClicked {
        void onClick();
    }

}
