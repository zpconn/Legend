package legend;

import java.util.Random;

public class BlockBuilder extends BlockMapGenerationOperator
{
    public void operate(BlockMapGenerationDataPackage dataPackage)
    {
        System.out.println("Placing block structure...");
        
        Random rand = new Random();
        
        for (int i = 0; i < dataPackage.getWidth(); ++i)
        {
            for (int j = 0; j < dataPackage.getDepth(); ++j)
            {
                int height = Math.max(1, (int)(dataPackage.heightMap[i][j] * dataPackage.getHeight()));
                
                for (int k = 0; k < height; ++k)
                {
                    if (k == 0 && k == height - 1)
                    {
                        dataPackage.blocks[i][j][k] = BlockMap.BlockType.WATER;
                    }
                    else if (k <= 1)
                    {
                        dataPackage.blocks[i][j][k] = BlockMap.BlockType.SAND;
                    }
                    else if (k == 2)
                    {
                        if (Math.random() < 0.2f)
                        {
                            dataPackage.blocks[i][j][k] = BlockMap.BlockType.GRASS;
                        }
                        else
                        {
                            dataPackage.blocks[i][j][k] = BlockMap.BlockType.DIRT;
                        }
                    }
                    else if (k >= 3 && k <= 8)
                    {
                        dataPackage.blocks[i][j][k] = BlockMap.BlockType.GRASS;
                    }
                    else if (k < height - 1)
                    {
                        if (Math.random() < 0.2f)
                        {
                            dataPackage.blocks[i][j][k] = BlockMap.BlockType.DIRT;
                        }
                        else
                        {
                            dataPackage.blocks[i][j][k] = BlockMap.BlockType.STONE;
                        }                        
                    }
                    else if (k >= 12)
                    {
                        if (Math.random() < 0.2f)
                        {
                            dataPackage.blocks[i][j][k] = BlockMap.BlockType.STONE;
                        }
                        else
                        {
                            dataPackage.blocks[i][j][k] = BlockMap.BlockType.GRASS;
                        }
                    }
                    
                    if (k == height - 1 && dataPackage.blocks[i][j][k] == BlockMap.BlockType.GRASS
                        && Math.random() <= 0.065f && dataPackage.countTrees(i, j) == 0 && dataPackage.getHeight() - height >= 10)
                    {
                        dataPackage.trees[i][j] = 1 + rand.nextInt(3);
                        
                        for (int l = k + 1; l < dataPackage.getHeight(); ++l)
                        {
                            dataPackage.blocks[i][j][l] = BlockMap.BlockType.WOOD;
                        }
                    }
                    
                    if (k == height - 1 && (dataPackage.blocks[i][j][k] == BlockMap.BlockType.GRASS || dataPackage.blocks[i][j][k] == BlockMap.BlockType.SAND)
                        && Math.random() <= 0.02f)
                    {
                        dataPackage.objects[i][j] = 1 + rand.nextInt(2);
                    }
                    
                    if (k == height - 1 && dataPackage.blocks[i][j][k] == BlockMap.BlockType.GRASS && dataPackage.objects[i][j] == 0 && Math.random() <= 0.02f)
                    {
                        dataPackage.objects[i][j] = 3 + rand.nextInt(4);
                    }
                }                
            }
        }
    }
}