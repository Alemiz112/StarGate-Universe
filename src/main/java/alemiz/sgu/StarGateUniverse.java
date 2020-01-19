package alemiz.sgu;

import alemiz.sgu.packets.*;
import alemiz.sgu.tasks.ReconnectTask;
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

        initPackets();

        /* Starting Client for StarGate*/
        client = new Client();
        client.start();

        getServer().getScheduler().scheduleDelayedRepeatingTask(new ReconnectTask(), 20*30, 20*60*5);

        getLogger().info("§aEnabling StarGate Universe: Client");

        /*getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                for (int x = 0; x < 1000; x++){
                    isOnline("bob");
                }
            }
        }, 20*5);*/
    }

    @Override
    public void onDisable() {
        client.close(ConnectionInfoPacket.CLIENT_SHUTDOWN);
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
        RegisterPacket(new ForwardPacket());
        RegisterPacket(new ConnectionInfoPacket());
    }

    /* Using these function we can process packet from string to data
     *  After packet is successfully created we can handle that Packet*/
    public StarGatePacket processPacket(String packetString) throws IllegalAccessException, InstantiationException{
        String[] data = Convertor.getPacketStringData(packetString);
        int PacketId = Integer.decode(data[0]);

        if (!packets.containsKey(PacketId) || packets.get(PacketId) == null) return null;

        /* Here we decode Packet. Create from String Data*/
        StarGatePacket packet = packets.get(PacketId).getClass().newInstance();
        String uuid = data[data.length - 1];

        packet.uuid = uuid;

        packet.encoded = packetString;
        packet.decode();

        if (!(packet instanceof ConnectionInfoPacket)){
            handlePacket(packet);
        }
        return packet;
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

    public Client getClient() {
        return client;
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

    public String isOnline(String player){
        if (player == null) return null;

        PlayerOnlinePacket packet = new PlayerOnlinePacket();
        packet.customPlayer = player;

        packet.isEncoded = false;
        return  putPacket(packet);
    }

    /* Using ForwardPacket you can forward packet to other client*/
    public void forwardPacket(String client, StarGatePacket packet){
        ForwardPacket forwardPacket = new ForwardPacket();
        forwardPacket.client = client;

        if (!packet.isEncoded){
            packet.encode();
        }

        forwardPacket.encodedPacket = packet.encoded;
        forwardPacket.isEncoded = false;
        putPacket(forwardPacket);
    }

}
