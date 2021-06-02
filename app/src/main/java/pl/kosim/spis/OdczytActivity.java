package pl.kosim.spis;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

import pl.kosim.spis.dao.SpisManager;
import pl.kosim.spis.model.Licznik;
import pl.kosim.spis.model.Lokal;
import pl.kosim.spis.model.Media;
import pl.kosim.spis.model.Spis;

public class OdczytActivity extends AppCompatActivity {

    private static final String TAG = "OdczytActivity";
    private BigDecimal odczyt, pOdczyt;
    private Licznik licznik;
    private TextView fLicznik, fPoprzedniOdczyt;
    private EditText fOdczyt;
    private String sPrzedEdycja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odczyt);
        Toolbar toolbar = findViewById(R.id.toolbar_odczyt);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        fLicznik = findViewById(R.id.licz_nazwa);
        fOdczyt = findViewById(R.id.licz_odczyt);
        fPoprzedniOdczyt = findViewById(R.id.licz_poprz);

        zaladujDane();

        Button b = findViewById(R.id.btn_nast_licz);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpisManager.get().nastepnyLicznik();
                zaladujDane();
            }
        });
        b = findViewById(R.id.btn_poprz_licz);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpisManager.get().poprzedniLicznik();
                zaladujDane();
            }
        });

        fOdczyt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0) zapiszDane("0");
                else zapiszDane( s.toString() );
            }
        });
    }

    private void zaladujDane() {
        licznik = SpisManager.get().podajLicznik();
        fLicznik.setText( licznik.toString() );

        odczyt = SpisManager.get().podajSpis().getWskazanie(licznik);
        sPrzedEdycja = odczyt.signum()==0 ? "" : licznik.getMedia().format(odczyt);
        fOdczyt.setText( sPrzedEdycja );
        fOdczyt.setSelection( sPrzedEdycja.length() );

        pOdczyt = SpisManager.get().podajSpisPoprzedni().getWskazanie(licznik);

        fPoprzedniOdczyt = findViewById(R.id.licz_poprz);
        if( licznik.getLokal() != Lokal.DOM ) {
            fPoprzedniOdczyt.setText( licznik.getMedia().format(pOdczyt));
        } else if( licznik.getMedia() == Media.WODA_ZIMNA ) {
            fPoprzedniOdczyt.setText( licznik.getMedia().format(pOdczyt) + "+L=" + Media.ZAOK.format(pOdczyt.add(SpisManager.get().zuzycieWody)));
        } else {
            fPoprzedniOdczyt.setText( licznik.getMedia().format(pOdczyt));
        }
    }

    private void zapiszDane( String sPoEdycji ) {
        //String sPoEdycji = fOdczyt.getText().toString();
        if( ! sPrzedEdycja.equals(sPoEdycji) ) {
            SpisManager.get().zapiszWskazanie(licznik,new BigDecimal(sPoEdycji.replace(',','.')));
        }
    }
}
