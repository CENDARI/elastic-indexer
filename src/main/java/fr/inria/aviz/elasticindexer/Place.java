package fr.inria.aviz.elasticindexer;


/**
 * Description for a Place
 */
public class Place {
    public String name;
    public String location;
    
    /**
     * Default constructor.
     */
    public Place() { }
    /**
     * Creates a Place with only a name.
     * @param name the name
     */
    public Place(String name) {
        this.name = name;
    }
    
    /**
     * Creates a Place with a name and a location.
     * @param name the name
     * @param location the location
     */
    public Place(String name, String location) {
        this.name = name;
        this.location = location;
    }
}
