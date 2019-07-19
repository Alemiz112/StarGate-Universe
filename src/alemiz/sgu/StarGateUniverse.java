package alemiz.sgu;

import alemiz.sgu.packets.*;
import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import alemiz.sgu.client.Client;
import alemiz.sgu.events.CustomPacketEvent;
import alemiz.sgu.untils.Convertor;
import tests.KickExample;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class StarGateUniverse extends PluginBase {

    public Config cfg;
    private Client client;

    /* Only for Developers for testing*/
    private boolean debug = false;

    private static StarGateUniverse instance;

    protected static Map<Integer, StarGatePacket> packets = new HashMap<>();

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

        debug();
    }

    public static StarGateUniverse getInstance() {
        return instance;
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

    /* This allows you to send packet*/
    public void putPacket(StarGatePacket packet){
        client.gatePacket(packet);
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

    /* This function is only for Developing this plugin*/
    public void debug(){
        if (!debug) return;

        getServer().getCommandMap().register("ekick", new KickExample());
    }
}
