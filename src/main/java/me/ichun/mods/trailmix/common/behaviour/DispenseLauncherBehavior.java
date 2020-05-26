package me.ichun.mods.trailmix.common.behaviour;

import me.ichun.mods.trailmix.common.item.ItemLauncher;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class DispenseLauncherBehavior extends DefaultDispenseItemBehavior
{
	private final DefaultDispenseItemBehavior defaultItemDispenseBehavior;

	final MinecraftServer mcServer;

	public DispenseLauncherBehavior(MinecraftServer server)
	{
		this.mcServer = server;
		this.defaultItemDispenseBehavior = new DefaultDispenseItemBehavior();
	}

	@Override
	public ItemStack dispenseStack(IBlockSource iblocksource, ItemStack is)
	{
		if(is.getDamage() >= 9)
		{
			return defaultItemDispenseBehavior.dispense(iblocksource, is);
		}
		World world = iblocksource.getWorld();
		ZombieEntity zombie = new ZombieEntity(world);
		Direction facing = iblocksource.getBlockState().get(DispenserBlock.FACING);
		zombie.setLocationAndAngles(iblocksource.getX() + ((double)facing.getZOffset() * 0.3D) + ((double)facing.getXOffset() * 1.125D) + Math.abs((double)facing.getYOffset() * 0.5D), iblocksource.getY() + ((double)facing.getYOffset() * 0.3D) - 1.45D, iblocksource.getZ() + ((double)facing.getXOffset() * -0.3D) + (double)((float)facing.getZOffset() * 1.125F) + Math.abs((double)facing.getYOffset() * 0.5D), facing == Direction.EAST ? 90.0F : facing == Direction.NORTH ? 180F : facing == Direction.WEST ? 270F : 0.0F, facing == Direction.UP ? -90F : facing == Direction.DOWN ? 90F : 0.0F);
		//		zombie.setHeldItem(Hand.MAIN_HAND, is);
		ItemLauncher.shootPig(zombie, is);

		return is;
	}

	@Override
	protected void playDispenseSound(IBlockSource par1IBlockSource)
	{
	}
}
