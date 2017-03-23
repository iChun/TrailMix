package me.ichun.mods.trailmix.common.behaviour;

import me.ichun.mods.trailmix.common.core.EntityHelperTrailMix;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BehaviorDispenseLauncher extends BehaviorDefaultDispenseItem
{
	private final BehaviorDefaultDispenseItem defaultItemDispenseBehavior;

	final MinecraftServer mcServer;

	public BehaviorDispenseLauncher(MinecraftServer server)
	{
		this.mcServer = server;
		this.defaultItemDispenseBehavior = new BehaviorDefaultDispenseItem();
	}

	@Override
	public ItemStack dispenseStack(IBlockSource iblocksource, ItemStack is)
	{
		if(is.getItemDamage() >= 9)
		{
			return defaultItemDispenseBehavior.dispense(iblocksource, is);
		}
		World world = iblocksource.getWorld();
		EntityZombie zombie = new EntityZombie(world);
		EnumFacing facing = iblocksource.getBlockState().getValue(BlockDispenser.FACING);
		zombie.setLocationAndAngles(iblocksource.getX() + ((double)facing.getFrontOffsetZ() * 0.3D) + ((double)facing.getFrontOffsetX() * 1.125D) + Math.abs((double)facing.getFrontOffsetY() * 0.5D), iblocksource.getY() + ((double)facing.getFrontOffsetY() * 0.3D) - 1.45D, iblocksource.getZ() + ((double)facing.getFrontOffsetX() * -0.3D) + (double)((float)facing.getFrontOffsetZ() * 1.125F) + Math.abs((double)facing.getFrontOffsetY() * 0.5D), facing == EnumFacing.EAST ? 90.0F : facing == EnumFacing.NORTH ? 180F : facing == EnumFacing.WEST ? 270F : 0.0F, facing == EnumFacing.UP ? -90F : facing == EnumFacing.DOWN ? 90F : 0.0F);
		zombie.setHeldItem(EnumHand.MAIN_HAND, is);
		EntityHelperTrailMix.launchPig(zombie);

		return is;
	}

	@Override
	protected void playDispenseSound(IBlockSource par1IBlockSource)
	{
	}

}
