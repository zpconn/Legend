package legend;

import java.util.Random;

public class VoronoiTesselator extends BlockMapGenerationOperator
{
    public void operate(BlockMapGenerationDataPackage dataPackage)
    {
        System.out.println("Constructing Voronoi tesselation...");
        
        Random rand = new Random();
        
        int maxDistance = dataPackage.getWidth() * dataPackage.getWidth() + 
                          dataPackage.getDepth() * dataPackage.getDepth();
        
        int numFeaturePoints = dataPackage.getWidth();
        
        int[][] featurePoints = new int[numFeaturePoints][2];
        
        for (int i = 0; i < numFeaturePoints; ++i)
        {
            featurePoints[i][0] = rand.nextInt(dataPackage.getWidth());
            featurePoints[i][1] = rand.nextInt(dataPackage.getDepth());
        }
        
        for (int i = 0; i < dataPackage.getWidth(); ++i)
        {
            for (int j = 0; j < dataPackage.getDepth(); ++j)
            {
                int closestIndex = 0;
                int minDistance = maxDistance;
                
                for (int k = 0; k < numFeaturePoints; ++k)
                {
                    int distance = (featurePoints[k][0] - i) * (featurePoints[k][0] - i) +
                                   (featurePoints[k][1] - j) * (featurePoints[k][1] - j);
                    
                    if (distance < minDistance)
                    {
                        minDistance = distance;
                        closestIndex = k;
                    }
                }
                
                int secondClosestIndex = 0;
                int secondMinDistance = maxDistance;
                
                for (int k = 0; k < numFeaturePoints; ++k)
                {
                    int distance = (featurePoints[k][0] - i) * (featurePoints[k][0] - i) +
                                   (featurePoints[k][1] - j) * (featurePoints[k][1] - j);
                    
                    if (distance < secondMinDistance && k != closestIndex)
                    {
                        secondMinDistance = distance;
                        secondClosestIndex = k;
                    }
                }
                
                dataPackage.heightMap[i][j] = 1.0f - Math.min((float)(secondMinDistance - minDistance) / Math.abs((float)maxDistance / 4000), 1.0f);
            }
        }
    }
}