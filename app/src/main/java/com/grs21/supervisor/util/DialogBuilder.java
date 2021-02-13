package com.grs21.supervisor.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.grs21.supervisor.R;

public class DialogBuilder {

    private String title;
    private String message;
    private String primaryButtonTitle;
    private String secondaryButtonTitle;
    private Dialog.OnClickListener primaryButtonListener;
    private Dialog.OnClickListener secondaryButtonListener;
    private Activity context;
    private boolean showIcon;
    private boolean cancellable;

    public DialogBuilder(Activity context) {
        this.context = context;
    }

    public DialogBuilder setTitle(@StringRes int title) {
        this.title = context.getString(title);
        return this;
    }

    public DialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogBuilder setMessage(@StringRes int message) {
        this.message = context.getString(message);
        return this;
    }

    public DialogBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public DialogBuilder setShowIcon() {
        showIcon = true;
        return this;
    }

    public DialogBuilder setPrimaryButton(@StringRes int title, Dialog.OnClickListener listener) {
        primaryButtonTitle = context.getString(title);
        primaryButtonListener = listener;
        return this;
    }

    public DialogBuilder setSecondaryButton(@StringRes int title, Dialog.OnClickListener listener) {
        secondaryButtonTitle = context.getString(title);
        secondaryButtonListener = listener;
        return this;
    }

    public DialogBuilder setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
        return this;
    }

    public AlertDialog create() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(android.R.layout.select_dialog_item, null);
        builder.setView(dialogView);

        // get your custom views here and configure them based on given settings (field values of this class)

        final AlertDialog dialog = builder.create();
        return dialog;
    }
}
