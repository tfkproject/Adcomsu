package app.tfkproject.adcomsu;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import app.tfkproject.adcomsu.Model.Materi;

public class MainActivity extends AppCompatActivity {

    LinearLayout btnComp, btnSup, btnQuiz, btnDic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnComp = (LinearLayout) findViewById(R.id.btn_comp);
        btnSup = (LinearLayout) findViewById(R.id.btn_sup);
        btnQuiz = (LinearLayout) findViewById(R.id.btn_quiz);
        btnDic = (LinearLayout) findViewById(R.id.btn_dic);

        btnComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MateriActivity.class);
                intent.putExtra("key_judul", "Comparative");
                intent.putExtra("key_materi", "file:///android_asset/materi_comp.html");
                intent.putExtra("key_jenis_soal", "soal_comp");
                intent.putExtra("key_color", "#f4c554");
                startActivity(intent);
            }
        });

        btnSup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MateriActivity.class);
                intent.putExtra("key_judul", "Superlative");
                intent.putExtra("key_materi", "file:///android_asset/materi_sup.html");
                intent.putExtra("key_jenis_soal", "soal_sup");
                intent.putExtra("key_color", "#73c0f7");
                startActivity(intent);
            }
        });

        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QuizPilihActivity.class);
                intent.putExtra("key_judul", "Quiz");
                intent.putExtra("key_color", "#FF5ECE72");
                startActivity(intent);
            }
        });

        btnDic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DictActivity.class);
                intent.putExtra("key_judul", "Dictionary");
                intent.putExtra("key_color", "#b1aeab");
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.act_exit){
            dialogBox("Really?", "You will quit the app!");
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogBox(String judul, String pesan){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(judul);
        builder.setMessage(pesan);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Keluar aplikasi
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @Override
    public void onBackPressed() {
        dialogBox("Really?", "You will quit the app!");
    }
}
