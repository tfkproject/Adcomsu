package app.tfkproject.adcomsu;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.tfkproject.adcomsu.Adapter.AdapterDitc;
import app.tfkproject.adcomsu.Model.Dict;

public class DictDetailActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener{


    TextView txtAdj, txtCmp, txtSup;
    ImageButton btnAdj, btnCmp, btnSup;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictdetail);

        tts = new TextToSpeech(this, this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String key_judul = getIntent().getStringExtra("key_word");
        String key_color = getIntent().getStringExtra("key_color");
        String key_comp = getIntent().getStringExtra("key_cmp");
        String key_sup = getIntent().getStringExtra("key_sup");

        getSupportActionBar().setTitle(key_judul);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(key_color)));
        int colorToDarken = Color.parseColor(key_color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darker(colorToDarken));
        }

        txtAdj = (TextView) findViewById(R.id.txt_adj);
        txtAdj.setText(key_judul);

        btnAdj = (ImageButton) findViewById(R.id.btn_adj);
        btnAdj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String text = txtAdj.getText().toString();
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }

        });

        txtCmp = (TextView) findViewById(R.id.txt_cmp);
        txtCmp.setText(key_comp);

        btnCmp = (ImageButton) findViewById(R.id.btn_cmp);
        btnCmp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String text = txtCmp.getText().toString();
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }

        });

        txtSup = (TextView) findViewById(R.id.txt_sup);
        txtSup.setText(key_sup);

        btnSup = (ImageButton) findViewById(R.id.btn_sup);
        btnSup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String text = txtSup.getText().toString();
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }

        });

    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

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

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

}
