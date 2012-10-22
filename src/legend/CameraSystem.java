package legend;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

public class CameraSystem extends EntitySystem
{
    private GameContainer container;
    private float offsetX = 0;
    private float offsetY = 0;
    
    public CameraSystem(GameContainer container)
    {
        super();
        this.container = container;
    }
    
    @Override
    public void initialize()
    {
    }
    
    @Override
    protected void processEntities(ImmutableBag<Entity> entities)
    {
        Input input = container.getInput();
        
        if (input.isKeyDown(Input.KEY_LEFT))
        {
            offsetX -= 0.4f * world.getDelta();
        }
        else if (input.isKeyDown(Input.KEY_RIGHT))
        {
            offsetX += 0.4f * world.getDelta();
        }
        
        if (input.isKeyDown(Input.KEY_UP))
        {
            offsetY -= 0.4f * world.getDelta();
        }
        else if (input.isKeyDown(Input.KEY_DOWN))
        {
            offsetY += 0.4f * world.getDelta();
        }
    }
    
    @Override
    protected boolean checkProcessing()
    {
        return true;
    }
    
    public float getOffsetX()
    {
        return -offsetX;
    }
    
    public float getOffsetY()
    {
        return -offsetY;
    }
}