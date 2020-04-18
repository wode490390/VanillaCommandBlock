package cn.wode490390.nukkit.cmdblock.block;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.wode490390.nukkit.cmdblock.blockentity.BlockEntityCommandBlock;

public class BlockCommandBlockChain extends BlockCommandBlock {

    public BlockCommandBlockChain() {
        this(0);
    }

    public BlockCommandBlockChain(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockId.CHAIN_COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return "Chain Command Block";
    }

    @Override
    protected CompoundTag createCompoundTag(CompoundTag nbt) {
        nbt.putBoolean(BlockEntityCommandBlock.TAG_AUTO, true);
        return nbt;
    }
}
