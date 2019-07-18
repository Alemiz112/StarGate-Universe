package alemiz.sgu.events;

import cn.nukkit.event.Event;
import alemiz.sgu.packets.StarGatePacket;

public class CustomPacketEvent extends Event {

    private StarGatePacket packet;

    public CustomPacketEvent(StarGatePacket packet){
        this.packet = packet;
    }

    /* Returns unofficial packet handled by server*/
    public StarGatePacket getPacket() {
        return packet;
    }
}
