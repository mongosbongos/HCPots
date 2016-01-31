package io.ohho.hcfold.crate.type;

import com.parapvp.util.ItemBuilder;

import io.ohho.hcf.ConfigurationService;
import io.ohho.hcf.api.Crowbar;
import io.ohho.hcfold.crate.EnderChestKey;

import java.util.Map;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ExoKey
  extends EnderChestKey
{
  public ExoKey()
  {
    super("HCPots", 4);
    setupRarity(new ItemStack(Material.ENDER_PEARL, 8), 10);
    setupRarity(new ItemStack(Material.GOLDEN_APPLE, 1, (short)1), 4);
    setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.LOOT_BONUS_BLOCKS, 1).build(), 6);
    setupRarity(new ItemBuilder(Material.DIAMOND_AXE).enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.LOOT_BONUS_BLOCKS, 1).build(), 5);
    setupRarity(new ItemBuilder(Material.DIAMOND_SPADE).enchant(Enchantment.DIG_SPEED, 3).build(), 3);
    setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).intValue()).build(), 8);
    setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 3).build(), 3);
    setupRarity(new ItemStack(Material.ENDER_PORTAL_FRAME, 1), 3);
    setupRarity(new ItemStack(Material.EXP_BOTTLE, 24), 7);
    setupRarity(new Crowbar().getItemIfPresent(), 1);
    setupRarity(new ItemStack(Material.BEACON, 1), 1);
    setupRarity(new ItemBuilder(Material.MOB_SPAWNER).displayName(ChatColor.GREEN + "Spawner").loreLine(ChatColor.WHITE + WordUtils.capitalizeFully(EntityType.PIG.name())).build(), 3);
    setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 3), 6);
    setupRarity(new ItemStack(Material.GOLD_BLOCK, 4), 5);
    setupRarity(new ItemStack(Material.IRON_BLOCK, 2), 5);
    setupRarity(new ItemStack(Material.CHAINMAIL_HELMET), 4);
    setupRarity(new ItemStack(Material.CHAINMAIL_CHESTPLATE), 1);
    setupRarity(new ItemStack(Material.CHAINMAIL_LEGGINGS), 2);
    setupRarity(new ItemStack(Material.CHAINMAIL_BOOTS), 3);
    setupRarity(new ItemBuilder(Material.CHAINMAIL_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).build(), 2);
    setupRarity(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).build(), 1);
    setupRarity(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).build(), 1);
    setupRarity(new ItemBuilder(Material.CHAINMAIL_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ((Integer)ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).intValue()).build(), 2);
    setupRarity(new ItemBuilder(Material.SKULL_ITEM).data((short)1).build(), 3);
    setupRarity(new ItemBuilder(Material.GHAST_TEAR).build(), 3);
    setupRarity(new ItemStack(Material.QUARTZ, 3), 3);
    setupRarity(new ItemStack(Material.MAGMA_CREAM, 5), 3);
    setupRarity(new ItemBuilder(Material.MONSTER_EGG).data((short)92).build(), 3);
    setupRarity(new ItemBuilder(Material.POTION).data((short)16421).build(), 1);
  }
  
  public ChatColor getColour()
  {
    return ChatColor.LIGHT_PURPLE;
  }
  
  public boolean getBroadcastItems()
  {
    return false;
  }
}
