package legend;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.particles.ParticleSystem;

public class BlockMapRenderSystem extends EntitySystem
{
    private final int numLeaves = 60;
    
    private GameContainer container;
    private BlockMap blockMap;
    private ComponentMapper<SpatialForm> spatialFormMapper;
    private ComponentMapper<Transform> transformMapper;
    private CameraSystem cameraSystem;
    private Bag<Spatial> spatials;
    private ParticleSystem particleSystem;
    
    private int time = 91;
    private float waveTime = 0.0f;
    private float grassTime = 0.0f;
    private int grassTimeDirection = 1;
    private float woodTime = 0.0f;
    private int timeDirection = 1;
    
    private float[][] shadowOverlay;
    private float[][] leafOffsets;
    
    public BlockMapRenderSystem(GameContainer container, BlockMap blockMap)
    {
        super(Transform.class, SpatialForm.class);
        this.container = container;
        this.blockMap = blockMap;
        spatials = new Bag<Spatial>();        
    }
    
    @Override
    public void initialize()
    {
        spatialFormMapper = new ComponentMapper<SpatialForm>(SpatialForm.class, world.getEntityManager());
        transformMapper = new ComponentMapper<Transform>(Transform.class, world.getEntityManager());
        cameraSystem = world.getSystemManager().getSystem(CameraSystem.class);
        
        initializeAuxiliaryData();
    }
    
    private void initializeAuxiliaryData()
    {
        shadowOverlay = new float[blockMap.getWidth() + 1][blockMap.getDepth() + 1];
        leafOffsets = new float[numLeaves][2];
        
        float max = (float)Math.sqrt((double)(2 * 5 * 5));
        
        for (int i = 0; i < blockMap.getWidth(); ++i)
        {
            for (int j = 0; j < blockMap.getDepth(); ++j)
            {
                if (blockMap.dataPackage.trees[i][j] != 0)
                {
                    shadowOverlay[i][j] = 0.2f;
                }
                else
                {
                    int n = 1;
                    boolean foundTree = false;
                    while (!foundTree)
                    {

                        for (int x = -n; x <= n; ++x)
                        {
                            for (int y = -n; y <= n; ++y)
                            {
                                if (i+x >= 0 && i+x <= blockMap.getWidth() - 1 && j+y >= 0 && j+y <= blockMap.getDepth() - 1 
                                    && blockMap.dataPackage.trees[i+x][j+y] != 0)
                                {
                                    foundTree = true;
                                    shadowOverlay[i][j] = 0.2f + 0.8f * (float)Math.sqrt((double)(x*x + y*y)) / max;
                                }

                                if (foundTree)
                                    break;
                            }

                            if (foundTree)
                                break;
                        }

                        if (foundTree == false && n >= 5)
                        {
                            shadowOverlay[i][j] = 1.0f;
                            foundTree = true;
                            break;
                        }

                        ++n;
                    }
                }
            }
        }   
        
        for (int i = 0; i < numLeaves; ++i)
        {
            float r = (float)(i % 4) / 4.0f * 300.0f;
            float a = (float)((float)(i / 4) / ((float)numLeaves / 4) * 2.0 * Math.PI);
            leafOffsets[i][0] = r * (float)Math.cos(a);
            leafOffsets[i][1] = r * (float)Math.sin(a);
        }
        
        particleSystem = new ParticleSystem(AssetManager.getInstance().getImage("tree-particle-1"));
        particleSystem.addEmitter(new TreeLeafEmitter(blockMap, world, container));
    }
    
    @Override
    protected final void processEntities(ImmutableBag<Entity> entities)
    {
        Image blockImage = null;
        
        updateTimers();
        
        int cx = (int)cameraSystem.getOffsetX();
        int cy = (int)cameraSystem.getOffsetY();
        
        int rowLowerBound = Math.max(0, (cy > 0 ? 0 : Math.abs(cy / blockMap.getBlockDepth()) - 1));
        int rowUpperBound = Math.min(blockMap.getDepth(), Math.abs(cy / blockMap.getBlockDepth()) + 1 
                + container.getHeight() / blockMap.getBlockDepth() + 1 + blockMap.getHeight());
        
        int columnLowerBound = Math.max(0, (cx > 0 ? 0 : Math.abs(cx / blockMap.getBlockWidth()) - 2));
        int columnUpperBound = Math.min(blockMap.getWidth(), Math.abs(cx / blockMap.getBlockWidth()) + 1 
                    + container.getWidth() / blockMap.getBlockWidth() + 2);
        
        for (int i = rowLowerBound; i < rowUpperBound; ++i)
        {
            for (int j = columnLowerBound; j < columnUpperBound; ++j)
            {
                for (int k = 0; k < blockMap.getHeight() && blockMap.getBlock(j, i, k) != BlockMap.BlockType.NONE; ++k)
                {                                        
                    if (i > 0 &&
                        i < blockMap.getDepth() - 1 &&
                        (blockMap.getTowerHeight(j, i + 1) >= blockMap.getTowerHeight(j, i) - 1 && 
                        k < blockMap.getTowerHeight(j,i)))
                        continue;
                    
                    blockImage = produceBlockImage(j, i, k, blockMap.getBlock(j, i, k));
                    computeLighting(blockImage, j, i, k);
                    blockImage.draw(cx + computeBlockHorizontalOffset(j, i, k, blockMap.getBlock(j, i, k)), 
                            cy + computeBlockVerticalOffset(j, i, k, blockMap.getBlock(j, i, k)));
                    
                    if (k == blockMap.getTowerHeight(j, i) && blockMap.dataPackage.trees[j][i] > 0)
                    {
                         renderTreeLeaves(j, i, k, blockMap.dataPackage.trees[j][i]);
                    }
                    else if (k == blockMap.getTowerHeight(j, i) && blockMap.dataPackage.trees[j][i] == 0
                             && blockMap.getBlock(j, i, k) == BlockMap.BlockType.GRASS && countHeightDifferences(j, i) < 6
                             && blockMap.dataPackage.objects[j][i] == 0)
                    {
                        float x = cx + computeBlockHorizontalOffset(j, i, k, BlockMap.BlockType.GRASS);
                        float y = cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) + 0 * blockMap.getBlockHeight();
                        
                        Image grassImage = AssetManager.getInstance().getImage("grass" + Integer.toString(2 + (int)Math.abs(((int)x - cx) % 5)) + "-" + Integer.toString((int)(22.0f / 2.0f + 20.0f / 4.0f * Math.sin(5 * (x - cx) + grassTime) + 20.0f / 4.0f * Math.sin(5 * (y - cy) + grassTime)))).getScaledCopy(124, 50);

                        computeLighting(grassImage, j, i, k);
                        grassImage.draw(cx + computeBlockHorizontalOffset(j, i, k, BlockMap.BlockType.GRASS) - 15,
                                        cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) + 0 * blockMap.getBlockHeight() - 20); 
                        
                        y = cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) + .6f * blockMap.getBlockHeight();
                                                
                        grassImage = AssetManager.getInstance().getImage("grass" + Integer.toString(2 + (int)Math.abs(((int)y - cy) % 5)) + "-" + Integer.toString((int)(22.0f / 2.0f + 20.0f / 4.0f * Math.sin(5 * (x - cx) + grassTime) + 20.0f / 4.0f * Math.sin(5 * (y - cy) + grassTime)))).getScaledCopy(124, 50);

                        computeLighting(grassImage, j, i, k);
                        grassImage.draw(cx + computeBlockHorizontalOffset(j, i, k, BlockMap.BlockType.GRASS) - 10,
                                        cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) + .6f * blockMap.getBlockHeight() - 20);                
                        
                        y = cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) + 1.2f * blockMap.getBlockHeight();                       
                        
                        grassImage = AssetManager.getInstance().getImage("grass4-" + Integer.toString((int)(22.0f / 2.0f + 20.0f / 4.0f * Math.sin(5 * (x - cx) + grassTime) + 20.0f / 4.0f * Math.sin(5 * (y - cy) + grassTime)))).getScaledCopy(124, 60);

                        computeLighting(grassImage, j, i, k);
                        grassImage.draw(cx + computeBlockHorizontalOffset(j, i, k, BlockMap.BlockType.GRASS) - 15,
                                        cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) + 1.2f * blockMap.getBlockHeight() - 20);    
                    }
                    else if (k == blockMap.getTowerHeight(j, i) && (blockMap.dataPackage.objects[j][i] == 1 || blockMap.dataPackage.objects[j][i] == 2))
                    {
                        float x = cx + computeBlockHorizontalOffset(j, i, k, BlockMap.BlockType.GRASS);
                        float y = cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) + 0 * blockMap.getBlockHeight();
                        
                        Image rockImage = AssetManager.getInstance().getImage("rock-" + Integer.toString(blockMap.dataPackage.objects[j][i]));
                        
                        computeLighting(rockImage, j, i, k);
                        rockImage.draw(cx + computeBlockHorizontalOffset(j, i, k, BlockMap.BlockType.GRASS),
                                       cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) - 30);
                    }
                    else if (k == blockMap.getTowerHeight(j, i) && blockMap.dataPackage.objects[j][i] >= 3)
                    {
                        float x = cx + computeBlockHorizontalOffset(j, i, k, BlockMap.BlockType.GRASS);
                        float y = cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) + 0 * blockMap.getBlockHeight();
                        
                        Image bushImage = AssetManager.getInstance().getImage("bush-" + Integer.toString(blockMap.dataPackage.objects[j][i] - 2));
                        
                        computeLighting(bushImage, j, i, k);
                        bushImage.draw(cx + computeBlockHorizontalOffset(j, i, k, BlockMap.BlockType.GRASS),
                                       cy + computeBlockVerticalOffset(j, i, k, BlockMap.BlockType.GRASS) - 30);                        
                    }
                }
            }
        }
        
        particleSystem.render();
        particleSystem.setPosition(cx, cy);
    }
    
    private int countHeightDifferences(int i, int j)
    {
        int count = 0;
        int here = blockMap.getTowerHeight(i, j);
        
        if (i > 0 && blockMap.getTowerHeight(i - 1, j) != here)
        {
            count++;
        }
        
        if (i < blockMap.getWidth() - 1 && blockMap.getTowerHeight(i + 1, j) != here)
        {
            count++;
        }
        
        if (j > 0 && blockMap.getTowerHeight(i, j - 1) != here)
        {
            count++;
        }
        
        if (j < blockMap.getDepth() - 1 && blockMap.getTowerHeight(i, j + 1) != here)
        {
            count++;
        }
        
        if (i > 0 && j > 0 && blockMap.getTowerHeight(i - 1, j - 1) != here)
        {
            count++;
        }
        
        if (i > 0 && j < blockMap.getDepth() - 1 && blockMap.getTowerHeight(i - 1, j + 1) != here)
        {
            count++;
        }
        
        if (i < blockMap.getWidth() - 1 && j > 0 && blockMap.getTowerHeight(i + 1, j - 1) != here)
        {
            count++;
        }
        
        if (i < blockMap.getWidth() - 1 && j < blockMap.getDepth() - 1 && blockMap.getTowerHeight(i + 1, j + 1) != here)
        {
            count++;
        }
        
        return count;
    }
    
    private void renderTreeLeaves(int i, int j, int k, int treeType)
    {
        int cx = (int)cameraSystem.getOffsetX();
        int cy = (int)cameraSystem.getOffsetY();
        
        Image treeLeaves = AssetManager.getInstance().getImage("tree-leaves-" + Integer.toString(treeType));
        
        for (int l = 0; l < numLeaves; ++l)
        {
            float x = cx + computeBlockHorizontalOffset(i, j, k, BlockMap.BlockType.WOOD) + leafOffsets[l][0] - 30
                      - computeWoodHorizontalOffset(i, j, k);
            float y = cy + computeBlockVerticalOffset(i, j, k, BlockMap.BlockType.WOOD) + leafOffsets[l][1]
                      - computeWoodVerticalOffset(i, j, k);
            
            float factor = 0.7f + 0.3f * (leafOffsets[l][0] * leafOffsets[l][0] + leafOffsets[l][1] * leafOffsets[l][1]) / (300 * 300);
            
            treeLeaves.setColor(Image.TOP_LEFT, factor, factor, factor, 1);
            treeLeaves.setColor(Image.TOP_RIGHT, factor, factor, factor, 1);
            treeLeaves.setColor(Image.BOTTOM_LEFT, factor, factor, factor, 1);
            treeLeaves.setColor(Image.BOTTOM_RIGHT, factor, factor, factor, 1);
            
            float angle = 8.0f * ((float)Math.sin(y - cy + (float)waveTime) - (float)Math.sin(x - cx + (float)waveTime));
            
            treeLeaves.rotate(angle);
            treeLeaves.draw(x + computeWoodHorizontalOffset(i, j, k), y + computeWoodVerticalOffset(i, j, k));
            treeLeaves.rotate(-angle);
        }
    }
    
    private void updateTimers()
    {
        int delta = world.getDelta();
                          
        particleSystem.update(delta);
        
        time += delta * timeDirection;
        if (time >= 1000)
        {
            timeDirection = -1;
            time = 999;
        }
        else if (time < 91)
        {
            timeDirection = 1;
            time = 91;
        }
        
        grassTime += (float)delta * grassTimeDirection / 750.0f;
        if (grassTime >= 2 * Math.PI)
        {
            grassTime = grassTime - 2 * (float)Math.PI;
        }

        waveTime += (float)delta / 300.0f;
        if (waveTime > 2 * Math.PI)
        {
            waveTime = waveTime - 2 * (float)Math.PI;
        }
        
        woodTime += (float)delta / 900.0f;
        if (woodTime > 2 * Math.PI)
        {
            woodTime = woodTime - 2 * (float)Math.PI;
        }
    }
    
    private float computeBlockHorizontalOffset(int i, int j, int k, BlockMap.BlockType blockType)
    {
        float factor = 1.0f;
        float add = 0.0f;
        
        if (blockType == BlockMap.BlockType.WOOD)
        {
            factor = 1.0f - (float)k / (3.0f * (float)blockMap.getHeight());
            add = computeWoodHorizontalOffset(i, j, k);
        }
        
        return i * blockMap.getBlockWidth() + (1.0f - factor) * blockMap.getBlockWidth() / 2.0f + add;
    }
    
    private float computeBlockVerticalOffset(int i, int j, int k, BlockMap.BlockType blockType)
    {
        float offset = j * blockMap.getBlockDepth() - k * blockMap.getBlockHeight() - blockMap.getBlockHeight();
        
        if (blockType == BlockMap.BlockType.WATER)
        {
            offset += computeWaterOffset(i, j);
        }
        else if (blockType == BlockMap.BlockType.WOOD)
        {
            offset += computeWoodVerticalOffset(i, j, k);
        }
        
        return offset;
    }
    
    private float computeWaterOffset(int i, int j)
    {
        return (((float)blockMap.getBlockHeight() + (float)blockMap.getBlockDepth()) / 4.0f
                      + ((float)Math.sin((j + (float)(waveTime))) - (float)Math.sin((i + (float)(waveTime)))) 
                      * ((float)blockMap.getBlockHeight() + (float)blockMap.getBlockDepth()) / 5.0f)
                      * blockMap.getShoreDistance(i, j) / 18.0f;
    }
    
    private float computeWoodHorizontalOffset(int i, int j, int k)
    {
        float factor = 30 * (k - computeTreeBase(i, j)) / (blockMap.getHeight() - 1);
        
        return ((float)Math.cos(i + (float)(woodTime)) - (float)Math.sin(j + (float)(woodTime))) * factor;
    }
    
    private float computeWoodVerticalOffset(int i, int j, int k)
    {
        float factor = 30 * (k - computeTreeBase(i, j)) / (blockMap.getHeight() - 1);
        
        return ((float)Math.sin(i + (float)(woodTime)) - (float)Math.cos(j + (float)(woodTime))) * factor;
    }
    
    private Image produceBlockImage(int i, int j, int k, BlockMap.BlockType blockType)
    {
        Image blockImage = null;
        
        switch (blockType)
        {
            case GRASS:
                blockImage = AssetManager.getInstance().getImage("block-grass");
                break;
                
            case DIRT:
                blockImage = AssetManager.getInstance().getImage("block-dirt");
                break;
                
            case WATER:
                blockImage = AssetManager.getInstance().getImage("block-water-" + Integer.toString(time / 91));
                break;
                
            case STONE:
                blockImage = AssetManager.getInstance().getImage("block-stone");
                break;
                
            case SAND:
                blockImage = AssetManager.getInstance().getImage("block-sand");
                break;
                
            case WOOD:
                blockImage = AssetManager.getInstance().getImage("block-wood").getScaledCopy(1.0f - (float)k / (3.0f * (float)blockMap.getHeight()));
                break;
        }
        
        return blockImage;
    }
    
    private int computeTreeBase(int i, int j)
    {
        if (blockMap.dataPackage.trees[i][j] > 0)
        {
            int treeStart = 0;

            for (; blockMap.getBlock(i, j, treeStart) != BlockMap.BlockType.WOOD; ++treeStart) ;
            
            return treeStart;
        }
        
        return -1;
    }
    
    private void computeLighting(Image blockImage, int j, int i, int k)
    {
        blockImage.setColor(Image.TOP_LEFT, shadowOverlay[j][i] * 1, shadowOverlay[j][i] * 1, shadowOverlay[j][i] * 1, 1);
        blockImage.setColor(Image.TOP_RIGHT, shadowOverlay[j+1][i] * 1, shadowOverlay[j+1][i] * 1, shadowOverlay[j+1][i] * 1, 1);
        blockImage.setColor(Image.BOTTOM_LEFT, shadowOverlay[j][i+1] * 1, shadowOverlay[j][i+1] * 1, shadowOverlay[j][i+1] * 1, 1);
        blockImage.setColor(Image.BOTTOM_RIGHT, shadowOverlay[j+1][i+1] * 1, shadowOverlay[j+1][i+1] * 1, shadowOverlay[j+1][i+1] * 1, 1);

        if (k == blockMap.getTowerHeight(j, i))
        {
            if (j < blockMap.getWidth() - 1 && blockMap.getTowerHeight(j + 1, i) > blockMap.getTowerHeight(j, i))
            {
                blockImage.setColor(Image.TOP_RIGHT, shadowOverlay[j+1][i] * 0.45f, shadowOverlay[j+1][i] * 0.45f, shadowOverlay[j+1][i] * 0.45f, 1);
                blockImage.setColor(Image.BOTTOM_RIGHT,  shadowOverlay[j+1][i+1] * 0.45f, shadowOverlay[j+1][i+1] * 0.45f, shadowOverlay[j+1][i+1] * 0.45f, 1);
            }
            else if (j < blockMap.getWidth() - 1 && blockMap.getBlock(j, i, k) == BlockMap.BlockType.WATER 
                     && j < blockMap.getWidth() - 1 && blockMap.getBlock(j+1, i, blockMap.getTowerHeight(j + 1, i)) == BlockMap.BlockType.WATER)
            {
                float waterOffsetHere = computeWaterOffset(j, i);
                float waterOffsetThere = computeWaterOffset(j + 1, i);
                float ratio = 2*(waterOffsetHere - waterOffsetThere) 
                              / (2.0f * ((float)blockMap.getBlockHeight() + (float)blockMap.getBlockDepth()) / 5.0f);
                if (ratio < 0)
                    ratio = 0;

                blockImage.setColor(Image.TOP_RIGHT, shadowOverlay[j+1][i] * (1.0f - 0.55f * ratio), 
                                                     shadowOverlay[j+1][i] * (1.0f - 0.55f * ratio), 
                                                     shadowOverlay[j+1][i] * (1.0f - 0.55f * ratio), 
                                                     1.0f);
                blockImage.setColor(Image.BOTTOM_RIGHT, shadowOverlay[j+1][i+1] * (1.0f - 0.55f * ratio), 
                                                        shadowOverlay[j+1][i+1] * (1.0f - 0.55f * ratio), 
                                                        shadowOverlay[j+1][i+1] * (1.0f - 0.55f * ratio), 
                                                        1.0f);
            }            

            if (j > 0 && blockMap.getTowerHeight(j - 1, i) > blockMap.getTowerHeight(j, i))
            {
                blockImage.setColor(Image.TOP_LEFT, shadowOverlay[j][i] * 0.45f, shadowOverlay[j][i] * 0.45f, shadowOverlay[j][i] * 0.45f, 1);
                blockImage.setColor(Image.BOTTOM_LEFT, shadowOverlay[j][i+1] * 0.45f, shadowOverlay[j][i+1] * 0.45f, shadowOverlay[j][i+1] * 0.45f, 1);
            }
            else if (j < blockMap.getWidth() - 1 && blockMap.getBlock(j, i, k) == BlockMap.BlockType.WATER 
                     && j > 0 && blockMap.getBlock(j-1, i, blockMap.getTowerHeight(j - 1, i)) == BlockMap.BlockType.WATER)
            {
                float waterOffsetHere = computeWaterOffset(j, i);
                float waterOffsetThere = computeWaterOffset(j + 1, i);
                float ratio = 2*(waterOffsetHere - waterOffsetThere) 
                              / (2.0f * ((float)blockMap.getBlockHeight() + (float)blockMap.getBlockDepth()) / 5.0f);
                if (ratio < 0)
                    ratio = 0;

                blockImage.setColor(Image.TOP_RIGHT, shadowOverlay[j][i] * (1.0f - 0.55f * ratio), 
                                                     shadowOverlay[j][i] * (1.0f - 0.55f * ratio), 
                                                     shadowOverlay[j][i] * (1.0f - 0.55f * ratio), 
                                                     1.0f);
                blockImage.setColor(Image.BOTTOM_RIGHT, shadowOverlay[j][i+1] * (1.0f - 0.55f * ratio), 
                                                        shadowOverlay[j][i+1] * (1.0f - 0.55f * ratio), 
                                                        shadowOverlay[j][i+1] * (1.0f - 0.55f * ratio), 
                                                        1.0f);
            } 

            if (i < blockMap.getDepth() - 1 && blockMap.getTowerHeight(j, i + 1) > blockMap.getTowerHeight(j, i))
            {
                blockImage.setColor(Image.BOTTOM_LEFT, shadowOverlay[j][i+1] * 0.45f, shadowOverlay[j][i+1] * 0.45f, shadowOverlay[j][i+1] * 0.45f, 1);
                blockImage.setColor(Image.BOTTOM_RIGHT, shadowOverlay[j+1][i+1] * 0.45f, shadowOverlay[j+1][i+1] * 0.45f, shadowOverlay[j+1][i+1] * 0.45f, 1);
            }
            else if (j < blockMap.getWidth() - 1 && blockMap.getBlock(j, i, k) == BlockMap.BlockType.WATER 
                     && i < blockMap.getDepth() - 1 && blockMap.getBlock(j, i+1, blockMap.getTowerHeight(j, i+1)) == BlockMap.BlockType.WATER)
            {
                float waterOffsetHere = computeWaterOffset(j, i);
                float waterOffsetThere = computeWaterOffset(j, i+1);
                float ratio = 2*(waterOffsetHere - waterOffsetThere) 
                              / (2.0f * ((float)blockMap.getBlockHeight() + (float)blockMap.getBlockDepth()) / 5.0f);
                if (ratio < 0)
                    ratio = 0;

                blockImage.setColor(Image.TOP_RIGHT, shadowOverlay[j][i+1] * (1.0f - 0.55f * ratio), 
                                                     shadowOverlay[j][i+1] * (1.0f - 0.55f * ratio), 
                                                     shadowOverlay[j][i+1] * (1.0f - 0.55f * ratio), 
                                                     1.0f);
                blockImage.setColor(Image.BOTTOM_RIGHT, shadowOverlay[j+1][i+1] * (1.0f - 0.55f * ratio), 
                                                        shadowOverlay[j+1][i+1] * (1.0f - 0.55f * ratio), 
                                                        shadowOverlay[j+1][i+1] * (1.0f - 0.55f * ratio), 
                                                        1.0f);
            }             

            if (i > 0 && blockMap.getTowerHeight(j, i - 1) > blockMap.getTowerHeight(j, i))
            {
                blockImage.setColor(Image.TOP_LEFT, shadowOverlay[j][i] * 0.45f, shadowOverlay[j][i] * 0.45f, shadowOverlay[j][i] * 0.45f, 1);
                blockImage.setColor(Image.TOP_RIGHT, shadowOverlay[j+1][i] * 0.45f, shadowOverlay[j+1][i] * 0.45f, shadowOverlay[j+1][i] * 0.45f, 1);
            }
            else if (j < blockMap.getWidth() - 1 && blockMap.getBlock(j, i, k) == BlockMap.BlockType.WATER 
                     && i > 0 && blockMap.getBlock(j, i-1, blockMap.getTowerHeight(j, i-1)) == BlockMap.BlockType.WATER)
            {
                float waterOffsetHere = computeWaterOffset(j, i);
                float waterOffsetThere = computeWaterOffset(j, i-1);
                float ratio = 2*(waterOffsetHere - waterOffsetThere) 
                              / (2.0f * ((float)blockMap.getBlockHeight() + (float)blockMap.getBlockDepth()) / 5.0f);
                if (ratio < 0)
                    ratio = 0;

                blockImage.setColor(Image.TOP_RIGHT, shadowOverlay[j][i] * (1.0f - 0.55f * ratio), 
                                                     shadowOverlay[j][i] * (1.0f - 0.55f * ratio), 
                                                     shadowOverlay[j][i] * (1.0f - 0.55f * ratio), 
                                                     1.0f);
                blockImage.setColor(Image.BOTTOM_RIGHT, shadowOverlay[j+1][i] * (1.0f - 0.55f * ratio), 
                                                        shadowOverlay[j+1][i] * (1.0f - 0.55f * ratio), 
                                                        shadowOverlay[j+1][i] * (1.0f - 0.55f * ratio), 
                                                        1.0f);
            }                         

            if (j < blockMap.getWidth() - 1 && i < blockMap.getDepth() - 1 && 
                blockMap.getTowerHeight(j + 1, i + 1) > blockMap.getTowerHeight(j, i))
            {
                blockImage.setColor(Image.BOTTOM_RIGHT, shadowOverlay[j+1][i+1] * 0.45f, shadowOverlay[j+1][i+1] * 0.45f, shadowOverlay[j+1][i+1] * 0.45f, 1);
            }

            if (j < blockMap.getWidth() - 1 && i > 0 && blockMap.getTowerHeight(j + 1, i - 1) > blockMap.getTowerHeight(j, i))
            {
                blockImage.setColor(Image.TOP_RIGHT, shadowOverlay[j+1][i] * 0.45f, shadowOverlay[j+1][i] * 0.45f, shadowOverlay[j+1][i] * 0.45f, 1);
            }

            if (j > 0 && i < blockMap.getDepth() - 1 && blockMap.getTowerHeight(j - 1, i + 1) > blockMap.getTowerHeight(j, i))
            {
                blockImage.setColor(Image.BOTTOM_LEFT,  shadowOverlay[j][i+1] * 0.45f, shadowOverlay[j][i+1] * 0.45f, shadowOverlay[j][i+1] * 0.45f, 1);
            }

            if (j > 0 && i > 0 && blockMap.getTowerHeight(j - 1, i - 1) > blockMap.getTowerHeight(j, i))
            {
                blockImage.setColor(Image.TOP_LEFT, shadowOverlay[j][i] * 0.45f, shadowOverlay[j][i] * 0.45f, shadowOverlay[j][i] * 0.45f, 1);
            }
        }
        
        if (blockMap.getBlock(j, i, k) == BlockMap.BlockType.WOOD)
        {
            float factor = 0.5f + 0.5f * (k - computeTreeBase(j, i)) / (blockMap.getHeight() - 1);
            
            blockImage.setColor(Image.TOP_LEFT, factor, factor, factor, 1);
            blockImage.setColor(Image.TOP_RIGHT, factor, factor, factor, 1);
            blockImage.setColor(Image.BOTTOM_LEFT, factor, factor, factor, 1);
            blockImage.setColor(Image.BOTTOM_RIGHT, factor, factor, factor, 1);
        }
    }
    
    @Override
    protected void added(Entity e)
    {
        Transform transform = transformMapper.get(e);
    }
    
    @Override
    protected void removed(Entity e)
    {
        spatials.set(e.getId(), null);
        
    }
    
    @Override
    protected boolean checkProcessing()
    {
        return true;
    }
}