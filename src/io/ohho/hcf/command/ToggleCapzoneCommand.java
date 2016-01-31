package io.ohho.hcf.command;

import io.ohho.hcf.HCF;
import com.exodon.hcf.user.FactionUser;
import io.ohho.hcf.user.UserManager;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class ToggleCapzoneCommand
  implements CommandExecutor, TabExecutor
{
  private final HCF plugin;
  
  public ToggleCapzoneCommand(HCF plugin)
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
    FactionUser factionUser = this.plugin.getUserManager().getUser(((Player)sender).getUniqueId());
    boolean newStatus = !factionUser.isCapzoneEntryAlerts();
    factionUser.setCapzoneEntryAlerts(newStatus);
    sender.sendMessage(ChatColor.AQUA + "You will now " + (newStatus ? ChatColor.GREEN.toString() : new StringBuilder().append(ChatColor.RED).append("un").toString()) + "able" + ChatColor.AQUA + " to see capture zone entry messages.");
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return Collections.emptyList();
  }
}
