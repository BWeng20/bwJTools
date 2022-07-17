/*
 * (c) copyright 2022 Bernd Wengenroth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bw.jtools.io.data;

/**
 * Types handled by the data io package.
 */
enum DataType
{
    BOOLEAN(1, false),
    BYTE(2, true),
    SHORT(3, true),
    INT(4, true),
    LONG(5, true),
    FLOAT(6, true),
    DOUBLE(7, true),
    STRING(8, false),
    OBJECT(9, false),
    ARRAY(10, false),
    NULL(11, false),
    CHAR(12, true),
    END_OBJECT(13, false);

    /**
     * Get the byte value of the type, as written by the data package.
     */
    public byte getId()
    {
        return id_;
    }

    public boolean isNumeric()
    {
        return numeric_;
    }

    /**
     * Get the enum value from written byte value.
     */
    public static DataType getById(int id)
    {
        switch (id)
        {
            case 1: return BOOLEAN;
            case 2: return BYTE;
            case 3: return SHORT;
            case 4: return INT;
            case 5: return LONG;
            case 6: return FLOAT;
            case 7: return DOUBLE;
            case 8: return STRING;
            case 9: return OBJECT;
            case 10: return ARRAY;
            case 11: return NULL;
            case 12: return CHAR;
            case 13: return END_OBJECT;
            default: return null;
        }
    }

    private final byte id_;
    private final boolean numeric_;

    private DataType(int id, boolean numeric)
    {
        id_ = (byte) id;
        numeric_ = numeric;
    }
}
