package io.ohho.hcf.faction.argument.staff;

import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.claim.ClaimHandler;
import io.ohho.hcf.faction.claim.ClaimSelection;
import io.ohho.hcf.faction.type.ClaimableFaction;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.oCore.faction.FactionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FactionClaimForArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public FactionClaimForArgument(HCF plugin)
  {
    super("claimfor", "Claims land for another faction.");
    this.plugin = plugin;
    this.permission = ("hcf.command.faction.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <factionName>";
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    Faction targetFaction = this.plugin.getFactionManager().getFaction(args[1]);
    if (!(targetFaction instanceof ClaimableFaction))
    {
      sender.sendMessage(ChatColor.RED + "Claimable faction named " + args[1] + " not found.");
      return true;
    }
    Player player = (Player)sender;
    UUID uuid = player.getUniqueId();
    ClaimSelection claimSelection = (ClaimSelection)this.plugin.getClaimHandler().claimSelectionMap.get(uuid);
    if ((claimSelection == null) || (!claimSelection.hasBothPositionsSet()))
    {
      player.sendMessage(ChatColor.RED + "You have not set both positions of this claim selection.");
      return true;
    }
    if (this.plugin.getClaimHandler().tryPurchasing(player, claimSelection.toClaim(targetFaction)))
    {
      this.plugin.getClaimHandler().clearClaimSelection(player);
      player.setItemInHand(new ItemStack(Material.AIR, 1));
      sender.sendMessage(ChatColor.YELLOW + "Successfully claimed this land for " + ChatColor.RED + targetFaction.getName() + ChatColor.YELLOW + '.');
    }
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    if ((args.length != 2) || (!(sender instanceof Player))) {
      return Collections.emptyList();
    }
    if (args[1].isEmpty()) {
      return null;
    }
    Player player = (Player)sender;
    List<String> results = new ArrayList(this.plugin.getFactionManager().getFactionNameMap().keySet());
    for (Player target : Bukkit.getOnlinePlayers()) {
      if ((player.canSee(target)) && (!results.contains(target.getName()))) {
        results.add(target.getName());
      }
    }
    return results;
  }
}
