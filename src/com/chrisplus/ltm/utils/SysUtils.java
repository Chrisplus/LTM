/* (C) 2012 Pragmatic Software
   This Source Code Form is subject to the terms of the Mozilla Public
   License, v. 2.0. If a copy of the MPL was not distributed with this
   file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package com.chrisplus.ltm.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.chrisplus.ltm.R;
import com.chrisplus.ltm.activies.ErrorDialogActivity;

public class SysUtils {
    private final static String TAG = SysUtils.class.getSimpleName();

    private static String iptablesBinary;
    private static String iptablesMd5;
    private static int iptablesResource;
    private static String grepBinary;
    private static String grepMd5;
    private static int grepResource;
    private static String nflogBinary;
    private static String nflogMd5;
    private static int nflogResource;

    public static boolean getBinariesIdentifiers() {
        String cpu_abi = Build.CPU_ABI.toLowerCase();

        if (cpu_abi.contains("armeabi-v7")) {
            iptablesBinary = "iptables_armv7";
            iptablesMd5 = "5515873b7ce1617f3d724a3332c2b947";
            iptablesResource = R.raw.iptables_armv7;
            grepBinary = "grep_armv7";
            grepMd5 = "69d0726f2b314a32fcd906a753deaabb";
            grepResource = R.raw.grep_armv7;
            nflogBinary = "nflog_armv7";
            nflogMd5 = "286eb86e340610727b262593e75e8939";
            nflogResource = R.raw.nflog_armv7;
        } else if (cpu_abi.contains("armeabi")) {
            iptablesBinary = "iptables_armv5";
            iptablesMd5 = "50e39f66369344b692084a9563c185d4";
            iptablesResource = R.raw.iptables_armv5;
            grepBinary = "grep_armv5";
            grepMd5 = "7904ae3e4f310f9a1bf9867cfadb71ef";
            grepResource = R.raw.grep_armv5;
            nflogBinary = "nflog_armv5";
            nflogMd5 = "509e613ca8734a6bcc6d3327298a9320";
            nflogResource = R.raw.nflog_armv5;
        } else if (cpu_abi.contains("x86")) {
            iptablesBinary = "iptables_x86";
            iptablesMd5 = "3e7090f93ae3964c98e16016b742acbc";
            iptablesResource = R.raw.iptables_x86;
            grepBinary = "grep_x86";
            grepMd5 = "75210f186d666f32a14d843fd1e9fac5";
            grepResource = R.raw.grep_x86;
            nflogBinary = "nflog_x86";
            nflogMd5 = "0bc6661f7cc3de8c875e021229effb1d";
            nflogResource = R.raw.nflog_x86;
        } else if (cpu_abi.contains("mips")) {
            iptablesBinary = "iptables_mips";
            iptablesMd5 = "c208f8f9a6fa8d7b436c069b71299668";
            iptablesResource = R.raw.iptables_mips;
            grepBinary = "grep_mips";
            grepMd5 = "a29534a420f9eb9cc519088eacf6b7e7";
            grepResource = R.raw.grep_mips;
            nflogBinary = "nflog_mips";
            nflogMd5 = "86574f4085b3ab74d01c4e3d2ecb1c91";
            nflogResource = R.raw.nflog_mips;
        } else {
            iptablesBinary = null;
            grepBinary = null;
            nflogBinary = null;
            return false;
        }
        return true;
    }

    public static String getIptablesBinary() {
        if (iptablesBinary == null) {
            getBinariesIdentifiers();
        }
        return iptablesBinary;
    }

    public static String getGrepBinary() {
        if (grepBinary == null) {
            getBinariesIdentifiers();
        }
        return grepBinary;
    }

    public static String getNflogBinary() {
        if (nflogBinary == null) {
            getBinariesIdentifiers();
        }
        return nflogBinary;
    }

    public static boolean installBinary(Context context, String binary, String md5sum,
            int resource, String path) {
        boolean needsInstall = false;
        File file = new File(path);

        if (file.isFile()) {
            String hash = MD5Sum.digestFile(file);
            if (!hash.equals(md5sum)) {
                needsInstall = true;
            }
        } else {
            needsInstall = true;
        }

        if (needsInstall) {
            try {
                Log.d(TAG, binary + " not found: installing to " + path);

                InputStream raw = context.getResources().openRawResource(resource);
                ZipInputStream zip = new ZipInputStream(raw);
                zip.getNextEntry();

                InputStream in = zip;
                FileOutputStream out = new FileOutputStream(path);

                byte buf[] = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }

                out.close();
                in.close();

                Runtime.getRuntime().exec("chmod 755 " + path).waitFor();
            } catch (Exception e) {
                Resources res = context.getResources();
                showError(context, res.getString(R.string.error_default_title),
                        String.format(res.getString(R.string.error_install_binary_text), binary)
                                + e.getMessage());
                return false;
            }
        } else {
            Log.d(TAG, binary + " found at " + path);
        }

        return true;
    }

    public static boolean installBinaries(Context context) {
        if (!getBinariesIdentifiers()) {
            Resources res = context.getResources();
            showError(context, res.getString(R.string.error_unsupported_system_title),
                    String.format(res.getString(R.string.error_unsupported_system_text),
                            Build.CPU_ABI));
            return false;
        }

        String iptablesPath = context.getFilesDir().getAbsolutePath() + File.separator
                + iptablesBinary;
        if (!installBinary(context, iptablesBinary, iptablesMd5, iptablesResource, iptablesPath)) {
            return false;
        }

        String grepPath = context.getFilesDir().getAbsolutePath() + File.separator + grepBinary;
        if (!installBinary(context, grepBinary, grepMd5, grepResource, grepPath)) {
            return false;
        }

        String nflogPath = context.getFilesDir().getAbsolutePath() + File.separator + nflogBinary;
        if (!installBinary(context, nflogBinary, nflogMd5, nflogResource, nflogPath)) {
            return false;
        }

        return true;
    }

    public static void showError(final Context context, final String title, final String message) {
        Log.d("NetworkLog", "Got error: [" + title + "] [" + message + "]");

        context.startActivity(new Intent(context, ErrorDialogActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("title", title)
                .putExtra("message", message));
    }

    public static boolean isServiceRunning(Context context, String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    public static void checkFileEnvironment(String fileName) {
        File path = new File(Constants.LOG_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }

        File file = new File(Constants.LOG_PATH + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
