package alemiz.sgu.nukkit.utils;

import alemiz.sgu.nukkit.StarGateUniverse;
import alemiz.stargate.client.StarGateClient;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;

public class ReconnectTask extends Task {

	@Override
	public void onRun(int currentTick) {
		StarGateUniverse instance = StarGateUniverse.getInstance();
		for (StarGateClient client : instance.getClientsCopy()) {
			if (!client.isConnected() && instance.isAutoStart()) {
				instance.getLogger().info(TextFormat.AQUA + "Reconnecting to StarGate");
				client.run();
			}
		}

	}

}
