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

import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.LibraryCache;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.view.ElementType;
import com.maehem.mangocad.model.element.highlevel.Package3d;
import com.maehem.mangocad.model.element.basic.PackageInstance;
import com.maehem.mangocad.model.element.highlevel.Device;
import com.maehem.mangocad.model.element.basic.Gate;
import com.maehem.mangocad.view.ControlPanel;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.SplitPane;
import static javafx.scene.layout.Priority.ALWAYS;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class TableOfContentsPane extends SplitPane {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static final double PANE_W = 0.15;
    private Library lib = new Library();
    private final LibraryElementListView deviceList;
    private final LibraryElementListView footprintList;
    private final LibraryElementListView package3dList;
    private final LibraryElementListView symbolList;
    private final DetailsArea detailsArea = new DetailsArea();

    private boolean selectorUpdating = false;

    public TableOfContentsPane(File lbrFile) {

//        try {
//            lib = EagleCADUtils.importLBR(lbrFile);
//        } catch (IOException ex) {
//            LOGGER.log(Level.SEVERE, null, ex);
//        } catch (EagleCADLibraryFileException ex) {
//            LOGGER.log(Level.SEVERE, null, ex);
//        }
        lib = LibraryCache.getInstance().getLibrary(lbrFile);
        if ( lib == null ) {
            LOGGER.log(Level.SEVERE, "OOPS! Library File didn't load!");
        }

        // TODO: Add selectoin listener.
        deviceList = new LibraryElementListView(this, lib, ElementType.DEVICE);
        //VBox footprintList = new VBox(new Label("Footprint List"));
        footprintList = new LibraryElementListView(this, lib, ElementType.FOOTPRINT);
        //VBox package3dList = new VBox(new Label("3D Package List"));
        package3dList = new LibraryElementListView(this, lib, ElementType.PACKAGE3D);
        //VBox symbolList = new VBox(new Label("Symbol List"));
        symbolList = new LibraryElementListView(this, lib, ElementType.SYMBOL);


        // Cause the panes to expand to height of window.
        //deviceList.getChildren().forEach(child -> VBox.setVgrow(child, ALWAYS));
        footprintList.getChildren().forEach(child -> VBox.setVgrow(child, ALWAYS));
        package3dList.getChildren().forEach(child -> VBox.setVgrow(child, ALWAYS));
        symbolList.getChildren().forEach(child -> VBox.setVgrow(child, ALWAYS));
        //detailsArea.getChildren().forEach(child -> VBox.setVgrow(child, ALWAYS));

        getItems().addAll(deviceList, footprintList, package3dList, symbolList, detailsArea);

        setDividerPosition(0, 1 * PANE_W);
        setDividerPosition(1, 2 * PANE_W);
        setDividerPosition(2, 3 * PANE_W);
        setDividerPosition(3, 4 * PANE_W);
        setDividerPosition(4, 5 * PANE_W);
    }

    /**
     * User has clicked an item in one of the panels. Update all other panels
     * for matching cross-reference values.
     *
     * @param aThis
     * @param newValue
     */
    void selectionChanged(ElementType src, String newValue) {
        // Selecting any list item programatially causes events to call this
        // selectionChanged method repeatedly. So we ignore changes while updating.
        if (selectorUpdating) {
            return;
        }

        selectorUpdating = true;
        switch (src) {
            case DEVICE -> {
                footprintList.clearSelections();
                symbolList.clearSelections();
                package3dList.clearSelections();

                for (DeviceSet ds : lib.getDeviceSets()) {
                    if (newValue.equals(ds.getName())) {
                        for (Device d : ds.getDevices()) {
                            footprintList.select(d.getFootprint());
                            // loop through package3d where pkginst matches d.getFootprint
                            for (Package3d p3d : lib.getPackages3d()) {
                                for (PackageInstance pi : p3d.getPackageInstances()) {
                                    if (pi.getName().equals(d.getFootprint())) {
                                        package3dList.select(p3d.getName());
                                    }
                                }
                            }
                        }
                        for (Gate g : ds.getGates()) {
                            symbolList.select(g.getSymbol());
                        }
                    }
                }

            }
            case FOOTPRINT -> {
                symbolList.clearSelections();
                package3dList.clearSelections();
                deviceList.clearSelections();

                // Devices
                for (DeviceSet ds : lib.getDeviceSets()) {
                    for (Device d : ds.getDevices()) {
                        if (newValue.equals(d.getFootprint())) {
                            deviceList.select(ds.getName());
                            // loop through package3d where pkginst matches d.getFootprint
                            for (Package3d p3d : lib.getPackages3d()) {
                                for (PackageInstance pi : p3d.getPackageInstances()) {
                                    if (pi.getName().equals(newValue)) {
                                        package3dList.select(p3d.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            case PACKAGE3D -> {
                footprintList.clearSelections();
                symbolList.clearSelections();
                deviceList.clearSelections();

                for (Package3d p3d : lib.getPackages3d()) {
                    for (PackageInstance pi : p3d.getPackageInstances()) {
                        if (p3d.getName().equals(newValue)) {
                            footprintList.select(pi.getName());
                            for (DeviceSet ds : lib.getDeviceSets()) {
                                for (Device d : ds.getDevices()) {
                                    if (pi.getName().equals(d.getFootprint())) {
                                        deviceList.select(ds.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            case SYMBOL -> {
                footprintList.clearSelections();
                package3dList.clearSelections();
                deviceList.clearSelections();

                for (DeviceSet ds : lib.getDeviceSets()) {
                    for (Gate g : ds.getGates()) {
                        if (g.getSymbol().equals(newValue)) {
                            deviceList.select(ds.getName());
                        }
                    }
                }
            }

        }
        selectorUpdating = false;
        detailsArea.setItem(lib, src, newValue);
    }

}
