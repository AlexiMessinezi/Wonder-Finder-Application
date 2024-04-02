package com.example.wonderfinder;

import com.example.wonderfinder.Model.GoogleLandmarkModel.GeometryModel;
import com.example.wonderfinder.Model.GoogleLandmarkModel.PhotoModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleLandmarkModel {
    @SerializedName("business_status")
    @Expose
    private String businessStatus;

    //Returns the location of the landmark
    @SerializedName("geometry")
    @Expose
    private GeometryModel geometry;

    //Returns the icon of the landmark
    @SerializedName("icon")
    @Expose
    private String icon;

    //Returns the name of the landmark
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("obfuscated_type")
    @Expose
    private List<Object> obfuscatedType = null;

    //Returns of a photo of the landmark
    @SerializedName("photos")
    @Expose
    private List<PhotoModel> photos = null;

    //Place id by google remains the same
    @SerializedName("place_id")
    @Expose
    private String placeId;

    //Returns a rating of the landmark given by people
    @SerializedName("rating")
    @Expose
    private Double rating;

    @SerializedName("reference")
    @Expose
    private String reference;

    @SerializedName("scope")
    @Expose
    private String scope;

    //Returns landmark types such as restaurant have food/bar etc
    @SerializedName("types")
    @Expose
    private List<String> types = null;

    //Returns the total ratings given by people
    @SerializedName("user_ratings_total")
    @Expose
    private Integer userRatingsTotal;

    //Returns the address of the landmark
    @SerializedName("vicinity")
    @Expose
    private String vicinity;

    @Expose(serialize = false, deserialize = false)
    private boolean isSaved;

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public GeometryModel getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryModel geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getObfuscatedType() {
        return obfuscatedType;
    }

    public void setObfuscatedType(List<Object> obfuscatedType) {
        this.obfuscatedType = obfuscatedType;
    }


    public List<PhotoModel> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoModel> photos) {
        this.photos = photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }


    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(Integer userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
}
