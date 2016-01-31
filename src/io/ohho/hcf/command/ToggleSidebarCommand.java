package io.ohho.hcf.command;

import io.ohho.hcf.HCF;
import com.exodon.hcf.scoreboard.PlayerBoard;
import com.exodon.hcf.scoreboard.ScoreboardHandler;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class ToggleSidebarCommand
  implements CommandExecutor, TabExecutor
{
  private final HCF plugin;
  
  public ToggleSidebarCommand(HCF plugin)
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
    PlayerBoard playerBoard = this.plugin.getScoreboardHandler().getPlayerBoard(((Player)sender).getUniqueId());
    boolean newVisibile = !playerBoard.isSidebarVisible();
    playerBoard.setSidebarVisible(newVisibile);
    sender.sendMessage(ChatColor.YELLOW + "Scoreboard sidebar is " + (newVisibile ? ChatColor.GREEN + "now" : new StringBuilder().append(ChatColor.RED).append("no longer").toString()) + ChatColor.YELLOW + " visible.");
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return Collections.emptyList();
  }
}
