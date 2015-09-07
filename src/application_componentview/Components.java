package application_componentview;

/**
 * Keeps track of the different types of components, their description and image URI
 * @author Yngve Sekse Kristiansen
 *
 */
public enum Components {
    DESTINATION("images/nondestination.png", "Destination"),
    NONDESTINATION("images/destination.png", "Non-Destination"),
    LINK("images/link.png", "Link"),
    CURSOR("images/cursor.png", "Select nodes/links"),
    REMOVECURSOR("images/removecursor.png", "Remove nodes/links");

    private String imagePath;
    private String description;

    private Components(String imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    public boolean isNode() {
        return this == DESTINATION || this == NONDESTINATION;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public String getDescription() {
        return this.description;
    }

}
