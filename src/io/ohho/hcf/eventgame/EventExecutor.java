package io.ohho.hcf.eventgame;

import com.exodon.hcf.eventgame.argument.EventDeleteArgument;
import com.exodon.hcf.eventgame.argument.EventRenameArgument;
import com.exodon.hcf.eventgame.argument.EventSetAreaArgument;
import com.exodon.hcf.eventgame.argument.EventSetCapzoneArgument;
import com.exodon.hcf.eventgame.argument.EventStartArgument;
import com.parapvp.util.command.ArgumentExecutor;

import io.ohho.hcf.HCF;
import io.ohho.hcf.eventgame.argument.EventCancelArgument;
import io.ohho.hcf.eventgame.argument.EventCreateArgument;
import io.ohho.hcf.eventgame.argument.EventUptimeArgument;

public class EventExecutor
  extends ArgumentExecutor
{
  public EventExecutor(com.exodon.hcf.HCF plugin)
  {
    super("event");
    addArgument(new EventCancelArgument(plugin));
    addArgument(new EventCreateArgument(plugin));
    addArgument(new EventDeleteArgument(plugin));
    addArgument(new EventRenameArgument(plugin));
    addArgument(new EventSetAreaArgument(plugin));
    addArgument(new EventSetCapzoneArgument(plugin));
    addArgument(new EventStartArgument(plugin));
    addArgument(new EventUptimeArgument(plugin));
  }
}
