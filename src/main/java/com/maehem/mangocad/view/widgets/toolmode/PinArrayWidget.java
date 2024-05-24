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
package com.maehem.mangocad.view.widgets.toolmode;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.element.basic.Pin;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.ViewUtils;
import java.util.logging.Level;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinArrayWidget extends ToolModeWidget {

    private static final String ICON_ARRAY_PATH = "/icons/pin-array.png";

    private final Button button;

    private final Pin pin;

    public PinArrayWidget(Element e) {
        if (e instanceof Pin p) {
            this.pin = p;
            this.pin.addListener(this);
        } else {
            this.pin = null;
            LOGGER.log(Level.SEVERE, "PinVisibleToggleWidget: element is not of type Pin!");
        }

        Image img = ViewUtils.getImage(ICON_ARRAY_PATH);
        ImageView arrayButtonImg = ViewUtils.createIcon(img, ICON_SIZE);

        button = new Button("", arrayButtonImg); // TODO MSG Tooltip

        getChildren().add(button);

        button.setOnAction((t) -> {
            LOGGER.log(Level.SEVERE, "Pop up pin array dialog...");
        });

    }

    @Override
    public void stopListening() {
        pin.removeListener(this);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
    }

}
