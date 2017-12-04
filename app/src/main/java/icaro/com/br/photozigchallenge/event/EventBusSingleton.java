package icaro.com.br.photozigchallenge.event;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by icaro on 02/12/2017.
 */

public class EventBusSingleton {

    private static EventBusSingleton instance = null;
    private EventBus bus;

    private EventBusSingleton(){
//        bus = EventBus.builder().eventInheritance(false).installDefaultEventBus();
        bus = EventBus.getDefault();
    }

    public static EventBusSingleton getInstance() {
        if(instance == null) {
            instance = new EventBusSingleton();
        }
        return instance;
    }

    public EventBus getBus(){
        return bus;
    }
}
