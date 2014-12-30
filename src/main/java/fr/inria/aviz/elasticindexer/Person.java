package fr.inria.aviz.elasticindexer;

/**
 * 
 * Description for a Person
 */
public class Person {
    public String name;
    public String email;
    
    /**
     * Default constructor. 
     */
    public Person() {
    }

    /**
     * Creates a Person with only a name.
     * @param name the name
     */
    public Person(String name) { 
        this.name = name; 
    }
    
    /**
     * Creates a Person.
     * @param name the name
     * @param email the email
     */
    public Person(String name, String email) { 
        this.name = name;
        this.email = email;
    }
}