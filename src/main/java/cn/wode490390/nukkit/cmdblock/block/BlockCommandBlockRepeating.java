package cn.wode490390.nukkit.cmdblock.block;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.wode490390.nukkit.cmdblock.blockentity.BlockEntityCommandBlock;

public class BlockCommandBlockRepeating extends BlockCommandBlock {

    public BlockCommandBlockRepeating() {
        this(0);
    }

    public BlockCommandBlockRepeating(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockId.REPEATING_COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return "Repeating Command Block";
    }

    @Override
    protected CompoundTag createCompoundTag(CompoundTag nbt) {
        nbt.putBoolean(BlockEntityCommandBlock.TAG_EXECUTE_ON_FIRST_TICK, true);
        return nbt;
    }
}
