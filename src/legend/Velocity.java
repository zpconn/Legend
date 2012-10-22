package legend;

public class Velocity extends Component
{
    private float velocity;
    
    public Velocity()
    {
        
    }
    
    public Velocity(float velocity)
    {
        this.velocity = velocity;
    }
    
    public float getVelocity()
    {
        return velocity;
    }
    
    public void setVelocity(float velocity)
    {
        this.velocity = velocity;
    }
    
    public void addVelocity(float velocity)
    {
        this.velocity += velocity;
    }
}