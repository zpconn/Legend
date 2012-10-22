package legend;

public class PlasmaFractalPerturbator extends BlockMapGenerationOperator
{
    public void operate(BlockMapGenerationDataPackage dataPackage)
    {
        System.out.println("Invoking the plasma fractal perturbator...");
        
        PlasmaFractalGenerator noiseGenerator = new PlasmaFractalGenerator(dataPackage.getWidth(), dataPackage.getDepth(), 
                (float)dataPackage.getWidth() / 3.0f);
        
        for (int i = 0; i < dataPackage.getWidth(); ++i)
        {
            for (int j = 0; j < dataPackage.getDepth(); ++j)
            {
                dataPackage.heightMap[i][j] = 0.25f * (float)noiseGenerator.noise(i, j) + 0.75f * dataPackage.heightMap[i][j];
                dataPackage.heightMap[i][j] = Math.min(1, Math.max(0, dataPackage.heightMap[i][j]));
            }
        }
    }
}