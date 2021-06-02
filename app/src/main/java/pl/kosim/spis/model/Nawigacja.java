package pl.kosim.spis.model;

public class Nawigacja {
    public Licznik licznik;
    public int nastepny;
    public int poprzedni;

    public Nawigacja(Licznik licznik, int nastepny, int poprzedni) {
        this.licznik = licznik;
        this.nastepny = nastepny;
        this.poprzedni = poprzedni;
    }
}
