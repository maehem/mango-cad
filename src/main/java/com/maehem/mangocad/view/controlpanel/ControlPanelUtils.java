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

import com.maehem.mangocad.model.library.Library;
import com.maehem.mangocad.model.library.LibraryCache;
import com.maehem.mangocad.model.library.element.DeviceSet;
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.Package3d;
import com.maehem.mangocad.model.library.element.Symbol;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ControlPanelUtils {

    private static final Logger LOGGER = Logger.getLogger(ControlPanelUtils.class.getSimpleName());

    /**
     * Search in this directory for the DESCRIPTION.md
     *
     * @param dir that might contain a Description file.
     * @return first line of the Description file (DESCRIPTION.md)
     */
    public static String getFolderDescriptionShort(File dir) {
        String description = "";

        File descFile = new File(dir, "DESCRIPTION.md");

        // Is it really the description file?
        if (descFile.exists() && descFile.isFile() && descFile.canRead()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(descFile));
                description = br.readLine(); // We just need the first line here.
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger("ModuleList").log(Level.SEVERE, "Description not found.");
        }

        return description;
    }

    public static String getItemDescriptionFull(ControlPanelListItem item) {
        File fileOrDir = item.getFile();
        if (fileOrDir != null) {
            LOGGER.log(Level.SEVERE, "getFolderDesc for: " + fileOrDir.getName());
        }
        if (fileOrDir != null && fileOrDir.isDirectory()) {
            File descFile = new File(fileOrDir, "DESCRIPTION.md");
            if (!descFile.exists() || descFile.isDirectory() || !descFile.canRead()) {
                return null;
            }
            try {
                String readString = Files.readString(descFile.toPath());
                if (readString == null) {
                    return "";
                }
                return readString;
            } catch (IOException ex) {
                Logger.getLogger(ProjectSubFolderItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (fileOrDir != null && fileOrDir.isFile() && fileOrDir.canRead()) {
            String fName = fileOrDir.getName();
            if (fName.endsWith(".lbr")) {
                Library library = LibraryCache.getInstance().getLibrary(fileOrDir);
                if (item.getName().equals(fName)) {
                    // Get description from file.
                    if (library != null) {
                        return library.getDescription();
                    } else {
                        LOGGER.log(Level.SEVERE, "Library requested was null: " + fileOrDir.getName());
                    }
                } else { // One of the sub-items
                    // TODO: Include Parent library information.
                    for (Symbol s : library.getSymbols()) {
                        if (s.getName().equals(item.getName())) {
                            return s.getDescription();
                        }
                    }
                    for (Footprint f : library.getPackages()) {
                        if (f.getName().equals(item.getName())) {
                            //LOGGER.log(Level.SEVERE, "Package raw desc.: " + f.getDescription());
                            return f.getDescription();
                        }
                    }
                    for (Package3d p : library.getPackages3d()) {
                        if (p.getName().equals(item.getName())) {
                            return p.getDescription();
                        }
                    }
                    for (DeviceSet ds : library.getDeviceSets()) {
                        if (ds.getName().equals(item.getName())) {
                            return ds.getDescription();
                        }
                    }
                }
            }
        }

        return "";
    }

    public static Node markdownNode(double scale, String text) {
        String content = text;
        LOGGER.log(Level.SEVERE, "Process: " + text);
        if (content.contains("<p>") || content.contains("<br>")
                || content.contains("<b>" )
                || content.contains("<h1>" )
                || content.contains("<h2>" )
                || content.contains("<h3>" )
                || content.contains("<h4>" )
                || content.contains("<h5>" )
                || content.contains("<h6>" )
                ) {
            LOGGER.log(Level.FINER, "text contains HTML");
            content = html2markdown(text);
        }
        VBox node = new VBox();
        node.setSpacing(0);
        node.setPadding(Insets.EMPTY);
        // parse thngs.

        content = content.translateEscapes(); // Was fun figuring out this one.
        if ( !content.startsWith("#") ) {
            content = "#" + content;
        }
        String[] lines = content.split("\n");
//        for (String s : lines) {
//            LOGGER.log(Level.SEVERE, "Lines are :{0}", s);
//        }
        Font f = Font.getDefault();
        Font h1 = Font.font(f.getFamily(), FontWeight.BLACK, f.getSize()* scale);
        Font h2 = Font.font(f.getFamily(), FontWeight.BOLD, (f.getSize() - 1.0) * scale);
        Font h3 = Font.font(f.getFamily(), FontWeight.BOLD, (f.getSize() - 2.0) * scale);
        Font body = f;

        //Logger.getLogger("ControlPanelUtils").log(Level.SEVERE, "Line Count: " + lines.length);
        for (String line : lines) {
            line = line.strip();
            if (line.startsWith("#####")) {
                Text t = new Text(line.substring(5));
                t.setFont(h3);
                t.setFill(Color.WHITE);
                node.getChildren().add(t);
                //continue;
            } else if (line.startsWith("####")) {
                Text t = new Text(line.substring(4));
                t.setFont(h3);
                t.setFill(Color.LIGHTBLUE);
                node.getChildren().add(t);
                //continue;
            } else if (line.startsWith("###")) {
                Text t = new Text(line.substring(3));
                t.setFont(h3);
                t.setFill(Color.GRAY);
                node.getChildren().add(t);
                //continue;
            } else if (line.startsWith("##")) {
                Text t = new Text(line.substring(2));
                t.setFont(h2);
                t.setFill(Color.DARKGRAY);
                node.getChildren().add(t);
                //continue;
            } else if (line.startsWith("#")) {
                // Heading
                Text t = new Text(line.substring(1));
                t.setFont(h1);
                t.setFill(Color.KHAKI);
                node.getChildren().add(t);
                //continue;
            } else {
                Text t = new Text(line);
                t.setFont(body);
                t.setFill(Color.LIGHTGRAY);
                node.getChildren().add(t);
                //continue;
            }

        }

        return node;
    }

    /**
     * Convert HTML snippets to Marddown
     * Using this guide:
     * https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet#lines
     * 
     * 
     * TODO:   Case insensitive
     * 
     *          <B> bold tag  _word_  or *word*
     *        LI/UL lists
     *          - Unordered lists:  + - or *
     *          - Ordered List:  Number ( 1. )
     *          - SubList:  ..1.  or ..-
     * 
     *         LINKS:  Ability to open browser.
     * 
     *        CODE STYLING:  three backticks with optional type?
     *              ``` java
     *                  foo();
     *              ```
     *          HORIZ RULE (three or more )
     *                  ---
     *                  ___
     *                  ***
     * 
     * @param content
     * @return 
     */
    public static String html2markdown(String content) {
        //StringBuilder sb = new StringBuilder();
        return content
                .replaceAll("<b>", "__")
                .replaceAll("</b>", "__")
                .replaceAll("<i>", "_")
                .replaceAll("</i>", "_")
                .replaceAll("<code>", "```")
                .replaceAll("</code>", "```")
                .replaceAll("<p>", "\n")
                .replaceAll("</p>", "\n")
                .replaceAll("<br>", "\n")
                .replaceAll("<h1>", "\n# ")
                .replaceAll("</h1>", "")
                .replaceAll("<h2>", "\n## ")
                .replaceAll("</h2>", "")
                .replaceAll("<h3>", "\n### ")
                .replaceAll("</h3>", "")
                .replaceAll("<h4>", "\n#### ")
                .replaceAll("</h4>", "")
                .replaceAll("<h5>", "\n##### ")
                .replaceAll("</h5>", "")
                .replaceAll("<h6>", "\n###### ")
                .replaceAll("</h6>", "");

    }

}
