package legend;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.GameContainer;

public class TreeLeafEmitter implements ParticleEmitter
{
    private BlockMap blockMap;
    private World world;
    private CameraSystem cameraSystem;
    private GameContainer container;
    
    private int timer = 0;
    private int interval = 375;
    private float size = 150;
    
    public TreeLeafEmitter(BlockMap map, World world, GameContainer container)
    {
        this.blockMap = map;
        this.world = world;
        this.container = container;
        cameraSystem = world.getSystemManager().getSystem(CameraSystem.class);
    }
    
    @Override
    public void update(ParticleSystem system, int delta)
    {
        int cx = (int)cameraSystem.getOffsetX();
        int cy = (int)cameraSystem.getOffsetY();
        
        timer -= delta;
        
        if (timer <= 0)
        {
            timer = interval;
            
            int rowLowerBound = Math.max(0, (cy > 0 ? 0 : Math.abs(cy / blockMap.getBlockDepth()) - 1) - 5);
            int rowUpperBound = Math.min(blockMap.getDepth(), Math.abs(cy / blockMap.getBlockDepth()) + 1 
                + container.getHeight() / blockMap.getBlockDepth() + 1 + blockMap.getHeight() + 5);
        
            int columnLowerBound = Math.max(0, (cx > 0 ? 0 : Math.abs(cx / blockMap.getBlockWidth()) - 2) - 5);
            int columnUpperBound = Math.min(blockMap.getWidth(), Math.abs(cx / blockMap.getBlockWidth()) + 1 
                    + container.getWidth() / blockMap.getBlockWidth() + 2 + 5);
            
            int sy = rowLowerBound + (int)(Math.random() * (rowUpperBound - rowLowerBound - 1));
            int sx = columnLowerBound + (int)(Math.random() * (columnUpperBound - columnLowerBound - 1));
            
            boolean foundTree = false;
            
            for (int j = sy; !foundTree && j < rowUpperBound; ++j)
            {                
                for (int i = sx; !foundTree && i < columnUpperBound; ++i)
                {
                    if (blockMap.dataPackage.trees[i][j] > 0)
                    {
                        foundTree = true;
                        
                        Particle p = system.getNewParticle(this, 3000);
                        p.setImage(AssetManager.getInstance().getImage("tree-particle-" + Integer.toString((int)((float)Math.random() * 100) % 3 + 1)));
                        double r = Math.random();
            
                        p.setColor(1, 1, 1, 0);
                        p.setSize(50);
                        p.setVelocity((float)Math.cos(r * (float)Math.PI / 4.0f), (float)Math.sin(r * (float)Math.PI / 4.0f), 0.3f);
                        p.setOriented(true);
                        
                        r = (float)Math.random() * 300.0f;
                        float a = (float)(Math.random() *  2.0 * Math.PI);  
                        int height = Math.max(1, (int)(blockMap.dataPackage.heightMap[j][i] * blockMap.dataPackage.getHeight()));
                        
                        p.setPosition(i * blockMap.getBlockWidth() - 30,
                                      j * blockMap.getBlockDepth() - (blockMap.getTowerHeight(i, j) + 1) * blockMap.getBlockHeight());
                    }
                }
            }
        }
    }
    
    @Override
    public void updateParticle(Particle particle, int delta)
    {   
        if (particle.getLife() <= 600)
        {
            particle.setColor(1, 1, 1, (float)particle.getLife() / 600.0f);
        }
        
        if (particle.getLife() >= 3000 - 600)
        {
            particle.setColor(1, 1, 1, (3000.0f - (float)particle.getLife()) / 600.0f);
        }
        
        particle.setSpeed(0.3f);
    }
    
    @Override
    public boolean isEnabled()
    {
        return true;
    }
    
    @Override
    public void setEnabled(boolean enabled)
    {
        
    }
    
    @Override
    public boolean completed()
    {
        return false;
    }
    
    @Override
    public boolean useAdditive()
    {
        return false;
    }
    
    @Override
    public Image getImage()
    {
        return null;
    }
    
    @Override
    public boolean usePoints(ParticleSystem system)
    {
        return false;
    }
    
    @Override
    public boolean isOriented()
    {
        return true;
    }
    
    @Override
    public void wrapUp()
    {
        
    }
    
    @Override
    public void resetState()
    {
        
    }
}