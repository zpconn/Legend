package legend;

import org.newdawn.slick.Image;

public class BlockMap extends Component
{
    private int width = 0;
    private int depth = 0;
    private int height = 0;
    
    BlockMapGenerationDataPackage dataPackage;
    
    private int blockWidth = 101;
    private int blockDepth = 82;
    private int blockHeight = 38;
    
    private int[][] towerHeights;
    private float[][] shoreDistances;
    
    public enum BlockType
    {
        DIRT, GRASS, WATER, STONE, SAND, WOOD, NONE
    }
    
    public BlockMap(BlockMapGenerationDataPackage dataPackage)
    {
        this.width = dataPackage.getWidth();
        this.depth = dataPackage.getDepth();
        this.height = dataPackage.getHeight();
        
        this.dataPackage = dataPackage;
        towerHeights = new int[width][depth];
        shoreDistances = new float[width][depth];
        
        computeTowerHeights();
        computeShoreDistances();
    }
    
    public void computeTowerHeights()
    {
        for (int i = 0; i < width; ++i)
        {
            for (int j = 0; j < depth; ++j)
            {
                computeTowerHeight(i, j);
            }
        }
    }
    
    private void computeShoreDistances()
    {
        for (int i = 0; i < width; ++i)
        {
            for (int j = 0; j < depth; ++j)
            {
                if (dataPackage.blocks[i][j][0] != BlockType.WATER)
                {
                    shoreDistances[i][j] = 0.0f;
                }
                else
                {
                    int n = 1;
                    boolean foundShore = false;
                    while (!foundShore)
                    {

                        for (int x = -n; x <= n; ++x)
                        {
                            for (int y = -n; y <= n; ++y)
                            {
                                if (i+x >= 0 && i+x <= width - 1 && j+y >= 0 && j+y <= depth - 1 
                                    && dataPackage.blocks[i+x][j+y][0] != BlockType.WATER)
                                {
                                    foundShore = true;
                                    shoreDistances[i][j] = (float)Math.sqrt((double)(x*x + y*y));
                                }

                                if (foundShore)
                                    break;
                            }

                            if (foundShore)
                                break;
                        }

                        if (foundShore == false && n >= 35)
                        {
                            shoreDistances[i][j] = (float)Math.sqrt((double)(2 * n * n));
                            foundShore = true;
                            break;
                        }

                        ++n;
                    }
                }
            }
        }        
    }
    
    private void computeTowerHeight(int i, int j)
    {
        int k = 0;
        for (; k < height && dataPackage.blocks[i][j][k] != BlockType.NONE; ++k) ;
        towerHeights[i][j] = k - 1;
    }
    
    public int getTowerHeight(int i, int j)
    {
        return towerHeights[i][j];
    }
    
    public float getShoreDistance(int i, int j)
    {
        return shoreDistances[i][j];
    }
    
    public BlockType getBlock(int i, int j, int k)
    {
        return dataPackage.blocks[i][j][k];
    }
    
    public void setBlock(int i, int j, int k, BlockType blockType)
    {
        dataPackage.blocks[i][j][k] = blockType;
        computeTowerHeight(i, j);
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getDepth()
    {
        return depth;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public int getBlockWidth()
    {
        return blockWidth;
    }
    
    public int getBlockDepth()
    {
        return blockDepth;
    }
    
    public int getBlockHeight()
    {
        return blockHeight;
    }
}