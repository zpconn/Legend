package legend;

import java.util.Random;

public class PlasmaFractalGenerator
{
    private int gridWidth;
    private int gridHeight;
    private float roughness;
    private float[][] grid;
    
    public PlasmaFractalGenerator(int gridWidth, int gridHeight, float roughness)
    {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.roughness = roughness;
        this.grid = new float[gridWidth][gridHeight];
        
        divideGrid(0, 0, (float)gridWidth, (float)gridHeight, (float)Math.random(), (float)Math.random(), (float)Math.random(), (float)Math.random());
    }
    
    public float noise(int x, int y)
    {
        return grid[x][y];
    }
    
    private void divideGrid(float x, float y, float width, float height, float c1, float c2, float c3, float c4)
    {
        float edge1, edge2, edge3, edge4, middle;
        float newWidth = width / 2.0f;
        float newHeight = height / 2.0f;
        
        if (width > 1.0f || height > 1.0f)
        {
            middle = (c1 + c2 + c3 + c4) / 4.0f + displace(newWidth + newHeight);
            edge1 = (c1 + c2) / 2.0f;
            edge2 = (c2 + c3) / 2.0f;
            edge3 = (c3 + c4) / 2.0f;
            edge4 = (c4 + c1) / 2.0f;
            
            if (middle < 0)
            {
                middle = 0;
            }
            else if (middle > 1.0f)
            {
                middle = 1.0f;
            }
            
            divideGrid(x, y, newWidth, newHeight, c1, edge1, middle, edge4);
            divideGrid(x + newWidth, y, newWidth, newHeight, edge1, c2, edge2, middle);
            divideGrid(x + newWidth, y + newHeight, newWidth, newHeight, middle, edge2, c3, edge3);
            divideGrid(x, y + newHeight, newWidth, newHeight, edge4, middle, edge3, c4);
        }        
        else
        {
            float c = (c1 + c2 + c3 + c4) / 4.0f;
            grid[(int)x][(int)y] = c;
        }
    }
    
    private float displace(float num)
    {
        float max = num / (float)(gridWidth + gridHeight) * roughness;
        return ((float)Math.random() - 0.5f) * max;
    }
}