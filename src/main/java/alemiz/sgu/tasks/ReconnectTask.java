package alemiz.sgu.tasks;

import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.client.Client;
import cn.nukkit.scheduler.Task;

import java.util.HashMap;
import java.util.Map;

public class ReconnectTask extends Task {

    @Override
    public void onRun(int i) {
        Map<String, Client> clients = new HashMap<>(StarGateUniverse.getInstance().getClients());

        clients.forEach((String name, Client client)->{
            if (!client.isConnected()){
                /* Close old sockets first*/
                StarGateUniverse.getInstance().restart(name);
            }
        });
    }
}
