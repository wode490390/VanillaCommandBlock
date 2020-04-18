package cn.wode490390.nukkit.cmdblock.scheduler;

import cn.nukkit.scheduler.PluginTask;
import cn.wode490390.nukkit.cmdblock.CommandBlockPlugin;
import cn.wode490390.nukkit.cmdblock.ICommandBlock;

public class CommandBlockTrigger extends PluginTask<CommandBlockPlugin> {

    private final ICommandBlock commandBlock;
    private final int chain;

    public CommandBlockTrigger(ICommandBlock commandBlock, int chain) {
        super(CommandBlockPlugin.getInstance());
        this.commandBlock = commandBlock;
        this.chain = chain;
    }

    @Override
    public void onRun(int currentTick) {
        this.commandBlock.execute(this.chain);
    }
}
