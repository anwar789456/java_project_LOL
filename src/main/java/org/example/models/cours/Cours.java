package org.example.models.cours;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Cours {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty ownerId = new SimpleIntegerProperty();
    private final StringProperty titre = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final StringProperty img = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final BooleanProperty isFree = new SimpleBooleanProperty();
    private final ObjectProperty<LocalDateTime> dateCreation = new SimpleObjectProperty<>();

    // Constructor
    public Cours(int id, String titre, String description, double price, boolean isFree) {
        this.id.set(id);
        this.titre.set(titre);
        this.description.set(description);
        this.price.set(price);
        this.isFree.set(isFree);
    }

    public Cours() {}

    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getOwnerId() { return ownerId.get(); }
    public void setOwnerId(int value) { ownerId.set(value); }
    public IntegerProperty ownerIdProperty() { return ownerId; }

    public String getTitre() { return titre.get(); }
    public void setTitre(String value) { titre.set(value); }
    public StringProperty titreProperty() { return titre; }

    public double getPrice() { return price.get(); }
    public void setPrice(double value) { price.set(value); }
    public DoubleProperty priceProperty() { return price; }

    public String getImg() { return img.get(); }
    public void setImg(String value) { img.set(value); }
    public StringProperty imgProperty() { return img; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    public boolean isIsFree() { return isFree.get(); }
    public void setIsFree(boolean value) { isFree.set(value); }
    public BooleanProperty isFreeProperty() { return isFree; }

    public LocalDateTime getDateCreation() { return dateCreation.get(); }
    public void setDateCreation(LocalDateTime value) { dateCreation.set(value); }
    public ObjectProperty<LocalDateTime> dateCreationProperty() { return dateCreation; }
}
