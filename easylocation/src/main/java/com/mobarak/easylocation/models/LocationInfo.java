package com.mobarak.easylocation.models;

/**
 * Created by Mobarak on 25 August, 2019
 * @author Mobarak Hosen
 */
public class LocationInfo {

    private String latitude;
    private String longitude;
    private String altitude;
    private int accuracy;
    private long query_time;

    private LocationInfo(LocationInfoBuilder locationInfoBuilder){
        this.latitude=locationInfoBuilder.latitude;
        this.longitude=locationInfoBuilder.longitude;
        this.altitude=locationInfoBuilder.altitude;
        this.accuracy=locationInfoBuilder.accuracy;
        this.query_time=locationInfoBuilder.query_time;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public long getQuery_time() {
        return query_time;
    }

    public static class LocationInfoBuilder{

        private String latitude;
        private String longitude;
        private String altitude;
        private int accuracy;
        private long query_time;

        public LocationInfoBuilder() {
        }

        public LocationInfoBuilder setLatitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public LocationInfoBuilder setLongitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

        public LocationInfoBuilder setAltitude(String altitude) {
            this.altitude = altitude;
            return this;
        }

        public LocationInfoBuilder setAccuracy(int accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public LocationInfoBuilder setQuery_time(long query_time) {
            this.query_time = query_time;
            return this;
        }

        public LocationInfo build(){
            return new LocationInfo(this);
        }

    }
}
