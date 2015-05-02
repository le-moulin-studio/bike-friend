package com.lemoulinstudio.bikefriend.parser;

import java.net.URL;
import com.lemoulinstudio.bikefriend.Utils;

public class StaticUrlProvider implements UrlProvider {

    private final URL url;

    public StaticUrlProvider(String url) {
        this.url = Utils.toUrl(url);
    }

    @Override
    public URL getUrl() {
        return url;
    }

}
