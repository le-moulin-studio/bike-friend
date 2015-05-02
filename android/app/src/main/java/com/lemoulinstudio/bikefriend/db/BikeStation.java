package com.lemoulinstudio.bikefriend.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "bikeStation")
public class BikeStation {

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public DataSourceEnum dataSource;

    @DatabaseField
    public float latitude;

    @DatabaseField
    public float longitude;

    @DatabaseField
    public String englishName;

    @DatabaseField
    public String englishAddress;

    @DatabaseField
    public String englishDescription;

    @DatabaseField
    public String chineseName;

    @DatabaseField
    public String chineseAddress;

    @DatabaseField
    public String chineseDescription;

    @DatabaseField
    public int nbEmptySlots;

    @DatabaseField
    public int nbBicycles;

    @DatabaseField
    public Date lastUpdate;

    @DatabaseField
    public boolean isPreferred;

    public boolean isValid() {
        return (lastUpdate != null) &&
                (latitude != 0.0f) &&
                (longitude != 0.0f) &&
                (nbBicycles != -1) &&
                (nbEmptySlots != -1);
    }

    public void updateFrom(BikeStation otherStation, boolean isFromDb) {
        if (otherStation.lastUpdate != null) {
            if (this.lastUpdate != null && this.lastUpdate.after(otherStation.lastUpdate)) {
                return;
            }
            this.lastUpdate = otherStation.lastUpdate;
        }

        if (isFromDb) {
            this.isPreferred = otherStation.isPreferred;
        }

        if (otherStation.latitude != 0.0f) {
            this.latitude = otherStation.latitude;
        }
        if (otherStation.longitude != 0.0f) {
            this.longitude = otherStation.longitude;
        }
        if (otherStation.nbEmptySlots != -1) {
            this.nbEmptySlots = otherStation.nbEmptySlots;
        }
        if (otherStation.nbBicycles != -1) {
            this.nbBicycles = otherStation.nbBicycles;
        }
        if (otherStation.chineseName != null) {
            this.chineseName = otherStation.chineseName;
        }
        if (otherStation.chineseAddress != null) {
            this.chineseAddress = otherStation.chineseAddress;
        }
        if (otherStation.chineseDescription != null) {
            this.chineseDescription = otherStation.chineseDescription;
        }
        if (otherStation.englishName != null) {
            this.englishName = otherStation.englishName;
        }
        if (otherStation.englishAddress != null) {
            this.englishAddress = otherStation.englishAddress;
        }
        if (otherStation.englishDescription != null) {
            this.englishDescription = otherStation.englishDescription;
        }
    }

    @Override
    public String toString() {
        return String.format("[" +
                        "id = %s, " +
                        "dataSource = %s, " +
                        "latitude = %f, " +
                        "longitude = %f" +
                        "]",
                id, dataSource, latitude, longitude);
    }
}
