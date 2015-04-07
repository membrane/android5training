package de.predic8.meinerecyclerviewdemo;

import java.util.ArrayList;
import java.util.List;

public class Note {
    public final String text;
    public final int grafik;

    public Note(String text, int grafik) {
        this.text = text;
        this.grafik = grafik;
    }


    private static List<Note> noten;

    public synchronized static List<Note> getNoten() {
        if (noten == null) {
            noten = new ArrayList<Note>();
            noten.add(new Note("do", R.drawable.do1));
            noten.add(new Note("re", R.drawable.re));
            noten.add(new Note("mi", R.drawable.mi));
            noten.add(new Note("fa", R.drawable.fa));
            noten.add(new Note("sol", R.drawable.sol));
            noten.add(new Note("la", R.drawable.la));
            noten.add(new Note("ti", R.drawable.ti));
            noten.add(new Note("do", R.drawable.do2));
        }
        return noten;
    }

}
