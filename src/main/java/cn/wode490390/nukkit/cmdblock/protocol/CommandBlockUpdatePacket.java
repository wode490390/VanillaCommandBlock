package cn.wode490390.nukkit.cmdblock.protocol;

public class CommandBlockUpdatePacket extends cn.nukkit.network.protocol.CommandBlockUpdatePacket {

    public int tickDelay;
    public boolean executingOnFirstTick;

    @Override
    public void decode() {
        super.decode();
        this.tickDelay = this.getLInt();
        this.executingOnFirstTick = this.getBoolean();
    }

    @Override
    public void encode() {

    }
}
