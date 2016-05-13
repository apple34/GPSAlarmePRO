package br.com.abner.naopassedalinha3;

/**
 * Created by Abner on 19/03/2016.
 */
public class Circulos {

    public Circulos(){}
    public Circulos( String id, Double latitude, Double longitude ){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private String id;
    private Double latitude;
    private Double longitude;

    public Double getLongitude() {
        return longitude;
    }
    public Double getLatitude() {
        return latitude;
    }
    public String getId() {
        return id;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
