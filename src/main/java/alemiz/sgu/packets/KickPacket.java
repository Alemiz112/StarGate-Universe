package alemiz.sgu.packets;


import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.untils.Convertor;
import cn.nukkit.Player;

public class KickPacket extends StarGatePacket {

    public String reason;
    public Player player;

    public KickPacket(){
        super("KICK_PACKET", Packets.KICK_PACKET);
    }

    @Override
    public void decode() {
        isEncoded = false;

        String[] data = Convertor.getPacketStringData(encoded);
        player = StarGateUniverse.getInstance().getServer().getPlayer(data[1]);
        reason = data[2];
    }

    @Override
    public void encode() {
        Convertor convertor = new Convertor(getID());

        convertor.putString(player.getName());
        convertor.putString(reason);

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

    public String getReason() {
        return reason;
    }
}
