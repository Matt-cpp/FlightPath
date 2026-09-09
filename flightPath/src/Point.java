public class Point {
    private double longitude;
    private double latitude;

    public Point (double lon,double lat){
        longitude=lon;
        latitude=lat;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
