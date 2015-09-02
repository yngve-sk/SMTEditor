package application;

/**
 * Keeps track of the different types of components, their description and image URI
 * @author Yngve Sekse Kristiansen
 *
 */
public enum Components {
    DESTINATION("anImagePath", "Non-Destination"),
    NONDESTINATION("anImagePath", "Destination"),
    LINK("anImagePath", "Link");

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
