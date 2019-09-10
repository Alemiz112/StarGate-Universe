package alemiz.sgu.packets;

import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.untils.Convertor;
import cn.nukkit.Player;

import java.util.Optional;

public class PlayerOnlinePacket extends StarGatePacket {

    public Player player = null;
    public String customPlayer = null;

    public PlayerOnlinePacket(){
        super("PLAYER_ONLINE_PACKET", Packets.PLAYER_ONLINE_PACKET);
    }

    @Override
    public void decode() {
        isEncoded = false;

        String[] data = Convertor.getPacketStringData(encoded);
        Player player = StarGateUniverse.getInstance().getServer().getPlayer(data[1]);

        if (player == null){
            customPlayer = data[1];
        }else this.player = player;

    }

    @Override
    public void encode() {
        Convertor convertor = new Convertor(getID());
        if (player != null){
            convertor.putString(player.getName());
        }else convertor.putString(customPlayer);


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

    public String getCustomPlayer() {
        return customPlayer;
    }
}
