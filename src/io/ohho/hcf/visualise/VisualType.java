package io.ohho.hcf.visualise;

import io.ohho.hcf.HCF;
import io.ohho.hcf.faction.struct.Relation;
import io.ohho.hcf.faction.type.Faction;

import java.util.ArrayList;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import io.ohho.hcf.visualise.BlockFiller;
import io.ohho.oCore.faction.FactionManager;

public enum VisualType
{
  SPAWN_BORDER,  CLAIM_BORDER,  SUBCLAIM_MAP,  CLAIM_MAP,  CREATE_CLAIM_SELECTION;
  
  private VisualType() {}
  
  abstract BlockFiller blockFiller();
}
