package alemiz.sgu.events;

import cn.nukkit.event.Event;
import alemiz.sgu.packets.StarGatePacket;
import cn.nukkit.event.HandlerList;

public class CustomPacketEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private StarGatePacket packet;

    public CustomPacketEvent(StarGatePacket packet){
        this.packet = packet;
    }

    /* Returns unofficial packet handled by server*/
    public StarGatePacket getPacket() {
        return packet;
    }

    public static HandlerList getHandlers() {
        return CustomPacketEvent.handlers;
    }
}
