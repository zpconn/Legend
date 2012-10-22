package legend;

public final class Entity
{
    private int id;
    private long uniqueId;
    private long typeBits;
    private long systemBits;
    
    private World world;
    private EntityManager entityManager;
    
    protected Entity(World world, int id)
    {
        this.world = world;
        this.entityManager = world.getEntityManager();
        this.id = id;
    }
    
    public int getId()
    {
        return id;
    }
    
    protected void setUniqueId(long uniqueId)
    {
        this.uniqueId = uniqueId;
    }
    
    public long getUniqueId()
    {
        return uniqueId;
    }
    
    protected long getTypeBits()
    {
        return typeBits;
    }
    
    protected void addTypeBit(long bit)
    {
        typeBits |= bit;
    }
    
    protected void removeTypeBit(long bit)
    {
        typeBits &= ~bit;
    }
    
    protected long getSystemBits()
    {
        return systemBits;
    }
    
    protected void addSystemBit(long bit)
    {
        systemBits |= bit;
    }
    
    protected void removeSystemBit(long bit)
    {
        systemBits &= ~bit;
    }
    
    protected void setSystemBits(long systemBits)
    {
        this.systemBits = systemBits;
    }
    
    protected void setTypeBits(long typeBits)
    {
        this.typeBits = typeBits;
    }
    
    protected void reset()
    {
        systemBits = 0;
        typeBits = 0;
    }
    
    @Override
    public String toString()
    {
        return "[Entity["+id+"]";
    }
    
    public void addComponent(Component component)
    {
        entityManager.addComponent(this, component);
    }
    
    public void removeComponent(Component component)
    {
        entityManager.removeComponent(this, component);
    }
    
    public void removeComponent(ComponentType type)
    {
        entityManager.removeComponent(this, type);
    }
    
    public boolean isActive()
    {
        return entityManager.isActive(id);
    }
    
    public Component getComponent(ComponentType type)
    {
        return entityManager.getComponent(this, type);
    }
    
    public <T extends Component> T getComponent(Class<T> type)
    {
        return type.cast(getComponent(ComponentTypeManager.getTypeFor(type)));
    }
    
    public ImmutableBag<Component> getComponents()
    {
        return entityManager.getComponents(this);
    }
    
    public void refresh()
    {
        world.refreshEntity(this);
    }
    
    public void delete()
    {
        world.deleteEntity(this);
    }
    
    public void setGroup(String group)
    {
        world.getGroupManager().set(group, this);
    }
    
    public void setTag(String tag)
    {
        world.getTagManager().register(tag, this);
    }
}