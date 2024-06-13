/*
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with this
    work for additional information regarding copyright ownership.  The ASF
    licenses this file to you under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with the
    License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
    License for the specific language governing permissions and limitations
    under the License.
 */
package com.maehem.mangocad.view.controlpanel;

import com.maehem.mangocad.view.ControlPanel;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RepoPath {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    public static final String GITHUB_PREFIX = "https://github.com/";
    private static final String GITHUB_RAW_PREFIX = "https://raw.githubusercontent.com/";

    private final String uid;
    private String url;
    private String rawUrl;
    private String description;

    public RepoPath(String url, String description) {
        this(url, description, randomUID());
    }

    public RepoPath(String url, String description, String uid) {
        this.url = url;
        this.description = description;
        this.uid = uid;

        initRawUrl();
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
        initRawUrl();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    private static String randomUID() {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random((long) (Double.MAX_VALUE * Math.random()));
        while (sb.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return "RepoPath: " + "[" + uid + "] " + url + " :: " + description;
    }

    private void initRawUrl() {
        LOGGER.log(Level.FINER, "Input URL: " + url);
        if (url.startsWith(GITHUB_PREFIX)) {

            // Example raw link:
            //  https://raw.githubusercontent.com/maehem/Thump/master/README.md

            // Turn the browser-freindly URL into a raw-content URL.
            String githubDeets = getUrl().substring(GITHUB_PREFIX.length());
            String[] split = githubDeets.split("\\/");
            String userName = split[0];
            String repoName = split[1];
            String branch1 = "master"; // Seems to work for 'main' as well.
            String branch2 = "main";
            String urlPrefix = GITHUB_RAW_PREFIX + userName + "/" + repoName + "/" + branch1;
            StringBuilder sb = new StringBuilder(urlPrefix);
            for ( int i=2; i<split.length;i++) {
                sb.append("/" + split[i]);
            }
            LOGGER.log(Level.SEVERE, "Raw URL: " + sb.toString());
            rawUrl = sb.toString();
        } else {
            rawUrl = url;
        }
    }

    public String getRawUrl() {
        return rawUrl;
    }
}
