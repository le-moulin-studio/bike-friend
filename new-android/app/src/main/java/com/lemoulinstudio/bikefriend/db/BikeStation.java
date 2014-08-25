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
    public String chineseName;

    @DatabaseField
    public String chineseAddress;

    @DatabaseField
    public int nbEmptySlots;

    @DatabaseField
    public int nbBicycles;

    @DatabaseField
    public Date lastUpdate;

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
