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
package com.maehem.mangocad.view.library;

import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.element.highlevel.Device;
import com.maehem.mangocad.model.element.basic.DevicePackageInstance3d;
import com.maehem.mangocad.view.ControlPanel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DevicePackageList extends TableView<Map> {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private final TableColumn<Map, String> pkgColumn = new TableColumn<>("Package");
    private final TableColumn<Map, String> tdColumn = new TableColumn<>("3D");
    private final TableColumn<Map, String> variantColumn = new TableColumn<>("Variant");

    private final String CUBE_EMOJI = "\ud83e\uddca";  // u+1F9CA
    //private final Character CUBE = new Character(0x1f9ca);

    public DevicePackageList(DeviceSet ds) {

        pkgColumn.setCellValueFactory(new MapValueFactory<>("footprint"));
        tdColumn.setCellValueFactory(new MapValueFactory<>("threeD"));
        variantColumn.setCellValueFactory(new MapValueFactory<>("name"));
        getColumns().addAll(pkgColumn, tdColumn, variantColumn);

        ObservableList<Map<String, Object>> items
                = FXCollections.<Map<String, Object>>observableArrayList();

        for (Device d : ds.getDevices()) {
            Map<String, Object> deviceDeets = new HashMap<>();
            deviceDeets.put("name", d.getName());
            deviceDeets.put("footprint", d.getFootprint());
            String threeDexists = "";

            if (!d.getPackage3dInstances().isEmpty()) {
                DevicePackageInstance3d instance = d.getPackage3dInstances().get(0);
                // Devices always have a initial 3d Urn defined as version 1. Usually a blank/block file.
                if (!instance.getPackage3dUrn().endsWith("/1")) {
                    threeDexists = CUBE_EMOJI;
                }
            }
            deviceDeets.put("threeD", threeDexists);
            items.add(deviceDeets);
        }

        getItems().addAll(items);

        // Dress up column widths
        // Run later since we might not know the area size yet.
        Platform.runLater(() -> {
            pkgColumn.setPrefWidth(getBoundsInLocal().getWidth() * 0.33);
        });
    }

}
