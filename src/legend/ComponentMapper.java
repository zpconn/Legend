package legend;

public class ComponentMapper<T extends Component>
{
    private ComponentType type;
    private EntityManager entityManager;
    private Class<T> classType;
    
    public ComponentMapper(Class<T> type, EntityManager entityManager)
    {
        this.entityManager = entityManager;
        this.type = ComponentTypeManager.getTypeFor(type);
        this.classType = type;
    }
    
    public T get(Entity e)
    {
        return classType.cast(entityManager.getComponent(e, type));
    }
}