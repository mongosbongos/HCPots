package io.ohho.hcf.eventgame.conquest;

import com.exodon.hcf.eventgame.conquest.ConquestSetpointsArgument;
import com.parapvp.util.command.ArgumentExecutor;

import io.ohho.hcf.HCF;

public class ConquestExecutor
  extends ArgumentExecutor
{
  public ConquestExecutor(HCF plugin)
  {
    super("conquest");
    addArgument(new ConquestSetpointsArgument(plugin));
  }
}
