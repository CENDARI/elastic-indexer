package fr.inria.aviz.elasticindexer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Class DocumentInfo
 * 
 * @author Jean-Daniel Fekete
 */
public class DocumentInfo {
    private String application;
    private String[] artifact;
    private Person[] contributor;
    private Person[] creator;
    private String[] date;
    private String[] event;
    private String format;
    private String[] language;
    private String[] org;
    private Person[] person;
    private Place[] place;
    private String[] publisher;
    private String[] ref;
    private String[] tag;
    private String text;
    private String uri;
    private String[] groups_allowed;
    private String[] users_allowed;
    private Map<String , Object> otherProperties = new HashMap<String , Object>();
    /** Date printer for elasticsearch */
    public final DateTimeFormatter DATE_PRINTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

    
    /**
     * @return the application
     */
    public String getApplication() {
        return application;
    }
    /**
     * @param application the application to set
     */
    public void setApplication(String application) {
        this.application = application;
    }
    /**
     * @return the artifact
     */
    public String[] getArtifact() {
        return artifact;
    }
    /**
     * @param artifact the artifact to set
     */
    public void setArtifact(String... artifact) {
        this.artifact = artifact;
    }
    /**
     * @return the contributor
     */
    public Person[] getContributor() {
        return contributor;
    }
    /**
     * @param name the contributor names to set
     */
    public void setContributorName(String... name) {
        Person[] person = new Person[name.length];
        for (int i = 0; i < name.length; i++) {
            person[i] = new Person(name[i]);
        }
        setContributor(person);
    }

    /**
     * @param contributor the contributor to set
     */
    public void setContributor(Person... contributor) {
        this.contributor = contributor;
    }
    /**
     * @return the creator
     */
    public Person[] getCreator() {
        return creator;
    }
    /**
     * @param creator the creator to set
     */
    public void setCreator(Person... creator) {
        this.creator = creator;
    }
    
    /**
     * @param name the creator names to set
     */
    public void setCreatorName(String... name) {
        Person[] person = new Person[name.length];
        for (int i = 0; i < name.length; i++) {
            person[i] = new Person(name[i]);
        }
        setCreator(person);
    }
    
    /**
     * @return the date
     */
    public String[] getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate(String... date) {
        this.date = date;
    }
    
    /**
     * @param date the date to set
     */
    public void setDateJavaDate(Date... date) {
        String[] res = new String[date.length];
        for (int i = 0; i < date.length; i++) {
            res[i] = DATE_PRINTER.print(new DateTime(date[i]));
        }
        setDate(res);
    }
    
    /**
     * @param date the date to set
     */
    public void setDateJodaDate(DateTime... date) {
        String[] res = new String[date.length];
        for (int i = 0; i < date.length; i++) {
            res[i] = DATE_PRINTER.print(date[i]);
        }
        setDate(res);
    }


    /**
     * @return the event
     */
    public String[] getEvent() {
        return event;
    }
    /**
     * @param event the event to set
     */
    public void setEvent(String... event) {
        this.event = event;
    }
    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }
    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }
    /**
     * @return the language
     */
    public String[] getLanguage() {
        return language;
    }
    /**
     * @param language the language to set
     */
    public void setLanguage(String... language) {
        this.language = language;
    }
    /**
     * @return the org
     */
    public String[] getOrg() {
        return org;
    }
    /**
     * @param org the org to set
     */
    public void setOrg(String... org) {
        this.org = org;
    }
    /**
     * @return the person
     */
    public Person[] getPerson() {
        return person;
    }
    /**
     * @param person the person to set
     */
    public void setPerson(Person ... person) {
        this.person = person;
    }
    
    /**
     * @param name the person names to set
     */
    public void setPersonName(String... name) {
        Person[] person = new Person[name.length];
        for (int i = 0; i < name.length; i++) {
            person[i] = new Person(name[i]);
        }
        setPerson(person);
    }
    /**
     * @return the place
     */
    public Place[] getPlace() {
        return place;
    }
    
    /**
     * @param name the place names to set
     */
    public void setPlaceName(String ... name) {
        Place[] place= new Place[name.length];
        for (int i = 0; i < name.length; i++) {
            place[i] = new Place(name[i]);
        }
        setPlace(place);
    }
    /**
     * @param place the place to set
     */
    public void setPlace(Place ... place) {
        this.place = place;
    }
    /**
     * @return the publisher
     */
    public String[] getPublisher() {
        return publisher;
    }
    /**
     * @param publisher the publisher to set
     */
    public void setPublisher(String... publisher) {
        this.publisher = publisher;
    }
    /**
     * @return the ref
     */
    public String[] getRef() {
        return ref;
    }
    /**
     * @param ref the ref to set
     */
    public void setRef(String ... ref) {
        this.ref = ref;
    }
    /**
     * @return the tag
     */
    public String[] getTag() {
        return tag;
    }
    /**
     * @param tag the tag to set
     */
    public void setTag(String ... tag) {
        this.tag = tag;
    }
    /**
     * @return the text
     */
    public String getText() {
        return text;
    }
    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }
    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }
    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
    /**
     * @return the groups_allowed
     */
    public String[] getGroups_allowed() {
        return groups_allowed;
    }
    /**
     * @param groups_allowed the groups_allowed to set
     */
    public void setGroups_allowed(String... groups_allowed) {
        this.groups_allowed = groups_allowed;
    }
    /**
     * @return the users_allowed
     */
    public String[] getUsers_allowed() {
        return users_allowed;
    }
    /**
     * @param users_allowed the users_allowed to set
     */
    public void setUsers_allowed(String... users_allowed) {
        this.users_allowed = users_allowed;
    }
    
    /**
     * Return another property by name
     * @param name property name
     * @return property value
     */
    public Object get(String name) {
        return otherProperties.get(name);
    }
    /**
     * Return all the other properties.
     * @return other properties
     */
    @JsonAnyGetter
    public Map<String , Object> any() {
        return otherProperties;
    }
 
    /**
     * Set a misc property
     * @param name property name
     * @param value property value
     */
    @JsonAnySetter
    public void set(String name, Object value) {
        otherProperties.put(name, value);
    }

    /**
     * Creates a DocumentInfo from its JSON representation.
     * @param json the json string
     * @param mapper the ObjectMapper
     * @return a DocumentInfo 
     * @throws JsonParseException if the json is invalid
     * @throws JsonMappingException if the mapping does not work
     * @throws IOException 
     */
    public static DocumentInfo fromJSON(String json, ObjectMapper mapper) 
            throws JsonParseException, JsonMappingException, IOException {
        //mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        DocumentInfo info = mapper.readValue(json, DocumentInfo.class);
        return info;
    }
    
    /**
     * Converts a DocumentInfo to JSON representation.
     * @param mapper the ObjectMapper
     * @return a JSON string
     * @throws JsonProcessingException if conversion fails
     */
    public String toJSON(ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object that) {
        if ( this == that) return true;
        if ( !(that instanceof DocumentInfo) ) return false;
        DocumentInfo d = (DocumentInfo)that;
        if (! ((application == d.application) || (application != null && application.equals(d.application))))
            return false;
        if (! ((format == d.format) || (format != null && format.equals(d.format))))
            return false;
        if (! ((text == d.text) || (text != null && text.equals(d.text))))
            return false;
        if (! ((uri == d.uri) || (uri != null && uri.equals(d.uri))))
            return false;

       return Arrays.equals(artifact, d.artifact) &&
               Arrays.equals(contributor, d.contributor) &&
               Arrays.equals(creator, d.creator) &&
               Arrays.equals(date, d.date) &&
               Arrays.equals(event, d.event) &&
               Arrays.equals(language, d.language) &&
               Arrays.equals(org, d.org) &&
               Arrays.equals(person, d.person) &&
               Arrays.equals(place, d.place) &&
               Arrays.equals(publisher, d.publisher) &&
               Arrays.equals(ref, d.ref) &&
               Arrays.equals(tag, d.tag) &&
               Arrays.equals(groups_allowed, d.groups_allowed) &&
               Arrays.equals(users_allowed, d.users_allowed) &&
               otherProperties.equals(d.otherProperties);
    }
}
