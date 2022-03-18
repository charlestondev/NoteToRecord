package com.ntr.preferences;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.ntr.DBAdapter;
import com.ntr.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by charleston on 27/05/14.
 */
public class RestoreWords extends YesNo{
    public RestoreWords(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.dialogTitle = context.getResources().getString(R.string.restore_data);
        this.dialogTextNegativeButton = context.getResources().getString(R.string.cancel);
        this.dialogTextPositiveButton = "ok";
        this.dialogMessage= context.getResources().getString(R.string.message_restore);
    }
    @Override
    public void action() {
        if(isExternalStorageWritable())
        {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"words_meanings.txt");
            DBAdapter dbAdapter = new DBAdapter(getContext());
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while((line = br.readLine())!=null)
                    dbAdapter.insertWord(line.substring(0,line.indexOf(";")),line.substring(line.indexOf(";")+1));
                Toast.makeText(getContext(),getContext().getResources().getString(R.string.restored),Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(),getContext().getResources().getString(R.string.not_possible_restore),Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getContext(),getContext().getResources().getString(R.string.not_possible_restore),Toast.LENGTH_LONG).show();
            }
        }
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
