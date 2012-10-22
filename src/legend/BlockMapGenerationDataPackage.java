package legend;

public class BlockMapGenerationDataPackage
{
    private int width = 0;
    private int depth = 0;
    private int height = 0;
    
    public BlockMap.BlockType[][][] blocks;
    public float[][] heightMap;
    public int[][] trees;
    public int[][] objects;
    
    public BlockMapGenerationDataPackage(int width, int depth, int height)
    {
        this.width = width;
        this.depth = depth;
        this.height = height;
        
        heightMap = new float[width][depth];
        blocks = new BlockMap.BlockType[width][depth][height];
        trees = new int[width][depth];
        objects = new int[width][depth];
                
        for (int i = 0; i < width; ++i)
        {
            for (int j = 0; j < depth; ++j)
            {
                trees[i][j] = 0;
                objects[i][j] = 0;
                
                for (int k = 0; k < height; ++k)
                {
                    blocks[i][j][k] = BlockMap.BlockType.NONE;
                }
            }
        }
    }
    
    public int countTrees(int x, int y)
    {
        int count = 0;

        if (x > 0 && trees[x-1][y] > 0)
            ++count;

        if (y > 0 && trees[x][y-1] > 0)
            ++count;

        if (x > 0 && y > 0 && trees[x-1][y-1] > 0)
            ++count;

        if (x < width - 1 && trees[x+1][y] > 0)
            ++count;

        if (y < depth - 1 && trees[x][y+1] > 0)
            ++count;

        if (x < width - 1 && y < depth - 1 && trees[x+1][y+1] > 0)
            ++count;

        if (x > 0 && y < depth - 1 && trees[x-1][y+1] > 0)
            ++count;

        if (y > 0 && x < width - 1 && trees[x+1][y-1] > 0)
            ++count;

        return count;
    }
    
    public void setBlock(int i, int j, int k, BlockMap.BlockType type)
    {
        blocks[i][j][k] = type;
    }            
    
    public BlockMap.BlockType getBlock(int i, int j, int k)
    {
        return blocks[i][j][k];
    }
    
    public void setHeightMapValue(int i, int j, float height)
    {
        heightMap[i][j] = height;
    }
    
    public float getHeightMapValue(int i, int j)
    {
        return heightMap[i][j];
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public int getDepth()
    {
        return depth;
    }
}