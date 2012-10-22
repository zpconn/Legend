package legend;

public interface ImmutableBag<E>
{
    E get(int index);
    
    int size();
    
    boolean isEmpty();
}