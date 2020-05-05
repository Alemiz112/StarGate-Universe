package alemiz.sgu.events;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class StarGateClientConnectEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String clientName;
    private String configName;

    public StarGateClientConnectEvent(String clientName, String configName){
        this.clientName = clientName;
        this.configName = configName;
    }

    public String getClientName() {
        return this.clientName;
    }

    public String getConfigName() {
        return configName;
    }

    public static HandlerList getHandlers() {
        return StarGateClientConnectEvent.handlers;
    }
}
