package tests;

import alemiz.sgu.tasks.ResponseCheckTask;

public class SimpleResponseCheckTask {

    /* This is example where we have defined plugin instance by your plugin
    * 'plugin÷ must NOT be PluginBase instance!!!
    * It must be instance of your main plugin class*/

    public void test(){
        plugin.getServer().getScheduler().scheduleDelayedTask(this, new ResponseCheckTask(plugin, uuid, expectedResult) {
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
        }, 10);
    }
}
