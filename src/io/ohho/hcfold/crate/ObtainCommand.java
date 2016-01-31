package io.ohho.hcfold.crate;

import com.parapvp.base.BasePlugin;
import com.parapvp.base.kit.Kit;
import com.parapvp.base.kit.KitManager;

import io.ohho.hcf.HCF;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ObtainCommand
  implements CommandExecutor, TabCompleter
{
  private final HCF plugin;
  
  public ObtainCommand(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    Player player = (Player)sender;
    Kit kit = BasePlugin.getPlugin().getKitManager().getKit("Obtain");
    if (kit == null)
    {
      sender.sendMessage(ChatColor.RED + "An obtain kit has not been created.");
      return true;
    }
    if (!kit.applyTo(player, false, true)) {
      player.sendMessage(ChatColor.RED + "Failed to apply kit " + kit.getName() + '.');
    }
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return Collections.emptyList();
  }
}
