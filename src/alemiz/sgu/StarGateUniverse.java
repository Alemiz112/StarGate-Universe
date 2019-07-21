package alemiz.sgu;

import alemiz.sgu.packets.*;
import tests.OnlineCommand;
import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import alemiz.sgu.client.Client;
import alemiz.sgu.events.CustomPacketEvent;
import alemiz.sgu.untils.Convertor;

import java.util.HashMap;
import java.util.Map;

public class StarGateUniverse extends PluginBase {

    public Config cfg;
    private Client client;

    private static StarGateUniverse instance;

    protected static Map<Integer, StarGatePacket> packets = new HashMap<>();

    public Map<String, String> responses = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.cfg = getConfig();

        /* Starting Client for StarGate*/
        client = new Client();
        client.start();

        initPackets();

        getLogger().info("§aEnabling StarGate Universe: Client");
    }

    public static StarGateUniverse getInstance() {
        return instance;
    }

    public Map<String, String> getResponses() {
        return responses;
    }

    /**
     * Here we are registring new Packets, may be useful for DEV
     * Every packet Extends @class StarGatePacket*/
    private void initPackets(){
        RegisterPacket(new WelcomePacket());
        RegisterPacket(new PingPacket());
        RegisterPacket(new PlayerTransferPacket());
        RegisterPacket(new KickPacket());
    }

    /* Using these function we can process packet from string to data
     *  After packet is successfully created we can handle that Packet*/
    public boolean processPacket(String packetString){
        String[] data = Convertor.getPacketStringData(packetString);
        int PacketId = Integer.decode(data[0]);

        if (!packets.containsKey(PacketId) || packets.get(PacketId) == null) return false;

        /* Here we decode Packet. Create from String Data*/
        StarGatePacket packet = packets.get(PacketId);
        String uuid = data[data.length - 1];

        packet.uuid = uuid;

        packet.encoded = packetString;
        packet.decode();

        handlePacket(packet);
        return true;
    }

    private void handlePacket(StarGatePacket packet){
        int type = packet.getID();

        switch (type){
            default:
                /** Here we call Event that will send packet to DEVs plugin*/
                getServer().getPluginManager().
                        callEvent(new CustomPacketEvent(packet));
                break;
        }
    }

    /* Beginning of API section*/

    /* This allows you to send packet
    * Returns packets UUID*/
    public String putPacket(StarGatePacket packet){
        return client.gatePacket(packet);
    }

    /* Really simple method for registring Packet*/
    public static void RegisterPacket(StarGatePacket packet){
        packets.put(packet.getID(), packet);
    }

    /* Transfering player to other server*/
    public void transferPlayer(Player player, String server){
        if (player == null) return;
        PlayerTransferPacket packet = new PlayerTransferPacket();

        packet.player = player;
        packet.destination = server;

        packet.isEncoded = false;
        putPacket(packet);
    }

    /* Kick player from any server connected to StarGate network*/
    public void kickPlayer(Player player, String reason){
        if (player == null) return;
        KickPacket packet = new KickPacket();

        packet.player = player;
        packet.reason = reason;

        packet.isEncoded = false;
        putPacket(packet);
    }
    /* We can check if player is online somewhere in network
    * After sending packet we must handle response by UUID
    * Example can be found in /tests/OnlineExample.java*/
    public String isOnline(Player player){
        if (player == null) return null;
        PlayerOnlinePacket packet = new PlayerOnlinePacket();

        packet.player = player;
        packet.isEncoded = false;
        return putPacket(packet);
    }

}
