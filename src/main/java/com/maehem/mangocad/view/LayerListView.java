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

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.misc.LayerElement;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LayerListView extends TreeTableView<LayerElement> {

    private static final ResourceBundle MSG = ResourceBundle.getBundle("i18n/Editor");

    private final Image eyeImage = ViewUtils.getImage("/icons/eye.png");
    private final ImageView eyeHeader = ViewUtils.createIcon(eyeImage, 16);

    private final Image trashImage = ViewUtils.getImage("/icons/trash-can.png");
    private final ImageView trashHeader = ViewUtils.createIcon(trashImage, 12);

    private final TreeTableColumn<LayerElement, Boolean> visibleColumn = new TreeTableColumn<>("EYE");
    private final TreeTableColumn<LayerElement, String> numColumn = new TreeTableColumn<>(MSG.getString("LAYER_LIST_NUM_COL"));
    private final TreeTableColumn<LayerElement, Integer> swatchColumn = new TreeTableColumn<>(" ");
    private final TreeTableColumn<LayerElement, String> nameColumn = new TreeTableColumn<>(MSG.getString("LAYER_LIST_NAME_COL"));
    private final TreeTableColumn<LayerElement, Boolean> deleteColumn = new TreeTableColumn<>("X");

    private static final Double VIS_COL_WIDTH = 48.0;
    private static final Double SWATCH_COL_WIDTH = 35.0;
    private static final Double NUM_COL_WIDTH = 30.0;
    private static final Double NAME_COL_WIDTH = 150.0;
    private static final Double DEL_COL_WIDTH = 25.0;

    public LayerListView(ColorPalette palette, Layers layers) {
        initColumns(palette);

        TreeItem<LayerElement> root = new TreeItem<>(new LayerElement());

        // for each layer generate item
        for (LayerElement le : layers.getElements()) {
            root.getChildren().add(new TreeItem<>(le));
        }

        setRoot(root);
        setShowRoot(false);

        MenuItem showLayersItem = new MenuItem("Show Layers");
        //entry1.setOnAction(ae -> ...);
        showLayersItem.setOnAction((ev) -> {
            LOGGER.log(Level.SEVERE, "Show Layers");
        });
        MenuItem hideLayersItem = new MenuItem("Hide layers");
        hideLayersItem.setOnAction((ev) -> {
            LOGGER.log(Level.SEVERE, "Hide Layers");
        });
        MenuItem layerPropertiesItem = new MenuItem("Properties");
        layerPropertiesItem.setOnAction((ev) -> {
            LOGGER.log(Level.SEVERE, "Get Props for: {0}", getSelectionModel().getSelectedItem().getValue().getName());
        });
//        MenuItem deleteLayerItem = new MenuItem("Delete");
//        deleteLayerItem.setOnAction((ev) -> {
//            LOGGER.log(Level.SEVERE, "Delete: {0}", getSelectionModel().getSelectedItem().getValue().getName());
//        });

        setContextMenu(new ContextMenu(
                showLayersItem,
                hideLayersItem,
                new SeparatorMenuItem(),
                layerPropertiesItem //,
        //new SeparatorMenuItem(),
        //deleteLayerItem
        ));

    }

    private void initColumns(ColorPalette palette) {
        visibleColumn.setGraphic(eyeHeader);
        visibleColumn.setText(null);
        visibleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("visible"));
        visibleColumn.setMinWidth(VIS_COL_WIDTH);
        visibleColumn.setPrefWidth(VIS_COL_WIDTH);
        visibleColumn.setCellFactory((p) -> {
            return new TreeTableCell<>() {

                ImageView imageView;

                @Override
                protected void updateItem(Boolean visible, boolean empty) {
                    if (!empty && visible != null) {
                        //LOGGER.log(Level.SEVERE, "updateItem(" + item + ")");

                        if (imageView == null) {
                            imageView = ViewUtils.createIcon(eyeImage, 16);
                        }

                        LayerElement item = getTableRow().getItem();
                        imageView.setImage(eyeImage);
                        setText(null);
                        setOnMouseClicked((t) -> {
                            // TODO: Put changed things into a list so that we
                            // can apply or ignore based on whether user chooses OK or Cancel.

                            item.setVisible(!item.isVisible());
                            imageView.setOpacity(item.isVisible() ? 1.0 : 0.5);
                        });
                        if (!visible) {
                            imageView.setOpacity(0.5);
                        }
                        setGraphic(imageView);
                        setAlignment(Pos.CENTER);
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };

        });

        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameColumn.setMinWidth(NAME_COL_WIDTH * 0.7);
        nameColumn.setPrefWidth(NAME_COL_WIDTH);
        nameColumn.setCellFactory((p) -> {

            return new TreeTableCell<>() {

                @Override
                protected void updateItem(String text, boolean empty) {
                    super.updateItem(text, empty);

                    if (!empty && text != null) {
                        //LOGGER.log(Level.SEVERE, "updateItem(" + item + ")");
                        LayerElement item = getTableRow().getItem();
                        setText(item.getName());
                        //setContextMenu(initContextMenu(item));
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }

            };
        });

        numColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("number"));
        numColumn.setMinWidth(NUM_COL_WIDTH);
        numColumn.setPrefWidth(NUM_COL_WIDTH);

        swatchColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("colorIndex")); // TODO: Swatch factory
        swatchColumn.setMinWidth(SWATCH_COL_WIDTH);
        swatchColumn.setPrefWidth(SWATCH_COL_WIDTH);
        swatchColumn.setCellFactory((p) -> {

            return new TreeTableCell<>() {
                @Override
                protected void updateItem(Integer t, boolean bln) {
                    super.updateItem(t, bln);
                    LayerElement item = getTableRow().getItem();
                    if (item != null) {
                        Color c = ColorUtils.getColor(palette.getHex(item.getColorIndex()));
                        setGraphic(FillStyle.getSwatch(item.getFill(), c, 24));
                    }
                }

            };
        });

        deleteColumn.setGraphic(trashHeader);
        deleteColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("allowDelete"));
        deleteColumn.setMinWidth(DEL_COL_WIDTH);
        deleteColumn.setPrefWidth(DEL_COL_WIDTH);
        deleteColumn.setCellFactory((p) -> {

            return new TreeTableCell<>() {
                ImageView imageView;

                @Override
                protected void updateItem(Boolean allowDelete, boolean empty) {
                    if (!empty && allowDelete != null) {
                        //LOGGER.log(Level.SEVERE, "updateItem(" + item + ")");

                        if (imageView == null) {
                            imageView = ViewUtils.createIcon(trashImage, 12);
                            //imageView = new ImageView();
                            //imageView.setFitHeight(16);
                            //imageView.setPreserveRatio(true);
                            //imageView.setSmooth(true);
                        }

                        LayerElement item = getTableRow().getItem();
                        //imageView.setImage(eyeImage);
                        setText(null);
                        setGraphic(imageView);
                        setAlignment(Pos.CENTER);
                        if (!allowDelete) {
                            imageView.setOpacity(0.4);
                            setTooltip(null);
                        } else {
                            imageView.setOpacity(1.0);
                            setTooltip(new Tooltip("Deletable Layer..."));
                        }
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };

        });

        getColumns().add(visibleColumn);
        getColumns().add(numColumn);
        getColumns().add(swatchColumn);
        getColumns().add(nameColumn);
        getColumns().add(deleteColumn);

    }

    private ContextMenu initContextMenu(LayerElement el) {
        MenuItem entry1 = new MenuItem("Show Layers");
        //entry1.setOnAction(ae -> ...);
        entry1.setOnAction((ev) -> {
            LOGGER.log(Level.SEVERE, "selected row: {0} ", el.getName());
        });
        MenuItem entry2 = new MenuItem("Hide layers");
        //entry2.setOnAction(ae -> ...);
        MenuItem entry3 = new MenuItem("Properties");
        MenuItem entry4 = new MenuItem("Delete");

        return new ContextMenu(
                entry1,
                entry2,
                new SeparatorMenuItem(),
                entry3,
                new SeparatorMenuItem(),
                entry4
        );

    }
}
