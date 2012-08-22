package TheMinecraftGames.FissionStudios.common;

import java.util.Random;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockHurdleFallen extends BlockContainer {
	public BlockHurdleFallen(int id) {
		super(id, Material.ground);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.15F, 1.0F);
	}

	public int idDropped(int par1, Random par2Random, int par3) {
		return mod_TheMinecraftGames.hurdleItem.shiftedIndex;
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
	
	public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
	{
		//stuff
	}
	
	private Class anEntityClass;

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityHurdleFallen();
	}
}