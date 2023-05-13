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
package com.maehem.mangocad.view.controlpanel.tab;

import com.maehem.mangocad.view.controlpanel.ControlPanelListItem;
import com.maehem.mangocad.view.controlpanel.ControlPanelUtils;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PreviewContent extends BorderPane {
    
    public PreviewContent() {
        
        setCenter(new TextArea("Content Area"));
        
    }

    public void setItem(ControlPanelListItem item) {
//       contentArea.clear();
//       contentArea.setText(ControlPanelUtils.getItemDescriptionFull(item.getFile()));
        Node cNode = ControlPanelUtils.markdownNode(1.5, ControlPanelUtils.getItemDescriptionFull(item));
        setCenter(cNode);
    }
    
}
