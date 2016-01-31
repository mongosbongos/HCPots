package io.ohho.hcf.hcfold;

import com.parapvp.util.ItemBuilder;
import com.parapvp.util.chat.Lang;

import io.ohho.hcf.ConfigurationService;
import io.ohho.hcf.HCF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class MapKitCommand
  implements CommandExecutor, TabCompleter, Listener
{
  private final Set<Inventory> tracking;
  
  public MapKitCommand(HCF plugin)
  {
    this.tracking = new HashSet();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    List<ItemStack> items = new ArrayList();
    for (Map.Entry<Enchantment, Integer> entry : ConfigurationService.ENCHANTMENT_LIMITS.entrySet()) {
      items.add(new ItemBuilder(Material.ENCHANTED_BOOK).displayName(ChatColor.YELLOW + Lang.fromEnchantment((Enchantment)entry.getKey()) + ": " + ChatColor.GREEN + entry.getValue()).build());
    }
    for (Map.Entry<PotionType, Integer> entry2 : ConfigurationService.POTION_LIMITS.entrySet()) {
      if (((Integer)entry2.getValue()).intValue() > 0) {
        items.add(new ItemBuilder(new Potion((PotionType)entry2.getKey()).toItemStack(1)).displayName(ChatColor.YELLOW + WordUtils.capitalizeFully(((PotionType)entry2.getKey()).name().replace('_', ' ')) + ": " + ChatColor.GREEN + entry2.getValue()).build());
      }
    }
    Player player = (Player)sender;
    int inventorySize = (items.size() + 8) / 9 * 9;
    Inventory inventory = Bukkit.createInventory(player, inventorySize, ChatColor.YELLOW + "Map " + 2.0D + " Kit");
    this.tracking.add(inventory);
    for (ItemStack item : items) {
      inventory.addItem(new ItemStack[] { item });
    }
    player.openInventory(inventory);
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return Collections.emptyList();
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onInventoryClick(InventoryClickEvent event)
  {
    if (this.tracking.contains(event.getInventory())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPluginDisable(PluginDisableEvent event)
  {
    for (Inventory inventory : this.tracking)
    {
      Collection<HumanEntity> viewers = new HashSet(inventory.getViewers());
      for (HumanEntity viewer : viewers) {
        viewer.closeInventory();
      }
    }
  }
}
