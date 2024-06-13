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
package com.maehem.mangocad.view.widgets.selection;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.basic.Dimension;
import com.maehem.mangocad.model.element.basic.ElementCircle;
import com.maehem.mangocad.model.element.basic.ElementRectangle;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.basic.Wire;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.MarkdownUtils;
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SelectionOverviewWidget extends HBox {

    private static final double ICON_SIZE = 16;
    private static final double NAME_LABEL_WIDTH = 60;
    private static final double VALUE_LABEL_WIDTH = 60;
    private static final char LF = '\n';

    // TODO: Static images come from a Util class.
    private final static Image LINE_IMG = ViewUtils.getImage("/icons/line.png");
    private final static Image DIM_IMG = ViewUtils.getImage("/icons/dimension.png");
    private final static Image PIN_IMG = ViewUtils.getImage("/icons/pin.png");
    private final static Image TEXT_IMG = ViewUtils.getImage("/icons/text.png");
    private final static Image RECT_IMG = ViewUtils.getImage("/icons/rectangle.png");
    private final static Image CIRC_IMG = ViewUtils.getImage("/icons/circle.png");

    private final Label valueLabel = new Label();
    private final Label nameLabel = new Label();
    private final Label layerlabel = new Label();

    public SelectionOverviewWidget(Element item) {
        setId("selection-overview-widget");
        nameLabel.setMinWidth(NAME_LABEL_WIDTH);
        nameLabel.setPrefWidth(NAME_LABEL_WIDTH);
        nameLabel.setMaxWidth(NAME_LABEL_WIDTH);
        valueLabel.setMinWidth(VALUE_LABEL_WIDTH);
        valueLabel.setPrefWidth(VALUE_LABEL_WIDTH);
        valueLabel.setMaxWidth(VALUE_LABEL_WIDTH);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinWidth(ICON_SIZE);

        if (item != null) {
            ImageView icon;

            if (item instanceof Wire w) {
                icon = ViewUtils.createIcon(LINE_IMG, ICON_SIZE);
            } else if (item instanceof Dimension) {
                icon = ViewUtils.createIcon(DIM_IMG, ICON_SIZE);
            } else if (item instanceof Pin p) {
                String value = p.getName().lines().findFirst().get();

                if (value.length() > 13) {
                    nameLabel.setText(value.substring(0, 10) + "...");
                } else {
                    nameLabel.setText(value);
                }
                icon = ViewUtils.createIcon(PIN_IMG, ICON_SIZE);
            } else if (item instanceof ElementText et) {
                String value = et.getValue().lines().findFirst().get();

                if (value.length() > 13) {
                    valueLabel.setText(value.substring(0, 10) + "...");
                } else {
                    valueLabel.setText(value);
                }
                icon = ViewUtils.createIcon(TEXT_IMG, ICON_SIZE);
            } else if (item instanceof ElementRectangle r) {
                icon = ViewUtils.createIcon(RECT_IMG, ICON_SIZE);
            } else if (item instanceof ElementCircle c) {
                icon = ViewUtils.createIcon(CIRC_IMG, ICON_SIZE);
            } else {
                icon = new ImageView();
            }

            if (item instanceof LayerNumberProperty lp) {
                layerlabel.setText(String.valueOf(lp.getLayerNum()));
            }

            Label iconLabel = new Label("", icon);

            Tooltip tt = generateTooltip(item);
            iconLabel.setTooltip(tt);
            nameLabel.setTooltip(tt);
            valueLabel.setTooltip(tt);
            layerlabel.setTooltip(tt);

            getChildren().addAll(iconLabel, nameLabel, valueLabel, layerlabel, spacer);
        } else {
            LOGGER.log(Level.SEVERE, "SelectionOverviewWidget: Item is null!!!  But why?");
        }
    }

    private Tooltip generateTooltip(Element item) {
        Tooltip t = new Tooltip();

        /*
        For fixing tooltip stage pop issue:
                public class FixedTooltip extends Tooltip {

                    public FixedTooltip(String string) {
                        super(string);
                    }

                    @Override
                    protected void show() {
                        Window owner = getOwnerWindow();
                        if (owner.isFocused())
                            super.show();
                    }

                }
         */
        t.setText(item.getElementName());
        if (item instanceof Wire w) {
            StringBuilder sb = new StringBuilder(" ####Wire").append(LF);
            sb.append("* on layer *").append(w.getLayerNum()).append("*").append(LF);
            sb.append(LF);
            sb.append("*x1:* ").append(w.getX1()).append("    *y1:* ").append(w.getY1()).append(LF);
            sb.append("*x2:* ").append(w.getX2()).append("    *y2:* ").append(w.getY2()).append(LF);
            sb.append(LF);
            sb.append("Line Length: ").append(w.getLength()).append(LF);
            sb.append(" Line Width: ").append(w.getWidth()).append(LF);
            sb.append(" Line Style: ").append(w.getStyle().code()).append(LF);
            sb.append("      Curve: ").append(w.getCurve()).append(LF);

            //t.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            t.setGraphic(MarkdownUtils.markdownNode(0.75, sb.toString(), null));
            t.setText(null);

        }

        return t;
    }

}
