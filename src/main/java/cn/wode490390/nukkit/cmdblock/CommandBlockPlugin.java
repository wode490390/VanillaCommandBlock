package cn.wode490390.nukkit.cmdblock;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.PluginBase;
import cn.wode490390.nukkit.cmdblock.block.*;
import cn.wode490390.nukkit.cmdblock.blockentity.*;
import cn.wode490390.nukkit.cmdblock.protocol.CommandBlockUpdatePacket;
import cn.wode490390.nukkit.cmdblock.util.MetricsLite;

import java.lang.reflect.Constructor;

public class CommandBlockPlugin extends PluginBase implements Listener {

    private static CommandBlockPlugin INSTANCE;

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        try {
            new MetricsLite(this, 6923);
        } catch (Throwable ignore) {

        }

        this.getServer().getNetwork().registerPacket(ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET, CommandBlockUpdatePacket.class);
        this.registerBlock(BlockId.COMMAND_BLOCK, BlockCommandBlock.class);
        this.registerBlock(BlockId.CHAIN_COMMAND_BLOCK, BlockCommandBlockChain.class);
        this.registerBlock(BlockId.REPEATING_COMMAND_BLOCK, BlockCommandBlockRepeating.class);
        BlockEntity.registerBlockEntity(BlockEntityId.COMMAND_BLOCK, BlockEntityCommandBlock.class);
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    private void registerBlock(int id, Class<? extends Block> clazz) {
        Item.list[id] = clazz;
        Block.list[id] = clazz;
        Block block;

        try {
            block = clazz.newInstance();
            try {
                Constructor<? extends Block> constructor = clazz.getDeclaredConstructor(int.class);
                constructor.setAccessible(true);
                for (int data = 0; data < 16; ++data) {
                    Block.fullList[(id << 4) | data] = constructor.newInstance(data);
                }
                Block.hasMeta[id] = true;
            } catch (NoSuchMethodException ignore) {
                for (int data = 0; data < 16; ++data) {
                    Block.fullList[(id << 4) | data] = block;
                }
            }
        } catch (Exception e) {
            this.getLogger().alert("Error while registering " + clazz.getName(), e);
            for (int data = 0; data < 16; ++data) {
                Block.fullList[(id << 4) | data] = new BlockUnknown(id, data);
            }
            return;
        }

        Block.solid[id] = block.isSolid();
        Block.transparent[id] = block.isTransparent();
        Block.hardness[id] = block.getHardness();
        Block.light[id] = block.getLightLevel();

        if (block.isSolid()) {
            if (block.isTransparent()) {
                Block.lightFilter[id] = 1;
            } else {
                Block.lightFilter[id] = 15;
            }
        } else {
            Block.lightFilter[id] = 1;
        }
    }

    @EventHandler
    public void onDataPacketReceive(DataPacketReceiveEvent event) {
        DataPacket packet = event.getPacket();
        if (packet instanceof CommandBlockUpdatePacket) {
            CommandBlockUpdatePacket pk = (CommandBlockUpdatePacket) packet;
            Player player = event.getPlayer();
            if (player.isOp() && player.isCreative()) {
                if (pk.isBlock) {
                    BlockEntity blockEntity = player.level.getBlockEntity(new Vector3(pk.x, pk.y, pk.z));
                    if (blockEntity instanceof BlockEntityCommandBlock) {
                        BlockEntityCommandBlock commandBlock = (BlockEntityCommandBlock) blockEntity;
                        Block block = commandBlock.getLevelBlock();

                        switch (pk.commandBlockMode) {
                            case ICommandBlock.MODE_REPEATING:
                                if (block.getId() != BlockId.REPEATING_COMMAND_BLOCK) {
                                    block = Block.get(BlockId.REPEATING_COMMAND_BLOCK, block.getDamage());
                                    commandBlock.scheduleUpdate();
                                }
                                break;
                            case ICommandBlock.MODE_CHAIN:
                                if (block.getId() != BlockId.CHAIN_COMMAND_BLOCK) {
                                    block = Block.get(BlockId.CHAIN_COMMAND_BLOCK, block.getDamage());
                                }
                                break;
                            case ICommandBlock.MODE_NORMAL:
                            default:
                                if (block.getId() != BlockId.COMMAND_BLOCK) {
                                    block = Block.get(BlockId.COMMAND_BLOCK, block.getDamage());
                                }
                                break;
                        }

                        int meta = block.getDamage();
                        boolean conditional = pk.isConditional;

                        if (conditional) {
                            if (meta < 8) {
                                block.setDamage(meta + 8);
                            }
                        } else {
                            if (meta > 8) {
                                block.setDamage(meta - 8);
                            }
                        }

                        player.level.setBlock(commandBlock, block, true);

                        commandBlock.setCommand(pk.command);
                        commandBlock.setName(pk.name);
                        commandBlock.setTrackOutput(pk.shouldTrackOutput);
                        commandBlock.setConditional(conditional);
                        commandBlock.setTickDelay(pk.tickDelay);
                        commandBlock.setExecutingOnFirstTick(pk.executingOnFirstTick);

                        boolean isRedstoneMode = pk.isRedstoneMode;
                        commandBlock.setAuto(!isRedstoneMode);
                        if (!isRedstoneMode && pk.commandBlockMode == ICommandBlock.MODE_NORMAL) {
                            commandBlock.trigger();
                        }

//                        commandBlock.spawnToAll();
                    }
                }/* else {
                    Entity entity = this.getLevel().getEntity(commandBlockUpdatePacket.minecartEid);
                    if (entity instanceof EntityMinecartCommandBlock) {
                        EntityMinecartCommandBlock commandMinecart = (EntityMinecartCommandBlock) entity;
                        //TODO: Minecart with Command Block
                    }
                }*/
            }
        }
    }

    public static CommandBlockPlugin getInstance() {
        return INSTANCE;
    }
}
