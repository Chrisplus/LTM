/* (C) 2012 Pragmatic Software
   This Source Code Form is subject to the terms of the Mozilla Public
   License, v. 2.0. If a copy of the MPL was not distributed with this
   file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package com.chrisplus.ltm.utils;

import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

public class StringPool {
    private final static int maxPoolSize = 1024;
    private final static ConcurrentHashMap<String, String> pool = new ConcurrentHashMap<String, String>(
            maxPoolSize);
    private final static ConcurrentHashMap<String, String> lowercasePool = new ConcurrentHashMap<String, String>(
            maxPoolSize);
    private final static ConcurrentHashMap<Integer, String> integerPool = new ConcurrentHashMap<Integer, String>(
            maxPoolSize);
    private final static CharArrayStringAATree charPool = new CharArrayStringAATree();
    private static int poolSize = 0;
    private static int lowercasePoolSize = 0;
    private static int integerPoolSize = 0;
    private static CharArray charBuffer = new CharArray();

    private static final String TAG = StringPool.class.getSimpleName();

    public static void clearCharPool() {
        charPool.clear();
    }

    public static String get(CharArray chars) {
        return get(chars.getValue(), chars.getOffset(), chars.getLength());
    }

    public static String get(char[] chars, int offset, int length) {
        synchronized (charBuffer) {
            charBuffer.setValue(chars, offset, length);

            if (charPool.size + 1 >= maxPoolSize) {
                // clear pool to free memory and allow pool to rebuild
                Log.d(TAG, "[StringPool] Clearing charPool");
                charPool.clear();
            }

            return charPool.insert(charBuffer);
        }
    }

    public static String get(String string) {
        if (string == null) {
            return "";
        }

        String result = pool.get(string);

        if (result == null) {
            String newString = new String(string); // decouple string from
                                                   // substring(), etc

            pool.put(newString, newString);
            poolSize++;

            Log.d(TAG, "[StringPool] new addition [" + newString + "]; pool size: " + poolSize);

            if (poolSize >= maxPoolSize) {
                // clear pool to free memory and allow pool to rebuild
                Log.d(TAG, "[StringPool] Clearing pool");
                pool.clear();
                poolSize = 0;
            }

            return newString;
        } else {
            return result;
        }
    }

    public static String getLowerCase(String string) {
        if (string == null) {
            return "";
        }

        String result = lowercasePool.get(string);

        if (result == null) {
            String newString = new String(string.toLowerCase());

            lowercasePool.put(string, newString);
            lowercasePoolSize++;

            Log.d(TAG, "[StringPool] new lowercase addition [" + newString + "]; pool size: "
                    + lowercasePoolSize);

            if (lowercasePoolSize >= maxPoolSize) {
                // clear pool to free memory and allow pool to rebuild
                Log.d(TAG, "[StringPool] Clearing lowercase pool");
                lowercasePool.clear();
                lowercasePoolSize = 0;
            }

            return newString;
        } else {
            return result;
        }
    }

    public static String get(Integer integer) {
        if (integer == null) {
            return "";
        }

        String result = integerPool.get(integer);

        if (result == null) {
            String newString = String.valueOf(integer);

            integerPool.put(integer, newString);
            integerPoolSize++;

            Log.d(TAG, "[StringPool] new integer addition [" + newString + "]; pool size: "
                    + integerPoolSize);

            if (integerPoolSize >= maxPoolSize) {
                // clear pool to free memory and allow pool to rebuild
                Log.d(TAG, "[StringPool] Clearing integer pool");
                integerPool.clear();
                integerPoolSize = 0;
            }

            return newString;
        } else {
            return result;
        }
    }
}
