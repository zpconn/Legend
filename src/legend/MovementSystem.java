package legend;

public class MovementSystem extends EntityProcessingSystem
{
    public MovementSystem()
    {
        super(Transform.class, Velocity.class);
    }
    
    private ComponentMapper<Transform> transformMapper;
    private ComponentMapper<Velocity> velocityMapper;
    
    @Override
    public void initialize()
    {
        transformMapper = new ComponentMapper<Transform>(Transform.class, world.getEntityManager());
        velocityMapper = new ComponentMapper<Velocity>(Velocity.class, world.getEntityManager());
    }
    
    @Override
    protected void process(Entity e)
    {
        Transform t = transformMapper.get(e);
        Velocity velocity = velocityMapper.get(e);
        
        float r = t.getRotationAsRadians();
        float v = velocity.getVelocity();
        
        float xn = t.getX() + (TrigLUT.cos(r) * v * world.getDelta());
        float yn = t.getY() + (TrigLUT.sin(r) * v * world.getDelta());
        
        t.setLocation(xn, yn);
    }
}