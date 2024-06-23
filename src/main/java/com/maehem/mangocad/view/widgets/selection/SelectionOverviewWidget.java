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

import com.maehem.mangocad.model.element.Element;
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
import java.text.DecimalFormat;
import java.util.logging.Level;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Window;

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
    public final Element element;

    public SelectionOverviewWidget(Element item) {
        this.element = item;
        nameLabel.setMinWidth(NAME_LABEL_WIDTH);
        nameLabel.setPrefWidth(NAME_LABEL_WIDTH);
        nameLabel.setMaxWidth(NAME_LABEL_WIDTH);
        valueLabel.setMinWidth(VALUE_LABEL_WIDTH);
        valueLabel.setPrefWidth(VALUE_LABEL_WIDTH);
        valueLabel.setMaxWidth(VALUE_LABEL_WIDTH);

        highlight(false);
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

    public void highlight(boolean highlight) {
        setId(highlight ? "selection-overview-widget-highlight" : "selection-overview-widget");
    }

    //For fixing tooltip stage pop issue:
    public class FixedTooltip extends Tooltip {

        public FixedTooltip() {
            super();
        }

        @Override
        protected void show() {
            Window owner = getOwnerWindow();
            if (owner.isFocused()) {
                super.show();
            }
        }

    }

    private Tooltip generateTooltip(Element item) {
        Tooltip t = new FixedTooltip();
        int PRECISION = 3;
        DecimalFormat df = new DecimalFormat("#");

        t.setText(item.getElementName());
        if (item instanceof Wire w) {
            StringBuilder sb = new StringBuilder(" ####Wire").append(LF);
            sb.append("* on layer *").append(w.getLayerNum()).append("*").append(LF);
            sb.append(LF)
                    .append("*x1:* ")
                    .append(w.x1Property.getPrecise(PRECISION))
                    .append("    *y1:* ")
                    .append(w.y1Property.getPrecise(PRECISION))
                    .append(LF)
                    .append("*x2:* ")
                    .append(w.x2Property.getPrecise(PRECISION))
                    .append("    *y2:* ")
                    .append(w.y2Property.getPrecise(PRECISION))
                    .append(LF)
                    .append(LF);

            // Length but pretty
            StringBuilder pattern = new StringBuilder("#.");
            for (int i = 0; i < PRECISION; i++) {
                pattern.append("#");
            }

            sb.append("Line Length: ").append(df.format(w.getLength())).append(LF)
                    .append(" Line Width: ").append(w.widthProperty.getPrecise(PRECISION)).append(LF)
                    .append(" Line Style: ").append(w.getStyle().code()).append(LF)
                    .append("      Curve: ").append(w.curveProperty.getPrecise(PRECISION)).append(LF);

            t.setGraphic(MarkdownUtils.markdownNode(0.75, sb.toString(), null));
            t.setText(null);
        } else if (item instanceof Pin p) {
            StringBuilder sb = new StringBuilder(" ####Pin").append(LF);
                    sb.append("Name:  **").append(p.nameProperty.get()).append("** ")
                    .append(LF)
                    .append(LF)
                    .append("*x:* ")
                    .append(p.xProperty.getPrecise(PRECISION))
                    .append("    *y:* ")
                    .append(p.yProperty.getPrecise(PRECISION))
                    .append(LF)
                            .append("*Rotation:* ").append(df.format(p.getRot()))
                            .append(LF)
                            .append("*Function:* ").append(p.getFunction().code()).append(LF)
                            .append("*Direction:* ").append(p.getDirection().code()).append(LF)
                            .append("*Visible:* ").append(p.getVisible().code()).append(LF)
                            .append("*Length:* ").append(p.getLength().code()).append(LF)
                            .append("*Swap Level:* ").append(String.valueOf(p.getSwapLevel())).append(LF)                            ;
            t.setGraphic(MarkdownUtils.markdownNode(0.75, sb.toString(), null));
            t.setText(null);
        } else if (item instanceof Dimension d) {
            StringBuilder sb = new StringBuilder(" ####Dimension").append(LF);
            sb.append(LF)
                    .append("*x1:* ")
                    .append(d.x1Property.getPrecise(PRECISION))
                    .append("    *y1:* ")
                    .append(d.y1Property.getPrecise(PRECISION))
                    .append(LF)
                    .append("*x2:* ")
                    .append(d.x2Property.getPrecise(PRECISION))
                    .append("    *y2:* ")
                    .append(d.y2Property.getPrecise(PRECISION))
                    .append(LF)
                    .append("*x3:* ")
                    .append(d.x3Property.getPrecise(PRECISION))
                    .append("    *y3:* ")
                    .append(d.y3Property.getPrecise(PRECISION))
                    .append(LF)
                    .append(LF)
                    .append("*Width:* ").append(d.widthProperty.getPrecise(PRECISION)).append(LF)
                    .append("*Units:* ").append(d.getUnit().code()).append(LF)
                    .append("*Ext. Width:* ").append(d.extwidthProperty.getPrecise(PRECISION)).append(LF)
                    .append("*Ext. Length:* ").append(d.extlengthProperty.getPrecise(PRECISION)).append(LF)
                    .append("*Ext. Offset:* ").append(d.extoffsetProperty.getPrecise(PRECISION)).append(LF)
                    .append(LF)
                    .append("*Text Size:* ").append(d.textsizeProperty.getPrecise(PRECISION)).append(LF)
                    .append("*Text Ratio:* ").append(d.textratioProperty.get())
                    .append(LF);
            t.setGraphic(MarkdownUtils.markdownNode(0.75, sb.toString(), null));
            t.setText(null);
        } else if (item instanceof ElementCircle c) {
            StringBuilder sb = new StringBuilder(" ####Pin").append(LF);
            sb.append(LF)
                    .append(LF)
                    .append("*x:* ")
                    .append(c.xProperty.getPrecise(PRECISION))
                    .append("    *y:* ")
                    .append(c.yProperty.getPrecise(PRECISION))
                    .append(LF)
                    .append("*Radius:* ").append(c.radiusProperty.getPrecise(PRECISION)).append(LF)
                    .append("*Width:* ").append(c.widthProperty.getPrecise(PRECISION)).append(LF);
            t.setGraphic(MarkdownUtils.markdownNode(0.75, sb.toString(), null));
            t.setText(null);
        } else if (item instanceof ElementRectangle er) {
            StringBuilder sb = new StringBuilder(" ####Dimension").append(LF);
            sb.append("* on layer *").append(er.getLayerNum()).append("*").append(LF);
            sb.append(LF)
                    .append("*x1:* ")
                    .append(er.x1Property.getPrecise(PRECISION))
                    .append("    *y1:* ")
                    .append(er.y1Property.getPrecise(PRECISION))
                    .append(LF)
                    .append("*x2:* ")
                    .append(er.x2Property.getPrecise(PRECISION))
                    .append("    *y2:* ")
                    .append(er.y2Property.getPrecise(PRECISION))
                    .append(LF)
                    .append(LF)
                    .append("*Rotation:* ").append(df.format(er.getRot())).append(LF);
            t.setGraphic(MarkdownUtils.markdownNode(0.75, sb.toString(), null));
            t.setText(null);
        } else if (item instanceof ElementText et) {
            StringBuilder sb = new StringBuilder(" ####Text").append(LF);
            sb.append("* on layer *").append(et.getLayerNum()).append("*").append(LF);
            sb.append("Value:  **").append(et.valueProperty.get()).append("** ")
                    .append(LF)
                    .append(LF)
                    .append("*x:* ")
                    .append(et.xProperty.getPrecise(PRECISION))
                    .append("    *y:* ")
                    .append(et.yProperty.getPrecise(PRECISION))
                    .append(LF)
                    .append("*Rotation:* ").append(df.format(et.getRot())).append(LF)
                    .append("*Constrained:* ").append((et.rotation.isConstrained() ? "YES" : "NO")).append(LF)
                    .append("*Mirror:* ").append((et.isMirrored()?"YES":"NO")).append(LF)
                    .append("*Spin:* ").append((et.isSpin()?"YES":"NO")).append(LF).append(LF)
                    .append("*Size:* ").append(et.sizeProperty.getPrecise(PRECISION)).append(LF)
                    .append("*Text Ratio:* ").append(et.ratioProperty.get()).append(LF)
                    .append("*Align:* ").append(et.getAlign().code()).append(LF)
                    .append("*Font:* ").append(et.getFont().code()).append(LF)
                    .append("*Line Distance:* ").append(et.getDistance()).append(LF);
            t.setGraphic(MarkdownUtils.markdownNode(0.75, sb.toString(), null));
            t.setText(null);
        }

        return t;
    }

}
