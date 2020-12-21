/*
 * Copyright 2020 Alemiz
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package alemiz.sgu.nukkit;

import alemiz.sgu.nukkit.events.ClientCreationEvent;
import alemiz.sgu.nukkit.utils.NukkitLogger;
import alemiz.sgu.nukkit.utils.ReconnectTask;
import alemiz.stargate.client.StarGateClient;
import alemiz.stargate.codec.StarGatePackets;
import alemiz.stargate.protocol.ServerInfoRequestPacket;
import alemiz.stargate.protocol.ServerInfoResponsePacket;
import alemiz.stargate.protocol.ServerTransferPacket;
import alemiz.stargate.protocol.StarGatePacket;
import alemiz.stargate.protocol.types.HandshakeData;
import alemiz.stargate.utils.ServerLoader;
import alemiz.stargate.utils.StarGateLogger;
import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class StarGateUniverse extends PluginBase implements ServerLoader {

    private static StarGateUniverse instance;
    private NukkitLogger logger;

    private final Map<String, StarGateClient> clients = new ConcurrentHashMap<>();
    private String defaultClient;

    private int logLevel;
    private boolean autoStart;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.logger = new NukkitLogger(this);
        this.logger.setDebug(this.getConfig().getBoolean("debug"));
        this.defaultClient = this.getConfig().getString("defaultClient");
        this.logLevel = this.getConfig().getInt("logLevel");
        this.autoStart = this.getConfig().getBoolean("autoStart");

        for (String clientName : this.getConfig().getSection("connections").getKeys(false)){
            this.createClient(clientName);
        }
        this.getServer().getScheduler().scheduleDelayedRepeatingTask(new ReconnectTask(), 20*60, 20*60, false);
    }

    @Override
    public void onDisable() {
        for (StarGateClient client : this.clients.values()){
            client.shutdown();
        }
    }

    private void createClient(String clientName){
        if (!this.getConfig().exists("connections."+clientName)){
            this.getLogger().warning("§cCan not load client "+clientName+"! Wrong config!");
            return;
        }

        String password = this.getConfig().getString("connections."+clientName+".password");
        HandshakeData handshakeData = new HandshakeData(clientName, password, HandshakeData.SOFTWARE.NUKKIT);

        String addressString = this.getConfig().getString("connections."+clientName+".address");
        int port = this.getConfig().getInt("connections."+clientName+".port");
        InetSocketAddress address = new InetSocketAddress(addressString, port);
        StarGateClient client = new StarGateClient(address, handshakeData, this);

        this.onClientCreation(clientName, client);
    }

    public void onClientCreation(String clientName, StarGateClient client){
        if (this.clients.containsKey(clientName)){
            return;
        }

        client.setClientListener(new StarGateClientListener(this));
        client.getProtocolCodec().registerPacket(StarGatePackets.SERVER_INFO_REQUEST_PACKET, ServerInfoRequestPacket.class);
        client.getProtocolCodec().registerPacket(StarGatePackets.SERVER_INFO_RESPONSE_PACKET, ServerInfoResponsePacket.class);

        ClientCreationEvent event = new ClientCreationEvent(client, this);
        this.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()){
            return;
        }

        if (this.autoStart){
            client.start();
        }
        this.clients.put(clientName, client);
    }

	public boolean isAutoStart() {
		return autoStart;
	}
	
    public static StarGateUniverse getInstance() {
        return instance;
    }

    @Override
    public StarGateLogger getStarGateLogger() {
        return this.logger;
    }

    public StarGateClient getClient(String clientName) {
        return this.clients.get(clientName);
    }

    public StarGateClient getDefaultClient() {
        return this.getClient(this.defaultClient);
    }

    public List<StarGateClient> getClientsCopy(){
        return new ArrayList<>(this.clients.values());
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public void transferPlayer(Player player, String targetServer) {
        this.transferPlayer(player, targetServer, null);
    }

    /**
     * Transfer player to another server.
     * @param player instance to be transferred.
     * @param targetServer server where player will be sent.
     * @param clientName client name that will be used.
     */
    public void transferPlayer(Player player, String targetServer, String clientName) {
        StarGateClient client = this.getClient(clientName == null? this.defaultClient : clientName);
        if (client == null){
            return;
        }
        ServerTransferPacket packet = new ServerTransferPacket();
        packet.setPlayerName(player.getName());
        packet.setTargetServer(targetServer);
        client.sendPacket(packet);
    }

    /**
     * Get info about another server or master server.
     * @param serverName name of server that info will be send. In selfMode it can be custom.
     * @param selfMode if send info of master server, StarGate server.
     * @param clientName client name that will be used.
     * @return future that can be used to get response data.
     */
    public CompletableFuture<StarGatePacket> serverInfo(String serverName, boolean selfMode, String clientName) {
        StarGateClient client = this.getClient(clientName == null? this.defaultClient : clientName);
        if (client == null){
            return null;
        }
        ServerInfoRequestPacket packet = new ServerInfoRequestPacket();
        packet.setServerName(serverName);
        packet.setSelfInfo(selfMode);
        return client.responsePacket(packet);
    }
}
