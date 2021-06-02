package pl.kosim.spis.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.Objects;

public class Licznik {

    private Media media;
    private Lokal lokal;

    public Licznik(Media media, Lokal lokal) {
        this.media = media;
        this.lokal = lokal;
    }

    public Media getMedia() {
        return media;
    }

    public Lokal getLokal() {
        return lokal;
    }

    public boolean is( Media media ) { return this.media.equals(media); };
    public boolean is( Lokal lokal ) { return this.lokal.equals(lokal); };
    public boolean is(Licznik obj) { return is(obj.media) && is(obj.lokal); }

    @Override
    public boolean equals(@Nullable Object obj) {
        return is((Licznik) obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(media,lokal);
    }

    @NonNull
    @Override
    public String toString() {
        return media+"."+lokal;
    }

}
