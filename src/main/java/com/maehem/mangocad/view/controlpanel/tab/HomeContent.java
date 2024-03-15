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
package com.maehem.mangocad.view.controlpanel.tab;

import com.maehem.mangocad.view.MarkdownUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class HomeContent extends BorderPane {

    public HomeContent() {
        setCenter(anxiousAlicanto());
    }

    private static Node anxiousAlicanto() {
        VBox node = new VBox();
        VBox.setVgrow(node, Priority.ALWAYS);
//        node.getChildren().add(new Text("Mango Viewer 1.0"));
//        node.getChildren().add(new Text("(Anxious Alicanto)"));

        InputStream mdContent = HomeContent.class.getResourceAsStream("/home/BodaciousBoobie.md");

        String str = "# Ooops";
        try {
            str = new String(mdContent.readAllBytes());
        } catch (IOException ex) {
            Logger.getLogger(HomeContent.class.getName()).log(Level.SEVERE, null, ex);
        }
        Node markdownNode = MarkdownUtils.markdownNode(1.0, str, "/home");

        node.getChildren().add(markdownNode);

        return node;
    }
}
