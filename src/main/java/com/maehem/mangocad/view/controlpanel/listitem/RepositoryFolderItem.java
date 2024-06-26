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
import com.maehem.mangocad.view.controlpanel.RepoAddDialog;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RepositoryFolderItem extends ControlPanelListItem {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static final Image iconImage = new Image(
            ControlPanelListItem.class.getResourceAsStream("/icons/folder.png")
    );

    private Stage stage = null;
    private RepoPath repoPath;
    //private final String prefixShorten;
    private String previewTabContent = "";

    public RepositoryFolderItem(RepoPath repoPath) {
        this(repoPath, "");
    }

    public RepositoryFolderItem(RepoPath repoPath, String prefixShorten) {
        super(repoPath.getUrl().substring(prefixShorten.length()), repoPath.getDescription());
        this.repoPath = repoPath;
        //this.prefixShorten = prefixShorten;
        fetchURLPreview();
    }

    @Override
    public ContextMenu getContextMenu() {
        LOGGER.log(Level.SEVERE, "getContextMenu(): Repository Folder Item");
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem1 = new MenuItem("Add Repository...");
        menuItem1.setOnAction((t) -> new RepoAddDialog());

        contextMenu.getItems().addAll(
                menuItem1
);

        return contextMenu;
    }

    @Override
    public Image getImage() {
        return iconImage;
    }

    private void fetchURLPreview() {
        //  https://raw.githubusercontent.com/maehem/Thump/master/README.md
        HttpClient client = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                //.proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
                //.authenticator(Authenticator.getDefault())
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(repoPath.getUrl()))
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
        return new Text(previewTabContent);
    }

}
