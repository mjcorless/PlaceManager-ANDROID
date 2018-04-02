package mjcorless.bsse.asu.edu.placemanager.models;

/**
 * Dummy class to encapsulate an Address (The name of the address and the Full Postal Address
 */
public class Address
{
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * The name of the Address
     */
    private String title;

    public String getPostalAddress()
    {
        return PostalAddress;
    }

    public void setPostalAddress(String postalAddress)
    {
        PostalAddress = postalAddress;
    }

    /**
     * The full Postal Address. From the assignment instructions it is assumed to only be US Postal Addresses.
     * TODO: Look at ripping this apart into multiple fields
     */
    private String PostalAddress;

}
