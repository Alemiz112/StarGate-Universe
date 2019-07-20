package tests;

import alemiz.sgu.StarGateUniverse;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class KickExample extends Command {

    public KickExample(){
        super("ekick","", "StarGate test");
        this.setPermission("test");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        StarGateUniverse.getInstance().kickPlayer(player, "EnchanedResponseCheckTask");
        return true;
    }
}
