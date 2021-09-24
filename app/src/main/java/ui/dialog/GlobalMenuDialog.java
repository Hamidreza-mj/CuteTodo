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
    private OnClickDeleteAll onClickDeleteAll;

    public GlobalMenuDialog(Context context, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);

        hlv.cute.todo.databinding.DialogGlobalMenuBinding binding = DialogGlobalMenuBinding.inflate(LayoutInflater.from(context), null, false);
        TextView txtCategories = binding.txtCategories;
        TextView txtDeleteAll = binding.txtDeleteAll;

        txtCategories.setOnClickListener(view -> {
            if (onClickCategories == null) {
                if (dialog != null)
                    dismiss();

                return;
            }

            onClickCategories.onClick();
        });

        txtDeleteAll.setOnClickListener(view -> {
            if (onClickDeleteAll == null) {
                dismiss();
                return;
            }

            onClickDeleteAll.onClick();
        });

        builder.setView(binding.getRoot());

        if (dialog == null)
            dialog = builder.create();
    }

    public GlobalMenuDialog(Context context) {
        this(context, true);
    }

    public void setOnClickCategories(OnClickCategories onClickCategories) {
        this.onClickCategories = onClickCategories;
    }

    public void setOnClickDeleteAll(OnClickDeleteAll onClickDeleteAll) {
        this.onClickDeleteAll = onClickDeleteAll;
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

    public interface OnClickDeleteAll {
        void onClick();
    }

}
