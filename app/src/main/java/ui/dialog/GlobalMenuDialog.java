package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.DialogGlobalMenuBinding;

public class GlobalMenuDialog {

    private AlertDialog dialog;

    private OnClickCategories onClickCategories;

    public GlobalMenuDialog(Context context, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);

        hlv.cute.todo.databinding.DialogGlobalMenuBinding binding = DialogGlobalMenuBinding.inflate(LayoutInflater.from(context), null, false);
        TextView txtCategories = binding.txtCategories;
        TextView txtInfo = binding.txtInfo;

        txtCategories.setOnClickListener(view -> {
            if (onClickCategories == null) {
                if (dialog != null)
                    dismiss();

                return;
            }

            onClickCategories.onClick();
        });

        builder.setView(binding.getRoot());

        if (dialog == null)
            dialog = builder.create();
    }

    public GlobalMenuDialog(Context context) {
        this(context, true);
    }

    public GlobalMenuDialog setOnClickCategories(OnClickCategories onClickCategories) {
        this.onClickCategories = onClickCategories;
        return this;
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();

    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public interface OnClickCategories {
        void onClick();
    }

}
