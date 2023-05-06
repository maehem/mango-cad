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

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DirectoriesConfigDialog extends Dialog<ButtonType> {

    public DirectoriesConfigDialog() {
        ButtonType okButtonType = ButtonType.OK;
        ButtonType cancelButtonType = ButtonType.CANCEL;
        getDialogPane().getButtonTypes().addAll(
                okButtonType, cancelButtonType
        );
        boolean disabled = false; // computed based on content of text fields, for example
        getDialogPane().lookupButton(okButtonType).setDisable(disabled);
        DirectoriesConfigPanel directoriesConfigPanel = new DirectoriesConfigPanel();
        getDialogPane().setContent(directoriesConfigPanel);
        showAndWait().ifPresent(response -> {
            if (response == okButtonType) {
                // Tell the pane to save the changes.
                directoriesConfigPanel.doSave();
                System.out.println("OK pressed.");
            } else {
                System.out.println(response);
            }
        });
    }

}
