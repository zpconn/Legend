package legend;

public abstract class EntityProcessingSystem extends EntitySystem
{
    public EntityProcessingSystem(Class<? extends Component> requiredType, Class<? extends Component>... otherTypes)
    {
        super(getMergedTypes(requiredType, otherTypes));
    }
    
    protected abstract void process(Entity e);
    
    @Override
    protected final void processEntities(ImmutableBag<Entity> entities)
    {
        for (int i = 0, s = entities.size(); i < s; ++i)
        {
            process(entities.get(i));
        }
    }
    
    @Override
    protected boolean checkProcessing()
    {
        return true;
    }
}