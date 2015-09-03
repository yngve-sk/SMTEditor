package application__componentview;

/**
 * Keeps track of the different types of components, their description and image URI
 * @author Yngve Sekse Kristiansen
 *
 */
public enum Components {
    DESTINATION("images/destination.png", "Non-Destination"),
    NONDESTINATION("images/nondestination.png", "Destination"),
    LINK("images/link.png", "Link"),
    CURSOR("images/cursor.png", "Select");

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