package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Random;

import wildgrass.common.Wildgrass;

public class BlockGrass extends Block
{
	private Random rand;
	
    protected BlockGrass(int par1)
    {
        super(par1, Material.grass);
        this.blockIndexInTexture = 3;
        this.setTickRandomly(true);
        this.setRequiresSelfNotify();
        this.setCreativeTab(CreativeTabs.tabBlock);
        rand = new Random();
    }
    
    public int getRenderType()
    {
    	return Wildgrass.wildgrassRenderID;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par1 == 1 ? 0 : (par1 == 0 ? 2 : 3);
    }

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public int getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (par5 == 1)
        {
            return 0;
        }
        else if (par5 == 0)
        {
            return 2;
        }
        else
        {
            Material var6 = par1IBlockAccess.getBlockMaterial(par2, par3 + 1, par4);
            return var6 != Material.snow && var6 != Material.craftedSnow ? 3 : 68;
        }
    }
        
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) 
    {
    	if(rand.nextInt(100) < 5)
    		if(world.getBlockMetadata(x, y, z) > 0) world.setBlockMetadata(x, y, z, world.getBlockMetadata(x, y, z) - 1);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World world, int x, int y, int z, Random random)
    {
        if (!world.isRemote)
        {
            updateGrassTick(world, x, y, z, random);
            if (world.getBlockLightValue(x, y + 1, z) < 4 && Block.lightOpacity[world.getBlockId(x, y + 1, z)] > 2)
            {
            	world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
            }
            else if (world.getBlockLightValue(x, y + 1, z) >= 9)
            {
                for (int var6 = 0; var6 < 4; ++var6)
                {
                    int var7 = x + random.nextInt(3) - 1;
                    int var8 = y + random.nextInt(5) - 3;
                    int var9 = z + random.nextInt(3) - 1;
                    int var10 = world.getBlockId(var7, var8 + 1, var9);

                    if (world.getBlockId(var7, var8, var9) == Block.dirt.blockID && world.getBlockLightValue(var7, var8 + 1, var9) >= 4 && Block.lightOpacity[var10] <= 2)
                    {
                    	world.setBlockWithNotify(var7, var8, var9, Block.grass.blockID);
                    }
                }
            }
        }
    }
    
    public int getGrassNearby(World world, int x, int y, int z)
    {
        int grass = 0;
        for(int i = 0; i <= 2; i++)
        {
            for(int j = 0; j <= 2; j++)
            {
                if(getGrassHeight(world, (x + i) - 1, y, (z + j) - 1) > 0)
                {
                	grass++;
                }
            }
        }
        return grass;
    }

    public void spreadGrassNearby(World world, int x, int y, int z, Random random)
    {
        double scale = getGrassBiomeScale(world, x, y, z);
        if(random.nextInt(250) <= (int)(1.0D + 7D * scale) && getGrassNearby(world, x, y, z) < (int)(1.0D + 4D * scale))
        {
            int randX = (x + random.nextInt(3)) - 1;
            int randY = y;
            int randZ = (z + random.nextInt(3)) - 1;
            if(placeWildGrass(world, randX, randY, randZ))
            {
                int grassHeight = getGrassHeight(world, x, y, z) + 1;
                setGrassHeight(true, world, x, y, z, grassHeight);
            }
        }
    }
    
    public boolean checkNewGrass(World world, int x, int y, int z, Random random)
    {
        double scale = getGrassBiomeScale(world, x, y, z);
        if(getGrassHeight(world, x, y, z) < 0)
        {
            int grassHeight = 0;
            if(random.nextInt(4) <= (int)(1.0D + 7D * scale))
            {
            	grassHeight += random.nextInt(getGrassBiomeHeight(world, x, y, z));
            }
            if(getGrassBiomeHeight(world, x, y, z) == 12)
            {
            	grassHeight += 6;
            }
            setGrassHeight(true, world, x, y, z, grassHeight);
            return true;
        } else
        {
            return false;
        }
    }

    public void setGrassHeight(boolean notify, World world, int x, int y, int z, int grassHeight)
    {
        if(grassHeight < -1)
        {
        	grassHeight = -1;
        }
        if(grassHeight > getGrassBiomeHeight(world, x, y, z))
        {
        	grassHeight = getGrassBiomeHeight(world, x, y, z);
        }
        if(notify)
        {
            world.setBlockMetadataWithNotify(x, y, z, grassHeight + 1);
        } else
        {
            world.setBlockMetadata(x, y, z, grassHeight + 1);
        }
    }

    public int getGrassHeight(World world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        return metadata - 1;
    }

    public void updateGrassTick(World world, int x, int y, int z, Random random)
    {
        checkNewGrass(world, x, y, z, random);
        if(!checkNewGrass(world, x, y, z, random))
        {
            double d = getGrassBiomeScale(world, x, y, z);
            if(getGrassHeight(world, x, y, z) >= 0)
            {
                spreadGrassNearby(world, x, y, z, random);
            } else
            if(random.nextInt(200) <= (int)(10D * d) && getGrassNearby(world, x, y, z) < (int)(5D + 4D * d))
            {
                placeWildGrass(world, x, y, z);
            }
            if(getTallGrassBlocked(world, x, y, z))
            {
                setGrassHeight(false, world, x, y, z, 0);
            }
            if(getTallGrassBlocked(world, x, y + 1, z) && world.getBlockMetadata(x, y, z) > 6)
            {
            	setGrassHeight(false, world, x, y, z, 0);
            }
        }
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID)
    {
        if(getTallGrassBlocked(world, x, y, z))
        {
            setGrassHeight(false, world, x, y, z, 0);
        }
        if(getTallGrassBlocked(world, x, y + 1, z) && world.getBlockMetadata(x, y, z) > 6)
        {
        	setGrassHeight(false, world, x, y, z, 0);
        }
    }

    public boolean getTallGrassBlocked(World world, int x, int y, int z)
    {
        return world.getBlockId(x, y + 1, z) != 0 && world.getBlockId(x, y + 1, z) != Block.snow.blockID && world.getBlockId(x, y + 1, z) != Block.fence.blockID && world.getBlockId(x, y + 1, z) != Block.netherFence.blockID && world.getBlockId(x, y + 1, z) != Block.torchWood.blockID && world.getBlockId(x, y + 1, z) != Block.fenceGate.blockID && world.getBlockId(x, y + 1, z) != Block.signPost.blockID;
    }

    public double getGrassBiomeScale(World world, int x, int y, int z)
    {
        BiomeGenBase biomeGenerator = world.getBiomeGenForCoords(x, z);
        double d = biomeGenerator.rainfall;
        double d1 = biomeGenerator.temperature;
        return d * d1;
    }
    
    public int getGrassBiomeHeight(World world, int x, int y, int z)
    {
    	BiomeDecorator biomedecorator = world.getBiomeGenForCoords(x, z).theBiomeDecorator;
    	if(biomedecorator.biome.biomeID == biomedecorator.biome.jungle.biomeID || biomedecorator.biome.biomeID == biomedecorator.biome.jungleHills.biomeID)
    	{
    		return 12;
    	}
    	else
    	{
    		return 6;
    	}
    }

    public boolean placeWildGrass(World world, int x, int y, int z)
    {
        if(getGrassHeight(world, x, y, z) < 0 && world.getBlockId(x, y, z) == Block.snow.blockID && world.getBlockId(x, y + 1, z) == 0)
        {
            setGrassHeight(true, world, x, y, z, 1);
            return true;
        } else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        double var1 = 0.5D;
        double var3 = 1.0D;
        return ColorizerGrass.getGrassColor(var1, var3);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    public int getRenderColor(int par1)
    {
        return this.getBlockColor();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = 0;
        int var6 = 0;
        int var7 = 0;

        for (int var8 = -1; var8 <= 1; ++var8)
        {
            for (int var9 = -1; var9 <= 1; ++var9)
            {
                int var10 = par1IBlockAccess.getBiomeGenForCoords(par2 + var9, par4 + var8).getBiomeGrassColor();
                var5 += (var10 & 16711680) >> 16;
                var6 += (var10 & 65280) >> 8;
                var7 += var10 & 255;
            }
        }

        return (var5 / 9 & 255) << 16 | (var6 / 9 & 255) << 8 | var7 / 9 & 255;
    }
    
    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Block.dirt.idDropped(0, par2Random, par3);
    }
}
