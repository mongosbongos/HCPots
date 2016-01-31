package io.ohho.hcfold.crate.type;

import com.parapvp.util.ItemBuilder;

import io.ohho.hcf.ConfigurationService;
import io.ohho.hcfold.crate.EnderChestKey;

import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class WinterKey
  extends EnderChestKey
{
  public WinterKey()
  {
    super("Winter", 4);
    setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).enchant(Enchantment.DURABILITY, 3).build(), 10);
    setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).enchant(Enchantment.DURABILITY, 3).build(), 10);
    setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).enchant(Enchantment.DURABILITY, 3).build(), 10);
    setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).build(), 10);
    setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.LOOT_BONUS_MOBS, 3).enchant(Enchantment.DAMAGE_ALL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).intValue()).build(), 10);
    setupRarity(new ItemBuilder(Material.DIAMOND_BLOCK, 16).build(), 10);
    setupRarity(new ItemBuilder(Material.GOLD_BLOCK, 16).build(), 10);
    setupRarity(new ItemBuilder(Material.IRON_BLOCK, 16).build(), 10);
    setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 5).enchant(Enchantment.LOOT_BONUS_BLOCKS, 3).enchant(Enchantment.DURABILITY, 3).build(), 10);
    setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 5).enchant(Enchantment.SILK_TOUCH, 1).enchant(Enchantment.DURABILITY, 3).build(), 10);
  }
  
  public ChatColor getColour()
  {
    return ChatColor.DARK_AQUA;
  }
}
