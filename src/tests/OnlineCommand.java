package tests;

import alemiz.sgu.StarGateUniverse;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class OnlineCommand extends Command {

    public OnlineCommand(){
        super("online","", "Online test");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        String uuid = StarGateUniverse.getInstance().isOnline(player);

        OnlineExample task = new OnlineExample(plugin, uuid, "true", player, "alemiz003");
        StarGateUniverse.getInstance().getServer().getScheduler().scheduleDelayedTask(task, 5);
        return true;
    }
}
