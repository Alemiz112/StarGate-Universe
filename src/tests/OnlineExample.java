package tests;

import alemiz.sgu.tasks.ResponseCheckTask;
import cn.nukkit.Player;

public class OnlineExample extends ResponseCheckTask {

    private Player executor;
    private String finding;

    public OnlineExample(YourPlugin plugin, String uuid, String expectedResult, Player executor, String finding){
        super(plugin, uuid, expectedResult);
        this.executor = executor;
        this.finding = finding;
    }

    @Override
    public void handleResult(String response, String expectedResult) {
        plugin.getLogger().info("§bHandled OnlinePlayer response");

        if (response.equals("false")){
            executor.sendMessage("§ePlayer §e"+finding+"is OFFLINE");
            return;
        }

        String[] data = response.split("!");
        executor.sendMessage("§6Player §e"+finding+"§6is ONLINE at server§e"+data[1]);
    }

    @Override
    public void error() {
        plugin.getLogger().info("§cResponse for uuid §5"+uuid+"§cwas not received!");
    }
}
