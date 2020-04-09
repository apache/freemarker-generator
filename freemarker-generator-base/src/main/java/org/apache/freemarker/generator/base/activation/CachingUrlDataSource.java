package org.apache.freemarker.generator.base.activation;

import javax.activation.URLDataSource;
import java.net.URL;

/**
 * The standard UrlDataSource actually does network calls when
 * getting the content type. Try to avoid multiple calls to
 * determine the content type.
 */
public class CachingUrlDataSource extends URLDataSource {

    private String contentType;

    public CachingUrlDataSource(URL url) {
        super(url);
    }

    @Override
    public synchronized String getContentType() {
        if (contentType == null) {
            contentType = super.getContentType();
        }
        return contentType;
    }
}
