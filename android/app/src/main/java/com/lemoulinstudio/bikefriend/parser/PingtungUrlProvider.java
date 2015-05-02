package com.lemoulinstudio.bikefriend.parser;

import android.util.Base64;

import com.lemoulinstudio.bikefriend.Utils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PingtungUrlProvider implements UrlProvider {

    private final SimpleDateFormat dateFormat;

    public PingtungUrlProvider() {
        TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setTimeZone(taiwanTimeZone);
    }

    @Override
    public URL getUrl() {
        String dateStr = "PBike@Krtc" + dateFormat.format(new Date());
        String base64Str = Base64.encodeToString(Utils.md5(dateStr).getBytes(), Base64.URL_SAFE);
        String url = "http://pbike.pthg.gov.tw/BikeApp/BikeStationHandler.ashx?Key=" + base64Str;
        //Log.i("bikefriend", dateStr);
        //Log.i("bikefriend", base64Str);
        return Utils.toUrl(url);
    }

}
