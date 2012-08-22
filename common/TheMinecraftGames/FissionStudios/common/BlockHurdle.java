package net.minecraft.src;

import java.util.Random;

import net.minecraft.client.Minecraft;

public class BlockHurdle extends BlockContainer
{
	private Minecraft mc;
	
	public BlockHurdle(int i, int j, Class c) {
		super(i, j, Material.ground);
		anEntityClass = c;
		setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 0.625F, 0.55F);
	}

	public int idDropped(int par1, Random par2Random, int par3) {
		return mod_Olympics.hurdleItem.shiftedIndex;
	}

	public int quantityDropped(Random rand) {
		return 1;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return -1;
	}
	
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		if(par5Entity instanceof EntityPlayer){
			par5Entity.attackEntityFrom(DamageSource.cactus, 1);
			if(par5Entity.isAirBorne)
			{
				par5Entity.isAirBorne = false;
			}
			if(par5Entity.isSprinting())
			{
				par5Entity.setSprinting(false);
			}
			int bhf = mod_Olympics.hurdleFallen.blockID;
			par1World.setBlockWithNotify(par2, par3, par4, bhf);
		}
	}
	
	//public TileEntity getBlockEntity() {
		//return new TileEntityHurdle();
	//}
	
	private Class anEntityClass;

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityHurdle();
	}
}