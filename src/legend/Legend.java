package legend;

import org.newdawn.slick.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Legend extends BasicGame
{
    private World world;
    
    private BlockMap overworldMap;
    private EntitySystem movementSystem;
    private EntitySystem cameraSystem;
    private EntitySystem blockMapRenderSystem;
    
    public Legend()
    {
        super("Legend");
    }

    @Override
    public void init(GameContainer gc)
            throws SlickException
    {
        world = new World();
        
        try
        {
            AssetManager.getInstance().loadResources(new FileInputStream("assets.xml"));
        }
        catch (FileNotFoundException e)
        {
            throw new SlickException("Could not load assets.xml", e);
        }
        
        BlockMapGenerationDataPackage dataPackage = new BlockMapGenerationDataPackage(900, 900, 15);
        
        BlockMapGenerationOperator voronoiTesselator = new VoronoiTesselator();
        BlockMapGenerationOperator plasmaFractalPerturbator = new PlasmaFractalPerturbator();
        BlockMapGenerationOperator thermalEroder = new ThermalEroder();
        BlockMapGenerationOperator blockBuilder = new BlockBuilder();
        
        voronoiTesselator.operate(dataPackage);
        plasmaFractalPerturbator.operate(dataPackage);
        thermalEroder.operate(dataPackage);
        blockBuilder.operate(dataPackage);
        
        overworldMap = new BlockMap(dataPackage);
        
        SystemManager systemManager = world.getSystemManager();
        
        movementSystem = systemManager.setSystem(new MovementSystem());
        cameraSystem = systemManager.setSystem(new CameraSystem(gc));
        blockMapRenderSystem = systemManager.setSystem(new BlockMapRenderSystem(gc, overworldMap));
        
        systemManager.initializeAll();
    }

    @Override
    public void update(GameContainer gc, int delta)
            throws SlickException
    {
        world.loopStart();
        
        world.setDelta(delta);
        
        movementSystem.process();
        cameraSystem.process();        
    }

    public void render(GameContainer gc, Graphics g)
            throws SlickException
    {
        blockMapRenderSystem.process();
    }

    public static void main(String[] args)
            throws SlickException
    {
        AppGameContainer app = new AppGameContainer(new legend.Legend());
        app.setDisplayMode(Math.round(app.getScreenWidth()), Math.round(app.getScreenHeight()), false);
        
        app.start();
    }
}