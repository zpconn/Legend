package legend;

public abstract class Spatial
{
    protected World world;
    protected Entity owner;
    
    public Spatial(World world, Entity owner)
    {
        this.world = world;
        this.owner = owner;
    }
    
    public abstract void initialize();
    
    public abstract void render();
}