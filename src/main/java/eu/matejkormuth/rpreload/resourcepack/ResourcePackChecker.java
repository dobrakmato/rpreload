/**
 * rpreload - Resource pack management made easy.
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.matejkormuth.rpreload.resourcepack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResourcePackChecker {

    private static final Logger log = LoggerFactory.getLogger(ResourcePackChecker.class);

    public static class Status {
        private final boolean error;
        private final String url;
        private final String message;

        public Status(boolean error, @Nonnull String url, @Nonnull String message) {
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

    public Status test(@Nonnull String url) {
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
