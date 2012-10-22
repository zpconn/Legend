package legend;

public class World
{
    private SystemManager systemManager;
    private EntityManager entityManager;
    private TagManager tagManager;
    private GroupManager groupManager;
    
    private int delta;
    private Bag<Entity> refreshed;
    private Bag<Entity> deleted;
    
    public World()
    {
        entityManager = new EntityManager(this);
        systemManager = new SystemManager(this);
        tagManager = new TagManager(this);
        groupManager = new GroupManager(this);
        
        refreshed = new Bag<Entity>();
        deleted = new Bag<Entity>();
    }
    
    public GroupManager getGroupManager()
    {
        return groupManager;
    }
    
    public SystemManager getSystemManager()
    {
        return systemManager;
    }
    
    public EntityManager getEntityManager()
    {
        return entityManager;
    }
    
    public TagManager getTagManager()
    {
        return tagManager;
    }
    
    public int getDelta()
    {
        return delta;
    }
    
    public void setDelta(int delta)
    {
        this.delta = delta;
    }
    
    public void deleteEntity(Entity e)
    {
        if (!deleted.contains(e))
        {
            deleted.add(e);
        }
    }
    
    public void refreshEntity(Entity e)
    {
        refreshed.add(e);
    }
    
    public Entity createEntity()
    {
        return entityManager.create();
    }
    
    public Entity getEntity(int entityId)
    {
        return entityManager.getEntity(entityId);
    }
    
    public void loopStart()
    {
        if (!refreshed.isEmpty())
        {
            for (int i = 0; i < refreshed.size(); ++i)
            {
                entityManager.refresh(refreshed.get(i));
            }
            
            refreshed.clear();
        }
        
        if (!deleted.isEmpty())
        {
            for (int i = 0; i < deleted.size(); ++i)
            {
                Entity e = deleted.get(i);
                groupManager.remove(e);
                entityManager.remove(e);
            }
            
            deleted.clear();
        }
    }
}