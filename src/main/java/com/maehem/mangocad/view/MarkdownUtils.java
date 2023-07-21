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
package com.maehem.mangocad.view;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class MarkdownUtils {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    public static Node markdownNode(double scale, String text) {
        String content = text;
        VBox node = new VBox();
        node.setSpacing(0);
        node.setPadding(new Insets(6));
        if (content == null) {
            return node;
        }

        LOGGER.log(Level.FINER, "Process: " + text);
        if (content.contains("<p>") || content.contains("<br>")
                || content.contains("<b>")
                || content.contains("<h1>")
                || content.contains("<h2>")
                || content.contains("<h3>")
                || content.contains("<h4>")
                || content.contains("<h5>")
                || content.contains("<h6>")) {
            LOGGER.log(Level.FINER, "text contains HTML");
            content = html2markdown(text);
        }
        // parse thngs.

        content = content.translateEscapes(); // Was fun figuring out this one.
        if (!content.startsWith("#")) {
            content = "#" + content; // First line is always heading.
        }
        String[] lines = content.split("\n");

        final String H6 = "######";
        final String H5 = "#####";
        final String H4 = "####";
        final String H3 = "###";
        final String H2 = "##";
        final String H1 = "#";

        for (String line : lines) {
            line = line.strip();
            if (line.startsWith("***") | line.startsWith("---") | line.startsWith("___")) { // <HR>
                node.getChildren().add(new Separator());
            } else if (line.startsWith(H6)) {
                Text t = new Text(line.substring(H6.length()));
                t.getStyleClass().addAll("md-heading", "md-h6");
                node.getChildren().add(styleLine(t));
            } else if (line.startsWith(H5)) {
                Text t = new Text(line.substring(H5.length()));
                t.getStyleClass().addAll("md-heading", "md-h5");
                node.getChildren().add(styleLine(t));
            } else if (line.startsWith(H4)) {
                Text t = new Text(line.substring(H4.length()));
                t.getStyleClass().addAll("md-heading", "md-h4");
                node.getChildren().add(styleLine(t));
            } else if (line.startsWith(H3)) {
                Text t = new Text(line.substring(H3.length()));
                t.getStyleClass().addAll("md-heading", "md-h3");
                node.getChildren().add(styleLine(t));
            } else if (line.startsWith(H2)) {
                Text t = new Text(line.substring(H2.length()));
                t.getStyleClass().addAll("md-heading", "md-h2");
                node.getChildren().add(styleLine(t));
            } else if (line.startsWith(H1)) {
                Text t = new Text(line.substring(H1.length()));
                t.getStyleClass().addAll("md-heading", "md-h1");
                node.getChildren().add(styleLine(t));
            } else if (line.trim().startsWith("- ") // Bullets
                    || line.trim().startsWith("+ ")
                    || line.trim().startsWith("* ")) {
                // Bullet Item
                Text bul = new Text("    \u2022 ");
                bul.getStyleClass().add("md-body");
                Text t = new Text(line.substring(2));
                t.getStyleClass().add("md-body");
                t.setWrappingWidth(400);
                t.setTextAlignment(TextAlignment.JUSTIFY);
                HBox bulletArea = new HBox(bul, t);
                bulletArea.setAlignment(Pos.TOP_LEFT);
                node.getChildren().add(bulletArea);
            } else {
                Text t = new Text(line);
                t.getStyleClass().add("md-body");
                t.setWrappingWidth(600);
                t.setTextAlignment(TextAlignment.JUSTIFY);
                node.getChildren().add(styleLine(t));
            }

            LOGGER.log(Level.SEVERE, "=============End Line===================");
        }

        return node;
    }

    private static TextFlow styleLine(Text text) {
        TextFlow flow = new TextFlow();
        String str = text.getText();
        boolean bold = false;
        boolean italic = false;

        for (String word : str.split(" ")) {
            Text wordText = new Text();
            wordText.getStyleClass().addAll(text.getStyleClass());

            boolean addDot = false;
            boolean addComma = false;
            boolean addQuest = false;
            boolean addSemiCol = false;

            String commaSpace = " ";
            if (word.endsWith(".")) {
                word = word.substring(0, word.length() - 1);
                addDot = true;
            }
            if (word.endsWith(",")) {
                word = word.substring(0, word.length() - 1);
                addComma = true;
            }
            if (word.endsWith("?")) {
                word = word.substring(0, word.length() - 1);
                addQuest = true;
            }
            if (word.endsWith(";")) {
                word = word.substring(0, word.length() - 1);
                addSemiCol = true;
            }

            if (word.startsWith("***") || word.startsWith("___")) {
                word = word.substring(3);
                if (word.endsWith("***") || word.endsWith("___")) {
                    wordText.getStyleClass().addAll("md-bold", "md-italic");
                    word = word.substring(0, word.length() - 3);
                    bold = false;
                    italic = false;
                } else {
                    bold = true;
                    italic = true;
                }
            }
            if (word.startsWith("**") || word.startsWith("__")) {
                word = word.substring(2);
                if (word.endsWith("**") || word.endsWith("__")) {
                    wordText.getStyleClass().add("md-bold");
                    word = word.substring(0, word.length() - 2);
                    bold = false;
                } else {
                    bold = true;
                }
            }
            if (word.startsWith("*") || word.startsWith("_")) {
                italic = true;
                word = word.substring(1);
                if (word.endsWith("*") || word.endsWith("_")) {
                    wordText.getStyleClass().add("md-italic");
                    word = word.substring(0, word.length() - 1);
                    italic = false;
                } else {
                    italic = true;
                }
            }

            if (word.endsWith("***") || word.endsWith("___")) {
                bold = false;
                italic = false;
                wordText.getStyleClass().addAll("md-bold", "md-italic");
                word = word.substring(0, word.length() - 3);
            }
            if (word.endsWith("**") || word.endsWith("__")) {
                bold = false;
                wordText.getStyleClass().add("md-bold");
                word = word.substring(0, word.length() - 2);
            }
            if (word.endsWith("*") || word.endsWith("_")) {
                wordText.getStyleClass().add("md-italic");
                italic = false;
                word = word.substring(0, word.length() - 1);
            }

            if (!addComma && !addDot && !addQuest && !addSemiCol) {
                word += commaSpace;
            }

            wordText.setText(word);

            if (italic) {
                wordText.getStyleClass().add("md-italic");
            }
            if (bold) {
                wordText.getStyleClass().add("md-bold");
            }

            flow.getChildren().add(wordText);

            if (addComma) {
                Text commaText = new Text(", ");
                commaText.getStyleClass().addAll(text.getStyleClass());
                flow.getChildren().add(commaText);
            }
            if (addDot) {
                Text commaText = new Text(". ");
                commaText.getStyleClass().addAll(text.getStyleClass());
                flow.getChildren().add(commaText);
            }
            if (addQuest) {
                Text commaText = new Text("? ");
                commaText.getStyleClass().addAll(text.getStyleClass());
                flow.getChildren().add(commaText);
            }
            if (addSemiCol) {
                Text commaText = new Text("; ");
                commaText.getStyleClass().addAll(text.getStyleClass());
                flow.getChildren().add(commaText);
            }
        }

        return flow;
    }

    /**
     * Convert HTML snippets to Marddown Using this guide:
     * https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet#lines
     *
     *
     * TODO: Case insensitive
     *
     * <B> bold tag _word_ or *word* LI/UL lists - Unordered lists: + - or * -
     * Ordered List: Number ( 1. ) - SubList: ..1. or ..-
     *
     * LINKS: Ability to open browser.
     *
     * CODE STYLING: three backticks with optional type? ``` java foo(); ```
     * HORIZ RULE (three or more ) --- ___ ***
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
