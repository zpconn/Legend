package legend;

import java.util.HashMap;
import java.util.Map;

public class TagManager
{
    private World world;
    private Map<String, Entity> entityByTag;
    
    public TagManager(World world)
    {
        this.world = world;
        entityByTag = new HashMap<String, Entity>();
    }
    
    public void register(String tag, Entity e)
    {
        entityByTag.put(tag, e);
    }
    
    public void unregister(String tag)
    {
        entityByTag.remove(tag);
    }
    
    public boolean isRegistered(String tag)
    {
        return entityByTag.containsKey(tag);
    }
    
    public Entity getEntity(String tag)
    {
        return entityByTag.get(tag);
    }
}