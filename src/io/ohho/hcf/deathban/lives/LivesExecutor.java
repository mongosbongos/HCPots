package io.ohho.hcf.deathban.lives;

import com.parapvp.util.command.ArgumentExecutor;

import io.ohho.hcf.HCF;
import io.ohho.hcf.deathban.lives.argument.LivesCheckArgument;
import io.ohho.hcf.deathban.lives.argument.LivesCheckDeathbanArgument;
import io.ohho.hcf.deathban.lives.argument.LivesClearDeathbansArgument;
import io.ohho.hcf.deathban.lives.argument.LivesGiveArgument;
import io.ohho.hcf.deathban.lives.argument.LivesReviveArgument;
import io.ohho.hcf.deathban.lives.argument.LivesSetArgument;
import io.ohho.hcf.deathban.lives.argument.LivesSetDeathbanTimeArgument;

public class LivesExecutor
  extends ArgumentExecutor
{
  public LivesExecutor(HCF plugin)
  {
    super("lives");
    addArgument(new LivesCheckArgument(plugin));
    addArgument(new LivesCheckDeathbanArgument(plugin));
    addArgument(new LivesClearDeathbansArgument(plugin));
    addArgument(new LivesGiveArgument(plugin));
    addArgument(new LivesReviveArgument(plugin));
    addArgument(new LivesSetArgument(plugin));
    addArgument(new LivesSetDeathbanTimeArgument());
  }
}
