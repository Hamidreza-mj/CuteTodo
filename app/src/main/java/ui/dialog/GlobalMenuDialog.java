package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import hlv.cute.todo.R;

public class GlobalMenuDialog {

    private AlertDialog dialog;

    public GlobalMenuDialog(Context context, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_global_menu, null, false);
        builder.setView(view);

        if (dialog == null)
            dialog = builder.create();
    }

    public GlobalMenuDialog(Context context) {
        this(context, true);
    }


    public GlobalMenuDialog dismiss() {
        if (dialog != null)
            dialog.dismiss();

        return this;
    }

    public void show() {
        if (dialog != null)
            dialog.show();

    }

}
