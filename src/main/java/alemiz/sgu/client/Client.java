package alemiz.sgu.client;

import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.packets.StarGatePacket;
import alemiz.sgu.packets.WelcomePacket;
import alemiz.sgu.tasks.ResponseRemoveTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class Client extends Thread {

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

                    if (iris.equals("GATE_OPENED")){
                        read = true;
                    }

                }catch (IOException e){
                    sgu.getLogger().info("§cWARNING: Error while opening iris!");
                    connect();
                }
            }

            //out.println("0x01!Test_Server!20!58!12");
            welcome();

            boolean end = false;
            while (!end){
                try {
                    if (socket.getInputStream().available() < 0) continue;

                    String message = in.readLine();

                    switch (message){
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
                    sgu.getLogger().info("§cWARNING: Error while reading from StarGate server!");
                    sgu.getLogger().info("§c"+e);


                    if (e.getMessage() == null || e.getMessage().equals("Connection reset")){
                        connect();
                        end = true;
                    }
                }
            }
        }

    }

    public void connect(){
        try {
            String address = StarGateUniverse.getInstance().cfg.getString("Address");
            int port = StarGateUniverse.getInstance().cfg.getInt("Port");

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
            String name = StarGateUniverse.getInstance().cfg.getString("Client");
            out.println("CHEVRON:"+name);
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
