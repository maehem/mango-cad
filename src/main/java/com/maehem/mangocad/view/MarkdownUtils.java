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

import com.maehem.mangocad.AppProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class MarkdownUtils {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    public final static double WRAP = 800;

    public static Node markdownNode(double scale, String text, String urlBase) {
        String content = text;
        VBox node = new VBox();
        node.setSpacing(0);
        node.setPadding(new Insets(6));
        node.setFillWidth(true);
        node.setPrefWidth(WRAP);
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
                node.getChildren().add(styleLine(t, urlBase));
            } else if (line.startsWith(H5)) {
                Text t = new Text(line.substring(H5.length()));
                t.getStyleClass().addAll("md-heading", "md-h5");
                node.getChildren().add(styleLine(t, urlBase));
            } else if (line.startsWith(H4)) {
                Text t = new Text(line.substring(H4.length()));
                t.getStyleClass().addAll("md-heading", "md-h4");
                node.getChildren().add(styleLine(t, urlBase));
            } else if (line.startsWith(H3)) {
                Text t = new Text(line.substring(H3.length()));
                t.getStyleClass().addAll("md-heading", "md-h3");
                node.getChildren().add(styleLine(t, urlBase));
            } else if (line.startsWith(H2)) {
                Text t = new Text(line.substring(H2.length()));
                t.getStyleClass().addAll("md-heading", "md-h2");
                node.getChildren().add(styleLine(t, urlBase));
            } else if (line.startsWith(H1)) {
                Text t = new Text(line.substring(H1.length()));
                t.getStyleClass().addAll("md-heading", "md-h1");
                node.getChildren().add(styleLine(t, urlBase));
            } else if (line.trim().startsWith("- ") // Bullets
                    || line.trim().startsWith("+ ")
                    || line.trim().startsWith("* ")) {
                // Bullet Item
                Text bul = new Text("    \u2022 ");
                bul.getStyleClass().add("md-body");
                Text t = new Text(line.substring(2));
                t.getStyleClass().add("md-body");
                t.setWrappingWidth(WRAP * 0.7);
                t.setTextAlignment(TextAlignment.JUSTIFY);
                HBox bulletArea = new HBox(bul, t);
                bulletArea.setAlignment(Pos.TOP_LEFT);
                node.getChildren().add(bulletArea);
            } else {
                Text t = new Text(line);
                t.getStyleClass().add("md-body");
                t.setWrappingWidth(WRAP);
                t.setTextAlignment(TextAlignment.JUSTIFY);
                node.getChildren().add(styleLine(t, urlBase));
            }
        }

        return node;
    }

    public static boolean hasImageLink(String str) {
        return str.contains("[") && str.contains("]") && str.contains("(") && str.contains(")");
    }

    public static void getImageNode(String str, String urlbase, TextFlow flow, List<String> styleClass) {
        boolean isImage = false;

        // Spaces in file name must be escaped.
        //urlbase = urlbase.trim().replaceAll(" ", "\\ ");
        // Split on [,  split on ],  split on (, split on )
        int idx1 = str.indexOf("[");
        int idx2 = str.indexOf("]", idx1);
        int idx3 = str.indexOf("(", idx1);
        int idx4 = str.indexOf(")", idx3);

        int linkIdx = str.indexOf("![", idx1);
        if (linkIdx >= 0 && linkIdx < idx2) {
            // Image link instead of linkPhrase
            idx2 = str.indexOf("]", idx2 + 1); // Next one.
            idx3 = str.indexOf("(", idx3 + 1); // Next one.
            idx4 = str.indexOf(")", idx4 + 1); // Next one.
            LOGGER.log(Level.SEVERE, "\n\nCorrect Index:\n    Phrase: {0}\n\n",
                    str.substring(idx1, idx2));
        }

        //if (idx1 > 0 && str.subSequence(idx1 - 1, idx1).equals("!")) {
        if (idx1 > 0 && str.charAt(idx1 - 1) == '!') {
            isImage = true;
            LOGGER.log(Level.SEVERE, "It's an image rather than link");
        }

        String linkPhrase = str.substring(idx1 + (isImage ? 1 : 1), idx2);
        LOGGER.log(Level.SEVERE, "LinkPhrase: " + linkPhrase);
        if (hasImageLink(linkPhrase)) {
            LOGGER.log(Level.SEVERE, "LinkPhrase has image.");

        }

        String settings[] = {};
        String lURL = str.substring(idx3 + 1, idx4);
        LOGGER.log(Level.SEVERE, "LURL: " + lURL);
        if (lURL.contains("\"")) {
            // Has old style Link Phrase, remove it.
            lURL = lURL.substring(0, lURL.indexOf(" \""));
        }
        if (lURL.contains("|")) {
            LOGGER.log(Level.SEVERE, "Link has settings. Maybe width or height?");
            String split[] = lURL.split("\\|");
            lURL = split[0].trim(); // Pass on URL portion.
            LOGGER.log(Level.SEVERE, "LURL(2): " + lURL);
            if (split.length > 1) {
                settings = split[1].split(" ");
                for (int i = 0; i < settings.length; i++) {
                    LOGGER.log(Level.SEVERE, "Process Setting: " + settings[i]);
                }
            }
        }
        final String linkURL = lURL;
        LOGGER.log(Level.SEVERE, "LinkURL: " + linkURL);
        String preString = "";
        if (idx1 > 0) {
            preString = str.substring(0, idx1 - 1);
        }
        LOGGER.log(Level.SEVERE, "PreString: " + preString);
        flow.getChildren().addAll(processWords(preString, styleClass));
        if (isImage) {
            //URL url;
            if (urlbase == null) {
                LOGGER.log(Level.SEVERE, "URL Base is NULL.");
            } else {
                LOGGER.log(Level.SEVERE, "URL BASE is " + urlbase);
            }
            try {
                if (urlbase != null && urlbase.startsWith("http")) {
                    // web image
                    LOGGER.log(Level.SEVERE, "Find the web image for: {0} :: {1}", new Object[]{urlbase, linkURL});
                    String fullUrl = urlbase + "/" + linkURL;
                    if (linkURL.startsWith("http")) {
                        fullUrl = linkURL;  // Not a relative link.
                    }
                    LOGGER.log(Level.SEVERE, "\nFull Image URL: {0}\n", fullUrl);
                    URL url = new URL(fullUrl);
                    if (fullUrl.contains(".svg") || fullUrl.contains("?svg=true")) {
                        
                        // SVG from a file is not supported in JavaFX.
                        // Going to need a library or write one.
//                        LOGGER.log(Level.SEVERE, "Handle SVG file.");
//                        SVGPath svg = new SVGPath();
//                        String svgContent = fetchWebFile(url);
//                        LOGGER.log(Level.SEVERE, "Fetched SVG content: " + svgContent);
//                        svg.setContent(svgContent);
//                        flow.getChildren().add(svg);
                        
                        // Place holder SVG Badge.
                        Image im = new Image(MarkdownUtils.class.getResourceAsStream("/icons/svg-badge.png"));
                        ImageView iv = new ImageView(im);
                        iv.setFitHeight(16);
                        iv.setPreserveRatio(true);
                        flow.getChildren().add(iv);
                    } else {
                        //url.openConnection();
                        InputStream is = url.openStream();
                        //InputStream is = MarkdownUtils.class.getResourceAsStream(urlbase + "/" + linkURL);
                        Image im = new Image(is);
                        is.close();
                        ImageView iv = new ImageView(im);
                        iv.setPreserveRatio(true);
                        iv.setFitHeight(240);
                        flow.getChildren().add(iv);
                    }

                } else if (urlbase != null && urlbase.startsWith("file://")) {
                    // local disk file
                    LOGGER.log(Level.SEVERE, "Find the file image for: {0} :: {1}", new Object[]{urlbase, linkURL});
                    File imgFile = new File(new URI(urlbase + linkURL.trim()));
                    //File imgFile = new File(urlbase.replace("file:", ""), linkURL);
                    InputStream is = new FileInputStream(imgFile);
                    //InputStream is = MarkdownUtils.class.getResourceAsStream(urlbase + "/" + linkURL);
                    Image im = new Image(is);
                    ImageView iv = new ImageView(im);
                    if (settings.length > 0) {
                        boolean hasWidth = false;
                        boolean hasHeight = false;
                        for (String setting : settings) {
                            String[] split = setting.split("=");
                            if (split[0].trim().equals("width")) {
                                iv.setFitWidth(Double.parseDouble(split[1].trim()));
                                hasWidth = true;
                            }
                            if (split[0].trim().equals("height")) {
                                iv.setFitHeight(Double.parseDouble(split[1].trim()));
                                hasHeight = true;
                            }
                        }
                        iv.setPreserveRatio(!(hasWidth && hasHeight));
                    }
                    flow.getChildren().add(iv);
                } else {
                    // in JAR file.
                    InputStream is = MarkdownUtils.class.getResourceAsStream(urlbase + "/" + linkURL);
                    Image im = new Image(is);
                    ImageView iv = new ImageView(im);
                    iv.setPreserveRatio(true);
                    iv.setFitHeight(240);
                    flow.getChildren().add(iv);
                }
                //url = new URL(urlbase + "/" + linkURL);
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.SEVERE, "Malformed URL: " + urlbase + "\n" + ex.toString(), ex);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "IO Exception: " + urlbase + "\n" + ex.toString(), ex);
            } catch (URISyntaxException ex) {
                LOGGER.log(Level.SEVERE, "URI Syntax Exception " + urlbase + "\n" + ex.toString(), ex);
            }

        } else {
            Text linkText = new Text(linkPhrase);
            linkText.getStyleClass().addAll(styleClass);
            linkText.getStyleClass().add("md-link");
            TextFlow styledLine = styleLine(linkText, urlbase);
            flow.getChildren().add(styledLine);
            Tooltip t = new Tooltip(linkURL);
            Tooltip.install(styledLine, t);
            styledLine.setOnMouseClicked((event) -> {
                URI uri;
                try {
                    uri = new URI(linkURL);
                    AppProperties.getInstance().getHostServices().showDocument(uri.toString());
                } catch (URISyntaxException ex) {
                    LOGGER.log(Level.SEVERE, "Could not open URL: " + linkURL, ex);
                }
            });
            //flow.getChildren().add(styledLine);

//            Tooltip t = new Tooltip(linkURL);
//            Tooltip.install(linkText, t);
//            linkText.setOnMouseClicked((event) -> {
//                URI uri;
//                try {
//                    uri = new URI(linkURL);
//                    AppProperties.getInstance().getHostServices().showDocument(uri.toString());
//                } catch (URISyntaxException ex) {
//                    LOGGER.log(Level.SEVERE, "Could not open URL: " + linkURL, ex);
//                }
//            });
//            linkText.getStyleClass().addAll(styleClass);
//            linkText.getStyleClass().add("md-link");
//            flow.getChildren().add(linkText);
        }

        String postString = "";
        if (idx4 < str.length() - 1) {
            postString = str.substring(idx4 + 1, str.length() - 1);
        }
        LOGGER.log(Level.SEVERE, "Post String: " + postString);
        flow.getChildren().addAll(processWords(postString, styleClass));
    }

    private static TextFlow styleLine(Text text, String urlbase) {
        TextFlow flow = new TextFlow();
        String str = text.getText();
        //LOGGER.log(Level.SEVERE, "String to style: \"" + str + "\"");

        if (hasImageLink(str)) {
            getImageNode(str, urlbase, flow, text.getStyleClass());
        } else {
            flow.getChildren().addAll(processWords(str, text.getStyleClass()).getChildren());
        }

        return flow;
    }

    private static TextFlow processWords(String str, List<String> styleClass) {
        TextFlow flow = new TextFlow();
        boolean bold = false;
        boolean italic = false;
        boolean strike = false;

        for (String word : str.split(" ")) {
            Text wordText = new Text();
            wordText.getStyleClass().addAll(styleClass);
            wordText.setStrikethrough(strike);

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
                word = word.substring(1);
                if (word.endsWith("*") || word.endsWith("_")) {
                    wordText.getStyleClass().add("md-italic");
                    word = word.substring(0, word.length() - 1);
                    italic = false;
                } else {
                    italic = true;
                }
            }
            if (word.startsWith("~~")) {
                word = word.substring(2);
                if (word.endsWith("~~")) {
                    wordText.setStrikethrough(true);
                    strike = false;
                } else {
                    strike = true;
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
            if (word.endsWith("~~")) {
                wordText.setStrikethrough(true);
                strike = false;
                word = word.substring(0, word.length() - 2);
            }

            if (!addComma && !addDot && !addQuest && !addSemiCol && !strike) {
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
                commaText.getStyleClass().addAll(styleClass);
                flow.getChildren().add(commaText);
            }
            if (addDot) {
                Text commaText = new Text(". ");
                commaText.getStyleClass().addAll(styleClass);
                flow.getChildren().add(commaText);
            }
            if (addQuest) {
                Text commaText = new Text("? ");
                commaText.getStyleClass().addAll(styleClass);
                flow.getChildren().add(commaText);
            }
            if (addSemiCol) {
                Text commaText = new Text("; ");
                commaText.getStyleClass().addAll(styleClass);
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

    private static String fetchWebFile(URL url) {
        StringBuilder sb = new StringBuilder();
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    //.proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
                    //.authenticator(Authenticator.getDefault())
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept((theResponse) -> {
                        sb.append(theResponse);
                        LOGGER.log(Level.SEVERE, "Recevied SVG. {0}.", theResponse);
                    });

        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, "URI Syntax Exception: " + url.toString() + "\n" + ex.toString(), ex);
        }
        
        // TODO:   StringBuilder is not holding the fetched data. Race contidion?
        return sb.toString();
    }

}
