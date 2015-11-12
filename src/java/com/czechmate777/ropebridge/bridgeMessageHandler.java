package com.czechmate777.ropebridge;

import com.czechmate777.ropebridge.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class bridgeMessageHandler implements IMessageHandler<bridgeMessage, IMessage> {

	@Override
	public IMessage onMessage(bridgeMessage bridgeMessage, MessageContext context) {
		final bridgeMessage message = bridgeMessage;
		final MessageContext ctx = context;
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		// or Minecraft.getMinecraft() on the client
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
            	EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        		WorldServer world = (WorldServer) player.worldObj;
                System.out.println("Received "+message.command+" from "+player.getDisplayName().getUnformattedText());
                switch (message.command) {
                	case 0: { // Sound
                		String name = "";
                		switch  (message.invIndex) {
                		case 0: { name = "random.bow";	break; }
                		case 1: { name = "dig.wood";	break; }
                		}
                		if (message.posX==0) {	// Sound at player
                			world.playSoundAtEntity(player, name, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                		}
                		else {					// Sound at coordinates
                			world.playSoundEffect(message.posX, message.posY, message.posZ, name, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                		}
                		break;
                	}
                	case 1: { // set a block
                		BlockPos blockPos = new BlockPos(message.posX, message.posY, message.posZ);
                		world.destroyBlock(blockPos, true);
                		Block blk;
                		if (message.invIndex == 1) {
                			blk = ModBlocks.bridgeBlockUpper;
                		}
                		else {
                			blk = ModBlocks.bridgeBlockLower;
                		}
                		world.setBlockState(blockPos, blk.getStateFromMeta(message.stackSize));
                		break;
                	}
                	case 2: { // set inventory
                		if (message.stackSize == 0) {
                			player.inventory.mainInventory[message.invIndex] = null;
                		}
                		else {
                    		player.inventory.mainInventory[message.invIndex].stackSize = message.stackSize;
                		}
                	}
                	case 3: { // damage item
                		player.getCurrentEquippedItem().damageItem(1, player);
                	}
                }
            }
        });
        return null; // no response in this case
	}
	
}