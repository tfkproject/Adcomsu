package app.tfkproject.adcomsu;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.tfkproject.adcomsu.Model.Soal;

public class QuizEssayActivity extends AppCompatActivity {


    TextView txtJudul, txtHalaman, txtSoal;
    Button btnPrev, btnNext, btnCheck, btnOk;
    EditText edtJawaban, namaUser;

    List<Soal> listSoal;
    JSONArray soal;
    boolean cekSoal = false;
    int urutanSoal = 0;

    private ProgressDialog pDialog;


    String jawabanYgDiTulis[] = null;
    String jawabanYgBenar[] = null;

    String noSalah = "";
    String dtMateri, key_judul, key_nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_essay);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key_judul = getIntent().getStringExtra("key_judul");
        key_nama = getIntent().getStringExtra("key_nama_user");
        String key_color = getIntent().getStringExtra("key_color");

        getSupportActionBar().setTitle(key_judul);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(key_color)));
        int colorToDarken = Color.parseColor(key_color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darker(colorToDarken));
        }

        listSoal = new ArrayList<Soal>();

        txtJudul = (TextView) findViewById(R.id.textView2);
        String key = getIntent().getStringExtra("key_jenis_soal");
        if(key.contains("soal_sup")){
            txtJudul.setText("(Superlative)");
        }

        if(key.contains("soal_comp")){
            txtJudul.setText("(Comparative)");
        }

        txtHalaman = (TextView) findViewById(R.id.txt_halaman);
        txtSoal = (TextView) findViewById(R.id.txt_soal);

        edtJawaban = (EditText) findViewById(R.id.edt_jawaban);

        btnPrev = (Button) findViewById(R.id.btn_prev);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnCheck = (Button) findViewById(R.id.btn_check);

        btnPrev.setEnabled(false);

        //showInputUser();
        new dapatkanMateri().execute();

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urutanSoal--;
                //aturJawaban_nya(urutanSoal);
                if (urutanSoal < 0){
                    urutanSoal = 0;
                }

                btnNext.setEnabled(true);

                if (urutanSoal == 0){
                    btnPrev.setEnabled(false);
                }

                tampilkanSoal(urutanSoal, cekSoal);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urutanSoal++;
                //aturJawaban_nya(urutanSoal);
                if (urutanSoal >= listSoal.size()){
                    urutanSoal = listSoal.size() - 1;
                }

                btnPrev.setEnabled(true);

                if (urutanSoal == listSoal.size() - 1){
                    btnNext.setEnabled(false);
                }

                tampilkanSoal(urutanSoal, cekSoal);
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aturJawaban_nya(urutanSoal);
            }
        });
    }

    private void showInputUser() {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.input_nama, null);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        dialog.setView(v);
        dialog.setTitle("Input your name");
        dialog.setCancelable(false);

        btnOk = (Button) v.findViewById(R.id.buttonOK);
        namaUser = (EditText) v.findViewById(R.id.editTextNama);

        btnOk.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(namaUser.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Can't empty!", Toast.LENGTH_LONG).show();
                }else{
                    new dapatkanMateri().execute();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    private class dapatkanMateri extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // tampilkan progress dialog
            pDialog = new ProgressDialog(QuizEssayActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String key = getIntent().getStringExtra("key_jenis_soal");
            if(key.contains("soal_sup")){
                dtMateri= getResources().getString(R.string.soal_sup_essay);
            }

            if(key.contains("soal_comp")){
                dtMateri = getResources().getString(R.string.soal_comp_essay);
            }

            if(dtMateri != null){
                try{
                    JSONObject jsonObject = new JSONObject(dtMateri);
                    soal = jsonObject.getJSONArray("data_soal");
                    Soal s = null;

                    for(int i = 0; i < soal.length(); i++){
                        JSONObject c = soal.getJSONObject(i);

                        s = new Soal();

                        String nonya = c.getString("no");
                        String soalnya = c.getString("soal");
                        String jawabanBenar = c.getString("benar");

                        s.setNo(nonya);
                        s.setSoal(nonya+". "+soalnya);
                        s.setJawaban(jawabanBenar);
                        //s.setGambar(gambar);
                        listSoal.add(s);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(QuizEssayActivity.this, "Tidak dapat memuat data atau data tidak ada!", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //dismis progress dialog
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }

            /*jawabanYgDiTulis = new String[listSoal.size()];
            Arrays.fill(jawabanYgDiTulis, -1);
            jawabanYgBenar = new String[listSoal.size()];
            Arrays.fill(jawabanYgBenar, -1);*/
            setUpSoal();
        }
    }

    private void setUpSoal() {
        //Collections.shuffle(listSoal);
        tampilkanSoal(0, cekSoal);
    }

    private void tampilkanSoal(int urutan_soal, boolean review){
        try{
            btnCheck.setEnabled(true);
            edtJawaban.setText("");
            Soal soal_ = new Soal();
            soal_ = listSoal.get(urutan_soal);
            /*if (jawabanYgBenar[urutan_soal] == -1) {
                jawabanYgBenar[urutan_soal] = Integer.parseInt(soal_.getJawaban());
            }*/

            txtSoal.setText(soal_.getSoal());

            /*if (jawabanYgDiTulis[urutan_soal] == 1)
                rg.check(R.id.rb_jawaban_a);
            if (jawabanYgDiTulis[urutan_soal] == 2)
                rg.check(R.id.rb_jawaban_b);
            /*if (jawabanYgDiTulis[urutan_soal] == 3)
                rg.check(R.id.rb_jawaban_c);*/

            if (urutan_soal == (listSoal.size() - 1)) {
                btnNext.setEnabled(false);
                btnCheck.setEnabled(true);
            }

            if (urutan_soal == 0)
                btnPrev.setEnabled(false);

            if (urutan_soal > 0)
                btnPrev.setEnabled(true);

            if (urutan_soal < (listSoal.size() - 1))
                btnNext.setEnabled(true);

            /*if (review) {
                Log.d("priksa", jawabanYgDiTulis[urutan_soal] + ""
                        + jawabanYgBenar[urutan_soal]);
                if (jawabanYgDiTulis[urutan_soal] != jawabanYgBenar[urutan_soal]) {
                    if (jawabanYgDiTulis[urutan_soal] == 1)
                        rbA.setTextColor(Color.RED);
                    if (jawabanYgDiTulis[urutan_soal] == 2)
                        rbB.setTextColor(Color.RED);
                    if (jawabanYgDiTulis[urutan_soal] == 3)
                        rbC.setTextColor(Color.RED);
                }
                if (jawabanYgBenar[urutan_soal] == 1)
                    rbA.setTextColor(Color.GREEN);
                if (jawabanYgBenar[urutan_soal] == 2)
                    rbB.setTextColor(Color.GREEN);
                if (jawabanYgBenar[urutan_soal] == 3)
                    rbC.setTextColor(Color.GREEN);
            }*/

            //set halaman
            txtHalaman.setText("Soal: "+(urutanSoal + 1)+ " dari " + listSoal.size());

        } catch (Exception e){
            Log.e(this.getClass().toString(), e.getMessage(), e.getCause());
        }
    }

    private void aturJawaban_nya(int urutan_soal) {

        Soal soal = new Soal();
        soal = listSoal.get(urutan_soal);
        String jwb = soal.getJawaban();
        if (jwb.equalsIgnoreCase(edtJawaban.getText().toString())) {
            Toast.makeText(this, "Correct answer!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Wrong answer", Toast.LENGTH_SHORT).show();
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

}
