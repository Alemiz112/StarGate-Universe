package tests;

import alemiz.sgu.packets.ServerManagePacket;
import alemiz.sgu.tasks.ResponseCheckTask;

public class DockerContainerCreate {

    public void startNewLobby(String serverName, String dockerImage){
        ServerManagePacket packet = new ServerManagePacket();
        packet.packetType = ServerManagePacket.DOCKER_ADD;

        packet.serverAddress = "192.168.0.100";
        packet.serverPort = "19134";
        packet.serverName = serverName; //lobby-1
        packet.containerImage = dockerImage; //pmmp-custom
        packet.exposedPorts = new String[]{"19134/19134"}; //own port bindings (UDP & TCP is bind)
        packet.envVariables = new String[]{"JAVA_RAM=2048M"};
        packet.dockerHost = "default"; //if you have more hosts

        String uuid = packet.putPacket();
        this.handleContainerCreation(uuid);
        
    }

    private void handleContainerCreation(String uuid){
        ResponseCheckTask task = new ResponseCheckTask(StarGateUniverse.getInstance(), uuid, "ignore") {
            @Override
            public void handleResult(String response, String expectedResult) {
                this.plugin.getLogger().info("§eHandled docker response!");

                String[] data = response.split(",");
                this.plugin.getLogger().info("§eStatus: "+data[0] + (data.length > 1? "Container ID: "+data[1] : ""));
            }

            @Override
            public void error() {
                //looks like something bad happened
                //we can ignore it
            }
        };

        /* Run first check after 1 sec*/
        task.scheduleTask(20);
    }
}
