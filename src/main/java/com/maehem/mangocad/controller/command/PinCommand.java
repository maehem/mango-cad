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
package com.maehem.mangocad.controller.command;

import com.maehem.mangocad.controller.command.exception.CommandException;
import com.maehem.mangocad.controller.command.exception.PinCommandException;
import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.enums.PinDirection;
import com.maehem.mangocad.model.element.enums.PinFunction;
import com.maehem.mangocad.model.element.enums.PinLength;
import com.maehem.mangocad.model.element.enums.PinVisible;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.element.property.Rotation;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinCommand extends Command {

    private final Symbol symbol;

    private String pinName = null;
    //ArrayList<String> coordList = new ArrayList<>();
    String coordString = null;
    PinDirection direction = null;
    PinFunction function = null;
    PinLength length = null;
    Rotation orientation = null;
    PinVisible visible = null;
    int swapLevel = -1;

    /**
     * Add a new @Pin to a @Symbol.
     *
     * ﻿PIN 'name' options
     *
     * Possible options:
     *
     *
     * Direction,Function,Length.Orientation.Visible,Swaplevel
     *
     * @param args
     *
     */
    public PinCommand(Symbol symbol, String args) throws CommandException {
        this.symbol = symbol;
        this.args = args;

        qualifyArgs();
    }

    @Override
    public String getName() {
        return "PIN";
    }

    @Override
    public String getDescription() {
        return "Define a connection point for Symbol.";
    }

    @Override
    public void execute() {
        //create Memento before executing
        // No memento needed. We are adding the new pin to a list.
        Pin pin = new Pin();
        obj = pin; // Remember the pin.

        //set new state
        symbol.getElements().add(pin);
    }

    @Override
    public void unExecute() {
        //this.obj = memento.getState();
        // remove the pin from the list.
        if (obj != null) {
            symbol.getElements().remove((Element) obj);
        }
    }

    /**
     * Checks the sanity of all command args.
     *
     * @throws CommandException if any arg has issues.
     */
    private void qualifyArgs() throws CommandException {
        // Semicolon will always terminate command and ignore everything after.
        // Name could be anywhere in args list, but is always denoted by single quotes.

        // Examples:
        // PIN '1'; // Pin named 1 with default values at location 0,0
        // pin short both pas 0 'SYS_CLKOUT1' (0.000000 0.000000);
        // pin short both pas 0 'VDDS_DPLL_MPU_USBHOST' (0.000000 -2.540000);
        // A semicolon terminates the line. Anything after that is ignored.
        StringTokenizer tok = new StringTokenizer(args, ";");
        if (tok.countTokens() < 1) {
            throw new PinCommandException("No arguments found! PIN needs at least a qualified name.");
        }
        List<String> argList = new ArrayList<>(Arrays.asList(tok.nextToken().split("\\s+")));

        pinName = extractPinName(argList);
        if (pinName == null) {
            throw new PinCommandException("Pin Name is required!"); // Pin Name is missing from command.
        } else {
            LOGGER.log(Level.SEVERE, "Pin Name is: " + pinName);
        }

        coordString = extractCoords(argList);

        // Pin should only have one set of coords.
        if (!argList.isEmpty()) { // At least one more pair of options in args.
            LOGGER.log(Level.SEVERE,
                    "There seems to be {0} more options to process.",
                    argList.size()
            );

            ListIterator<String> iter = argList.listIterator();
            while (iter.hasNext()) {
                String value = iter.next();
                LOGGER.log(Level.SEVERE, "Found: {0}", new Object[]{value});
                PinDirection pd = PinDirection.fromCode(value);
                if (pd != null) {
                    direction = pd;
                    iter.remove();
                    continue;
                }
                PinFunction pf = PinFunction.fromCode(value);
                if (pf != null) {
                    function = pf;
                    iter.remove();
                    continue;
                }
                PinLength pl = PinLength.fromCode(value);
                if (pl != null) {
                    length = pl;
                    iter.remove();
                    continue;
                }
                PinVisible pv = PinVisible.fromCode(value);
                if (pv != null) {
                    visible = pv;
                    iter.remove();
                    continue;
                }
                if (Pin.BASIC_ROTATIONS.contains(value)) {
                    orientation = new Rotation();
                    orientation.setValue(value);
                    iter.remove();
                    continue;
                }
                try {
                    Integer intVal = Integer.valueOf(value);
                    if (intVal >= 0 && intVal <= 255) {
                        swapLevel = intVal;
                        iter.remove();
                        continue;
                    } else {
                        throw new PinCommandException("Swaplevel is out of range. Found: " + swapLevel + "  required 0..255");
                    }
                } catch (NumberFormatException ex) {
                    // Ignore this value
                }
            }
            // List should be empty. Report left overs.
            if (!argList.isEmpty()) {
                LOGGER.log(Level.SEVERE, "Unknown arguments found. {0}", argList.toString());
            }

        }
        LOGGER.log(Level.SEVERE,
                "Pin Settings:\n     name: {0}\ndirection: {1}\n function: {2}\n   length: {3}\n  visible: {4}\n rotation: {5}\nswaplevel: {6}\n   coords: {7}",
                new Object[]{pinName, direction, function, length, visible, orientation, swapLevel, coordString});

    }
}
