package application;

/**
 * Keeps track of the different types of components, their description and image URI
 * @author Yngve Sekse Kristiansen
 *
 */
public enum Components {
    DESTINATION("images/destination.png", "Non-Destination"),
    NONDESTINATION("images/nondestination.png", "Destination"),
    LINK("images/link.png", "Link");

    private String imagePath;
    private String description;

    private Components(String imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public String getDescription() {
        return this.description;
    }

}
