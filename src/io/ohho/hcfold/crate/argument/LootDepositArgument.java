package io.ohho.hcfold.crate.argument;

import com.google.common.primitives.Ints;
import com.parapvp.util.command.CommandArgument;

import io.ohho.hcf.HCF;
import io.ohho.hcfold.crate.Key;
import io.ohho.hcfold.crate.KeyManager;

import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LootDepositArgument
  extends CommandArgument
{
  private final HCF plugin;
  
  public LootDepositArgument(HCF plugin)
  {
    super("deposit", "Deposits a crate key into your bank account");
    this.plugin = plugin;
    this.aliases = new String[] { "store" };
    this.permission = ("hcf.command.loot.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <amount>";
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
    Player player = (Player)sender;
    UUID uuid = player.getUniqueId();
    ItemStack stack = player.getItemInHand();
    Key key = this.plugin.getKeyManager().getKey(stack);
    if (key == null)
    {
      sender.sendMessage(ChatColor.RED + "You are not holding a crate key.");
      return true;
    }
    Integer quantity = Ints.tryParse(args[1]);
    if (quantity == null)
    {
      sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
      return true;
    }
    if (quantity.intValue() <= 0)
    {
      sender.sendMessage(ChatColor.RED + "You can only deposit crate keys in positive amounts.");
      return true;
    }
    String keyName = key.getName();
    if (quantity.intValue() > stack.getAmount())
    {
      sender.sendMessage(ChatColor.RED + "You tried to deposit " + quantity + ' ' + keyName + " keys, but you are only holding " + stack.getAmount() + '.');
      return true;
    }
    Map<String, Integer> crateKeyMap = this.plugin.getKeyManager().getDepositedCrateMap(uuid);
    int newAmount = ((Integer)crateKeyMap.getOrDefault(keyName, Integer.valueOf(0))).intValue() + quantity.intValue();
    crateKeyMap.put(keyName, Integer.valueOf(newAmount));
    if (quantity.intValue() == stack.getAmount()) {
      player.setItemInHand(new ItemStack(Material.AIR, 1));
    } else {
      stack.setAmount(stack.getAmount() - quantity.intValue());
    }
    sender.sendMessage(ChatColor.YELLOW + "Successfully deposited " + quantity + ' ' + keyName + " key ".toString() + (quantity.intValue() > 1 ? "s" : "") + ". You now have " + ChatColor.LIGHT_PURPLE + newAmount + ChatColor.YELLOW + " of these keys.");
    return true;
  }
}
