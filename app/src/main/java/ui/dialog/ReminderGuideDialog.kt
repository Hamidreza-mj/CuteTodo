package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.DialogReminderGuideBinding;

public class ReminderGuideDialog {

    private AlertDialog dialog;

    private final TextView txtTitle;
    private final TextView txtMessage;

    public ReminderGuideDialog(Context context, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);
        DialogReminderGuideBinding binding = DialogReminderGuideBinding.inflate(LayoutInflater.from(context), null, false);
        builder.setView(binding.getRoot());

        txtTitle = binding.txtTitle;
        txtMessage = binding.txtMessage;

        binding.txtOk.setOnClickListener(view -> dismiss());

        if (dialog == null)
            dialog = builder.create();
    }

    public ReminderGuideDialog(Context context) {
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

}
