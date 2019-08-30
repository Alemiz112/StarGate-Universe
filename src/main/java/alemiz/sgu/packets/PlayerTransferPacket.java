package alemiz.sgu.packets;


import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.untils.Convertor;
import cn.nukkit.Player;
import cn.nukkit.Server;

public class PlayerTransferPacket extends StarGatePacket {

    public Player player;
    public String destination;

    public PlayerTransferPacket(){
        super("PLAYER_TRANSFORM_PACKET", Packets.PLAYER_TRANSFORM_PACKET);
    }

    @Override
    public void decode() {
        isEncoded = false;

        String[] data = Convertor.getPacketStringData(encoded);
        player = StarGateUniverse.getInstance().getServer().getPlayer(data[1]);
        destination = data[2];
    }

    @Override
    public void encode() {
        Convertor convertor = new Convertor(getID());

        convertor.putString(player.getName());
        convertor.putString(destination);

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

    public String getDestination() {
        return destination;
    }
}
