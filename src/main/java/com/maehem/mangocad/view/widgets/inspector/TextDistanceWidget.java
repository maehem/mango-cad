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
package com.maehem.mangocad.view.widgets.inspector;

import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.basic.TextElement;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.control.Spinner;

/**
 * Settings for element angle rotations.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class TextDistanceWidget extends InspectorWidget {

    // TODO: Cell renderer for adding percent to value displayed.
    private final Spinner<Integer> spinner = new Spinner<>(0, 250, 0);
    private final TextElement text;

    public TextDistanceWidget(Element e, String msgKeyBase) {
        super(msgKeyBase);
        if (e instanceof TextElement p) {
            this.text = p;
            Platform.runLater(() -> {
                this.text.addListener(this);
            });
        } else {
            this.text = null;
            LOGGER.log(Level.SEVERE, "TextSwapLevelWidget: element is not of type Pin!");
        }

        spinner.setPrefWidth(70);
//        Label iconLabel = new Label(MSG.getString("TEXT_DISTANCE"));
//        iconLabel.setPadding(new Insets(4));
//        iconLabel.setAlignment(Pos.BASELINE_CENTER);

        updateSpinnerState(text.getDistance());

        getChildren().addAll(spinner);

        spinner.valueProperty().addListener((obsVal, oldValue, newValue) -> {
            text.setDistance((int) newValue);
        });

    }

    @SuppressWarnings("unchecked")
    private void updateSpinnerState(int pl) {
        spinner.getValueFactory().setValue(pl);
    }

    @Override
    public void stopListening() {
        if (text != null) {
            text.removeListener(this);
        }
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        // Update widgets.
        if (!field.equals(TextElement.Field.DISTANCE)) {
            return;
        }
        if (newVal == null) {
            return;
        }
        LOGGER.log(Level.SEVERE, "TextLineDistanceWidget: Text dist: ==> {0}", newVal.toString());

        if (newVal instanceof Integer pd) {
            updateSpinnerState(pd);
        }
    }

}
