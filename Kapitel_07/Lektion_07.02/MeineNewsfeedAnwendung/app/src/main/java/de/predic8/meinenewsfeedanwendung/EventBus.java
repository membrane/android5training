package de.predic8.meinenewsfeedanwendung;

import java.util.ArrayList;
import java.util.List;

public class EventBus {

    public interface IEventListener {
        public void onFeedUpdate();
    }


    private static List<IEventListener> listeners = new ArrayList<>();

    public static void register(IEventListener listener) {
        listeners.add(listener);
    }

    public static void unregister(IEventListener listener) {
        listeners.remove(listener);
    }


    public static void fireFeedUpdate() {
        for (IEventListener listener : listeners)
            listener.onFeedUpdate();
    }
}
