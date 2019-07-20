package alemiz.sgu.tasks;

import alemiz.sgu.StarGateUniverse;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;

import java.util.Map;

public abstract class ResponseCheckTask extends Task{

    private String uuid;
    private String expectedResult;

    /* This should be instance of your plugin.*/
    private PluginBase plugin;

    /* This is maximum delay in SECONDS that is tolerated by ping.
    If delay is bigger response will be removed. Also clients will be disconnected!*/
    private int timeout = 60;

    public ResponseCheckTask(PluginBase plugin, String uuid, String expectedResult){
        this.uuid = uuid;
        this.expectedResult = expectedResult;
    }

    @Override
    public void onRun(int i)  {
        if (timeout == 0) return;

        Map<String, String> responses = StarGateUniverse.getInstance().responses;

        /*Here we check if response is already handled/received*/
        if (!responses.containsKey(uuid) || responses.get(uuid) == null || responses.get(uuid).equals("unknown")){
            timeout--;

            if (timeout == 0){
                error();
                return;
            }
            StarGateUniverse.getInstance().getServer().getScheduler().scheduleDelayedTask(this, 10);
            return;
        }

        String response = responses.get(uuid);
        handleResult(response, expectedResult);
    }
    /* Now you can do what you want with result*/
    public abstract void handleResult(String response, String expectedResult);

    /* This function will be called if result will be never fetched*/
    public abstract void error();
}
