package app.tfkproject.adcomsu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import app.tfkproject.adcomsu.Model.Materi;

public class MateriActivity extends AppCompatActivity {

    WebView webView;

    List<Materi> listMateri;
    JSONArray materi;
    boolean cekMateri = false;
    int urutanMateri = 0;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materi);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listMateri = new ArrayList<Materi>();

        webView = (WebView) findViewById(R.id.webView);


        String key_judul = getIntent().getStringExtra("key_judul");
        String key_materi = getIntent().getStringExtra("key_materi");
        String key_color = getIntent().getStringExtra("key_color");

        getSupportActionBar().setTitle(key_judul);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(key_color)));
        int colorToDarken = Color.parseColor(key_color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darker(colorToDarken));
        }

        webView.loadUrl(key_materi);
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
