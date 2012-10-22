package legend;

public class ThermalEroder extends BlockMapGenerationOperator
{
    public void operate(BlockMapGenerationDataPackage dataPackage)
    {
        System.out.println("Simulating thermal erosion using a rotated von Neumann cellular automaton...");
        
        float[] h = new float[4];
        
        for (int n = 0; n < 50; ++n)
        {
            for (int i = 1; i < dataPackage.getWidth() - 1; ++i)
            {
                for (int j = 1; j < dataPackage.getDepth() - 1; ++j)
                {
                    h[0] = dataPackage.heightMap[i-1][j-1];
                    h[1] = dataPackage.heightMap[i+1][j-1];
                    h[2] = dataPackage.heightMap[i-1][j+1];
                    h[3] = dataPackage.heightMap[i+1][j+1];

                    int minIndex = 0;

                    for (int k = 0; k < 4; ++k)
                    {
                        if (h[k] < h[minIndex])
                        {
                            minIndex = k;
                        }
                    }

                    float here = dataPackage.heightMap[i][j];
                    float dh = (here - h[minIndex]) / 2.0f;
                    
                    if (dh < (float)dataPackage.getWidth() / 4.0f)
                        continue;

                    dataPackage.heightMap[i][j] -= dh;

                    if (minIndex == 0)
                    {
                        dataPackage.heightMap[i-1][j-1] += dh;
                        dataPackage.heightMap[i-1][j-1] = Math.min(1, Math.max(0, dataPackage.heightMap[i-1][j-1]));
                    }
                    else if (minIndex == 1)
                    {
                        dataPackage.heightMap[i+1][j-1] += dh;
                        dataPackage.heightMap[i+1][j-1] = Math.min(1, Math.max(0, dataPackage.heightMap[i+1][j-1]));
                    }
                    else if (minIndex == 2)
                    {
                        dataPackage.heightMap[i-1][j+1] += dh;
                        dataPackage.heightMap[i-1][j+1] = Math.min(1, Math.max(0, dataPackage.heightMap[i-1][j+1]));
                    }
                    else if (minIndex == 3)
                    {
                        dataPackage.heightMap[i+1][j+1] += dh;
                        dataPackage.heightMap[i+1][j+1] = Math.min(1, Math.max(0, dataPackage.heightMap[i+1][j+1]));
                    }
                }
            }   
        }
    }
}