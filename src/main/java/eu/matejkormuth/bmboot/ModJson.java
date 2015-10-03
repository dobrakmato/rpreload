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
package eu.matejkormuth.bmboot;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class ModJson {

    private static final Logger log = LoggerFactory.getLogger(ModJson.class);

    private String name;
    private String version;
    private String author;
    private String[] modules;

    public ModJson() {
    }

    public ModJson(@Nonnull String json) {
        try {
            JSONObject obj = new JSONObject(json);
            this.name = obj.getString("name");
            this.version = obj.getString("version");
            this.author = obj.getString("author");

            JSONArray array = obj.getJSONArray("modules");
            this.modules = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                this.modules[i] = array.getString(i);
            }
        } catch (Exception e) {
            log.error("Can't parse json file!", e);
        }
    }

    public ModJson(@Nonnull String name, @Nonnull String version, @Nonnull String author, @Nonnull String[] modules) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.modules = modules;
    }


    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String[] getModules() {
        return modules;
    }
}
