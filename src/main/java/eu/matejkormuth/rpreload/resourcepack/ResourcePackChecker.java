package eu.matejkormuth.rpreload.resourcepack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResourcePackChecker {

    private static final Logger log = LoggerFactory.getLogger(ResourcePackChecker.class);

    public static class Status {
        private final boolean error;
        private final String url;
        private final String message;

        public Status(boolean error, String url, String message) {
            this.error = error;
            this.url = url;
            this.message = message;
        }

        public boolean isError() {
            return error;
        }

        public String getMessage() {
            return message;
        }


        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "Status{" +
                    "error=" + error +
                    ", message='" + message + '\'' +
                    '}';
        }


    }

    public Status test(String url) {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("HEAD");
            int responseCode = httpURLConnection.getResponseCode();
            // Get response code.
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Check file size.
                int length = httpURLConnection.getContentLength();

                // From ResourcePackRepository.java in minecraft client.
                if (length > 52428800) {
                    return new Status(true, url, "File is too big! Maximum size is: 52428800 bytes, file has" +
                            length + " bytes.");
                } else {
                    return new Status(false, url, "Ok.");
                }
            } else {
                return new Status(true, url, "Server returned status code: " + responseCode + " "
                        + httpURLConnection.getResponseMessage());
            }
        } catch (IOException e) {
            return new Status(true, url, e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
