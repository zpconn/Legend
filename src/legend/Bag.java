package legend;

public class Bag<E> implements ImmutableBag<E>
{
    private Object[] data;
    private int size = 0;
    
    public Bag()
    {
        this(16);
    }
    
    public Bag(int capacity)
    {
        data = new Object[capacity];
    }
    
    public E remove(int index)
    {
        Object o = data[index];
        data[index] = data[--size];
        data[size] = null;
        return (E)o;
    }
    
    public E removeLast()
    {
        if (size > 0)
        {
            Object o = data[--size];
            data[size] = null;
            return (E)o;
        }
        
        return null;
    }
    
    public boolean remove(E o)
    {
        for (int i = 0; i < size; ++i)
        {
            Object o1 = data[i];
            
            if (o == o1)
            {
                data[i] = data[--size];
                data[size] = null;
                return true;
            }
        }
        
        return false;
    }
    
    public boolean contains(E o)
    {
        for (int i = 0; i < size; ++i)
        {
            if (o == data[i])
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean removeAll(Bag<E> bag)
    {
        boolean modified = false;
        
        for (int i = 0; i < bag.size(); ++i)
        {
            Object o1 = bag.get(i);
            
            for (int j = 0; j < size; ++j)
            {
                Object o2 = data[j];
                
                if (o1 == o2)
                {
                    remove(j);
                    j--;
                    modified = true;
                    break;
                }
            }
        }
        
        return modified;
    }
    
    public E get(int index)
    {
        return (E)data[index];
    }
    
    public int size()
    {
        return size;
    }
    
    public int getCapacity()
    {
        return data.length;
    }
    
    public boolean isEmpty()
    {
        return (size == 0);
    }
    
    public void add(E o)
    {
        if (size == data.length)
        {
            grow();
        }
        
        data[size++] = o;
    }
    
    public void set(int index, E o)
    {
        if (index >= data.length)
        {
            grow(index * 2);
            size = index + 1;
        }
        else if (index >= size)
        {
            size = index + 1;
        }
        
        data[index] = o;
    }
    
    private void grow()
    {
        int newCapacity = (data.length * 3) / 2 + 1;
        grow(newCapacity);
    }
    
    private void grow(int newCapacity)
    {
        Object[] oldData = data;
        data = new Object[newCapacity];
        System.arraycopy(oldData, 0, data, 0, oldData.length);
    }
    
    public void clear()
    {
        for (int i = 0; i < size; ++i)
        {
            data[i] = null;
        }
        
        size = 0;
    }
    
    public void addAll(Bag<E> items)
    {
        for (int i = 0; i < items.size(); ++i)
        {
            add(items.get(i));
        }
    }
}