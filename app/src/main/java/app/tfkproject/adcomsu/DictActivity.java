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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tfkproject.adcomsu.Adapter.AdapterDitc;
import app.tfkproject.adcomsu.Model.Dict;
import app.tfkproject.adcomsu.Model.Soal;

public class DictActivity extends AppCompatActivity {

    LinearLayout btnComp, btnSup, btnQuiz, btnDic;

    private ProgressDialog pDialog;

    JSONArray field;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    AdapterDitc adapter;
    List<Dict> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

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

        //panggil recycleView pada layout
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        items = new ArrayList<>();

        new dapatkanData().execute();

        adapter = new AdapterDitc(DictActivity.this, items);
        recyclerView.setAdapter(adapter);
    }

    private class dapatkanData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // tampilkan progress dialog
            pDialog = new ProgressDialog(DictActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //data materi
            //String data = getResources().getString(R.string.data_dic);

            //susun parameter
            HashMap<String,String> detail = new HashMap<>();

            String dataToSend = null;
            try {
                dataToSend = hashMapToUrl(detail);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //make a Http request and send data to php file
            String response = Request.post("http://203.153.21.11/app/adcomsu/output.php",dataToSend);

            //dapatkan respon
            Log.e("Respon", response);

            if(response != null){
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    field = jsonObject.getJSONArray("field");
                    Dict d = null;

                    for(int i = 0; i < field.length(); i++){
                        JSONObject c = field.getJSONObject(i);

                        d = new Dict();

                        String id = c.getString("id_word");
                        String adj = c.getString("adj");
                        String cmp = c.getString("cmp");
                        String sup = c.getString("sup");

                        d.setId(id);
                        d.setAdj(adj);
                        d.setCmp(cmp);
                        d.setSup(sup);
                        items.add(d);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(DictActivity.this, "Tidak dapat memuat data atau data tidak ada!", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

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
