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

import com.maehem.mangocad.controller.Command;
import com.maehem.mangocad.controller.command.exception.CommandException;
import com.maehem.mangocad.controller.command.exception.PinCommandException;
import com.maehem.mangocad.model._AQuantum;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.enums.PinDirection;
import com.maehem.mangocad.model.element.enums.PinFunction;
import com.maehem.mangocad.model.element.enums.PinLength;
import com.maehem.mangocad.model.element.enums.PinVisible;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.model.util.Rotation;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
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

        processArgs();
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
            symbol.getElements().remove((_AQuantum) obj);
        }
    }

    private void processArgs() throws CommandException {
        // Semicolon will always terminate command and ignore everything after.
        // Name could be anywhere in args list, but is always denoted by single quotes.

        // Examples:
        // PIN '1'; // Pin named 1 with default values at location 0,0
        // pin short both pas 0 'SYS_CLKOUT1' (0.000000 0.000000);
        // pin short both pas 0 'VDDS_DPLL_MPU_USBHOST' (0.000000 -2.540000);
        List<String> argList = new ArrayList<>(Arrays.asList(args.split("\\s+")));

        // Find 'NAME' item in list or fail.
        ListIterator<String> argIter = argList.listIterator();
        while (argIter.hasNext()) {
            String arg = argIter.next();
            if (arg.startsWith("'") && arg.endsWith("'")) {
                this.pinName = arg.replaceAll("'", "");
                argIter.remove();
                break;
            }
        }
        if (pinName == null) {
            throw new PinCommandException("Pin Name is required!"); // Pin Name is missing from command.
        } else {
            LOGGER.log(Level.SEVERE, "Pin Name is: " + pinName);
        }

        // Find any args that are coordinates ==>  (xxx ... yyy)
        ListIterator<String> iter = argList.listIterator();
        boolean coordHasEnd = false;
        while (iter.hasNext()) {
            String arg = iter.next();
            if (coordString != null) {
                if (arg.endsWith(")")) {
                    coordString += " " + arg.substring(0, arg.lastIndexOf(")"));
                    LOGGER.log(Level.SEVERE, "Found final coordinate: ({0})", coordString);
                    iter.remove();
                    coordHasEnd = true;
                    break;
                } else {
                    coordString += " " + arg;
                    LOGGER.log(Level.SEVERE, "Found coordinate chunk: {0}", arg);
                    iter.remove();
                }
            } else if (arg.startsWith("(")) {
                coordString = arg.substring(1);
                LOGGER.log(Level.SEVERE, "Found start of coord: {0}", arg);
                iter.remove();
            } // else ignore the arg
        }
        if (coordString != null && !coordHasEnd) {
            throw new PinCommandException("Coordinate had no end braces!");
        }

        // Pin should only have one set of coords.
        if (argList.size() > 1) { // At least one more pair of options in args.
            LOGGER.log(Level.SEVERE,
                    "There seems to be {0} more options to process.",
                    argList.size() / 2
            );

            iter = argList.listIterator();
            while (iter.hasNext()) {
                String setting = iter.next().toLowerCase();
                if (iter.hasNext()) {
                    String value = iter.next();
                    LOGGER.log(Level.SEVERE, "Found: {0} = {1}", new Object[]{setting, value});
                    switch (setting) {
                        case PinDirection.COMMAND_KEY -> {
                            direction = PinDirection.fromCode(value);
                        }
                        case PinFunction.COMMAND_KEY -> {
                            function = PinFunction.fromCode(value);
                        }
                        case PinLength.COMMAND_KEY -> { //       PinLength length = null;
                            length = PinLength.fromCode(value);
                        }
                        case PinVisible.COMMAND_KEY -> { //   PinVisible visible = null;
                            visible = PinVisible.fromCode(value);
                        }
                        case Rotation.COMMAND_SETTING -> { //   Rotation orientation = null;
                            orientation = new Rotation();
                            orientation.setValue(value);
                        }
                        case Pin.SWAPLEVEL_KEY -> { //   int swapLevel = -1;
                            swapLevel = Integer.valueOf(value);
                        }
                    }
                } else {
                    throw new PinCommandException("Setting (" + setting + ") is missing value! End of line reached.");
                }

            }
            LOGGER.log(Level.SEVERE,
                    "Pin Settings:\n     name: {0}\ndirection: {1}\n function: {2}\n   length: {3}\n  visible: {4}\n rotation: {5}\nswaplevel: {6}",
                    new Object[]{pinName, direction, function, length, visible, orientation, swapLevel});

        }
    }
}
