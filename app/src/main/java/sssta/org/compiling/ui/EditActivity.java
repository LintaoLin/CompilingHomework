package sssta.org.compiling.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import sssta.org.compiling.MyApplication;
import sssta.org.compiling.R;

public class EditActivity extends AppCompatActivity {

    private EditText editText;
    private File file;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editText = (EditText) findViewById(R.id.editView);
        file = new File(MyApplication.mInstance.getAppDirPath(),MyApplication.fileName);
        if (!file.exists()) {

        } else {
            String s = readFile(file);
            editText.setText(s);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    private String readFile(File file) {
        String result = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                result += s + '\n';
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "file not exist", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "file read error", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void saveFile(String res) {
        progressDialog.show();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(res);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            fileWriter.flush();
            fileWriter.close();
            setResult(Activity.RESULT_OK);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "file write error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        String res = editText.getText().toString();
        saveFile(res);
        finish();
    }
}
