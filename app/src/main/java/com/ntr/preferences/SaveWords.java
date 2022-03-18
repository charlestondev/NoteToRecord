package com.ntr.preferences;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.ntr.DBAdapter;
import com.ntr.R;
import com.ntr.Word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by charleston on 27/05/14.
 */
public class SaveWords extends YesNo {
    public static String SEPARATOR = ";";
    public SaveWords(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        this.dialogTitle = getContext().getResources().getString(R.string.save_data);
        this.dialogTextNegativeButton = getContext().getResources().getString(R.string.cancel);
        this.dialogTextPositiveButton = "ok";
        this.dialogMessage= "";
    }
    @Override
    public void action() {
        if(isExternalStorageWritable()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"words_meanings.txt");
            try {
                file.delete();
                //file.createNewFile();
                //FileOutputStream out = new FileOutputStream(file);
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                DBAdapter dbAdapter = new DBAdapter(getContext());
                List<Word> words = dbAdapter.getAllWords();
                for(Word word:words) {
                    //byte dataToWrite[] = (word.getWord()+SEPARATOR+(word.getDefinition().equals("")?"No definition":word.getDefinition())+System.lineSeparator()).getBytes();
                    //out.write(dataToWrite);
                    String line = word.getWord()+SEPARATOR+(word.getDefinition().equals("")?"No definition":word.getDefinition());
                    Log.i("line", line);
                    out.write(line);
                    out.newLine();
                }
                out.close();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.data_saved), Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.not_possible_save), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.not_possible_save), Toast.LENGTH_LONG).show();
            }
        }
        else
            Toast.makeText(getContext(),"Não pode salvar",Toast.LENGTH_LONG).show();
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
