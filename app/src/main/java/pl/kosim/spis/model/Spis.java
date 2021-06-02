package pl.kosim.spis.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

public class Spis {

    private static final String TAG = "Spis";
    private String nazwa; /* identyfikator */
    private HashMap<Licznik, BigDecimal> wskazania = new HashMap<Licznik, BigDecimal>();

    public Spis(String nazwa) {
        Log.d(TAG, "Spis: "+ nazwa);
        this.nazwa = nazwa;
    }

    public Spis( SQLiteDatabase db, String nazwa ) {
        Log.d(TAG, "Spis: z bazy danych "+nazwa);
        this.nazwa = nazwa;
        Cursor cursor = db.query("spisy",new String[] {"nazwa"},"nazwa='" + nazwa + "'",null,null,null,null);
        cursor.moveToFirst();
        nazwa = cursor.getString(0);
        selectOdczyty(db,nazwa);
    }

    public BigDecimal getWskazanie(Licznik licznik) { return wskazania.get(licznik); }

    public void setWskazanie(Licznik licznik, BigDecimal wskazanie) {
        wskazania.put(licznik,wskazanie);
    }

    public String getNazwa() {
        return nazwa;
    }

    public BigDecimal getSuma( Media media ) {
        BigDecimal s = new BigDecimal(0);
        for( Licznik licznik: wskazania.keySet() ) {
            if( licznik.is(media) && ! licznik.is(Lokal.DOM) ) {
                s = s.add( wskazania.get(licznik) );
            }
        }
        return s;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder opis = new StringBuilder("Spis ");
        opis.append(nazwa);
        for( Licznik licznik : wskazania.keySet() ) {
            opis.append(", ").append(licznik.toString()).append("=").append(wskazania.get(licznik).toString());
        }
        return opis.toString();
    }

    /* wstawia Spis do bazy */
    public void insertSpis( SQLiteDatabase db ) {
        ContentValues dane = new ContentValues();
        dane.put("nazwa",nazwa);
        db.insert("spisy",null,dane);
        for( Licznik licznik: wskazania.keySet() ) {
            dane = new ContentValues();
            dane.put("spis",nazwa);
            dane.put("lokal",licznik.getLokal().toString());
            dane.put("media",licznik.getMedia().toString());
            dane.put("wskazanie",wskazania.get(licznik).toString());
            db.insert("odczyty",null,dane);
        }
    }

    /* odczytanie wskazan z bazy */
    private void selectOdczyty( SQLiteDatabase db, String nazwa ) {
        Cursor cursor = db.query("odczyty",new String[] {"lokal","media","wskazanie"},"spis='" + nazwa + "'",null,null,null,null);
        while( cursor.moveToNext() ) {
            Licznik licznik = new Licznik( Media.valueOf( cursor.getString(1) ), Lokal.valueOf( cursor.getString(0) ) );
            setWskazanie( licznik, new BigDecimal( cursor.getString(2) ) );
        };
    }

    /* aktualizacja odczytu w bazie */
    public void updateOdczyt(SQLiteDatabase db, Licznik licznik, BigDecimal wskazanie) {
        setWskazanie(licznik,wskazanie);
        ContentValues dane = new ContentValues();
        dane.put("wskazanie",wskazanie.toString());
        db.update("odczyty",dane,"spis=? and lokal=? and media=?",new String[] {nazwa,licznik.getLokal().toString(), licznik.getMedia().toString()});
    }

    /* aktualizacja odczytu w bazie z JSONa */
    public void updateOdczyty(SQLiteDatabase db, JSONObject json) throws JSONException {
        JSONArray jsonOdczyty = json.getJSONArray("odczyty");
        for (int i = 0; i < jsonOdczyty.length(); i++) {
            JSONObject jsonOdczyt = jsonOdczyty.getJSONObject(i);
            Licznik licznik = new Licznik(Media.valueOf(jsonOdczyt.getString("media")), Lokal.valueOf(jsonOdczyt.getString("lokal")));
            BigDecimal wskazanie = new BigDecimal(jsonOdczyt.getString("wskazanie"));
            updateOdczyt(db, licznik, wskazanie);
        }
    }

    /* eksport Spisu do JSONa */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("nazwa", nazwa);
            JSONArray jsonOdczyty = new JSONArray();
            for( Licznik licznik: wskazania.keySet() ) {
                JSONObject jsonOdczyt = new JSONObject();
                jsonOdczyt.put("lokal",licznik.getLokal().toString());
                jsonOdczyt.put("media",licznik.getMedia().toString());
                jsonOdczyt.put("wskazanie",wskazania.get(licznik).toString());
                jsonOdczyty.put(jsonOdczyt);
            }
            json.put("odczyty",jsonOdczyty);
        } catch (Exception e) {
            Log.e(TAG, "toJSON: " + e.getMessage());
        }
        return json;
    }


}
