package io.ohho.hcfold.crate;

import com.parapvp.util.command.ArgumentExecutor;

import io.ohho.hcf.HCF;
import io.ohho.hcfold.crate.argument.LootBankArgument;
import io.ohho.hcfold.crate.argument.LootBroadcastsArgument;
import io.ohho.hcfold.crate.argument.LootDepositArgument;
import io.ohho.hcfold.crate.argument.LootGiveArgument;
import io.ohho.hcfold.crate.argument.LootListArgument;
import io.ohho.hcfold.crate.argument.LootWithdrawArgument;

public class LootExecutor
  extends ArgumentExecutor
{
  public LootExecutor(HCF plugin)
  {
    super("loot");
    addArgument(new LootBankArgument(plugin));
    addArgument(new LootBroadcastsArgument());
    addArgument(new LootDepositArgument(plugin));
    addArgument(new LootGiveArgument(plugin));
    addArgument(new LootListArgument(plugin));
    addArgument(new LootWithdrawArgument(plugin));
  }
}
