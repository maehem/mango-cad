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

import com.maehem.mangocad.AppProperties;
import com.maehem.mangocad.view.ControlPanel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manage a list of URLs to web source repositories that contain SCH and BRD
 * files.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RepoPathManager extends ArrayList<RepoPath> {

    private static final Logger LOGGER = ControlPanel.LOGGER;
    private static final String PROPERTIES_FILE_NAME = "repositories.properties";
    private static final String KEY_PREFIX = "Repo.";

    private static RepoPathManager instance = null;
    private File propFile;

    private RepoPathManager() {
        this.propFile = initPropFile();
        if (propFile.exists()) {
            load();
        } else {
            propFile.getParentFile().mkdirs();
            save();
        }
    }

    public static RepoPathManager getInstance() {
        if (instance == null) {
            instance = new RepoPathManager();
        }

        return instance;
    }

    public RepoPath getByUID(String uid) {
        return stream().filter((item) -> item.getUid().equals(uid)).findAny().orElse(null);
    }

    public final void load() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(propFile));
            LOGGER.log(Level.SEVERE, "Loaded MangoCAD repo paths file: {0}", propFile.getAbsolutePath());

            // Parse props into RepoPath objects.
            props.forEach((k, v) -> {
                String key = (String) k;
                String val = (String) v;

                LOGGER.log(Level.SEVERE, "Prop: key:{0}  val:{1}", new Object[]{k.toString(), v.toString()});
                if (key.startsWith(KEY_PREFIX)) {
                    String[] split = key.split("\\.");
                    String uid = split[1];
                    ingest(uid, split[2], val);
                }
            });
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        //LOGGER.log(Level.SEVERE, toString());
    }

    /**
     * Update or create and add to arrayList this item.
     *
     * @param uid
     * @param key
     * @param value
     */
    private void ingest(String uid, String key, String value) {
        RepoPath rp = getByUID(uid);
        if (rp == null) {
            rp = new RepoPath("", "", uid);
            add(rp);
        }
        
        switch (key) {
            case "url" -> {
                rp.setUrl(value);
            }
            case "desc" -> {
                rp.setDescription(value);
            }
        }

    }

    public final void save() {
        try {
            Properties props = new Properties();

            // Turn repo paths into props
            props.store(new FileOutputStream(propFile), "Saved mangoCAD repo paths");
            LOGGER.log(Level.SEVERE, "Saved MangoCAD repo paths file: {0}", propFile.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private static File initPropFile() {
        File appPropFile = AppProperties.getInstance().getPropFile();

        return new File(appPropFile.getParentFile(), PROPERTIES_FILE_NAME);

//        final String os = System.getProperty("os.name");
//        if (os != null && os.startsWith("Mac")) {
//            return new File(System.getProperty("user.home") 
//                    + File.separator + "Library"
//                    + File.separator + "Application Support"
//                    + File.separator + "MangoCAD"
//                    + File.separator + APP_VERSION
//                    + File.separator + PROPERTIES_FILE_NAME
//            );
//        } else {
//            // TODO: Sort this out per platform later.
//            return new File(System.getProperty("user.home")
//                    + File.separator + "mangoCAD-settings.properies"
//            );
//        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RepoPathManager:\n");
        forEach((rp) -> {
            sb.append(rp.toString() + "\n");
        });
        return sb.toString();
    }
    
    
}
