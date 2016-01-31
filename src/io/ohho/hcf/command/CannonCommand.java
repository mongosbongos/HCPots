package io.ohho.hcf.command;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.parapvp.util.BukkitUtils;

import io.ohho.hcf.ConfigurationService;
import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.faction.type.WarzoneFaction;
import io.ohho.oCore.faction.FactionManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CannonCommand
  implements CommandExecutor, TabCompleter
{
  private static final Material SPAWN_CANNON_BLOCK = Material.BEACON;
  private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("aim", "launch");
  private final HCF plugin;
  
  public CannonCommand(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "Only players may use the spawn cannon.");
      return true;
    }
    if (args.length < 1)
    {
      sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <launch|aim [x z])>");
      return true;
    }
    Player player = (Player)sender;
    World world = player.getWorld();
    if (world.getEnvironment() != World.Environment.NORMAL)
    {
      sender.sendMessage(ChatColor.RED + "You can only use the spawn cannon in the overworld.");
      return true;
    }
    Location location = player.getLocation();
    if (location.getBlock().getRelative(BlockFace.DOWN).getType() != SPAWN_CANNON_BLOCK)
    {
      sender.sendMessage(ChatColor.RED + "You are not on a spawn cannon (" + ChatColor.AQUA + SPAWN_CANNON_BLOCK.name() + ChatColor.RED + ").");
      return true;
    }
    if (!this.plugin.getFactionManager().getFactionAt(location).isSafezone())
    {
      sender.sendMessage(ChatColor.RED + "You can only use the spawn cannon in safe-zones.");
      return true;
    }
    if (args[0].equalsIgnoreCase("aim"))
    {
      if (!sender.hasPermission(command.getPermission() + ".aim"))
      {
        sender.sendMessage(ChatColor.WHITE + "Sub-Command not found.");
        return true;
      }
      if (args.length < 3)
      {
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <x> <z>");
        return true;
      }
      Integer x = Ints.tryParse(args[1]);
      Integer z;
      if ((x == null) || ((z = Ints.tryParse(args[2])) == null))
      {
        sender.sendMessage(ChatColor.RED + "Your x or z co-ordinate was invalid.");
        return true;
      }
      Integer z1 = null;
      launchPlayer(player, new Location(world, x.intValue(), 0.0D, z1.intValue()));
      return true;
    }
    if (!args[0].equalsIgnoreCase("launch"))
    {
      sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <launch|aim [x z])>");
      return true;
    }
    if (!sender.hasPermission(command.getPermission() + ".launch"))
    {
      sender.sendMessage(ChatColor.RED + "You do not have access to launch the cannon.");
      return true;
    }
    double min = ((Double)ConfigurationService.SPAWN_RADIUS_MAP.get(world.getEnvironment())).doubleValue();
    int max = 1000;
    int maxCannonDistance = getMaxCannonDistance(sender);
    Random random = this.plugin.getRandom();
    double x2 = Math.max(random.nextInt(Math.min(1000, maxCannonDistance)), min);
    if (random.nextBoolean()) {
      x2 = -x2;
    }
    double z2 = Math.max(random.nextInt(Math.min(1000, maxCannonDistance)), min);
    if (random.nextBoolean()) {
      z2 = -z2;
    }
    launchPlayer(player, new Location(world, x2, 0.0D, z2));
    return true;
  }
  
  public void launchPlayer(Player player, Location location)
  {
    Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
    if (!(factionAt instanceof WarzoneFaction))
    {
      player.sendMessage(ChatColor.RED + "You can only cannon to areas in the Warzone.");
      return;
    }
    int x = location.getBlockX();
    int z = location.getBlockZ();
    int maxDistance = getMaxCannonDistance(player);
    if ((Math.abs(x) > maxDistance) || (Math.abs(z) > maxDistance))
    {
      player.sendMessage(ChatColor.RED + "You cannot launch that far from the spawn cannon. Your limit is " + maxDistance + '.');
      return;
    }
    location = BukkitUtils.getHighestLocation(location).add(0.0D, 3.0D, 0.0D);
    player.sendMessage(ChatColor.YELLOW + "Launched To " + ChatColor.BLUE + x + ChatColor.YELLOW + ", " + z + ChatColor.YELLOW + '.');
    player.playSound(location, Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
    player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1));
    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));
    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1));
  }
  
  public int getMaxCannonDistance(CommandSender sender)
  {
    int decrement = 50;
    int i;
    for (int radius = i = (850 + decrement - 1) / decrement * decrement; i > 0; i -= decrement) {
      if (sender.hasPermission("hcf.spawncannon." + i)) {
        return i;
      }
    }
    return 100;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
  }
}
