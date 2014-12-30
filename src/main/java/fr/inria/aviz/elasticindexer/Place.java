package fr.inria.aviz.elasticindexer;


/**
 * Description for a Place
 */
public class Place {
    /** Full text name */
    public String name;
    /** Location as in elasticsearch */
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object that) {
        if ( this == that ) return true;
        if ( !(that instanceof Place) ) return false;
        Place p = (Place)that;
        return ((this.name == p.name) || (this.name != null && this.name.equals(p.name))) &&
                ((this.location == p.location) || (this.location != null && this.location.equals(p.location)));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31*result + (name !=null ? name.hashCode() : 0);
        result = 31*result + (location  !=null ? location.hashCode() : 0);
       
        return result;
    }
}
