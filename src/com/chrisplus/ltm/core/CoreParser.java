
package com.chrisplus.ltm.core;

import android.util.Log;

import com.chrisplus.ltm.utils.StringPool;

/**
 * This class is used to parse raw log line.
 * 
 * @author Chris Jiang
 */
public class CoreParser {
    private static final String TAG = CoreParser.class.getSimpleName();
    private FastParser parser = new FastParser();

    public void processRawLog(String rawLog) {

        //Log.d(TAG, "--------------- parsing network entry --------------");

        int pos = 0, lastpos, thisEntry, nextEntry, newline, space;
        String in, out, src, dst, proto, uidString;
        int spt, dpt, len, uid;
        parser.setLine(rawLog.toCharArray(), rawLog.length() - 1);

        while ((pos = rawLog.indexOf("{NL}", pos)) > -1) {

            pos += "{NL}".length(); // skip past "{NL}"

            thisEntry = pos;
            newline = rawLog.indexOf("\n", pos);
            nextEntry = rawLog.indexOf("{NL}", pos);

            if (newline == -1) {
                newline = rawLog.length();
            }

            if (nextEntry != -1 && nextEntry < newline) {
                pos = newline;
                continue;
            }

            try {
                pos = rawLog.indexOf("IN=", pos);

                if (pos == -1 || pos > newline) {
                    pos = newline;
                    continue;
                }

                space = rawLog.indexOf(" ", pos);

                if (space == -1 || space > newline) {
                    pos = newline;
                    continue;
                }

                parser.setPos(pos + 3);
                in = parser.getString();

                pos = rawLog.indexOf("OUT=", pos);

                if (pos == -1 || pos > newline) {
                    pos = newline;
                    continue;
                }

                space = rawLog.indexOf(" ", pos);

                if (space == -1 || space > newline) {
                    pos = newline;
                    continue;
                }

                parser.setPos(pos + 4);
                out = parser.getString();

                pos = rawLog.indexOf("SRC=", pos);

                if (pos == -1 || pos > newline) {
                    pos = newline;
                    continue;
                }

                space = rawLog.indexOf(" ", pos);

                if (space == -1 || space > newline) {
                    pos = newline;
                    continue;
                }

                parser.setPos(pos + 4);
                src = parser.getString();

                pos = rawLog.indexOf("DST=", pos);

                if (pos == -1 || pos > newline) {
                    pos = newline;
                    continue;
                }

                space = rawLog.indexOf(" ", pos);

                if (space == -1 || space > newline) {
                    pos = newline;
                    continue;
                }

                parser.setPos(pos + 4);
                dst = parser.getString();

                pos = rawLog.indexOf("LEN=", pos);

                if (pos == -1 || pos > newline) {
                    pos = newline;
                    continue;
                }

                space = rawLog.indexOf(" ", pos);

                if (space == -1 || space > newline) {
                    pos = newline;
                    continue;
                }

                parser.setPos(pos + 4);
                len = parser.getInt();

                pos = rawLog.indexOf("PROTO=", pos);

                if (pos == -1 || pos > newline) {
                    pos = newline;
                    continue;
                }

                space = rawLog.indexOf(" ", pos);

                if (space == -1 || space > newline) {
                    pos = newline;
                    continue;
                }

                parser.setPos(pos + 6);
                proto = parser.getString();

                lastpos = pos;
                pos = rawLog.indexOf("SPT=", pos);

                if (pos == -1 || pos > newline) {
                    // no SPT field, probably a broadcast packet
                    spt = 0;
                    pos = lastpos;
                } else {
                    space = rawLog.indexOf(" ", pos);

                    if (space == -1 || space > newline) {
                        pos = newline;
                        continue;
                    }

                    parser.setPos(pos + 4);
                    spt = parser.getInt();
                }

                lastpos = pos;
                pos = rawLog.indexOf("DPT=", pos);

                if (pos == -1 || pos > newline) {
                    // no DPT field, probably a broadcast packet
                    dpt = 0;
                    pos = lastpos;
                } else {
                    space = rawLog.indexOf(" ", pos);

                    if (space == -1 || space > newline) {
                        pos = newline;
                        continue;
                    }

                    parser.setPos(pos + 4);
                    dpt = parser.getInt();
                }

                lastpos = pos;
                pos = rawLog.indexOf("UID=", pos);

                if (pos == -1 || pos > newline) {
                    uid = -1;
                    uidString = "-1";
                    pos = lastpos;
                } else {
                    parser.setPos(pos + 4);
                    uid = parser.getInt();
                    parser.setPos(pos + 4);
                    uidString = parser.getString();
                }
            } catch (Exception e) {
                Log.e("NetworkLog", "Bad data for: [" + rawLog.substring(thisEntry, newline) + "]",
                        e);
                pos = newline;
                continue;
            }

            Log.d(TAG, "+++ entry: (" + uid + ") in=" + in + " out=" + out
                    + " "
                    + src + ":" + spt + " -> " + dst + ":" + dpt
                    + " proto=" + proto + " len=" + len);
        }

    }

    public class FastParser {

        char[] line;
        int len;
        int pos;
        char delimiter;

        public FastParser() {
            this(null, 0, ' ');
        }

        public FastParser(char delimiter) {
            this(null, 0, delimiter);
        }

        public FastParser(char[] line, int len) {
            this(line, len, ' ');
        }

        public FastParser(char[] line, int len, char delimiter) {
            this.line = line;
            this.len = len;
            this.delimiter = delimiter;
        }

        public void setLine(char[] line, int len) {
            this.line = line;
            this.len = len;
            pos = 0;
        }

        public void setPos(int newpos) {
            if (newpos < 0 || newpos >= len) {
                throw new IndexOutOfBoundsException("Attempt to set new pos " + newpos
                        + " is out of range 0 - " + len);
            }

            pos = newpos;
        }

        public void setDelimiter(char delimiter) {
            this.delimiter = delimiter;
        }

        public long getLong() {
            return getLong(delimiter);
        }

        public long getLong(char delimiter) {
            int newpos = pos;
            long value = 0;
            boolean neg = false;

            if (pos >= len) {
                throw new RuntimeException("pos at end of string");
            }

            if (line[pos] == '-') {
                neg = true;
                newpos++;
            }

            if (line[pos] == '+') {
                pos++;
                newpos++;
            }

            char thischar = 0;

            while (newpos < len && (thischar = line[newpos]) != delimiter && thischar >= '0'
                    && thischar <= '9') {
                value = value * 10 + (line[newpos] - '0');
                newpos++;
            }

            if (pos == newpos) {
                throw new RuntimeException("expected long but found [" + line[pos] + "] in ["
                        + new String(line, pos, len - pos) + "]");
            }

            pos = newpos;
            eatDelimiter();
            return neg ? -value : value;
        }

        public int getInt() {
            return getInt(delimiter);
        }

        public int getInt(char delimiter) {
            int newpos = pos;
            int value = 0;
            boolean neg = false;

            if (pos >= len) {
                throw new RuntimeException("pos at end of string");
            }

            if (line[pos] == '-') {
                neg = true;
                newpos++;
            }

            if (line[pos] == '+') {
                pos++;
                newpos++;
            }

            char thischar = 0;

            while (newpos < len && (thischar = line[newpos]) != delimiter && thischar >= '0'
                    && thischar <= '9') {
                value = value * 10 + (line[newpos] - '0');
                newpos++;
            }

            if (pos == newpos) {
                throw new RuntimeException("expected int but found [" + line[pos] + "] in ["
                        + new String(line, pos, len - pos) + "]");
            }

            pos = newpos;
            eatDelimiter();
            return neg ? -value : value;
        }

        public double getDouble() {
            int newpos = pos;
            double value = 0;
            boolean negative = false;
            boolean afterpoint = false;
            double divider = 1;
            char thischar = 0;

            while (newpos < len && (thischar = line[newpos]) != ' ' && thischar != 'e'
                    && thischar != '\t') {
                if (thischar == '-') {
                    negative = true;
                } else if (thischar == '.') {
                    afterpoint = true;
                } else {
                    int thisdigit = thischar - '0';
                    value = value * 10 + thisdigit;
                    if (afterpoint) {
                        divider *= 10;
                    }
                }

                newpos++;
            }

            if (thischar == 'e') {
                newpos++;
                boolean exponentnegative = false;
                int exponent = 0;

                while (newpos < len && (thischar = line[newpos]) != delimiter) {
                    if (thischar == '-') {
                        exponentnegative = true;
                    } else if (thischar != '+') {
                        exponent = exponent * 10 + (thischar - '0');
                    }

                    newpos++;
                }

                if (exponentnegative) {
                    exponent = -exponent;
                }

                value *= Math.pow(10, exponent);
            }

            if (negative) {
                value = -value;
            }

            value /= divider;

            if (pos == newpos) {
                throw new RuntimeException("expected double but found [" + line[pos] + "] in ["
                        + new String(line, pos, len - pos) + "]");
            }

            pos = newpos;
            eatDelimiter();
            return value;
        }

        public String getString() {
            return getString(delimiter);
        }

        public String getString(char delimiter) {
            int newpos = pos;
            String value;

            while (newpos < len && line[newpos] != delimiter) {
                newpos++;
            }

            if (pos == newpos) {
                value = "";
            } else {
                value = StringPool.get(line, pos, newpos - pos);
            }

            pos = newpos;
            eatDelimiter();
            return value;
        }

        public void eatDelimiter() {
            eatChar(delimiter);
        }

        public void eatChar(char target) {
            if (pos < len && line[pos] != target) {
                throw new RuntimeException("expected [" + target + "] but got " + line[pos]
                        + " in [" + new String(line, pos, len - pos) + "]");
            }
            pos++;
        }

        public boolean hasMore() {
            return pos < len;
        }

    }
}
