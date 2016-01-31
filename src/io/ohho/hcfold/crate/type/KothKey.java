package io.ohho.hcfold.crate.type;

import com.parapvp.util.ItemBuilder;

import io.ohho.hcf.ConfigurationService;
import io.ohho.hcf.api.Crowbar;
import io.ohho.hcfold.crate.EnderChestKey;

import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class KothKey
  extends EnderChestKey
{
  public KothKey()
  {
    super("Koth", 6);
    setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.FIRE_ASPECT, 2).enchant(Enchantment.DAMAGE_ALL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).intValue()).displayName(ChatColor.RED + "KOTH Fire").build(), 3);
    setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 16), 15);
    setupRarity(new ItemStack(Material.GOLD_BLOCK, 16), 15);
    setupRarity(new ItemStack(Material.IRON_BLOCK, 16), 15);
    setupRarity(new ItemBuilder(Material.CHAINMAIL_HELMET).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "Reaper Helmet").build(), 1);
    setupRarity(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "Reaper Chestplate").build(), 1);
    setupRarity(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "Reaper Leggings").build(), 1);
    setupRarity(new ItemBuilder(Material.CHAINMAIL_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "Reaper Boots").build(), 1);
    setupRarity(new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "Bard Helmet").build(), 1);
    setupRarity(new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "Bard Chestplate").build(), 1);
    setupRarity(new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "Bard Leggings").build(), 1);
    setupRarity(new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "Bard Boots").build(), 1);
    setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.LOOT_BONUS_MOBS, 5).displayName(ChatColor.RED + "KOTH Looting").build(), 7);
    setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 4).displayName(ChatColor.RED + "KOTH Fortune").build(), 5);
    setupRarity(new ItemBuilder(Material.SKULL_ITEM, 2).data((short)1).build(), 6);
    setupRarity(new ItemStack(Material.BEACON), 2);
    setupRarity(new ItemStack(Material.NETHER_STAR), 3);
    setupRarity(new Crowbar().getItemIfPresent(), 5);
    setupRarity(new ItemBuilder(Material.GOLDEN_APPLE).data((short)1).build(), 3);
    setupRarity(new ItemBuilder(Material.GOLDEN_APPLE, 3).data((short)1).build(), 3);
    setupRarity(new ItemBuilder(Material.GOLDEN_APPLE, 5).data((short)1).build(), 1);
    setupRarity(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.ARROW_DAMAGE)).intValue()).enchant(Enchantment.ARROW_FIRE, 1).enchant(Enchantment.ARROW_INFINITE, 1).displayName(ChatColor.RED + "KOTH Bow").build(), 3);
    setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "KOTH Helmet").build(), 1);
    setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "KOTH Chestplate").build(), 1);
    setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "KOTH Leggings").build(), 1);
    setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).displayName(ChatColor.RED + "KOTH Boots").build(), 1);
  }
  
  public ChatColor getColour()
  {
    return ChatColor.YELLOW;
  }
  
  public boolean getBroadcastItems()
  {
    return true;
  }
}
