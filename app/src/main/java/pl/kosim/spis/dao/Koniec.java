package pl.kosim.spis.dao;

import android.os.Handler;

import pl.kosim.spis.interfejsy.Oczekujacy;

public class Koniec {

    private Handler    handler;
    private Oczekujacy oczekujacy;

    public Koniec(Oczekujacy oczekujacy) {
        handler = new Handler();
        this.oczekujacy = oczekujacy;
    }

    public void zglosKoniec( final String wynik ) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                oczekujacy.koniecPracy( wynik );
            }
        });

    }
}
