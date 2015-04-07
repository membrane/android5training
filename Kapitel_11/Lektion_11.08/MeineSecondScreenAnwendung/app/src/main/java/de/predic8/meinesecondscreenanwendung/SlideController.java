package de.predic8.meinesecondscreenanwendung;

import android.transition.Slide;

import java.util.ArrayList;
import java.util.List;

public class SlideController {

    // SINGLETON PATTERN

    private static SlideController instance;

    public static synchronized SlideController getInstance() {
        if (instance == null)
            instance = new SlideController();
        return instance;
    }

    private SlideController() {}



    public interface SlideChangeListener {
        public void onSlideChange(int newSlide);
    }

    public static final int SLIDE_COUNT = 3;

    List<SlideChangeListener> listeners = new ArrayList<>();
    int slide = 0;


    public void register(SlideChangeListener listener) {
        listeners.add(listener);
    }

    public void unregister(SlideChangeListener listener) {
        listeners.remove(listener);
    }


    public void goToNext() {
        slide++;
        if (slide >= SLIDE_COUNT)
            slide = SLIDE_COUNT - 1;

        for (SlideChangeListener listener : listeners)
            listener.onSlideChange(slide);
    }

    public void goToPrevious() {
        slide--;
        if (slide < 0)
            slide = 0;

        for (SlideChangeListener listener : listeners)
            listener.onSlideChange(slide);
    }

    public int getCurrentSlide() {
        return slide;
    }
}
