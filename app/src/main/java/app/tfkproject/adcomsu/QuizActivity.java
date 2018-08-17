package app.tfkproject.adcomsu;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.tfkproject.adcomsu.Model.Soal;

public class QuizActivity extends AppCompatActivity {


    TextView txtJudul, txtHalaman, txtSoal;
    Button btnPrev, btnNext, btnDone, btnOk;
    EditText namaUser;

    List<Soal> listSoal;
    JSONArray soal;
    boolean cekSoal = false;
    int urutanSoal = 0;

    private ProgressDialog pDialog;

    private RadioGroup rg;
    private RadioButton rbA, rbB/*, rbC*/;

    int jawabanYgDiPilih[] = null;
    int jawabanYgBenar[] = null;

    String noSalah = "";
    String dtMateri, key_judul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key_judul = getIntent().getStringExtra("key_judul");
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

        rg = (RadioGroup) findViewById(R.id.rg_jawaban);
        rbA = (RadioButton) findViewById(R.id.rb_jawaban_a);
        rbB = (RadioButton) findViewById(R.id.rb_jawaban_b);
        //rbC = (RadioButton) findViewById(R.id.rb_jawaban_c);

        btnPrev = (Button) findViewById(R.id.btn_prev);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnDone = (Button) findViewById(R.id.btn_check);

        btnPrev.setEnabled(false);

        showInputUser();

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aturJawaban_nya();
                urutanSoal--;
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
                aturJawaban_nya();
                urutanSoal++;
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

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilHasilAkhir();
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
            pDialog = new ProgressDialog(QuizActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String key = getIntent().getStringExtra("key_jenis_soal");
            if(key.contains("soal_sup")){
                dtMateri= getResources().getString(R.string.soal_sup);
            }

            if(key.contains("soal_comp")){
                dtMateri = getResources().getString(R.string.soal_comp);
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
                        String jwbA = c.getString("A");
                        String jwbB = c.getString("B");
                        String jawabanBenar = c.getString("benar");

                        s.setNo(nonya);
                        s.setSoal(nonya+". "+soalnya);
                        s.setPil_a(jwbA);
                        s.setPil_b(jwbB);
                        s.setJawaban(jawabanBenar);
                        //s.setGambar(gambar);
                        listSoal.add(s);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(QuizActivity.this, "Tidak dapat memuat data atau data tidak ada!", Toast.LENGTH_SHORT).show();
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

            jawabanYgDiPilih = new int[listSoal.size()];
            java.util.Arrays.fill(jawabanYgDiPilih, -1);
            jawabanYgBenar = new int[listSoal.size()];
            java.util.Arrays.fill(jawabanYgBenar, -1);
            setUpSoal();
        }
    }

    private void setUpSoal() {
        //Collections.shuffle(listSoal);
        tampilkanSoal(0, cekSoal);
    }

    private void tampilkanSoal(int urutan_soal, boolean review){
        try{
            btnDone.setEnabled(false);
            rg.clearCheck();
            Soal soal_ = new Soal();
            soal_ = listSoal.get(urutan_soal);
            if (jawabanYgBenar[urutan_soal] == -1) {
                jawabanYgBenar[urutan_soal] = Integer.parseInt(soal_.getJawaban());
            }

            String soalnya = soal_.getSoal();
            txtSoal.setText(soalnya);

            rg.check(-1);

            //set gambar nnti disini

            rbA.setText(soal_.getPil_a());
            rbB.setText(soal_.getPil_b());
            //rbC.setText(soal_.getPil_c());

            Log.d("Pilih", jawabanYgDiPilih[urutan_soal] + "");
            if (jawabanYgDiPilih[urutan_soal] == 1)
                rg.check(R.id.rb_jawaban_a);
            if (jawabanYgDiPilih[urutan_soal] == 2)
                rg.check(R.id.rb_jawaban_b);
            /*if (jawabanYgDiTulis[urutan_soal] == 3)
                rg.check(R.id.rb_jawaban_c);*/

            if (urutan_soal == (listSoal.size() - 1)) {
                btnNext.setEnabled(false);
                btnDone.setEnabled(true);
            }

            if (urutan_soal == 0)
                btnPrev.setEnabled(false);

            if (urutan_soal > 0)
                btnPrev.setEnabled(true);

            if (urutan_soal < (listSoal.size() - 1))
                btnNext.setEnabled(true);

            if (review) {
                Log.d("priksa", jawabanYgDiPilih[urutan_soal] + ""
                        + jawabanYgBenar[urutan_soal]);
                if (jawabanYgDiPilih[urutan_soal] != jawabanYgBenar[urutan_soal]) {
                    if (jawabanYgDiPilih[urutan_soal] == 1)
                        rbA.setTextColor(Color.RED);
                    if (jawabanYgDiPilih[urutan_soal] == 2)
                        rbB.setTextColor(Color.RED);
                    /*if (jawabanYgDiTulis[urutan_soal] == 3)
                        rbC.setTextColor(Color.RED);*/
                }
                if (jawabanYgBenar[urutan_soal] == 1)
                    rbA.setTextColor(Color.GREEN);
                if (jawabanYgBenar[urutan_soal] == 2)
                    rbB.setTextColor(Color.GREEN);
                /*if (jawabanYgBenar[urutan_soal] == 3)
                    rbC.setTextColor(Color.GREEN);*/
            }

            //set halaman
            txtHalaman.setText("Soal: "+(urutanSoal + 1)+ " dari " + listSoal.size());

        } catch (Exception e){
            Log.e(this.getClass().toString(), e.getMessage(), e.getCause());
        }
    }

    private void aturJawaban_nya() {
        if (rbA.isChecked())
            jawabanYgDiPilih[urutanSoal] = 1;
        if (rbB.isChecked())
            jawabanYgDiPilih[urutanSoal] = 2;
        /*if (rbC.isChecked())
            jawabanYgDiTulis[urutanSoal] = 3;*/

        Log.d("", Arrays.toString(jawabanYgDiPilih));
        Log.d("", Arrays.toString(jawabanYgBenar));

    }

    private void tampilHasilAkhir() {
        //atur jawaban
        aturJawaban_nya();

        /*// hitung berapa yg benar
        int jumlahJawabanYgBenar = 0;
        for (int i = 0; i < jawabanYgBenar.length; i++) {
            if ((jawabanYgBenar[i] != -1) && (jawabanYgBenar[i] == jawabanYgDiTulis[i]))
                jumlahJawabanYgBenar++;
            if(jawabanYgBenar[i] != jawabanYgDiTulis[i])
                noSalah = noSalah+" " + Integer.toString(i+1);
        }
        if(noSalah == ""){
            noSalah = "Benar semua";
        }
        else{
            noSalah = "No yang salah"+noSalah;
        }
        AlertDialog tampilKotakAlert;
        tampilKotakAlert = new AlertDialog.Builder(QuizActivity.this).create();
        tampilKotakAlert.setTitle("Nilai");
        tampilKotakAlert.setMessage("Benar " +jumlahJawabanYgBenar + " dari "
                + (listSoal.size() +" soal. "+noSalah));

        tampilKotakAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "Lagi",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        cekSoal = false;
                        urutanSoal = 0;
                        noSalah="";
                        java.util.Arrays.fill(jawabanYgDiTulis, -2);
                        tampilkanSoal(0, cekSoal);
                    }
                });

        tampilKotakAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Review",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cekSoal = false;
                        noSalah = "";
                        tampilkanSoal(0, cekSoal);
                    }
                });

        tampilKotakAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Keluar",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        cekSoal = false;
                        finish();
                    }
                });

        tampilKotakAlert.show();*/
        int jumlahJawabanYgBenar = 0;
        for (int i = 0; i < jawabanYgBenar.length; i++) {
            if ((jawabanYgBenar[i] != -1)
                    && (jawabanYgBenar[i] == jawabanYgDiPilih[i]))
                jumlahJawabanYgBenar++;
        }
        /*AlertDialog tampilKotakAlert;
        tampilKotakAlert = new AlertDialog.Builder(QuizActivity.this)
                .create();
        tampilKotakAlert.setTitle(namaUser.getText().toString()+", hasil anda:");
        tampilKotakAlert.setMessage("Score " + jumlahJawabanYgBenar * 5);

        /*tampilKotakAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "Periksa",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        cekSoal = true;
                        urutanSoal = 0;
                        tampilkanSoal(0, cekSoal);
                    }
                });*/

        /*tampilKotakAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Lagi",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        java.util.Arrays.fill(jawabanYgDiTulis, -1);
                        cekSoal = false;
                        urutanSoal = 0;
                        tampilkanSoal(0, cekSoal);
                    }
                });

        tampilKotakAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Keluar",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        cekSoal = false;
                        finish();
                    }
                });

        tampilKotakAlert.show();

        tampilKotakAlert.setCancelable(false);
        tampilKotakAlert.setCanceledOnTouchOutside(false);*/

        ////fancyAlertDialog///
        new FancyAlertDialog.Builder(this)
                .setTitle("Hi, "+namaUser.getText().toString())
                .setBackgroundColor(Color.parseColor("#FF5ECE72"))  //Don't pass R.color.colorvalue
                .setMessage("YOUR SCORE "+ jumlahJawabanYgBenar * 5)
                .setNegativeBtnText("Next Essay")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Replay?")
                .setPositiveBtnBackground(Color.parseColor("#FF5ECE72"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.SLIDE)
                .isCancellable(true)
                .setIcon(R.drawable.ic_star_border_black_24dp, Icon.Visible)
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        cekSoal = false;

                        String key = getIntent().getStringExtra("key_jenis_soal");

                        Intent intent = new Intent(QuizActivity.this, QuizEssayActivity.class);
                        intent.putExtra("key_judul", key_judul);
                        intent.putExtra("key_jenis_soal", key);
                        intent.putExtra("key_nama_user", namaUser.getText().toString());
                        intent.putExtra("key_color", "#FF5ECE72");
                        startActivity(intent);
                    }
                })
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        Arrays.fill(jawabanYgDiPilih, -1);
                        cekSoal = false;
                        urutanSoal = 0;
                        tampilkanSoal(0, cekSoal);
                    }
                })
                .build();
        ////////
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
