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
package com.maehem.mangocad.view.controlpanel.listitem;

import com.maehem.mangocad.view.ControlPanel;
import com.maehem.mangocad.view.controlpanel.ControlPanelUtils;
import com.maehem.mangocad.view.controlpanel.RepoPath;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class GitHubFolderItem extends ControlPanelListItem {

    private static final Logger LOGGER = ControlPanel.LOGGER;
    public static final String GITHUB_PREFIX = "https://github.com/";
    private static final String GITHUB_RAW_PREFIX = "https://raw.githubusercontent.com/";

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/folder.png")
    );

    private Stage stage = null;
    private RepoPath repoPath;
    //private final String prefixShorten;
    private String previewTabContent = "";


    public GitHubFolderItem(RepoPath repoPath) {
        super(repoPath.getUrl().substring(GITHUB_PREFIX.length()), repoPath.getDescription());
        this.repoPath = repoPath;
        //this.prefixShorten = prefixShorten;
        fetchURLPreview();
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.SEVERE, "getContextMenu(): Repository Folder Item");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Open");
        MenuItem menuItem2 = new MenuItem("Rename");
        MenuItem menuItem3 = new MenuItem("Copy");
        MenuItem menuItem4 = new MenuItem("[x] In Use");
        MenuItem menuItem5 = new MenuItem("Show in Finder");

        menuItem1.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem1.getText()});

//            if (stage == null) {
//                stage = new Stage();
//                LibraryEditor root = new LibraryEditor( getFile() ); // TODO Project Editor
//                stage.setTitle("Project Editor: " + getName());
//                stage.setScene(new Scene(root, 1280, 960));
//                stage.centerOnScreen();
//                stage.setOnCloseRequest((t) -> {
//                    // TODO: Popup if file edited and not saved.
//
//                    stage.close();
//                    stage = null;
//                });
//            }
//            stage.toFront();
//            stage.show();
        });
        menuItem2.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem2.getText()});
        });
        menuItem3.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem3.getText()});
        });
        menuItem4.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem4.getText()});
        });
        menuItem5.setOnAction((event) -> {
            LOGGER.log(Level.SEVERE, "{0}: {1}", new Object[]{getName(), menuItem5.getText()});
        });

        contextMenu.getItems().addAll(
                menuItem1,
                menuItem2,
                menuItem3,
                menuItem4,
                new SeparatorMenuItem(),
                menuItem5);

        return contextMenu;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }

    private void fetchURLPreview() {
        //  https://raw.githubusercontent.com/maehem/Thump/master/README.md
        
        // Turn the browser-freindly URL into a raw-content URL.
        String sss = repoPath.getUrl().substring(GITHUB_PREFIX.length());
        LOGGER.log(Level.SEVERE, "SSS:" + sss);
        String[] split = sss.split("\\/");
        String userName = split[0];
        String repoName = split[1];
        String branch1 = "master";
        String branch2 = "main";
        
        String url = GITHUB_RAW_PREFIX + userName + "/" + repoName + "/" + branch1 + "/README.md";
        
        HttpClient client = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                //.proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
                //.authenticator(Authenticator.getDefault())
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .GET()
                .build();

        client.sendAsync(request, BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept((theResponse) -> {
                    previewTabContent = theResponse;
                });
    }

    @Override
    public Node getPreviewTabNode() {
        return ControlPanelUtils.markdownNode(1, previewTabContent);
    }

}
