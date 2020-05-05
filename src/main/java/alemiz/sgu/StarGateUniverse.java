package alemiz.sgu;

import alemiz.sgu.packets.*;
import alemiz.sgu.tasks.ReconnectTask;
import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import alemiz.sgu.client.Client;
import alemiz.sgu.events.CustomPacketEvent;
import alemiz.sgu.untils.Convertor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarGateUniverse extends PluginBase {

    public Config cfg;
    private static StarGateUniverse instance;

    protected Map<String, Client> clients = new HashMap<>();
    private List<Runnable> shutdownHandlers = new ArrayList<>();

    protected static Map<Integer, StarGatePacket> packets = new HashMap<>();
    public Map<String, String> responses = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.cfg = getConfig();
        this.initPackets();

        for (String clientName : this.cfg.getSection("connections").getKeys(false)){
            this.start(clientName);
        }

        this.getServer().getScheduler().scheduleDelayedRepeatingTask(new ReconnectTask(), 20*30, 20*60*5);
        this.getLogger().info("§aEnabling StarGate Universe: Client");

        /*this.getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                new DockerContainerCreate().startNewLobby("lobby4", "cubemc-lobby");
            }
        }, 20*10);*/
    }

    @Override
    public void onDisable() {
        for (Runnable handler : this.shutdownHandlers){
            handler.run();
        }

        Map<String, Client> clients = new HashMap<>(this.clients);
        clients.forEach((String name, Client client)->{
            client.close(ConnectionInfoPacket.CLIENT_SHUTDOWN, true);
        });
    }

    public static StarGateUniverse getInstance() {
        return instance;
    }

    private void start(String name){
        if (name == null || this.cfg.getSection(name) == null) return;

        Client client = new Client();
        client.name = this.cfg.getString("connections."+name+".name");
        client.configName = name;
        client.address = this.cfg.getString("connections."+name+".address");
        client.port = Integer.parseInt(this.cfg.getString("connections."+name+".port"));
        client.password = this.cfg.getString("connections."+name+".password");

        this.clients.put(name, client);
        client.start();
    }

    /*Reload whole client if needed
    * Make sure that old sockets are closed properly*/
    public void restart(String name){
        this.getLogger().info("§eReloading StarGate Client "+name);

        Client client = this.clients.remove(name);
        if (client != null) client.shutdown();

        this.start(name);
    }

    public boolean removeClient(String clientName){
        return this.clients.remove(clientName) != null;
    }

    /**
     * Register simple Runnable task which will be run before connection closes
     */
    public void registerShutdownHandler(Runnable task){
        if (task == null) return;
        this.shutdownHandlers.add(task);
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
        RegisterPacket(new ServerManagePacket());
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

        try {
            packet.decode();
        }catch (Exception e){
            this.getLogger().warning("§eUnable to decode packet with ID "+packet.getID());
            this.getLogger().warning("§c"+e.getMessage());
            return packet;
        }

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

    public Map<String, Client> getClients() {
        return clients;
    }

    public Map<String, String> getResponses() {
        return responses;
    }

    /* Beginning of API section*/

    /* This allows you to send packet
    * Returns packets UUID*/
    public String putPacket(StarGatePacket packet){
        return this.putPacket(packet, "default");
    }

    public String putPacket(StarGatePacket packet, String clientName){
        if (clientName == null || !this.clients.containsKey(clientName)) return null;
        return this.clients.get(clientName).gatePacket(packet);
    }

    /* Really simple method for registring Packet*/
    public static void RegisterPacket(StarGatePacket packet){
        packets.put(packet.getID(), packet);
    }

    /* Transfering player to other server*/
    public void transferPlayer(Player player, String server){
        this.transferPlayer(player.getName(), server, "default");
    }

    public void transferPlayer(Player player, String server, String client){
        this.transferPlayer(player.getName(), server, client);
    }

    public void transferPlayer(String player, String server){
        this.transferPlayer(player, server, "default");
    }

    public void transferPlayer(String player, String server, String client){
        if (player == null) return;
        PlayerTransferPacket packet = new PlayerTransferPacket();
        packet.player = player;
        packet.destination = server;

        packet.isEncoded = false; //This is no longer needed here
        this.putPacket(packet, client);
    }

    /* Kick player from any server connected to StarGate network*/
    public void kickPlayer(Player player, String reason){
        this.kickPlayer(player.getName(), reason, "default");
    }

    public void kickPlayer(Player player, String reason, String client){
        this.kickPlayer(player.getName(), reason, client);
    }

    public void kickPlayer(String player, String reason){
        this.kickPlayer(player, reason, "default");
    }

    public void kickPlayer(String player, String reason, String client){
        if (player == null) return;

        KickPacket packet = new KickPacket();
        packet.player = player;
        packet.reason = reason;
        this.putPacket(packet, client);
    }
    /* We can check if player is online somewhere in network
    * After sending packet we must handle response by UUID
    * Example can be found in /tests/OnlineExample.java
    * Specifying client allows you to check on what Waterdog server player is connected*/
    public String isOnline(Player player){
        return this.isOnline(player, "default");
    }

    public String isOnline(Player player, String client){
        if (player == null) return null;

        PlayerOnlinePacket packet = new PlayerOnlinePacket();
        packet.player = player;
        return this.putPacket(packet, client);
    }

    public String isOnline(String player){
        return this.isOnline(player, "default");
    }

    public String isOnline(String player, String client){
        if (player == null) return null;

        PlayerOnlinePacket packet = new PlayerOnlinePacket();
        packet.customPlayer = player;
        return this.putPacket(packet, client);
    }

    /* Using ForwardPacket you can forward packet to other client*/
    public void forwardPacket(String destClient, String proxyServer, StarGatePacket packet){
        ForwardPacket forwardPacket = new ForwardPacket();
        forwardPacket.client = destClient;

        if (!packet.isEncoded){
            packet.encode();
        }

        forwardPacket.encodedPacket = packet.encoded;
        this.putPacket(forwardPacket, proxyServer);
    }

    /** Proxy will send response with status: "STATUS_FAILED" or "STATUS_SUCCESS,server_name"
     * Dont forget. Response can be handled using ResponseCheckTask
     * Returned variable is UUID or packets used to handle response NOT response*/
    public String addServer(String address, String port, String name){
        return this.addServer(address, port, name, "default");
    }

    public String addServer(String address, String port, String name, String client){
        ServerManagePacket packet = new ServerManagePacket();
        packet.packetType = ServerManagePacket.SERVER_ADD;
        packet.serverAddress = address;
        packet.serverPort = port;
        packet.serverName = name;

        return this.putPacket(packet, client);
    }

    /* Response: "STATUS_SUCCESS" or "STATUS_NOT_FOUND" */
    public String removeServer(String name){
        return this.removeServer(name, "default");
    }

    public String removeServer(String name, String client){
        ServerManagePacket packet = new ServerManagePacket();
        packet.packetType = ServerManagePacket.SERVER_ADD;
        packet.serverName = name;

        return this.putPacket(packet, client);
    }
}
