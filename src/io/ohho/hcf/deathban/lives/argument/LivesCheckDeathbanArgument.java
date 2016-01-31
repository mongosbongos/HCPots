package io.ohho.hcf.deathban.lives.argument;

import com.google.common.base.Strings;
import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.DateTimeFormats;
import io.ohho.hcf.HCF;
import io.ohho.hcf.deathban.Deathban;
import com.exodon.hcf.user.FactionUser;
import io.ohho.hcf.user.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesCheckDeathbanArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public LivesCheckDeathbanArgument(HCF plugin)
  {
    super("checkdeathban", "Check the deathban cause of player");
    this.plugin = plugin;
    this.permission = ("hcf.command.lives.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <playerName>";
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    if ((!target.hasPlayedBefore()) && (!target.isOnline()))
    {
      sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
      return true;
    }
    com.exodon.hcf.deathban.Deathban deathban = this.plugin.getUserManager().getUser(target.getUniqueId()).getDeathban();
    if ((deathban == null) || (!deathban.isActive()))
    {
      sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
      return true;
    }
    sender.sendMessage(ChatColor.YELLOW + "Deathban cause of " + target.getName() + '.');
    sender.sendMessage(ChatColor.AQUA + " Time: " + DateTimeFormats.HR_MIN.format(deathban.getCreationMillis()));
    sender.sendMessage(ChatColor.AQUA + " Duration: " + DurationFormatUtils.formatDurationWords(deathban.getExpiryMillis() - deathban.getCreationMillis(), true, true));
    Location location = deathban.getDeathPoint();
    if (location != null) {
      sender.sendMessage(ChatColor.AQUA + " Location: (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ") - " + location.getWorld().getName());
    }
    sender.sendMessage(ChatColor.AQUA + " Reason: " + Strings.nullToEmpty(deathban.getReason()));
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length != 2) {
      return Collections.emptyList();
    }
    List<String> results = new ArrayList();
    for (FactionUser factionUser : this.plugin.getUserManager().getUsers().values())
    {
      com.exodon.hcf.deathban.Deathban deathban = factionUser.getDeathban();
      if ((deathban != null) && (deathban.isActive()))
      {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
        String name = offlinePlayer.getName();
        if (name != null) {
          results.add(name);
        }
      }
    }
    return results;
  }
}
