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
package com.maehem.mangocad.model.element.misc;

import com.maehem.mangocad.model._AQuantum;

/**
 * <pre>
 * ELEMENT fusionsync EMPTY>
 *   ATTLIST fusionsync
 *      huburn                   %String;       #REQUIRED
 *      projecturn               %String;       #REQUIRED
 *      f3durn                   %String;       #REQUIRED
 *      pcbguid                  %String;       #REQUIRED
 *      lastsyncedchangeguid     %String;       #REQUIRED
 *      lastpulledtime           %String;       #REQUIRED
 *      // Added recently        %String;       #REQUIRED
 *      latestrevisionid         %String;       #REQUIRED
 *      lastsyncedrevisionid     %String;       #REQUIRED
 *      lastboardhashguid        %String;       #REQUIRED
 *      lastpushedtime           %String;       #REQUIRED
 *      linktopcb3d="true"     ( true | false ) #REQUIRED

 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class FusionSync extends _AQuantum {

    public static final String ELEMENT_NAME = "fusionsync";

    private String hubUrn;
    private String projectUrn;
    private String f3dUrn;
    private String pcbUid;
    private String lastSyncChangeUid;
    private String lastPulledTime;

    private String latestRevisionId;
    private String lastSyncedRevisionId;
    private String lastBoardHashGuid;
    private String lastPushedTime;
    private boolean linkToPcb3d = false;

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the hubUrn
     */
    public String getHubUrn() {
        return hubUrn;
    }

    /**
     * @param hubUrn the hubUrn to set
     */
    public void setHubUrn(String hubUrn) {
        this.hubUrn = hubUrn;
    }

    /**
     * @return the projectUrn
     */
    public String getProjectUrn() {
        return projectUrn;
    }

    /**
     * @param projectUrn the projectUrn to set
     */
    public void setProjectUrn(String projectUrn) {
        this.projectUrn = projectUrn;
    }

    /**
     * @return the f3dUrn
     */
    public String getF3dUrn() {
        return f3dUrn;
    }

    /**
     * @param f3dUrn the f3dUrn to set
     */
    public void setF3dUrn(String f3dUrn) {
        this.f3dUrn = f3dUrn;
    }

    /**
     * @return the pcbUid
     */
    public String getPcbUid() {
        return pcbUid;
    }

    /**
     * @param pcbUid the pcbUid to set
     */
    public void setPcbUid(String pcbUid) {
        this.pcbUid = pcbUid;
    }

    /**
     * @return the lastSyncChangeUid
     */
    public String getLastSyncChangeUid() {
        return lastSyncChangeUid;
    }

    /**
     * @param lastSyncChangeUid the lastSyncChangeUid to set
     */
    public void setLastSyncChangeUid(String lastSyncChangeUid) {
        this.lastSyncChangeUid = lastSyncChangeUid;
    }

    /**
     * @return the lastPulledTime
     */
    public String getLastPulledTime() {
        return lastPulledTime;
    }

    /**
     * @param lastPulledTime the lastPulledTime to set
     */
    public void setLastPulledTime(String lastPulledTime) {
        this.lastPulledTime = lastPulledTime;
    }

    /**
     * @return the latestRevisionId
     */
    public String getLatestRevisionId() {
        return latestRevisionId;
    }

    /**
     * @param latestRevisionId the latestRevisionId to set
     */
    public void setLatestRevisionId(String latestRevisionId) {
        this.latestRevisionId = latestRevisionId;
    }

    /**
     * @return the lastSyncedRevisionId
     */
    public String getLastSyncedRevisionId() {
        return lastSyncedRevisionId;
    }

    /**
     * @param lastSyncedRevisionId the lastSyncedRevisionId to set
     */
    public void setLastSyncedRevisionId(String lastSyncedRevisionId) {
        this.lastSyncedRevisionId = lastSyncedRevisionId;
    }

    /**
     * @return the lastBoardHashGuid
     */
    public String getLastBoardHashGuid() {
        return lastBoardHashGuid;
    }

    /**
     * @param lastBoardHashGuid the lastBoardHashGuid to set
     */
    public void setLastBoardHashGuid(String lastBoardHashGuid) {
        this.lastBoardHashGuid = lastBoardHashGuid;
    }

    /**
     * @return the lastPushedTime
     */
    public String getLastPushedTime() {
        return lastPushedTime;
    }

    /**
     * @param lastPushedTime the lastPushedTime to set
     */
    public void setLastPushedTime(String lastPushedTime) {
        this.lastPushedTime = lastPushedTime;
    }

    /**
     * @return the linkToPcb3d
     */
    public boolean isLinkToPcb3d() {
        return linkToPcb3d;
    }

    /**
     * @param linkToPcb3d the linkToPcb3d to set
     */
    public void setLinkToPcb3d(boolean linkToPcb3d) {
        this.linkToPcb3d = linkToPcb3d;
    }

}
