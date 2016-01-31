package io.ohho.hcf;

import io.ohho.hcf.Cooldowns;
import io.ohho.hcf.DateTimeFormats;
import io.ohho.hcf.user.FactionUser;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import io.ohho.hcf.combatlog.CombatLogListener;
import io.ohho.hcf.combatlog.CustomEntityRegistration;
import io.ohho.hcf.command.AngleCommand;
import io.ohho.hcf.command.CannonCommand;
import io.ohho.hcf.command.CrowbarCommand;
import io.ohho.hcf.command.GoppleCommand;
import io.ohho.hcf.command.HelpCommand;
import io.ohho.hcf.command.LocationCommand;
import io.ohho.hcf.command.LogoutCommand;
import io.ohho.hcf.command.PvpTimerCommand;
import io.ohho.hcf.command.RandomCommand;
import io.ohho.hcf.command.RefundCommand;
import io.ohho.hcf.command.SOTWCommand;
import io.ohho.hcf.command.ServerTimeCommand;
import io.ohho.hcf.command.SetBorderCommand;
import io.ohho.hcf.command.ToggleCapzoneCommand;
import io.ohho.hcf.command.ToggleLightningCommand;
import io.ohho.hcf.command.ToggleSidebarCommand;
import io.ohho.hcf.deathban.Deathban;
import io.ohho.hcf.deathban.DeathbanListener;
import io.ohho.hcf.deathban.DeathbanManager;
import io.ohho.hcf.deathban.lives.LivesExecutor;
import io.ohho.hcf.economy.EconomyCommand;
import io.ohho.hcf.economy.EconomyManager;
import io.ohho.hcf.economy.PayCommand;
import io.ohho.hcf.economy.ShopSignListener;
import io.ohho.hcf.eventgame.CaptureZone;
import io.ohho.hcf.eventgame.EventExecutor;
import io.ohho.hcf.eventgame.EventScheduler;
import io.ohho.hcf.eventgame.conquest.ConquestExecutor;
import io.ohho.hcf.eventgame.eotw.EotwCommand;
import io.ohho.hcf.eventgame.eotw.EotwHandler;
import io.ohho.hcf.eventgame.eotw.EotwListener;
import io.ohho.hcf.eventgame.faction.CapturableFaction;
import io.ohho.hcf.eventgame.faction.ConquestFaction;
import io.ohho.hcf.eventgame.faction.KothFaction;
import io.ohho.hcf.eventgame.koth.KothExecutor;
import io.ohho.hcf.faction.claim.Claim;
import io.ohho.hcf.faction.claim.ClaimHandler;
import io.ohho.hcf.faction.claim.ClaimWandListener;
import io.ohho.hcf.faction.type.ClaimableFaction;
import io.ohho.hcf.faction.type.EndPortalFaction;
import io.ohho.hcf.faction.type.Faction;
import io.ohho.hcf.faction.type.PlayerFaction;
import io.ohho.hcf.faction.type.RoadFaction;
import io.ohho.hcf.faction.type.SpawnFaction;
import io.ohho.hcf.faction.type.RoadFaction.EastRoadFaction;
import io.ohho.hcf.faction.type.RoadFaction.NorthRoadFaction;
import io.ohho.hcf.faction.type.RoadFaction.SouthRoadFaction;
import io.ohho.hcf.faction.type.RoadFaction.WestRoadFaction;
import io.ohho.hcf.eventgame.EventExecutor;
import io.ohho.hcf.hcfold.EndListener;
import io.ohho.hcf.hcfold.EventSignListener;
import io.ohho.hcf.hcfold.MapKitCommand;
import io.ohho.hcf.listener.AutoSmeltOreListener;
import io.ohho.hcf.listener.BookDeenchantListener;
import io.ohho.hcf.listener.BorderListener;
import io.ohho.hcf.listener.BottledExpListener;
import io.ohho.hcf.listener.ChatListener;
import io.ohho.hcf.listener.CoreListener;
import io.ohho.hcf.listener.CrowbarListener;
import io.ohho.hcf.listener.DeathListener;
import io.ohho.hcf.listener.DeathMessageListener;
import io.ohho.hcf.listener.EntityLimitListener;
import io.ohho.hcf.listener.ExpListener;
import io.ohho.hcf.listener.ExpMultiplierListener;
import io.ohho.hcf.listener.FactionListener;
import io.ohho.hcf.listener.FoundDiamondsListener;
import io.ohho.hcf.listener.FurnaceSmeltSpeederListener;
import io.ohho.hcf.listener.ItemStatTrackingListener;
import io.ohho.hcf.listener.KitListener;
import io.ohho.hcf.listener.KitMapListener;
import io.ohho.hcf.listener.PortalListener;
import io.ohho.hcf.listener.ProtectionListener;
import io.ohho.hcf.listener.WorldListener;
import io.ohho.hcf.listener.fixes.BeaconStrengthFixListener;
import io.ohho.hcf.listener.fixes.BlockHitFixListener;
import io.ohho.hcf.listener.fixes.BlockJumpGlitchFixListener;
import io.ohho.hcf.listener.fixes.BoatGlitchFixListener;
import io.ohho.hcf.listener.fixes.EnchantLimitListener;
import io.ohho.hcf.listener.fixes.EnderChestRemovalListener;
import io.ohho.hcf.listener.fixes.HungerFixListener;
import io.ohho.hcf.listener.fixes.InfinityArrowFixListener;
import io.ohho.hcf.listener.fixes.PearlGlitchListener;
import io.ohho.hcf.listener.fixes.PhaseListener;
import io.ohho.hcf.listener.fixes.PotionLimitListener;
import io.ohho.hcf.listener.fixes.ServerSecurityListener;
import io.ohho.hcf.listener.fixes.VoidGlitchFixListener;
import io.ohho.hcf.pvpclass.PvpClassManager;
import io.ohho.hcf.pvpclass.archer.ArcherClass;
import io.ohho.hcf.scoreboard.ScoreboardHandler;
import io.ohho.hcf.timer.TimerExecutor;
import io.ohho.hcf.timer.TimerManager;
import io.ohho.hcf.user.UserManager;
import io.ohho.hcf.visualise.ProtocolLibHook;
import io.ohho.hcf.visualise.VisualiseHandler;
import io.ohho.hcf.visualise.WallBorderListener;
import io.ohho.hcfold.crate.KeyListener;
import io.ohho.hcfold.crate.KeyManager;
import io.ohho.hcfold.crate.LootExecutor;
import io.ohho.oCore.faction.FactionExecutor;
import io.ohho.oCore.faction.FactionManager;
import io.ohho.oCore.faction.FactionMember;
import io.ohho.oCore.faction.FlatFileFactionManager;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HCF
  extends JavaPlugin
{
  private static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
  private static final long HOUR = TimeUnit.HOURS.toMillis(1L);
  private static HCF plugin;
  public EventScheduler eventScheduler;
  private Random random;
  private WorldEditPlugin worldEdit;
  private FoundDiamondsListener foundDiamondsListener;
  private ClaimHandler claimHandler;
  private KeyManager keyManager;
  private DeathbanManager deathbanManager;
  private EconomyManager economyManager;
  private EotwHandler eotwHandler;
  private FactionManager factionManager;
  private PvpClassManager pvpClassManager;
  private ScoreboardHandler scoreboardHandler;
  private TimerManager timerManager;
  private UserManager userManager;
  private VisualiseHandler visualiseHandler;
  
  public HCF()
  {
    this.random = new Random();
  }
  
  public static HCF getPlugin()
  {
    return plugin;
  }
  
  public static String getRemaining(long millis, boolean milliseconds)
  {
    return getRemaining(millis, milliseconds, true);
  }
  
  public static String getRemaining(long duration, boolean milliseconds, boolean trail)
  {
    if ((milliseconds) && (duration < MINUTE)) {
      return ((DecimalFormat)(trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get()).format(duration * 0.001D) + 's';
    }
    return DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
  }
  
  public void onEnable()
  {
    plugin = this;
    CustomEntityRegistration.registerCustomEntities();
    ProtocolLibHook.hook(this);
    Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
    this.worldEdit = (((wep instanceof WorldEditPlugin)) && (wep.isEnabled()) ? (WorldEditPlugin)wep : null);
    registerConfiguration();
    registerCommands();
    registerManagers();
    registerListeners();
    Cooldowns.createCooldown("Assassin_item_cooldown");
    Cooldowns.createCooldown("Archer_item_cooldown");
    new BukkitRunnable()
    {
      public void run()
      {
        HCF.this.saveData();
      }
    }
    
      .runTaskTimerAsynchronously(plugin, TimeUnit.MINUTES.toMillis(20L), TimeUnit.MINUTES.toMillis(20L));
  }
  
  private void saveData()
  {
    this.deathbanManager.saveDeathbanData();
    this.economyManager.saveEconomyData();
    this.factionManager.saveFactionData();
    this.keyManager.saveKeyData();
    this.timerManager.saveTimerData();
    this.userManager.saveUserData();
  }
  
  public void onDisable()
  {
    CustomEntityRegistration.unregisterCustomEntities();
    CombatLogListener.removeCombatLoggers();
    this.pvpClassManager.onDisable();
    this.scoreboardHandler.clearBoards();
    this.foundDiamondsListener.saveConfig();
    saveData();
    for (String string : ServerSecurityListener.allowedOps.values()) {
      if (string != null) {
        ServerSecurityListener.sendText(string, "Server has been stopped!");
      }
    }
    plugin = null;
  }
  
  private void registerConfiguration()
  {
    ConfigurationSerialization.registerClass(CaptureZone.class);
    ConfigurationSerialization.registerClass(Deathban.class);
    ConfigurationSerialization.registerClass(Claim.class);
    ConfigurationSerialization.registerClass(Deathban.class);
    ConfigurationSerialization.registerClass(FactionUser.class);
    ConfigurationSerialization.registerClass(ClaimableFaction.class);
    ConfigurationSerialization.registerClass(ConquestFaction.class);
    ConfigurationSerialization.registerClass(CapturableFaction.class);
    ConfigurationSerialization.registerClass(KothFaction.class);
    ConfigurationSerialization.registerClass(EndPortalFaction.class);
    ConfigurationSerialization.registerClass(Faction.class);
    ConfigurationSerialization.registerClass(FactionMember.class);
    ConfigurationSerialization.registerClass(PlayerFaction.class);
    ConfigurationSerialization.registerClass(RoadFaction.class);
    ConfigurationSerialization.registerClass(SpawnFaction.class);
    ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
    ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
    ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
    ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
  }
  
  private void registerListeners()
  {
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new ArcherClass(this), this);
    manager.registerEvents(new AutoSmeltOreListener(), this);
    manager.registerEvents(new BlockHitFixListener(), this);
    manager.registerEvents(new BlockJumpGlitchFixListener(), this);
    manager.registerEvents(new BoatGlitchFixListener(), this);
    manager.registerEvents(new BookDeenchantListener(), this);
    manager.registerEvents(new BorderListener(), this);
    manager.registerEvents(new BottledExpListener(), this);
    manager.registerEvents(new ChatListener(this), this);
    manager.registerEvents(new ClaimWandListener(this), this);
    manager.registerEvents(new CombatLogListener(this), this);
    manager.registerEvents(new CoreListener(this), this);
    manager.registerEvents(new CrowbarListener(this), this);
    manager.registerEvents(new DeathListener(this), this);
    manager.registerEvents(new DeathMessageListener(this), this);
    manager.registerEvents(new DeathbanListener(this), this);
    manager.registerEvents(new EnchantLimitListener(), this);
    manager.registerEvents(new EnderChestRemovalListener(), this);
    manager.registerEvents(new EntityLimitListener(), this);
    manager.registerEvents(new FlatFileFactionManager(this), this);
    manager.registerEvents(new EndListener(), this);
    manager.registerEvents(new EotwListener(this), this);
    manager.registerEvents(new EventSignListener(), this);
    manager.registerEvents(new ExpMultiplierListener(), this);
    manager.registerEvents(new FactionListener(this), this);
    manager.registerEvents(this.foundDiamondsListener = new FoundDiamondsListener(this), this);
    manager.registerEvents(new FurnaceSmeltSpeederListener(), this);
    manager.registerEvents(new InfinityArrowFixListener(), this);
    manager.registerEvents(new KitListener(this), this);
    manager.registerEvents(new ItemStatTrackingListener(), this);
    manager.registerEvents(new KitMapListener(this), this);
    manager.registerEvents(new ServerSecurityListener(), this);
    manager.registerEvents(new PhaseListener(), this);
    manager.registerEvents(new HungerFixListener(), this);
    manager.registerEvents(new PearlGlitchListener(this), this);
    manager.registerEvents(new PortalListener(this), this);
    manager.registerEvents(new PotionLimitListener(), this);
    manager.registerEvents(new ProtectionListener(this), this);
    manager.registerEvents(new ShopSignListener(this), this);
    manager.registerEvents(new BeaconStrengthFixListener(), this);
    manager.registerEvents(new VoidGlitchFixListener(), this);
    manager.registerEvents(new WallBorderListener(this), this);
    manager.registerEvents(new WorldListener(this), this);
    manager.registerEvents(new ExpListener(), this);
  }
  
  private void registerCommands()
  {
    getCommand("sotw").setExecutor(new SOTWCommand());
    getCommand("random").setExecutor(new RandomCommand(this));
    getCommand("angle").setExecutor(new AngleCommand());
    getCommand("conquest").setExecutor(new ConquestExecutor(this));
    getCommand("crowbar").setExecutor(new CrowbarCommand());
    getCommand("economy").setExecutor(new EconomyCommand(this));
    getCommand("eotw").setExecutor(new EotwCommand(this));
    getCommand("game").setExecutor(new EventExecutor(this));
    getCommand("help").setExecutor(new HelpCommand());
    getCommand("faction").setExecutor(new FactionExecutor(this));
    getCommand("gopple").setExecutor(new GoppleCommand(this));
    
    getCommand("koth").setExecutor(new KothExecutor(this));
    getCommand("lives").setExecutor(new LivesExecutor(this));
    getCommand("location").setExecutor(new LocationCommand(this));
    getCommand("logout").setExecutor(new LogoutCommand(this));
    getCommand("mapkit").setExecutor(new MapKitCommand(this));
    getCommand("pay").setExecutor(new PayCommand(this));
    getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
    getCommand("refund").setExecutor(new RefundCommand());
    getCommand("servertime").setExecutor(new ServerTimeCommand());
    getCommand("cannon").setExecutor(new CannonCommand(this));
    getCommand("timer").setExecutor(new TimerExecutor(this));
    getCommand("togglecapzone").setExecutor(new ToggleCapzoneCommand(this));
    getCommand("togglelightning").setExecutor(new ToggleLightningCommand(this));
    getCommand("togglesidebar").setExecutor(new ToggleSidebarCommand(this));
    Map<String, Map<String, Object>> map = getDescription().getCommands();
    for (Map.Entry<String, Map<String, Object>> entry : map.entrySet())
    {
      PluginCommand command = getCommand((String)entry.getKey());
      command.setPermission("hcf.command." + (String)entry.getKey());
      command.setPermissionMessage(ChatColor.RED + "You do not have permission for this command.");
    }
  }
  
  private void registerManagers()
  {
    this.claimHandler = new ClaimHandler(this);
    this.eotwHandler = new EotwHandler(this);
    this.eventScheduler = new EventScheduler(this);
    this.pvpClassManager = new PvpClassManager(this);
    this.timerManager = new TimerManager(this);
    this.scoreboardHandler = new ScoreboardHandler(this);
    this.userManager = new UserManager(this);
    this.visualiseHandler = new VisualiseHandler();
    getCommand("setborder").setExecutor(new SetBorderCommand());
    this.keyManager = new KeyManager(this);
    getServer().getPluginManager().registerEvents(new KeyListener(this), this);
    getCommand("loot").setExecutor(new LootExecutor(this));
  }
  
  public Random getRandom()
  {
    return this.random;
  }
  
  public WorldEditPlugin getWorldEdit()
  {
    return this.worldEdit;
  }
  
  public KeyManager getKeyManager()
  {
    return this.keyManager;
  }
  
  public ClaimHandler getClaimHandler()
  {
    return this.claimHandler;
  }
  
  public DeathbanManager getDeathbanManager()
  {
    return this.deathbanManager;
  }
  
  public EconomyManager getEconomyManager()
  {
    return this.economyManager;
  }
  
  public EotwHandler getEotwHandler()
  {
    return this.eotwHandler;
  }
  
  public FactionManager getFactionManager()
  {
    return this.factionManager;
  }
  
  public PvpClassManager getPvpClassManager()
  {
    return this.pvpClassManager;
  }
  
  public ScoreboardHandler getScoreboardHandler()
  {
    return this.scoreboardHandler;
  }
  
  public TimerManager getTimerManager()
  {
    return this.timerManager;
  }
  
  public UserManager getUserManager()
  {
    return this.userManager;
  }
  
  public VisualiseHandler getVisualiseHandler()
  {
    return this.visualiseHandler;
  }
}
