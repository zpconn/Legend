package legend;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.loading.LoadingList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class AssetManager
{
    private static AssetManager instance = new AssetManager();
    
    private Map<String, Sound> soundMap;
    private Map<String, Image> imageMap;
    
    private AssetManager()
    {
        soundMap = new HashMap<String, Sound>();
        imageMap = new HashMap<String, Image>();
    }
    
    public final static AssetManager getInstance()
    {
        return instance;
    }
    
    public void loadResources(InputStream is)
            throws SlickException
    {
        loadResources(is, false);
    }
    
    public void loadResources(InputStream is, boolean deferred)
            throws SlickException
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;

        try
        {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new SlickException("Could not load resources", e);
        }


        Document doc = null;

        try
        {
            doc = docBuilder.parse(is);
        }
        catch (SAXException e)
        {
            throw new SlickException("Could not load resources", e);
        }
        catch (IOException e)
        {
            throw new SlickException("Could not load resources", e);
        }

        doc.getDocumentElement().normalize();

        NodeList listResources = doc.getElementsByTagName("resource");

        int totalResources = listResources.getLength();

        if (deferred)
        {
            LoadingList.setDeferredLoading(true);
        }

        for (int i = 0; i < totalResources; ++i)
        {
            Node resourceNode = listResources.item(i);

            if (resourceNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element resourceElement = (Element)resourceNode;

                String type = resourceElement.getAttribute("type");

                if(type.equals("image"))
                {
                    addElementAsImage(resourceElement);
                }
                else if (type.equals("sound"))
                {
                    addElementAsSound(resourceElement);
                }
            }
        }
    }
    
    public Sound loadSound(String id, String path) throws SlickException
    {
        if (path == null || path.length() == 0)
        {
            throw new SlickException("Sound resource [" + id + "] has invalid path");
        }

        Sound sound = null;

        try
        {
            sound = new Sound(path);
        }
        catch (SlickException e)
        {
            throw new SlickException("Could not load sound", e);
        }

        soundMap.put(id, sound);

        return sound;
    }

    public final Sound getSound(String id)
    {
        return soundMap.get(id);
    }

    private final void addElementAsSound(Element resourceElement) throws SlickException
    {
        loadSound(resourceElement.getAttribute("id"), resourceElement.getTextContent());
    }

    public Image loadImage(String id, String path) throws SlickException
    {
        if (path == null || path.length() == 0)
        {
            throw new SlickException("Image resource [" + id + "] has invalid path");
        }

        Image image = null;

        try
        {
            image = new Image(path);
        }
        catch (SlickException e)
        {
            throw new SlickException("Could not load image", e);
        }

        imageMap.put(id, image);

        return image;
    }

    public final Image getImage(String id)
    {
        return imageMap.get(id);
    }

    private final void addElementAsImage(Element resourceElement) throws SlickException
    {
        loadImage(resourceElement.getAttribute("id"), resourceElement.getTextContent());
    }
}