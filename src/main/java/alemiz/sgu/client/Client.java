package alemiz.sgu.client;

import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.packets.ConnectionInfoPacket;
import alemiz.sgu.packets.PingPacket;
import alemiz.sgu.packets.StarGatePacket;
import alemiz.sgu.packets.WelcomePacket;
import alemiz.sgu.tasks.ResponseRemoveTask;
import alemiz.sgu.untils.ArrayUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class Client extends Thread {

    public String address = "127.0.0.1";
    public int port = 47007;
    public String password = "123456789";

    public String configName = "default";
    public String name = null;

    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;

    private StarGateUniverse sgu;

    private long nextTick;
    private boolean isRunning = true;

    protected boolean isConnected = true;
    protected boolean authenticated = false;



    public Client(){
        this.sgu = StarGateUniverse.getInstance();
    }

    @Override
    public void run() {
        this.nextTick = System.currentTimeMillis();

        String error;
        if ((error = this.connect()) != null){
            this.force_close();
            this.sgu.getLogger().warning("§cUnable to connect to StarGate server §6@"+this.configName);
            this.sgu.getLogger().warning("§cError: "+error);
            return;
        }

        while (this.isRunning) {
            long now = System.currentTimeMillis();
            long time = now - this.nextTick;

            if (time < 0) {
                try {
                    Thread.sleep(Math.max(25, -time));
                } catch (InterruptedException e) {
                    this.sgu.getLogger().warning("§eError appear while ticking StarGate Client!");
                }
            }

            if (!this.tick()){
                if ((error = this.connect()) == null){
                    this.nextTick += 50;
                    continue;
                }

                this.isRunning = false;
                this.force_close();
                this.sgu.getLogger().warning("§cERROR: "+error);
            }

            this.nextTick += 50;
        }
    }

    private boolean tick(){
        if (!this.isAuthenticated()){
            this.authenticate();
            return true;
        }

        String message;
        try {
            message = in.readLine();
        }catch (Exception e){
            if (!this.isConnected){
                return true;
            }

            if (e.getMessage() == null || e.getMessage().equals("Connection reset")){
                this.sgu.getLogger().info("§cWARNING: Connection aborted! StarGate connection was unexpectedly closed! §cTrying to reconnect...");
                return false;
            }

            this.sgu.getLogger().info("§cWARNING: Error while reading from StarGate server!");
            this.sgu.getLogger().info("§c"+e);
            return true;
        }

        if (message == null || message.startsWith("GATE_STATUS")) return true;

        /* This is just resending ping data. No handle needed*/
        if (message.startsWith("GATE_PING")){
            String[] data = message.split(":");

            this.gatePacket(new PingPacket(){{
                client = name;
                pingData = data[1];
            }});
            return true;
        }

        if (message.startsWith("GATE_RESPONSE")){
            String[] data = message.split(":");
            String uuid = data[1];
            String response = data[2];

            /* 20*30 is maximum tolerated delay*/
            this.sgu.responses.put(uuid, response);
            this.sgu.getServer().getScheduler().scheduleDelayedTask(new ResponseRemoveTask(uuid), 20*30);
            return true;
        }

        try {
            StarGatePacket packet = sgu.processPacket(message);
            if (packet instanceof ConnectionInfoPacket){
                String reason = ((ConnectionInfoPacket) packet).getReason();

                switch (((ConnectionInfoPacket) packet).getPacketType()){
                    case ConnectionInfoPacket.CONNECTION_RECONNECT:
                        this.sgu.getLogger().info("§cWARNING: Reconnecting to StarGate server §6"+this.configName+"§c! Reason: "+((reason == null) ? "unknown" : reason));

                        this.force_close();
                        return false; //tries to reconnect
                    case ConnectionInfoPacket.CONNECTION_CLOSED:
                        this.sgu.getLogger().info("§cWARNING: Connection to StarGate server! Reason: §c"+((reason == null) ? "unknown" : reason));

                        this.force_close();
                        this.shutdown();
                        return true;
                }
                return true;
            }
        }catch (Exception e){
            this.sgu.getLogger().info("§cERROR: Problem appears while processing packet!");
            this.sgu.getLogger().info("§c" + e.getMessage());
        }
        return true;
    }

    private boolean authenticate(){
        try {
            String iris = this.in.readLine();
            StarGatePacket irisPacket = this.sgu.processPacket(iris);

            if (!(irisPacket instanceof ConnectionInfoPacket)) return true;

            int type = ((ConnectionInfoPacket) irisPacket).getPacketType();
            if (type == ConnectionInfoPacket.CONNECTION_ABORTED){
                String reason = ((ConnectionInfoPacket) irisPacket).getReason();

                this.sgu.getLogger().warning("§cERROR: StarGate client was not authenticated! Reason: §4"+((reason == null) ? "unknown" : reason));
                this.shutdown();
                return true;
            }

            if (type == ConnectionInfoPacket.CONNECTION_CONNECTED){
                this.authenticated = true;
            }

            this.welcome();
        }catch (Exception e){
            this.sgu.getLogger().info("§cWARNING: Error while opening iris!");
            this.sgu.getLogger().info("§c"+e);
            return false;
        }
        return true;
    }

    public String connect(){
        try {
            this.socket = new Socket(address, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            return e.getMessage();
        }

        String[] handshakeData = new String[3];
        handshakeData[0] = "CHEVRON";
        handshakeData[1] = name;
        handshakeData[2] = password;

        try {
            this.out.println(ArrayUtils.implode(":", handshakeData));
        }catch (Exception e){
            this.sgu.getLogger().info("§cWARNING: Unable to authenticate StarGate client! Please try to restart server");
            return e.getMessage();
        }

        this.sgu.getLogger().info("§aSuccessfully connected to StarGate server §6@"+this.configName+"§a! Authenticating ...");
        return null;
    }

    /* This function we use to send packet to Clients*/
    public String gatePacket(StarGatePacket packet){
        String packetString;
        if (!packet.isEncoded) {
            packet.encode();
        }
        packetString = packet.encoded;
        String uuid = UUID.randomUUID().toString();

        try {
            this.out.println(packetString +"!"+ uuid);
        }catch (Exception e){
            this.sgu.getLogger().info("§cWARNING: Packet was not sent!");
            this.sgu.getLogger().info("§c"+e.getMessage());
        }
        return uuid;
    }

    /* Sending WelcomePacket*/
    private void welcome(){
        WelcomePacket packet = new WelcomePacket();

        packet.server = this.name;
        packet.players = this.sgu.getServer().getOnlinePlayers().size();
        packet.tps = Math.round(sgu.getServer().getTicksPerSecond());
        packet.usage = Math.round(sgu.getServer().getTickUsage());

        packet.isEncoded = false;
        this.gatePacket(packet);
    }

    public void shutdown(){
        this.isRunning = false;
        this.isConnected = false;

        try {
            this.out.close();
            this.in.close();
            this.socket.close();
        }catch (Exception e){
            //ignore
        }

        this.sgu.removeClient(this.configName);
    }

    public void close(boolean shutdown){
        close(null, shutdown);
    }

    public void close(String reason, boolean shutdown){
        if (!this.isConnected){
            if (shutdown){
                this.shutdown();
                return;
            }

            this.force_close();
            return;
        }

        ConnectionInfoPacket packet = new ConnectionInfoPacket();
        packet.packetType = ConnectionInfoPacket.CONNECTION_CLOSED;
        packet.reason = reason;
        packet.putPacket(this.configName);

        if (shutdown){
            this.shutdown();
            return;
        }
        this.force_close();
    }

    private void force_close(){
        this.isConnected = false;

        try {
            this.out.close();
            this.in.close();
            this.socket.close();
        }catch (Exception e){
            //ignore
        }
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public StarGateUniverse getSgu() {
        return this.sgu;
    }

    public String getConfigName() {
        return configName;
    }
}
