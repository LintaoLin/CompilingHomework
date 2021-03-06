package sssta.org.compiling.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import sssta.org.compiling.R;
import sssta.org.compiling.compile.DrawView;
import sssta.org.compiling.compile.SyntaxParsing;

public class MainActivity extends AppCompatActivity {

    private DrawView drawView;
    private SyntaxParsing syntaxParsing;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
    }

    private void initView() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view) ->
            startActivityForResult(new Intent(MainActivity.this, EditActivity.class), 20)
        );

        drawView = findViewById(R.id.my_view);
        errorText = findViewById(R.id.error_text);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestReadExternalPermission();
            }
        }

        syntaxParsing = new SyntaxParsing(viewParamsList -> {
            if (viewParamsList != null) {
                drawView.setViewParamsArrayList(viewParamsList);
            }
        });

        parseFile();
    }

    private void parseFile() {
        try {
            syntaxParsing.parse();
            errorText.setVisibility(View.GONE);
            drawView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            errorText.setText(e.getMessage());
            errorText.setVisibility(View.VISIBLE);
            drawView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 20 && resultCode == Activity.RESULT_OK) {
            parseFile();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.M) private void requestReadExternalPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new ConfirmationDialog().show(getSupportFragmentManager(), "dialog");
        } else {
            requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
        }
    }

    public static class ConfirmationDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                .setMessage("need read external storage permission")
                .setPositiveButton(android.R.string.ok,
                    ((dialogInterface, i) -> {
                        if (Build.VERSION.SDK_INT > 23) {
                            requestPermissions(
                                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                1);
                        }
                    })
                )
                .setNegativeButton(android.R.string.cancel,
                    ((dialogInterface, i) -> {
                        Activity activity = parent.getActivity();
                        if (activity != null) {
                            activity.finish();
                        }
                    })
                )
                .create();
        }
    }
}
