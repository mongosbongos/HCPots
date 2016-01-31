package io.ohho.hcfold.crate.type;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.ohho.hcfold.crate.EnderChestKey;

public class LootKey
  extends EnderChestKey
{
  public LootKey()
  {
    super("Loot", 3);
    setupRarity(new ItemStack(Material.SPECKLED_MELON, 10), 10);
    setupRarity(new ItemStack(Material.SULPHUR, 6), 10);
    setupRarity(new ItemStack(Material.BLAZE_ROD, 4), 7);
    setupRarity(new ItemStack(Material.SUGAR, 6), 10);
    setupRarity(new ItemStack(Material.SPIDER_EYE, 4), 10);
    setupRarity(new ItemStack(Material.GLOWSTONE_DUST, 8), 10);
    setupRarity(new ItemStack(Material.GLASS_BOTTLE, 16), 15);
    setupRarity(new ItemStack(Material.ENDER_PEARL, 1), 5);
    setupRarity(new ItemStack(Material.POTATO, 4), 3);
    setupRarity(new ItemStack(Material.COOKED_BEEF, 8), 16);
    setupRarity(new ItemStack(Material.GOLDEN_APPLE, 1, (short)1), 1);
  }
  
  public ChatColor getColour()
  {
    return ChatColor.BLUE;
  }
}
