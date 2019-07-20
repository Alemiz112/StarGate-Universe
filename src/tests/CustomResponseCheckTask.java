package tests;

/* This class is here to show right example of how to get result from Sent Packet witch was executed with getting response
* This works only for CUSTOM PACKETS!
* So you must create on server site handler for that packet and use 'setResponse()' function to send response
* If you dont understand enough ask me here: https://discord.gg/VsHXm2M */

import alemiz.sgu.StarGateUniverse;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;

import java.util.Map;

public class CustomResponseCheckTask extends Task {

    private String uuid;
    private String expectedResult;

    /* This should be instance of your plugin. No PluginBase!*/
    private PluginBase plugin;

    /* This is maximum delay in SECONDS that is tolerated by ping.
    If delay is bigger response will be removed. Also clients will be disconnected!*/
    private int timeout = 30;

    public CustomResponseCheckTask(PluginBase plugin, String uuid, String expectedResult){
        this.uuid = uuid;
        this.expectedResult = expectedResult;
        this.plugin = plugin;
    }

    @Override
    public void onRun(int i) {
        if (timeout == 0) return;

        Map<String, String> responses = StarGateUniverse.getInstance().responses;

        /*Here we check if response is already handled/received*/
        if (!responses.containsKey(uuid) || responses.get(uuid) == null || responses.get(uuid).equals("unknown")){
            plugin.getServer().getScheduler().scheduleDelayedTask(this, 20);
            timeout--;
            return;
        }

        /* Now you can do what you want with result*/
        String response = responses.get(uuid);
    }
}
