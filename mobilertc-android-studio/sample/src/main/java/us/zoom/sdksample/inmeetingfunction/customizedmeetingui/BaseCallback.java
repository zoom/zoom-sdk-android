package us.zoom.sdksample.inmeetingfunction.customizedmeetingui;

import java.util.ArrayList;
import java.util.List;


public class BaseCallback<T extends BaseEvent>  {

    protected List<T> callbacks = new ArrayList<>();

    protected void init() {

    }

    public void addListener(T event) {
        if (!callbacks.contains(event)) {
            callbacks.add(event);
        }
    }

    public void removeListener(T event) {
        callbacks.remove(event);
    }

}
