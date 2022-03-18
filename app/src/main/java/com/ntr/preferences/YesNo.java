package com.ntr.preferences;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.ntr.R;

public abstract class YesNo extends DialogPreference
{
    public String dialogTitle;
    public String dialogTextPositiveButton;
    public String dialogTextNegativeButton;
    public String dialogMessage;
    public YesNo(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onClick()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(dialogTitle);
        //dialog.setMessage("This action will delete all your data. Are you sure you want to continue?");
        dialog.setCancelable(true);
        dialog.setPositiveButton(dialogTextPositiveButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                action();
            }
        });

        dialog.setNegativeButton(dialogTextNegativeButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dlg, int which)
            {
                dlg.cancel();
            }
        });

        AlertDialog al = dialog.create();
        al.show();
    }
    public abstract void action();
}
