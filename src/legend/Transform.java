package legend;

public class Transform extends Component
{
    private float x;
    private float y;
    private float rotation;
    
    private BlockMap blockMap;
    private int row;
    
    public Transform(BlockMap blockMap)
    {
        this.blockMap = blockMap;
        row = 0;
    }
    
    public Transform(BlockMap blockMap, float x, float y)
    {
        this(blockMap);
        this.x = x;
        this.y = y;
    }
    
    public Transform(BlockMap blockMap, float x, float y, float rotation)
    {
        this(blockMap, x, y);
        this.rotation = rotation;
    }
    
    private void updateRow()
    {
        if (blockMap != null)
        {
          
        }
    }
    
    public void addX(float x)
    {
        this.x += x;
    }
    
    public void addY(float y)
    {
        this.y += y;
        updateRow();
    }
    
    public float getX()
    {
        return x;
    }
    
    public void setX(float x)
    {
        this.x = x;
    }
    
    public float getY()
    {
        return y;
    }
    
    public void setY(float y)
    {
        this.y = y;
        updateRow();
    }
    
    public void setLocation(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    
    public float getRotation()
    {
        return rotation;
    }
    
    public void setRotation(float rotation)
    {
        this.rotation = rotation;
    }
    
    public void addRotation(float angle)
    {
        rotation = (rotation + angle) % 360;
    }
    
    public float getRotationAsRadians()
    {
        return (float)Math.toRadians(rotation);
    }
}