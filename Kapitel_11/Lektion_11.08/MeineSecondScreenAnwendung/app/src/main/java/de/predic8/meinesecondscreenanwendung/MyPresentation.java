package de.predic8.meinesecondscreenanwendung;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;

public class MyPresentation extends Presentation implements SlideController.SlideChangeListener {

    public MyPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onSlideChange(SlideController.getInstance().getCurrentSlide());
    }

    @Override
    protected void onStart() {
        super.onStart();
        SlideController.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        SlideController.getInstance().unregister(this);
        super.onStop();
    }


    @Override
    public void onSlideChange(int newSlide) {
        switch (newSlide) {
            case 0:
                setContentView(R.layout.presentation1);
                break;
            case 1:
                setContentView(R.layout.presentation2);
                break;
            case 2:
                setContentView(R.layout.presentation3);
                break;
        }

    }
}
