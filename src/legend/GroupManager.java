package legend;

import java.util.HashMap;
import java.util.Map;

public class GroupManager
{
    private World world;
    private Bag<Entity> EMPTY_BAG;
    private Map<String, Bag<Entity>> entitiesByGroup;
    private Bag<String> groupByEntity;
    
    public GroupManager(World world)
    {
        this.world = world;
        entitiesByGroup = new HashMap<String, Bag<Entity>>();
        groupByEntity = new Bag<String>();
        EMPTY_BAG = new Bag<Entity>();
    }
    
    public void set (String group, Entity e)
    {
        remove(e);
        
        Bag<Entity> entities = entitiesByGroup.get(group);
        
        if (entities == null)
        {
            entities = new Bag<Entity>();
            entitiesByGroup.put(group, entities);
        }
        
        entities.add(e);
        
        groupByEntity.set(e.getId(), group);
    }
    
    public ImmutableBag<Entity> getEntities(String group)
    {
        Bag<Entity> bag = entitiesByGroup.get(group);
        
        if (bag == null)
        {
            return EMPTY_BAG;
        }
        
        return bag;
    }
    
    public void remove(Entity e)
    {
        if (e.getId() < groupByEntity.getCapacity())
        {
            String group = groupByEntity.get(e.getId());
            
            if (group != null)
            {
                groupByEntity.set(e.getId(), null);
                
                Bag<Entity> entities = entitiesByGroup.get(group);
                
                if (entities != null)
                {
                    entities.remove(e);
                }
            }
        }
    }
    
    public String getGroupOf(Entity e)
    {
        if (e.getId() < groupByEntity.getCapacity())
        {
            return groupByEntity.get(e.getId());
        }
        
        return null;
    }
    
    public boolean isGrouped(Entity e)
    {
        return (getGroupOf(e) != null);
    }
}