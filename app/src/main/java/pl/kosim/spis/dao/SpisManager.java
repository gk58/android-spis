package pl.kosim.spis.dao;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import pl.kosim.spis.interfejsy.Oczekujacy;
import pl.kosim.spis.model.Licznik;
import pl.kosim.spis.model.Lokal;
import pl.kosim.spis.model.Media;
import pl.kosim.spis.model.Nawigacja;
import pl.kosim.spis.model.Spis;

public class SpisManager extends Application {

    private static final String TAG = "SpisManager";
    public static final int POM_KOTLOWNIA = 0;
    public static final int POM_POM_TECH_2 = 7;
    public static final int POM_POM_TECH_1 = 13;
    public static final int POM_KLATKA = 21;
    public static final int POM_ULICA = 29;
    private static final String DB_NAME = "SPIS";
    private static final int DB_VER = 3;
//    private static final String SPIS_URL = "http://10.2.40.198:8080/static/";
    private static final String SPIS_URL = "http://192.168.1.106:8080/kosim/";

    public static final String SPIS_PLIK = "spis";
//    public static final String SPISY_PLIK_SEPARATOR = "|";
//    public static final String SPISY_PLIK_NOWALINIA = System.getProperty("line.separator");
    private static SpisManager instance = null;
    private static SQLiteDatabase db = null;
    private static ArrayList<String> dbLista;
    private static ArrayList<Nawigacja> nawigacje;
    private static int kursor = 0; /* wskazuje bieżący licznik, pozycję w nawigacji */
    private static Spis bSpis = null; /* bieżący spis, wybrany */
    private static Spis pSpis = null; /* poprzedni spis względem bieżącego */
    public BigDecimal zuzycieWody;
    public BigDecimal zuzycieWodyDom;
    public BigDecimal zuzycieEnergii;
    public BigDecimal zuzycieEnergiiDom;
    public BigDecimal zuzycieCiepla;
    public BigDecimal zuzycieCieplaDom;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        /* inicjalizacja liczników w kolejnosci spisywania */
        nawigacje = new ArrayList<Nawigacja>();
        nawigacje.add( 0,new Nawigacja(new Licznik(Media.CIEPLO,      Lokal.DOM),  1, 6 ));
        nawigacje.add( 1,new Nawigacja(new Licznik(Media.WODA_ZIMNA,  Lokal.M5 ),  2, 0 ));
        nawigacje.add( 2,new Nawigacja(new Licznik(Media.WODA_ZIMNA,  Lokal.M6 ),  3, 1 ));
        nawigacje.add( 3,new Nawigacja(new Licznik(Media.WODA_CIEPLA, Lokal.M6 ),  4, 2 ));
        nawigacje.add( 4,new Nawigacja(new Licznik(Media.WODA_CIEPLA, Lokal.M5 ),  5, 3 ));
        nawigacje.add( 5,new Nawigacja(new Licznik(Media.CIEPLO,      Lokal.M5 ),  6, 4 ));
        nawigacje.add( 6,new Nawigacja(new Licznik(Media.CIEPLO,      Lokal.M6 ),  0, 5 ));

        nawigacje.add( 7,new Nawigacja(new Licznik(Media.WODA_ZIMNA,  Lokal.M3 ),  8, 12 ));
        nawigacje.add( 8,new Nawigacja(new Licznik(Media.WODA_ZIMNA,  Lokal.M4 ),  9,  7 ));
        nawigacje.add( 9,new Nawigacja(new Licznik(Media.WODA_CIEPLA, Lokal.M4 ), 10,  8 ));
        nawigacje.add(10,new Nawigacja(new Licznik(Media.WODA_CIEPLA, Lokal.M3 ), 11,  9 ));
        nawigacje.add(11,new Nawigacja(new Licznik(Media.CIEPLO,      Lokal.M3 ), 12, 10 ));
        nawigacje.add(12,new Nawigacja(new Licznik(Media.CIEPLO,      Lokal.M4 ),  7, 11 ));

        nawigacje.add(13,new Nawigacja(new Licznik(Media.GAZ,         Lokal.M1 ), 14, 20 ));
        nawigacje.add(14,new Nawigacja(new Licznik(Media.GAZ,         Lokal.M2 ), 15, 13 ));
        nawigacje.add(15,new Nawigacja(new Licznik(Media.WODA_ZIMNA,  Lokal.M1 ), 16, 14 ));
        nawigacje.add(16,new Nawigacja(new Licznik(Media.WODA_ZIMNA,  Lokal.M2 ), 17, 15 ));
        nawigacje.add(17,new Nawigacja(new Licznik(Media.WODA_CIEPLA, Lokal.M2 ), 18, 16 ));
        nawigacje.add(18,new Nawigacja(new Licznik(Media.WODA_CIEPLA, Lokal.M1 ), 19, 17 ));
        nawigacje.add(19,new Nawigacja(new Licznik(Media.CIEPLO,      Lokal.M1 ), 20, 18 ));
        nawigacje.add(20,new Nawigacja(new Licznik(Media.CIEPLO,      Lokal.M2 ), 13, 19 ));

        nawigacje.add(21,new Nawigacja(new Licznik(Media.ENERGIA,     Lokal.M1 ), 22, 28 ));
        nawigacje.add(22,new Nawigacja(new Licznik(Media.ENERGIA,     Lokal.M2 ), 23, 21 ));
        nawigacje.add(23,new Nawigacja(new Licznik(Media.ENERGIA,     Lokal.M3 ), 24, 22 ));
        nawigacje.add(24,new Nawigacja(new Licznik(Media.ENERGIA,     Lokal.M4 ), 25, 23 ));
        nawigacje.add(25,new Nawigacja(new Licznik(Media.ENERGIA,     Lokal.M5 ), 26, 24 ));
        nawigacje.add(26,new Nawigacja(new Licznik(Media.ENERGIA,     Lokal.M6 ), 27, 25 ));
        nawigacje.add(27,new Nawigacja(new Licznik(Media.ENERGIA,     Lokal.G1 ), 28, 26 ));
        nawigacje.add(28,new Nawigacja(new Licznik(Media.ENERGIA,     Lokal.G2 ), 21, 27 ));

        nawigacje.add(29,new Nawigacja(new Licznik(Media.ENERGIA,     Lokal.DOM), 30, 31 ));
        nawigacje.add(30,new Nawigacja(new Licznik(Media.GAZ,         Lokal.DOM), 31, 29 ));
        nawigacje.add(31,new Nawigacja(new Licznik(Media.WODA_ZIMNA,  Lokal.DOM), 29, 30 ));

        /* przygotowanie bazy danych */
        SQLiteOpenHelper dbHelper = new SQLiteOpenHelper( getApplicationContext(),DB_NAME,null,DB_VER) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                Log.d(TAG, "onCreate: tworze baze danych");
                db.execSQL("create table spisy ( nazwa text primary key)");
                db.execSQL("create table odczyty ( spis text not null, lokal text not null, media text not null, wskazanie text not null)");
                bSpis = new Spis("2019-01-01");
                inicjujOdczyty( 0 );
                bSpis.insertSpis( db );
                pSpis = null;
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("drop table spisy");
                db.execSQL("drop table odczyty");
                onCreate( db );
            }
        };
        db = dbHelper.getWritableDatabase();

        /* generowanie listy */
        dbLista = new ArrayList<String>();
        Cursor cursor = db.query("spisy",new String[] {"nazwa"},null,null,null,null,"nazwa");
        while( cursor.moveToNext() ) {
            dbLista.add( 0, cursor.getString(0) );
        }


//        String poprzedni = "2018-12-20";
//        int x = 1;
//        for (int i = 1; i <= 10; i++) {
//            String naDzien = String.format("2019-%02d-20", i);
//            Spis spis = new Spis(naDzien,poprzedni);
//            inicjujOdczyty( spis, x++ );
//            dbLista.add( 0, spis );
//            poprzedni = naDzien;
//        }
    }

    @Override
    public void onTerminate() {
        db.close();
        super.onTerminate();
    }

    public static SpisManager get() {
        return instance;
    }

    private static void inicjujOdczyty(int wartosc) {
        for( Nawigacja nawi : nawigacje ) {
            bSpis.setWskazanie(nawi.licznik,new BigDecimal( wartosc ));
        }
    }

    /* dodanie nowego Spisu i ustawienie go jako bieżący */
    public static String utworzSpis(String nazwa) {
        Log.d(TAG, "utworzSpis: "+nazwa);
        /* nie moze być innego spisu z tą datą */
        if( istniejeSpis(nazwa) )
            return "Spis " + nazwa + " już istnieje!";
        /* znajdz spis poprzedzajacy */
        String poprzedni = znajdzPoprzedni(nazwa);
        int pozPop = podajPozycje(poprzedni);
        bSpis = new Spis( nazwa );
        inicjujOdczyty(0);
        bSpis.insertSpis(db);
        pSpis = new Spis( db, poprzedni );
        dbLista.add(pozPop,nazwa);
        return "";
    }

    /* daje nazwę Spisu na potrzeby listy wyboru */
    public static String podajNazweSpisu( int poz ) {
        return dbLista.get(poz);
    }

    /* daje liczbę Spisów na potrzeby listy wyboru */
    public static int podajLiczbe() {
        return dbLista.size()-1;
    }

    /* czy istnieje spis na dzien */
    private static boolean istniejeSpis( String nazwa ) {
        return podajPozycje( nazwa ) != -1 ;
    }

    /* podaj pozycje spisu o nazwie */
    public static int podajPozycje( String nazwa ) {
        for( int i=0; i<dbLista.size(); i++ ) {
            if(dbLista.get(i).equals(nazwa))
                return i;
        }
        return -1;
    }

    /* znajduje ostatni spis przed datą, zwraca pozycje w dbLista */
    private static String znajdzPoprzedni( String nazwa ) {
        String dPoprzedni = "2000-01-01";
        for (int i =0; i<dbLista.size(); i++) {
            String dSpisu = dbLista.get(i);
            if(dSpisu.compareTo(dPoprzedni)>0 && dSpisu.compareTo(nazwa)<0 ) {
                dPoprzedni = dSpisu;
            }
        }
        return dPoprzedni;
    }

    /* podaje cały spis z pozycji */
    public static Spis podajSpis() { return bSpis; }
    public static Spis podajSpisPoprzedni() { return pSpis; }

    /* podaje cały spis z daty */
    //public static Spis podajSpis(String nazwa) { return podajSpis(podajPozycje(nazwa)); }

    /* przesuwa kursor na nastepny licznik */
    public static void nastepnyLicznik() { kursor = nawigacje.get(kursor).nastepny; }

    /* cofa kursor na poprzedni licznik */
    public static void poprzedniLicznik() { kursor = nawigacje.get(kursor).poprzedni; }

    /* ustawia kursor na podany licznik, zob stałe POM_... */
    public static void ustawLicznik( int poz ) { kursor = poz; }

    public static Licznik podajLicznik() { return nawigacje.get(kursor).licznik; }

    /* pobiera z bzy dane spisu biezącego i poprzedniego */
    public static void ustawSpis(int poz) {
        bSpis = new Spis( db, dbLista.get(poz) );
        pSpis = new Spis( db, dbLista.get(poz+1) );
    }

    /* zapisuje zmiane wskazania licznika */
    public static void zapiszWskazanie(Licznik licznik, BigDecimal bigDecimal) {
        bSpis.updateOdczyt( db, licznik, bigDecimal );
    }

    /* tworzy Spis z JSON'a */

    public static String zrobSpis(String jsonTxt) {
        String komunikat = "";
        try {
            JSONObject json = new JSONObject(jsonTxt);
            String nazwa = json.getString("nazwa");
            int poz = podajPozycje(nazwa);
            if(poz < 0) {
                bSpis = new Spis( nazwa );
                inicjujOdczyty(0);
                bSpis.insertSpis( db );
                String poprzedni = znajdzPoprzedni(nazwa);
                pSpis = new Spis( db, poprzedni );
                int pozPop = podajPozycje( poprzedni );
                dbLista.add(pozPop,nazwa);
                komunikat = "Spis "+nazwa+" utworzony, poz: "+pozPop;
            } else {
                ustawSpis(poz);
                komunikat = "Spis "+nazwa+" zaktualizowany, poz: "+poz;
            }
            bSpis.updateOdczyty( db, json );
            return komunikat;
        } catch (Exception e) {
            Log.e(TAG, "zrobSpis: "+ e.getMessage());
            return "BŁĄD: "+e.getMessage();
        }
    }
    /* pobieranie pliku json i wywowanie zrobSpis */

    public static void pobierzPlik(final Oczekujacy oczekujacy) {
        final Koniec koniec = new Koniec(oczekujacy);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
            HttpURLConnection urlConnection = null;
            try  {
                URL url = new URL(SPIS_URL+SPIS_PLIK);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Scanner s = new Scanner(in);
                String txt = "";
                while (s.hasNextLine()) {
                    txt += s.nextLine();
                    //Log.d(TAG, "pobierzPlik: "+s.nextLine() );
                }
                String wynik = zrobSpis(txt);
                koniec.zglosKoniec(wynik);
            } catch (Exception e) {
                Log.e(TAG, "pobierzPlik: ", e);
                koniec.zglosKoniec( "pobierzPlik: "+e.getMessage());
            } finally {
                if( urlConnection != null) urlConnection.disconnect();
            }
            }
        });
        thread.start();
    }

    /* wysyła spis w formie JSON */
    public static void wyslijSpis(Oczekujacy oczekujacy) {
        final Koniec koniec = new Koniec(oczekujacy);
//        Log.d(TAG, "wyslijSpis: " + bSpis.toJSON().toString());
        Log.d(TAG, "wyslijSpis: " + SPIS_URL+SPIS_PLIK);
//        koniec.zglosKoniec("Spis wysłany na konsole");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try  {
                    URL url = new URL(SPIS_URL+SPIS_PLIK);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setRequestMethod("POST");

                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    writer.write(bSpis.toJSON().toString());
                    writer.flush();
                    writer.close();
                    out.close();

                    int responseCode=urlConnection.getResponseCode();
                    if(responseCode==HttpURLConnection.HTTP_OK)
                        koniec.zglosKoniec("Spis wysłany");
                    else
                        koniec.zglosKoniec("Spis nie wysłany, "+responseCode);

                } catch (Exception e) {
                    Log.e(TAG, "wyslijSpis: ", e);
                    koniec.zglosKoniec( "wyslijSpis: "+e.getMessage());
                } finally {
                    if( urlConnection != null) urlConnection.disconnect();
                }
            }
        });
        thread.start();
    }

    public static void test(Oczekujacy oczekujacy) {
        Log.d(TAG, "test: start");
        final Koniec koniec = new Koniec(oczekujacy);
        koniec.zglosKoniec("Test");
        Log.d(TAG, "test: koniec");
    }
}
