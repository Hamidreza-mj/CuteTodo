package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import hlv.cute.todo.R;

public class MoreDialog {

    private AlertDialog dialog;

    public MoreDialog(Context context, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_more, null, false);
        builder.setView(view);

        if (dialog == null)
            dialog = builder.create();
    }

    public MoreDialog(Context context) {
        this(context, true);
    }


    public MoreDialog dismiss() {
        if (dialog != null)
            dialog.dismiss();

        return this;
    }

    public void show() {
        if (dialog != null)
            dialog.show();

    }

}
