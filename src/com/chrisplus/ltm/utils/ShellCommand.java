/* (C) 2012 Pragmatic Software
   This Source Code Form is subject to the terms of the Mozilla Public
   License, v. 2.0. If a copy of the MPL was not distributed with this
   file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package com.chrisplus.ltm.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.util.Log;

public class ShellCommand {
    private static final String TAG = ShellCommand.class.getSimpleName();
    private Runtime rt;
    private String[] command;
    private Process process;
    private BufferedReader stdout;
    private String tagStr;
    public int exit;

    public ShellCommand(String[] command, String tag) {
        this(command);
        this.tagStr = tag;
    }

    public ShellCommand(String[] command) {
        this.command = command;
        rt = Runtime.getRuntime();
    }

    public String start(boolean waitForExit) {

        try {
            process = new ProcessBuilder()
                    .command(command)
                    .redirectErrorStream(true)
                    .start();

            stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (Exception e) {
            Log.e(TAG, "Failure starting shell command [" + tagStr + "]", e);
            return e.getCause().getMessage();
        }

        if (waitForExit) {
            waitForExit();
        }
        return null;
    }

    public void waitForExit() {
        while (checkForExit() == false) {
            if (stdoutAvailable()) {

            } else {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    Log.d("NetworkLog", "waitForExit", e);
                }
            }
        }
    }

    public void finish() {

        try {
            if (stdout != null) {
                stdout.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception finishing [" + tagStr + "]", e);
        }

        process.destroy();
        process = null;
    }

    public boolean checkForExit() {
        try {
            exit = process.exitValue();
        } catch (Exception IllegalThreadStateException) {
            return false;
        }

        finish();
        return true;
    }

    public boolean stdoutAvailable() {
        try {
            /*
             * if(MyLog.enabled) { MyLog.d("stdoutAvailable [" + tag + "]: " +
             * stdout.ready()); }
             */
            return stdout.ready();
        } catch (java.io.IOException e) {
            Log.e("NetworkLog", "stdoutAvailable error", e);
            return false;
        }
    }

    public String readStdoutBlocking() {

        String line;

        if (stdout == null) {
            return null;
        }

        try {
            line = stdout.readLine();
        } catch (Exception e) {
            Log.e("NetworkLog", "readStdoutBlocking error", e);
            return null;
        }

        if (line == null) {
            return null;
        }
        else {
            return line + "\n";
        }
    }

    public String readStdout() {

        if (stdout == null) {
            return null;
        }

        try {
            if (stdout.ready()) {
                String line = stdout.readLine();

                if (line == null) {
                    return null;
                }
                else {
                    return line + "\n";
                }
            } else {

                return "";
            }
        } catch (Exception e) {
            Log.e("NetworkLog", "readStdout error", e);
            return null;
        }
    }
}
