package alemiz.sgu.events;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class StarGateClientConnectEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String client;

    public StarGateClientConnectEvent(String client){
        this.client = client;
    }

    public String getClient() {
        return this.client;
    }

    public static HandlerList getHandlers() {
        return StarGateClientConnectEvent.handlers;
    }
}
