package app.tfkproject.adcomsu;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class QuizPilihActivity extends AppCompatActivity {

    LinearLayout btnQComp, btnQSup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_pilih);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String key_judul = getIntent().getStringExtra("key_judul");
        String key_color = getIntent().getStringExtra("key_color");

        getSupportActionBar().setTitle(key_judul);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(key_color)));
        int colorToDarken = Color.parseColor(key_color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darker(colorToDarken));
        }

        btnQComp = (LinearLayout) findViewById(R.id.btn_q_comp);
        btnQComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizPilihActivity.this, QuizActivity.class);
                intent.putExtra("key_judul", "Quiz Comparative");
                intent.putExtra("key_jenis_soal", "soal_comp");
                intent.putExtra("key_color", "#57cbe6");
                startActivity(intent);
            }
        });

        btnQSup = (LinearLayout) findViewById(R.id.btn_q_sup);
        btnQSup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizPilihActivity.this, QuizActivity.class);
                intent.putExtra("key_judul", "Quiz Superlative");
                intent.putExtra("key_jenis_soal", "soal_sup");
                intent.putExtra("key_color", "#e6b657");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public int darker(int color){
        float[] hsv = new float[3];
        int clr = color;
        Color.colorToHSV(clr, hsv);
        hsv[2] *= 0.9f; //level darken
        clr = Color.HSVToColor(hsv);
        return clr;
    }

}
