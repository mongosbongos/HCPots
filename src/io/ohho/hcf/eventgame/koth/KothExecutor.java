package io.ohho.hcf.eventgame.koth;

import com.parapvp.util.command.ArgumentExecutor;

import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.koth.argument.KothHelpArgument;
import io.ohho.hcf.eventgame.koth.argument.KothNextArgument;
import io.ohho.hcf.eventgame.koth.argument.KothScheduleArgument;
import com.exodon.hcf.eventgame.koth.argument.KothSetCapDelayArgument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KothExecutor
  extends ArgumentExecutor
{
  private final KothScheduleArgument kothScheduleArgument;
  
  public KothExecutor(HCF plugin)
  {
    super("koth");
    addArgument(new KothHelpArgument(this));
    addArgument(new KothNextArgument(plugin));
    addArgument(this.kothScheduleArgument = new KothScheduleArgument(plugin));
    addArgument(new KothSetCapDelayArgument(plugin));
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 1)
    {
      this.kothScheduleArgument.onCommand(sender, command, label, args);
      return true;
    }
    return super.onCommand(sender, command, label, args);
  }
}
