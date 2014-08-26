package com.lemoulinstudio.bikefriend;

import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

public interface BikeStationParser {

    public void setDataSource(DataSourceEnum dataSource);

    // Note: this method should close the stream after it finished using it.
    public List<BikeStation> parse(InputStream in) throws IOException, ParsingException;

}
