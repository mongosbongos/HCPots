package io.ohho.hcf.economy;

import com.google.common.primitives.Ints;
import com.parapvp.base.BasePlugin;
import com.parapvp.util.InventoryUtils;
import com.parapvp.util.SignHandler;
import com.parapvp.util.itemdb.ItemDb;

import io.ohho.hcf.HCF;
import io.ohho.hcf.api.Crowbar;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

public class ShopSignListener
  implements Listener
{
  private static final long SIGN_TEXT_REVERT_TICKS = 100L;
  private static final Pattern ALPHANUMERIC_REMOVER = Pattern.compile("[^A-Za-z0-9]");
  private final HCF plugin;
  
  public ShopSignListener(HCF plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler(ignoreCancelled=false, priority=EventPriority.HIGH)
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
    {
      Block block = event.getClickedBlock();
      BlockState state = block.getState();
      if ((state instanceof Sign))
      {
        Sign sign = (Sign)state;
        String[] lines = sign.getLines();
        Integer quantity = Ints.tryParse(lines[2]);
        if (quantity == null) {
          return;
        }
        Integer price = Ints.tryParse(ALPHANUMERIC_REMOVER.matcher(lines[3]).replaceAll(""));
        if (price == null) {
          return;
        }
        ItemStack stack;
        ItemStack stack1;
        if (lines[1].equalsIgnoreCase("Crowbar")) {
          stack1 = new Crowbar().getItemIfPresent();
        } else if ((stack1 = BasePlugin.getPlugin().getItemDb().getItem(ALPHANUMERIC_REMOVER.matcher(lines[1]).replaceAll(""), quantity.intValue())) == null) {
          return;
        }
        Player player = event.getPlayer();
        String[] fakeLines = (String[])Arrays.copyOf(sign.getLines(), 4);
        if (((lines[0].contains("Sell")) && (lines[0].contains(ChatColor.RED.toString()))) || (lines[0].contains(ChatColor.AQUA.toString())))
        {
          int sellQuantity = Math.min(quantity.intValue(), InventoryUtils.countAmount(player.getInventory(), stack1.getType(), stack1.getDurability()));
          if (sellQuantity <= 0)
          {
            fakeLines[0] = (ChatColor.RED + "Not carrying any");
            fakeLines[2] = (ChatColor.RED + "on you.");
            fakeLines[3] = "";
          }
          else
          {
            int newPrice = price.intValue() / quantity.intValue() * sellQuantity;
            fakeLines[0] = (ChatColor.GREEN + "Sold " + sellQuantity);
            fakeLines[3] = (ChatColor.GREEN + "for " + '$' + newPrice);
            this.plugin.getEconomyManager().addBalance(player.getUniqueId(), newPrice);
            InventoryUtils.removeItem(player.getInventory(), stack1.getType(), (short)stack1.getData().getData(), sellQuantity);
            player.updateInventory();
          }
        }
        else
        {
          if ((!lines[0].contains("Buy")) || (!lines[0].contains(ChatColor.GREEN.toString()))) {
            return;
          }
          if (price.intValue() > this.plugin.getEconomyManager().getBalance(player.getUniqueId()))
          {
            fakeLines[0] = (ChatColor.RED + "Cannot afford");
          }
          else
          {
            fakeLines[0] = (ChatColor.GREEN + "Item bought");
            fakeLines[3] = (ChatColor.GREEN + "for " + '$' + price);
            this.plugin.getEconomyManager().subtractBalance(player.getUniqueId(), price.intValue());
            World world = player.getWorld();
            Location location = player.getLocation();
            Map<Integer, ItemStack> excess = player.getInventory().addItem(new ItemStack[] { stack1 });
            for (Map.Entry<Integer, ItemStack> excessItemStack : excess.entrySet()) {
              world.dropItemNaturally(location, (ItemStack)excessItemStack.getValue());
            }
            player.setItemInHand(player.getItemInHand());
          }
        }
        event.setCancelled(true);
        BasePlugin.getPlugin().getSignHandler().showLines(player, sign, fakeLines, 100L, true);
      }
    }
  }
}
