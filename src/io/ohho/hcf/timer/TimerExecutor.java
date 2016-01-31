package io.ohho.hcf.timer;

import com.parapvp.util.command.ArgumentExecutor;

import io.ohho.hcf.HCF;
import io.ohho.hcf.timer.argument.TimerCheckArgument;
import io.ohho.hcf.timer.argument.TimerSetArgument;

public class TimerExecutor
  extends ArgumentExecutor
{
  public TimerExecutor(HCF plugin)
  {
    super("timer");
    addArgument(new TimerCheckArgument(plugin));
    addArgument(new TimerSetArgument(plugin));
  }
}
