package tests;

import alemiz.sgu.StarGateUniverse;
import alemiz.sgu.tasks.ResponseCheckTask;
import cn.nukkit.plugin.PluginBase;

public class EnchanedResponseCheckTask extends ResponseCheckTask {

    public EnchanedResponseCheckTask(PluginBase plugin, String uuid, String expectedResult){
        super(plugin, uuid, expectedResult);
    }

    /* After successfully received response this function will be called*/
    @Override
    public void handleResult(String response, String expectedResult) {
        if (response == expectedResult){
            StarGateUniverse.getInstance().getLogger().info("§bRight Response handled!");
        }else{
            StarGateUniverse.getInstance().getLogger().info("§c"+response);
        }
    }

    /* This will be run when we dont receive response or we will check bad UUID*/
    @Override
    public void error() {
        StarGateUniverse.getInstance().getLogger().info("§cERROR!");
    }
}
