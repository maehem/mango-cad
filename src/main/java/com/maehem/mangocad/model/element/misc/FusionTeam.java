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
 * ELEMENT fusionteam EMPTY
 *    ATTLIST fusionteam
 *      huburn                   %String;       #REQUIRED
 *      projecturn               %String;       #REQUIRED
 *      folderUrn                %String;       #REQUIRED
 *      urn                      %String;       #REQUIRED
 *      versionUrn               %String;       #REQUIRED
 *      camFileUrn               %String;       #REQUIRED
 *      camFileVersionUrn        %String;       #REQUIRED
 *      lastpublishedchangeguid  %String;       #REQUIRED
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class FusionTeam extends _AQuantum {

    public static final String ELEMENT_NAME = "fusionteam";

    private String hubUrn;
    private String projectUrn;
    private String folderUrn;
    private String urn;
    private String versionUrn;
    private String camFileUrn;
    private String camFileVersionUrn;
    private String lastPublishedChangeUid;

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
     * @return the folderUrn
     */
    public String getFolderUrn() {
        return folderUrn;
    }

    /**
     * @param folderUrn the folderUrn to set
     */
    public void setFolderUrn(String folderUrn) {
        this.folderUrn = folderUrn;
    }

    /**
     * @return the urn
     */
    public String getUrn() {
        return urn;
    }

    /**
     * @param urn the urn to set
     */
    public void setUrn(String urn) {
        this.urn = urn;
    }

    /**
     * @return the versionUrn
     */
    public String getVersionUrn() {
        return versionUrn;
    }

    /**
     * @param versionUrn the versionUrn to set
     */
    public void setVersionUrn(String versionUrn) {
        this.versionUrn = versionUrn;
    }

    /**
     * @return the camFileUrn
     */
    public String getCamFileUrn() {
        return camFileUrn;
    }

    /**
     * @param camFileUrn the camFileUrn to set
     */
    public void setCamFileUrn(String camFileUrn) {
        this.camFileUrn = camFileUrn;
    }

    /**
     * @return the camFileVersionUrn
     */
    public String getCamFileVersionUrn() {
        return camFileVersionUrn;
    }

    /**
     * @param camFileVersionUrn the camFileVersionUrn to set
     */
    public void setCamFileVersionUrn(String camFileVersionUrn) {
        this.camFileVersionUrn = camFileVersionUrn;
    }

    /**
     * @return the lastPublishedChangeUid
     */
    public String getLastPublishedChangeUid() {
        return lastPublishedChangeUid;
    }

    /**
     * @param lastPublishedChangeUid the lastPublishedChangeUid to set
     */
    public void setLastPublishedChangeUid(String lastPublishedChangeUid) {
        this.lastPublishedChangeUid = lastPublishedChangeUid;
    }

}
