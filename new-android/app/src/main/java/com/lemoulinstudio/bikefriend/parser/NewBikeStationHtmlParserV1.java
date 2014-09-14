package com.lemoulinstudio.bikefriend.parser;

import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This parser extracts information from the website of NewBike.
 * This is a temporary solution until I find a data source which is easier to read (JSON or XML).
 * 
 * If you work for NewBike, please let me know if you know which data source I should use, thank you.
 * I can be contacted by email: "vincent.cantin at le-moulin-studio.com" (please replace "at" by "@").
 *
 * @author Vincent Cantin
 */
public class NewBikeStationHtmlParserV1 implements BikeStationParser {

  private final SimpleDateFormat dateFormat;
  private DataSourceEnum dataSource;

  public NewBikeStationHtmlParserV1() {
    TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
  }

  public void setDataSource(DataSourceEnum dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<BikeStation> parse(InputStream in) throws IOException, ParsingException {
      return new ArrayList<BikeStation>();
  }
  
}
