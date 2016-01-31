package io.ohho.hcf.deathban;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import org.bukkit.entity.Player;

import com.exodon.hcf.deathban.Deathban;

public abstract interface DeathbanManager
{
  public static final long MAX_DEATHBAN_TIME = TimeUnit.HOURS.toMillis(8L);
  
  public abstract TObjectIntMap<UUID> getLivesMap();
  
  public abstract int getLives(UUID paramUUID);
  
  public abstract int setLives(UUID paramUUID, int paramInt);
  
  public abstract int addLives(UUID paramUUID, int paramInt);
  
  public abstract int takeLives(UUID paramUUID, int paramInt);
  
  public abstract double getDeathBanMultiplier(Player paramPlayer);
  
  public abstract Deathban applyDeathBan(Player paramPlayer, String paramString);
  
  public abstract Deathban applyDeathBan(UUID paramUUID, Deathban paramDeathban);
  
  public abstract void reloadDeathbanData();
  
  public abstract void saveDeathbanData();
}
