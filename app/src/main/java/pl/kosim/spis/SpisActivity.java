package pl.kosim.spis;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import pl.kosim.spis.dao.SpisManager;
import pl.kosim.spis.interfejsy.Oczekujacy;
import pl.kosim.spis.model.Licznik;
import pl.kosim.spis.model.Lokal;
import pl.kosim.spis.model.Media;
import pl.kosim.spis.model.Spis;

public class SpisActivity extends AppCompatActivity implements Oczekujacy {

    private static final String TAG = "SpisActivity";

    public static final String SPIS_POZ = "pl.kosim.spis.SPIS_ID";
    private Spis spis, pSpis;
    private TextView fSpisNazwa,fSpisPoprzedni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spis);
        Toolbar toolbar = findViewById(R.id.toolbar_spis);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        fSpisNazwa = (TextView)findViewById(R.id.spis_nazwa);
        fSpisPoprzedni = (TextView)findViewById(R.id.spis_poprzedni);
        spis = SpisManager.get().podajSpis();
        if(spis == null) {
            fSpisNazwa.setText(String.format("BRAK SPISU"));
        } else {
            zaladujDane();
        }
    }

    @Override
    public void onResume(){
        zaladujDane();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.spis_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_odczyty_ko:
                odczyty(SpisManager.POM_KOTLOWNIA);
                return false;
            case R.id.action_odczyty_pt1:
                odczyty(SpisManager.POM_POM_TECH_1);
                return false;
            case R.id.action_odczyty_pt2:
                odczyty(SpisManager.POM_POM_TECH_2);
                return false;
            case R.id.action_odczyty_kl:
                odczyty(SpisManager.POM_KLATKA);
                return false;
            case R.id.action_odczyty_ul:
                odczyty(SpisManager.POM_ULICA);
                return false;
            case R.id.action_odczyty_x:
                wyslijSpis();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void wyslijSpis() {
        SpisManager.get().wyslijSpis(this);
    }

    private BigDecimal ustawTextView(int id, BigDecimal x, Media media) {
        TextView tv = (TextView)findViewById(id);
        if( x.signum()==-1 ) tv.setTextColor(getResources().getColor(R.color.colorAccent));
        tv.setText( Media.ZAOK.format(x));
        return x;
    }

    private BigDecimal ustawTextView(Media media, Lokal lokal, int id, Spis spis, Spis pSpis) {
        Licznik licznik = new Licznik(media, lokal);
        TextView tv = (TextView)findViewById(id);
        BigDecimal delta = spis.getWskazanie(licznik).subtract(pSpis.getWskazanie(licznik));
        if( delta.signum()==-1 ) tv.setTextColor(getResources().getColor(R.color.colorAccent));
        else                     tv.setTextColor(getResources().getColor(R.color.colorBasic));
        tv.setText( Media.ZAOK.format(delta) );
        return delta;
    }

    /* pokaz pierwszy odczyt pomieszczenia */
    private void odczyty( int pomieszczenie ) {
        Log.d(TAG, "odczyty: "+pomieszczenie);
        SpisManager.get().ustawLicznik(pomieszczenie);
        Intent intent = new Intent(this, OdczytActivity.class);
        startActivity(intent);
    }

    private void zaladujDane() {
        pSpis = SpisManager.get().podajSpisPoprzedni();
        fSpisNazwa.setText(spis.getNazwa());
        fSpisPoprzedni.setText("Poprzedni spis "+pSpis.getNazwa());
        /* tabela porownania podsumowan */
        Licznik l = new Licznik(Media.WODA_ZIMNA,Lokal.DOM);
        SpisManager.get().zuzycieWodyDom = ustawTextView(R.id.wz_delta, ustawTextView(R.id.wz_suma,spis.getWskazanie(l),Media.WODA_ZIMNA).subtract( ustawTextView(R.id.wz_poprz,pSpis.getWskazanie(l),Media.WODA_ZIMNA) ),Media.WODA_ZIMNA );
        l = new Licznik(Media.CIEPLO,Lokal.DOM);
        SpisManager.get().zuzycieCieplaDom = ustawTextView(R.id.c_delta, ustawTextView(R.id.c_suma,spis.getWskazanie(l),Media.CIEPLO).subtract( ustawTextView(R.id.c_poprz,pSpis.getWskazanie(l),Media.CIEPLO) ),Media.CIEPLO );
        l = new Licznik(Media.GAZ,Lokal.DOM);
        ustawTextView(R.id.ga_delta, ustawTextView(R.id.ga_suma,spis.getWskazanie(l),Media.GAZ).subtract( ustawTextView(R.id.ga_poprz,pSpis.getWskazanie(l),Media.GAZ) ),Media.GAZ );
        l = new Licznik(Media.ENERGIA,Lokal.DOM);
        SpisManager.get().zuzycieEnergiiDom = ustawTextView(R.id.en_delta, ustawTextView(R.id.en_suma,spis.getWskazanie(l),Media.ENERGIA).subtract( ustawTextView(R.id.en_poprz,pSpis.getWskazanie(l),Media.ENERGIA) ),Media.ENERGIA );
        /* tabela porownania lokali */
        SpisManager.get().zuzycieWody =
            ustawTextView(R.id.ra_wz,
                ustawTextView( Media.WODA_ZIMNA,  Lokal.M1, R.id.m1_wz, spis, pSpis )
                        .add( ustawTextView( Media.WODA_ZIMNA,  Lokal.M2, R.id.m2_wz, spis, pSpis ) )
                        .add( ustawTextView( Media.WODA_ZIMNA,  Lokal.M3, R.id.m3_wz, spis, pSpis ) )
                        .add( ustawTextView( Media.WODA_ZIMNA,  Lokal.M4, R.id.m4_wz, spis, pSpis ) )
                        .add( ustawTextView( Media.WODA_ZIMNA,  Lokal.M5, R.id.m5_wz, spis, pSpis ) )
                        .add( ustawTextView( Media.WODA_ZIMNA,  Lokal.M6, R.id.m6_wz, spis, pSpis ) ), Media.WODA_ZIMNA )
            .add (
                ustawTextView(R.id.ra_wc,
                        ustawTextView( Media.WODA_CIEPLA, Lokal.M1, R.id.m1_wc, spis, pSpis )
                                .add( ustawTextView( Media.WODA_CIEPLA, Lokal.M2, R.id.m2_wc, spis, pSpis ) )
                                .add( ustawTextView( Media.WODA_CIEPLA, Lokal.M3, R.id.m3_wc, spis, pSpis ) )
                                .add( ustawTextView( Media.WODA_CIEPLA, Lokal.M4, R.id.m4_wc, spis, pSpis ) )
                                .add( ustawTextView( Media.WODA_CIEPLA, Lokal.M5, R.id.m5_wc, spis, pSpis ) )
                                .add( ustawTextView( Media.WODA_CIEPLA, Lokal.M6, R.id.m6_wc, spis, pSpis ) ), Media.WODA_CIEPLA )
            );
        SpisManager.get().zuzycieCiepla = ustawTextView(R.id.ra_c,
                ustawTextView( Media.CIEPLO,      Lokal.M1, R.id.m1_c,  spis, pSpis )
                        .add( ustawTextView( Media.CIEPLO,      Lokal.M2, R.id.m2_c,  spis, pSpis ) )
                        .add( ustawTextView( Media.CIEPLO,      Lokal.M3, R.id.m3_c,  spis, pSpis ) )
                        .add( ustawTextView( Media.CIEPLO,      Lokal.M4, R.id.m4_c,  spis, pSpis ) )
                        .add( ustawTextView( Media.CIEPLO,      Lokal.M5, R.id.m5_c,  spis, pSpis ) )
                        .add( ustawTextView( Media.CIEPLO,      Lokal.M6, R.id.m6_c,  spis, pSpis ) ), Media.CIEPLO );
        SpisManager.get().zuzycieEnergii = ustawTextView(R.id.ra_en,
                ustawTextView( Media.ENERGIA,     Lokal.M1, R.id.m1_en, spis, pSpis )
                        .add( ustawTextView( Media.ENERGIA,     Lokal.M2, R.id.m2_en, spis, pSpis ) )
                        .add( ustawTextView( Media.ENERGIA,     Lokal.M3, R.id.m3_en, spis, pSpis ) )
                        .add( ustawTextView( Media.ENERGIA,     Lokal.M4, R.id.m4_en, spis, pSpis ) )
                        .add( ustawTextView( Media.ENERGIA,     Lokal.M5, R.id.m5_en, spis, pSpis ) )
                        .add( ustawTextView( Media.ENERGIA,     Lokal.M6, R.id.m6_en, spis, pSpis ) ), Media.ENERGIA );
        ustawTextView( R.id.ra_w, SpisManager.get().zuzycieWody, Media.WODA_ZIMNA );
        ustawTextView( R.id.wz_wsp, SpisManager.get().zuzycieWodyDom.subtract(SpisManager.get().zuzycieWody),Media.WODA_ZIMNA);
        ustawTextView( R.id.c_wsp, SpisManager.get().zuzycieCieplaDom.subtract(SpisManager.get().zuzycieCiepla),Media.CIEPLO);
        ustawTextView( R.id.en_wsp, SpisManager.get().zuzycieEnergiiDom.subtract(SpisManager.get().zuzycieEnergii),Media.ENERGIA);
    }

    @Override
    public void koniecPracy(String komunikat) {
        Toast.makeText(getApplicationContext(),komunikat,Toast.LENGTH_LONG).show();
    }
}
