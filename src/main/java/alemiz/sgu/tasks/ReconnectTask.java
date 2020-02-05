package alemiz.sgu.tasks;

import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.client.Client;
import cn.nukkit.scheduler.Task;

public class ReconnectTask extends Task {

    @Override
    public void onRun(int i) {
        Client client =  StarGateUniverse.getInstance().getClient();
       if (!client.canConnect() && !client.isConnected()){
           client.getSgu().getLogger().info("§eReloading StarGate Client");

           /* Close old sockets first*/
           client.force_close();

           StarGateUniverse.getInstance().restart();
       }
    }
}
