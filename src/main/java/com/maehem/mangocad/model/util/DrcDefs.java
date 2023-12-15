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
package com.maehem.mangocad.model.util;

/**
 * Design Rules and their default values.
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public enum DrcDefs {

    // Settings in order that they are written to the save file as Eagle would write them.
    LAYER_SETUP("layerSetup", "(1*16)"),
    MT_COPPER("mtCopper", "0.035 0.035 0.035 0.035 0.035 0.035 0.035 0.035 0.035 0.035 0.035 0.035 0.035 0.035 0.035 0.035"),
    MT_ISOLATE("mtIsolate", "1.5mm 0.15mm 0.2mm 0.15mm 0.2mm 0.15mm 0.2mm 0.15mm 0.2mm 0.15mm 0.2mm 0.15mm 0.2mm 0.15mm 0.2mm"),
    MD_WIRE2WIRE("mdWireWire", "6mil"),
    MD_WIRE2PAD("mdWirePad", "6mil"),
    MD_WIRE2VIA("mdWireVia", "6mil"),
    MD_PAD2PAD("mdPadPad", "6mil"),
    MD_PAD2VIA("mdPadVia", "6mil"),
    MD_VIA2VIA("mdViaVia", "6mil"),
    MD_SMD2PAD("mdSmdPad", "6mil"),
    MD_SMD2VIA("mdSmdVia", "6mil"),
    MD_SMD2SMD("mdSmdSmd", "6mil"),
    MD_VIA2VIA_SAME_LAYER("mdViaViaSameLayer", "6mil"),
    MN_LAYERS2VIA_IN_SMD("mnLayersViaInSmd", "2"),
    MD_COPPER2DIMENSION("mdCopperDimension", "40mil"),
    MD_DRILL("mdDrill", "6mil"),
    MD_SMD2STOP("mdSmdStop", "0mil"),
    MS_WIDTH("msWidth", "6mil"),
    MS_DRILL("msDrill", "0.35mm"),
    MS_MICRO_VIA("msMicroVia", "9.99mm"),
    MS_BLIND_VIA_RATIO("msBlindViaRatio", "0.5"),
    // Annular Ring Tab Settings
    RV_PAD_TOP("rvPadTop", "0.25"),
    RV_PAD_INNER("rvPadInner", "0.25"),
    RV_PAD_BOTTOM("rvPadBottom", "0.25"),
    RV_VIA_OUTER("rvViaOuter", "0.25"),
    RV_VIA_INNER("rvViaInner", "0.25"),
    RV_MICRO_VIA_OUTER("rvMicroViaOuter", "0.25"),
    RV_MICRO_VIA_INNER("rvMicroViaInner", "0.25"),
    RL_MIN_PAD_TOP("rlMinPadTop", "10mil"),
    RL_MAX_PAD_TOP("rlMaxPadTop", "20mil"),
    RL_MIN_PAD_INNER("rlMinPadInner", "10mil"),
    RL_MAX_PAD_INNER("rlMaxPadInner", "20mil"),
    RL_MIN_PAD_BOTTOM("rlMinPadBottom", "10mil"),
    RL_MAX_PAD_BOTTOM("rlMaxPadBottom", "20mil"),
    RL_MIN_VIA_OUTER("rlMinViaOuter", "8mil"),
    RL_MAX_VIA_OUTER("rlMaxViaOuter", "20mil"),
    RL_MIN_VIA_INNER("rlMinViaInner", "8mil"),
    RL_MAX_VIA_INNER("rlMaxViaInner", "20mil"),
    RL_MIN_MICRO_VIA_OUTER("rlMinMicroViaOuter", "4mil"),
    RL_MAX_MICRO_VIA_OUTER("rlMaxMicroViaOuter", "20mil"),
    RL_MIN_MICRO_VIA_INNER("rlMinMicroViaInner", "4mil"),
    RL_MAX_MICRO_VIA_INNER("rlMaxMicroViaInner", "20mil"),
    PS_TOP("psTop", "-1"),
    PS_BOTTOM("psBottom", "-1"),
    PS_FIRST("psFirst", "-1"),
    PS_ELONGATION_LONG("psElongationLong", "100"),
    PS_ELONGATION_OFFSET("psElongationOffset", "100"),
    MV_STOP_FRAME("mvStopFrame", "1"),
    MV_CREAM_FRAME("mvCreamFrame", "0"),
    ML_MIN_STOP_FRAME("mlMinStopFrame", "4mil"),
    ML_MAX_STOP_FRAME("mlMaxStopFrame", "4mil"),
    ML_MIN_CREAM_FRAME("mlMinCreamFrame", "0mil"),
    ML_MAX_CREAM_FRAME("mlMaxCreamFrame", "0mil"),
    ML_VIA_STOP_LIMIT("mlViaStopLimit", "0mil"),
    SR_ROUNDNESS("srRoundness", "0"),
    SR_MIN_ROUNDNESS("srMinRoundness", "0mil"),
    SR_MAX_ROUNDNESS("srMaxRoundness", "0mil"),
    SL_THERMAL_ISOLATE("slThermalIsolate", "10mil"),
    SL_THERMALS_FOR_VIAS("slThermalsForVias", "0"),
    DP_MAX_LENGTH_DIFFERENCE("dpMaxLengthDifference", "10mm"),
    DP_GAP_FACTOR("dpGapFactor", "2.5"),
    CHECK_ANGLE("checkAngle", "0"),
    CHECK_FONT("checkFont", "1"),
    CHECK_RESTRICT("checkRestrict", "1"),
    CHECK_STOP("checkStop", "0"),
    CHECK_VALUES("checkValues", "0"),
    CHECK_NAMES("checkNames", "1"),
    CHECK_WIRE_STUBS("checkWireStubs", "1"),
    CHECK_POLYGON_WIDTH("checkPolygonWidth", "0"),
    USE_DIAMETER("useDiameter", "13"), // Annular Ring Setting: 13: None  15:Pad  29:Via  31:Pad+Via
    MAX_ERRORS("maxErrors", "50");

    public final String label;
    public final String value;

    private DrcDefs(String label, String value) {
        this.label = label;
        this.value = value;
    }

}
