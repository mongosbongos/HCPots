package io.ohho.hcf.listener;

import com.google.common.base.Preconditions;

import io.ohho.hcf.HCF;
import io.ohho.hcf.user.FactionUser;
import io.ohho.hcf.user.UserManager;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessageListener
  implements Listener
{
  private final HCF plugin;
  
  public DeathMessageListener(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  public static String replaceLast(String text, String regex, String replacement)
  {
    return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onPlayerDeath(PlayerDeathEvent event)
  {
    String message = event.getDeathMessage();
    if ((message == null) || (message.isEmpty())) {
      return;
    }
    event.setDeathMessage(getDeathMessage(message, event.getEntity(), getKiller(event)));
  }
  
  private CraftEntity getKiller(PlayerDeathEvent event)
  {
    EntityLiving lastAttacker = ((CraftPlayer)event.getEntity()).getHandle().aX();
    return lastAttacker == null ? null : lastAttacker.getBukkitEntity();
  }
  
  private String getDeathMessage(String input, org.bukkit.entity.Entity entity, org.bukkit.entity.Entity killer)
  {
    input = input.replaceFirst("\\[", ChatColor.GRAY + "[" + ChatColor.GRAY);
    input = replaceLast(input, "]", ChatColor.GRAY + "]" + ChatColor.GRAY);
    if (entity != null) {
      input = input.replaceFirst("(?i)" + getEntityName(entity), ChatColor.RED + getDisplayName(entity) + ChatColor.YELLOW);
    }
    if ((killer != null) && ((entity == null) || (!killer.equals(entity)))) {
      input = input.replaceFirst("(?i)" + getEntityName(killer), ChatColor.RED + getDisplayName(killer) + ChatColor.YELLOW);
    }
    return input;
  }
  
  private String getEntityName(org.bukkit.entity.Entity entity)
  {
    Preconditions.checkNotNull(entity, "Entity cannot be null");
    return (entity instanceof Player) ? ((Player)entity).getName() : ((CraftEntity)entity).getHandle().getName();
  }
  
  private String getDisplayName(org.bukkit.entity.Entity entity)
  {
    Preconditions.checkNotNull(entity, "Entity cannot be null");
    if ((entity instanceof Player))
    {
      Player player = (Player)entity;
      return player.getName() + ChatColor.GRAY + '[' + ChatColor.GRAY + this.plugin.getUserManager().getUser(player.getUniqueId()).getKills() + ChatColor.GRAY + ']';
    }
    return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
  }
}
