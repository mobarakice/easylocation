package com.mobarak.easylocation.location;

/**
 * Created by Mobarak on 25 August, 2019
 * @author Mobarak Hosen
 */
public class LocationParams {
    public static final LocationParams NAVIGATION;
    public static final LocationParams BEST_EFFORT;
    public static final LocationParams LAZY;
    private long interval;
    private float distance;
    private LocationAccuracy accuracy;

    LocationParams(LocationAccuracy accuracy, long interval, float distance) {
        this.interval = interval;
        this.distance = distance;
        this.accuracy = accuracy;
    }

    public long getInterval() {
        return this.interval;
    }

    public float getDistance() {
        return this.distance;
    }

    public LocationAccuracy getAccuracy() {
        return this.accuracy;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof LocationParams)) {
            return false;
        } else {
            LocationParams that = (LocationParams)o;
            return Float.compare(that.distance, this.distance) == 0 && this.interval == that.interval && this.accuracy == that.accuracy;
        }
    }

    public int hashCode() {
        int result = (int)(this.interval ^ this.interval >>> 32);
        result = 31 * result + (this.distance != 0.0F ? Float.floatToIntBits(this.distance) : 0);
        result = 31 * result + this.accuracy.hashCode();
        return result;
    }

    static {
        NAVIGATION = (new Builder()).setAccuracy(LocationAccuracy.HIGH).setDistance(0.0F).setInterval(500L).build();
        BEST_EFFORT = (new Builder()).setAccuracy(LocationAccuracy.MEDIUM).setDistance(150.0F).setInterval(2500L).build();
        LAZY = (new Builder()).setAccuracy(LocationAccuracy.LOW).setDistance(500.0F).setInterval(5000L).build();
    }

    public static class Builder {
        private LocationAccuracy accuracy;
        private long interval;
        private float distance;

        public Builder() {
        }

        public Builder setAccuracy(LocationAccuracy accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public Builder setInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder setDistance(float distance) {
            this.distance = distance;
            return this;
        }

        public LocationParams build() {
            return new LocationParams(this.accuracy, this.interval, this.distance);
        }
    }
}
