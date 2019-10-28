package alemiz.sgu.client;

import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.packets.ConnectionInfoPacket;
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

    public String name = null;

    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;

    private StarGateUniverse sgu;

    protected boolean canConnect = true;
    protected boolean isConnected = false;

    public Client(){
        sgu = StarGateUniverse.getInstance();
    }

    @Override
    public void run() {
        /* Creating first connection*/
        connect();

        while (canConnect){
            boolean read = false;

            while (!read){
                try {
                    String iris = in.readLine();
                    System.out.println(iris);

                    StarGatePacket irisPacket = sgu.processPacket(iris);
                    if (irisPacket instanceof ConnectionInfoPacket){
                        int type = ((ConnectionInfoPacket) irisPacket).getPacketType();

                        if (type == ConnectionInfoPacket.CONNECTION_ABORTED){
                            String reason = ((ConnectionInfoPacket) irisPacket).getReason();

                            sgu.getLogger().warning("§cERROR: StarGate client was not authenticated! Reason: §4"+((reason == null) ? "unknown" : reason));
                            canConnect = false;
                            return;
                        }

                        if (type == ConnectionInfoPacket.CONNECTION_CONNECTED){
                            read = true;
                        }
                    }

                }catch (IOException e){
                    sgu.getLogger().info("§cWARNING: Error while opening iris!");
                    connect();
                }catch (Exception e){
                    sgu.getLogger().info("§cWARNING: Error while opening iris!");
                    sgu.getLogger().info("§c"+e);
                    connect();
                }
            }

            welcome();

            boolean end = false;
            while (!end){
                try {
                    if (socket.getInputStream().available() < 0) continue;

                    String message = in.readLine();

                    switch (message){ //TODO: Reconnect by ConnectionInfoPacket
                        case "GATE_RECONNECT":
                            close();
                            connect();

                            end = true;
                            break;
                        default:
                            //sgu.getLogger().info("§e"+message);

                            /* This is just patch for resending ping data*/
                            if (message.startsWith("GATE_PING")){
                                String[] data = message.split(":");
                                String name = StarGateUniverse.getInstance().cfg.getString("Client");

                                break;
                            }
                            if (message.startsWith("GATE_RESPONSE")){
                                String[] data = message.split(":");
                                String uuid = data[1];
                                String response = data[2];

                                sgu.responses.put(uuid, response);
                                /* 20*30 is maximum tolerated delay*/
                                sgu.getServer().getScheduler().scheduleDelayedTask(new ResponseRemoveTask(uuid), 20*30);
                                break;
                            }

                            sgu.processPacket(message);
                            break;
                    }

                }catch (Exception e){
                    if (e.getMessage() == null || e.getMessage().equals("Connection reset")){
                        sgu.getLogger().info("§cWARNING: Connection aborted! StarGate connection was unexpectedly closed!");
                        sgu.getLogger().info("§cTrying to reconnect...");

                        connect();
                        end = true;
                    }else{
                        sgu.getLogger().info("§cWARNING: Error while reading from StarGate server!");
                        sgu.getLogger().info("§c"+e);
                    }
                }
            }
        }

    }

    public void connect(){
        try {
            address = StarGateUniverse.getInstance().cfg.getString("Address");
            port = StarGateUniverse.getInstance().cfg.getInt("Port");
            password = StarGateUniverse.getInstance().cfg.getString("Password");

            socket = new Socket(address, port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        }catch (IOException e){
            sgu.getLogger().critical("§cERROR: Unable to connect to StarGate server!");
            sgu.getLogger().critical("§c"+e.getMessage());
            canConnect = false;

            return;
        }

        try {
            name = StarGateUniverse.getInstance().cfg.getString("Client");

            String[] handshakeData = new String[3];
            handshakeData[0] = "CHEVRON";
            handshakeData[1] = name;
            handshakeData[2] = password;

            out.println(ArrayUtils.implode(":", handshakeData));
            canConnect = true;

            sgu.getLogger().info("§aDone! Successfully connected to StarGate server! Authenticating ...");
        }catch (Exception e){
            sgu.getLogger().info("§cWARNING: Unable to authenticate StarGate client! Please try to restart server");
            canConnect = false;
        }
    }

    public void close(){
        try {
            out.close();
            in.close();
            socket.close();
        }catch (IOException e){
            sgu.getLogger().critical("ERROR: While connection closing"+e.getMessage());
        }
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
            out.println(packetString +"!"+ uuid);
            //sgu.getLogger().info("§6"+packetString +"!"+ uuid);
        }catch (Exception e){
            sgu.getLogger().info("§cWARNING: Packet was not sent!");
            sgu.getLogger().info("§c"+e.getMessage());
        }
        return uuid;
    }

    private void welcome(){
        /* Sending WelcomePacket*/
        WelcomePacket packet = new WelcomePacket();

        packet.server = StarGateUniverse.getInstance().cfg.getString("Client");

        packet.players = sgu.getServer().getOnlinePlayers().size();
        packet.tps = Math.round(sgu.getServer().getTicksPerSecond());
        packet.usage = Math.round(sgu.getServer().getTickUsage());

        packet.isEncoded = false;

        gatePacket(packet);
    }
}
