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
