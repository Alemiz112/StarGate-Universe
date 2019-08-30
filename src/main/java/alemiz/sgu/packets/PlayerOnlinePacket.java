package alemiz.sgu.packets;

import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.untils.Convertor;
import cn.nukkit.Player;

public class PlayerOnlinePacket extends StarGatePacket {

    public Player player;

    public PlayerOnlinePacket(){
        super("PLAYER_ONLINE_PACKET", Packets.PLAYER_ONLINE_PACKET);
    }

    @Override
    public void decode() {
        isEncoded = false;

        String[] data = Convertor.getPacketStringData(encoded);
        player = StarGateUniverse.getInstance().getServer().getPlayer(data[1]);
    }

    @Override
    public void encode() {
        Convertor convertor = new Convertor(getID());
        convertor.putString(player.getName());

        this.encoded = convertor.getPacketString();
        isEncoded = true;
    }

    @Override
    public StarGatePacket copy() throws CloneNotSupportedException {
        return null;
    }

    public Player getPlayer() {
        return player;
    }
}
