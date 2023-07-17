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
import com.maehem.mangocad.RepoPathManager;
import com.maehem.mangocad.view.ControlPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class RepoAddPanel extends VBox {

    public static final Logger LOGGER = ControlPanel.LOGGER;
    public static final String LIB_UTILS_PATHS_KEY = "Directories.CorePaths";
    public static final String PROJECT_PATHS_KEY = "Directories.Projects";
    
    // Repos use a key thus:   KEY.nmenonic.url and KEY.nmemonic.description
    public static final String REPOSITORY_PATHS_KEY = "Directories.Repositories";
    
    SimpleStringProperty urlPathProperty;
    SimpleStringProperty shortDescProperty;
    SimpleStringProperty uidProperty;
    
    public RepoAddPanel() {
        // TODO: Change this to "Places" where these are paths to root dirs with
        //       subdirs that handle most of these: Library, Scripts, ULP, etc.
        //       Libraries and Projects only.
        setSpacing(2);
        AppProperties appProps = AppProperties.getInstance();
        
        urlPathProperty = new SimpleStringProperty( "");
        Node box1 = pathWidget("URL", urlPathProperty);
        shortDescProperty = new SimpleStringProperty("");
        Node box2 = pathWidget("Short Description", shortDescProperty);
        uidProperty = new SimpleStringProperty("(optional)");
        Node box3 = pathWidget("UID", uidProperty);
        
        getChildren().addAll(box1, box2, box3 );        
    }
    
    private Node pathWidget(String label, SimpleStringProperty property ) {
        // TODO: Add "edit" button on right for a popup list editor
        // TODO: Make Tooltip that turns cotent in to list of paths.
        Label l = new Label(label + ":");
        l.setMaxWidth(140);
        l.setMinWidth(140);
        l.setPrefWidth(140);
        l.setPadding(new Insets(4,4, 0, 0));
        l.setAlignment(Pos.CENTER_RIGHT);
        
        TextField tf = new TextField(property.get());
        tf.textProperty().bindBidirectional(property);
        tf.setMaxWidth(500);
        tf.setPrefWidth(500);
        tf.setMinWidth(500);
        tf.setAlignment(Pos.CENTER_LEFT);
        
        HBox widget = new HBox(l, tf);
        widget.setPadding(new Insets(4));
//        widget.setBorder(new Border(new BorderStroke(
//                Color.LIGHTGRAY, BorderStrokeStyle.SOLID, 
//                new CornerRadii(4), new BorderWidths(1)
//        )));
        
        return widget;
    }
    
    /**
     * Do save to app properties.
     */
    protected void doSave() {
        RepoPathManager repoMgr = RepoPathManager.getInstance();
        
        RepoPath url = repoMgr.getByUrl(urlPathProperty.getValue());
        if ( url == null) {
            RepoPath repoPath = new RepoPath(urlPathProperty.get(), shortDescProperty.get());
            repoMgr.add(repoPath );
            LOGGER.log(Level.SEVERE, "Added new RepoPath: " + repoPath.toString());
        }
        repoMgr.save();
        
        // Cause Control Panel to re-load.
        
        
//        appProps.setProperty(PROJECT_PATHS_KEY, shortDescProperty.get());
//        appProps.setProperty(LIB_UTILS_PATHS_KEY, urlPathProperty.get());
//        appProps.setProperty(REPOSITORY_PATHS_KEY, uidProperty.get());
    }
}
