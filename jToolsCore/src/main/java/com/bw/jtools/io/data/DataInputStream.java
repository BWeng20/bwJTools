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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Data input that reads from an input stream
 */
public class DataInputStream implements DataInput
{

    private InputStream is_;

    public DataInputStream(InputStream is)
    {
        is_ = is;
        map_ = new DataReflectMap();
    }

    @Override
    public boolean hasNextField() throws IOException
    {
        if (currentFieldType_ == null)
        {
            readHeader();
        }
        return currentFieldType_ != DataType.END_OBJECT;
    }

    @Override
    public int getFieldId() throws IOException
    {
        if (currentFieldType_ == null)
        {
            readHeader();
        }
        return currentFieldId_;
    }

    @Override
    public boolean isFieldNull() throws IOException
    {
        if (currentFieldType_ == null)
        {
            readHeader();
        }
        return currentFieldType_ == null || currentFieldType_ == DataType.NULL;
    }

    @Override
    public boolean isFieldNumeric() throws IOException
    {
        if (currentFieldType_ == null)
        {
            readHeader();
        }
        return currentFieldType_ != null && currentFieldType_.isNumeric();
    }

    @Override
    public boolean isFieldString() throws IOException
    {
        if (currentFieldType_ == null)
        {
            readHeader();
        }
        return currentFieldType_ != null && currentFieldType_ == DataType.STRING;
    }

    @Override
    public boolean isFieldArray() throws IOException
    {
        if (currentFieldType_ == null)
        {
            readHeader();
        }
        return currentFieldType_ != null && currentFieldType_ == DataType.ARRAY;
    }

    @Override
    public Boolean readBoolean() throws IOException
    {
        if (verifyHeaderOrCheckNull(DataType.BOOLEAN))
        {
            return null;
        }
        readInternal(buffer_, 1);
        return 0 != buffer_[0];
    }

    @Override
    public Character readCharacter() throws IOException
    {
        if (verifyHeaderOrCheckNull(DataType.CHAR))
        {
            return null;
        }
        return readInternalChar();
    }

    @Override
    public Number readNumber() throws IOException
    {
        if (currentFieldType_ == null)
        {
            readHeader();
        }
        DataType t = currentFieldType_;
        currentFieldType_ = null;
        switch (t)
        {
            case BYTE:
                readInternal(buffer_, 1);
                return buffer_[0];
            case SHORT:
                return readInternalShort();
            case CHAR:
                // As char is unsigned, we need to use int as return type here.
                return (int) readInternalChar();
            case INT:
                return readInternalInt();
            case LONG:
                return readInternalLong();
            case FLOAT:
                return readInternalFloat();
            case DOUBLE:
                return readInternalDouble();
            default:
                throw new IOException("Expected was a number type but read " + currentFieldType_);
        }
    }

    @Override
    public String readString() throws IOException
    {
        if (verifyHeaderOrCheckNull(DataType.STRING))
        {
            return null;
        }
        return readInternalString();
    }

    protected final class BitAccumulator
    {

        int bits = 8;
        int accu = 0;

        public boolean readBit() throws IOException
        {
            if (bits == 8)
            {
                bits = 0;
                readInternal(buffer_, 1);
                accu = buffer_[0];
            }
            return (accu & (1 << (bits++))) > 0;
        }
    }

    @Override
    public boolean[] readBooleanArray() throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(DataType.BOOLEAN))
        {
            return null;
        }
        int len = readInternalInt();

        BitAccumulator accu = new BitAccumulator();
        boolean buffer[] = new boolean[len];

        for (int i = 0; i < len; ++i)
        {
            buffer[i] = accu.readBit();
        }
        return buffer;
    }

    @Override
    public byte[] readByteArray() throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(DataType.BYTE))
        {
            return null;
        }
        int len = readInternalInt();
        byte buffer[] = new byte[len];
        readInternal(buffer, len);
        return buffer;
    }

    @Override
    public char[] readCharArray() throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(DataType.CHAR))
        {
            return null;
        }
        int len = readInternalInt();
        char buffer[] = new char[len];
        for (int i = 0; i < len; ++i)
        {
            readInternal(buffer_, 2);
            buffer[i] = (char) (((buffer_[0] & 0x00FF) << 8) | (buffer_[1] & 0x00FF));
        }
        return buffer;
    }

    @Override
    public short[] readShortArray() throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(DataType.SHORT))
        {
            return null;
        }
        int len = readInternalInt();
        short buffer[] = new short[len];
        for (int i = 0; i < len; ++i)
        {
            readInternal(buffer_, 2);
            buffer[i] = (short) (((buffer_[0] & 0x00FF) << 8) | (buffer_[1] & 0x00FF));
        }
        return buffer;
    }

    @Override
    public int[] readIntArray() throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(DataType.INT))
        {
            return null;
        }
        int len = readInternalInt();
        int buffer[] = new int[len];
        for (int i = 0; i < len; ++i)
        {
            buffer[i] = readInternalInt();
        }
        return buffer;
    }

    @Override
    public long[] readLongArray() throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(DataType.LONG))
        {
            return null;
        }
        int len = readInternalInt();
        long buffer[] = new long[len];
        for (int i = 0; i < len; ++i)
        {
            buffer[i] = readInternalLong();
        }
        return buffer;
    }

    @Override
    public float[] readFloatArray() throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(DataType.FLOAT))
        {
            return null;
        }
        int len = readInternalInt();
        float buffer[] = new float[len];
        for (int i = 0; i < len; ++i)
        {
            buffer[i] = readInternalFloat();
        }
        return buffer;
    }

    @Override
    public double[] readDoubleArray() throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(DataType.DOUBLE))
        {
            return null;
        }
        int len = readInternalInt();
        double buffer[] = new double[len];
        for (int i = 0; i < len; ++i)
        {
            buffer[i] = readInternalDouble();
        }
        return buffer;
    }

    @Override
    public String[] readStringArray() throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(DataType.STRING))
        {
            return null;
        }
        int len = readInternalInt();
        String buffer[] = new String[len];
        for (int i = 0; i < len; ++i)
        {
            buffer[i] = readInternalString();
        }
        return buffer;
    }

    @Override
    public DataInput startObject() throws IOException
    {
        if (verifyHeaderOrCheckNull(DataType.OBJECT))
        {
            return null;
        }
        activeChild_ = new DataInputStream(this);
        return activeChild_;
    }

    private Object readDynamicArray(DataReflectMap.FieldInfo fi) throws IOException
    {
        if (verifyArrayHeaderOrCheckNull(fi.elementType))
        {
            return null;
        }

        int len = readInternalInt();

        // TODO: Create dynamical the right container type.
        Object ar = Array.newInstance(fi.field.getType().getComponentType(), len);

        readArrayInternal(ar, len, fi.elementType);
        return ar;
    }

    public Object readArray() throws IOException
    {
        if (verifyHeaderOrCheckNull(DataType.ARRAY))
        {
            return null;
        }

        readInternal(buffer_, 1);
        DataType t = DataType.getById(buffer_[0]);
        int len = readInternalInt();

        Object ar;

        switch (t)
        {
            case BOOLEAN:
                ar = new boolean[len];
                break;
            case BYTE:
                ar = new byte[len];
                break;
            case SHORT:
                ar = new short[len];
                break;
            case CHAR:
                ar = new char[len];
                break;
            case INT:
                ar = new int[len];
                break;
            case LONG:
                ar = new long[len];
                break;
            case FLOAT:
                ar = new float[len];
                break;
            case DOUBLE:
                ar = new double[len];
                break;
            case STRING:
                ar = new String[len];
                break;
            default:
                throw new IOException("Unsupported element type " + t);
        }
        readArrayInternal(ar, len, t);
        return ar;
    }

    private void readArrayInternal(Object ar, int len, DataType elementType) throws IOException
    {
        BitAccumulator accu = (elementType == DataType.BOOLEAN)
                ? new BitAccumulator() : null;

        for (int i = 0; i < len; ++i)
        {
            switch (elementType)
            {
                case BOOLEAN:
                    Array.set(ar, i, accu.readBit());
                    break;
                case BYTE:
                    readInternal(buffer_, 1);
                    Array.set(ar, i, buffer_[0]);
                    break;
                case SHORT:
                    Array.set(ar, i, readInternalShort());
                    break;
                case CHAR:
                    Array.set(ar, i, readInternalChar());
                    break;
                case INT:
                    Array.set(ar, i, readInternalInt());
                    break;
                case LONG:
                    Array.set(ar, i, readInternalLong());
                    break;
                case FLOAT:
                    Array.set(ar, i, readInternalFloat());
                    break;
                case DOUBLE:
                    Array.set(ar, i, readInternalDouble());
                    break;
                case STRING:
                    Array.set(ar, i, readInternalString());
                    break;
                default:
                    throw new IOException("Unsupported array type " + elementType);
            }
        }
    }

    private Object readFieldValue(DataReflectMap.FieldInfo fi) throws IOException
    {
        switch (fi.type)
        {
            case BOOLEAN:
                return readBoolean();
            case BYTE:
                return readNumber().byteValue();
            case SHORT:
                return readNumber().shortValue();
            case INT:
                return readNumber().intValue();
            case CHAR:
                return readCharacter();
            case LONG:
                return readNumber().longValue();
            case STRING:
                return readString();
            case FLOAT:
                return readNumber().floatValue();
            case DOUBLE:
                return readNumber().doubleValue();
            case OBJECT:
                return readObject();
            case ARRAY:
                return readDynamicArray(fi);
            case NULL:
                skip();
                return null;
            default:
                throw new IOException("Internal Error");
        }
    }

    @Override
    public boolean isFieldObject() throws IOException
    {
        if (currentFieldType_ == null)
        {
            readHeader();
        }
        return currentFieldType_ != null && currentFieldType_ == DataType.OBJECT;
    }

    @Override
    public Object readObject() throws IOException
    {
        DataInputStream is = (DataInputStream) startObject();
        if (is != null)
        {
            try
            {
                DataReflectMap.ClassInfo ci = map_.readClass(is, 1);

                Map<Integer, DataReflectMap.FieldInfo> idMap = ci.idToInfo;
                Object o = ci.ctor.newInstance();

                if (o instanceof Data)
                {
                    ((Data) o).read(is);
                } else
                {
                    while (is.hasNextField())
                    {
                        int fid = is.getFieldId();
                        DataReflectMap.FieldInfo fi = idMap.get(fid);
                        if (fi.field == null)
                        {
                            is.skip();
                        } else
                        {
                            if (is.isFieldNull())
                            {
                                is.skip();
                                fi.field.set(o, null);
                            } else
                            {
                                fi.field.set(o, is.readFieldValue(fi));
                            }
                        }
                    }
                }
                return o;
            } catch (IOException e)
            {
                throw e;
            } catch (Exception ee)
            {
                throw new IOException("Internal error in data mapping", ee);
            }
        } else
        {
            return null;
        }
    }

    @Override
    public void close() throws IOException
    {
        if (is_ != null)
        {
            is_.close();
            is_ = null;
        }
    }

    @Override
    public void skip() throws IOException
    {
        if (currentFieldType_ == null)
        {
            return;
        }
        int s = getSize(currentFieldType_);
        if (s >= 0)
        {
            // Fixed size
            skip(s);
        } else
        {
            // Dynamic size
            switch (currentFieldType_)
            {
                case END_OBJECT:
                    throw new IOException("Can't skip end of object");
                case OBJECT:
                {
                    DataInputStream d = (DataInputStream) startObject();
                    d.finish();
                }
                break;
                case STRING:
                    skip(readInternalInt());
                    break;
                case ARRAY:
                {
                    readInternal(buffer_, 1);
                    DataType subtype = DataType.getById(buffer_[0]);
                    int count = readInternalInt();
                    int esize = getSize(subtype);
                    if (esize >= 0)
                    {
                        // Constant size, skip in one
                        skip(esize * count);
                    } else
                    {
                        // Dynamic size, currently only for string
                        if (subtype != DataType.STRING)
                        {
                            throw new IOException("Expected type STRING, but read " + subtype);
                        }
                        while (count > 0)
                        {
                            skip(readInternalInt());
                            --count;
                        }
                    }
                }
                break;
            }
        }
        currentFieldType_ = null;
    }

    protected void skip(int amount) throws IOException
    {
        is_.skip(amount);
    }

    protected void readHeader() throws IOException
    {
        readInternal(buffer_, 1);

        currentFieldType_ = DataType.getById((byte) ((buffer_[0] & 0x00F0) >> 4));
        currentFieldId_ = buffer_[0] & 0x000F;
        if (currentFieldId_ == 0)
        {
            readInternal(buffer_, 2);
            currentFieldId_ = ((int) buffer_[0]) << 8 | ((int) buffer_[1]);
        }
    }

    protected void autoFinishChildStream() throws IOException
    {
        if (activeChild_ != null)
        {
            activeChild_.finish();
            activeChild_ = null;
        }
    }

    protected void finish() throws IOException
    {
        while (currentFieldType_ != DataType.END_OBJECT)
        {
            skip();
            readHeader();
        }
        if (currentFieldType_ != DataType.END_OBJECT)
        {
            throw new IOException("Protocol error. Missing end marker");
        }
        if (currentFieldId_ != 0x0F)
        {
            throw new IOException("Protocol error. Illegal id " + currentFieldId_ + " in marker");
        }
        is_ = null;
    }

    private char readInternalChar() throws IOException
    {
        readInternal(buffer_, 2);
        return (char) (((buffer_[0] & 0x00FF) << 8) | (buffer_[1] & 0x00FF));
    }

    private short readInternalShort() throws IOException
    {
        readInternal(buffer_, 2);
        return (short) (((buffer_[0] & 0x00FF) << 8) | (buffer_[1] & 0x00FF));
    }

    protected int readInternalInt() throws IOException
    {
        readInternal(buffer_, 4);
        return buffer_[0] << 24 | (buffer_[1] & 0x00FF) << 16 | (buffer_[2] & 0x00FF) << 8 | (buffer_[3] & 0x00FF);
    }

    protected long readInternalLong() throws IOException
    {
        return ((long) readInternalInt()) << 32 | (0x0FFFFFFFFl & (long) readInternalInt());
    }

    protected float readInternalFloat() throws IOException
    {
        return Float.intBitsToFloat(readInternalInt());
    }

    protected double readInternalDouble() throws IOException
    {
        return Double.longBitsToDouble(readInternalLong());
    }

    protected String readInternalString() throws IOException
    {
        int slen = readInternalInt();
        if (buffer_.length < slen)
        {
            buffer_ = new byte[slen + 512];
        }
        readInternal(buffer_, slen);
        if (buffer_[0] == 0)
        {
            return "";
        } else
        {
            return new String(buffer_, 0, slen, StandardCharsets.UTF_8);
        }
    }

    protected void readInternal(byte data[], int len) throws IOException
    {
        autoFinishChildStream();
        if (is_ == null)
        {
            throw new IOException("data stream already closed");
        }
        int c = 0;
        while (c < len)
        {
            int r = is_.read(data, c, len - c);
            if (r == -1)
            {
                throw new IOException("End of stream reached");
            }
            c += r;
        }
    }

    protected DataInputStream(DataInputStream is)
    {
        is_ = is.is_;
        map_ = is.map_;
    }

    protected boolean verifyArrayHeaderOrCheckNull(DataType itemType) throws IOException
    {
        if (verifyHeaderOrCheckNull(DataType.ARRAY))
        {
            return true;
        }
        readInternal(buffer_, 1);
        DataType t = DataType.getById(buffer_[0]);
        if (t != itemType)
        {
            throw new IOException("Expected item type " + itemType + " but read " + t);
        }
        return false;
    }

    protected boolean verifyHeaderOrCheckNull(DataType type) throws IOException
    {
        if (currentFieldType_ == null)
        {
            readHeader();
        }
        DataType t = currentFieldType_;
        currentFieldType_ = null;
        if (t == DataType.NULL)
        {
            return true;
        }
        if (t != type)
        {
            throw new IOException("Expected type " + type + " but " + t + " read");
        }
        return false;
    }

    /**
     * @return -1, if size is dynamic.
     */
    protected int getSize(DataType t)
    {
        switch (t)
        {
            case BYTE:
            case BOOLEAN:
                return 1;
            case INT:
            case FLOAT:
                return 4;
            case LONG:
            case DOUBLE:
                return 8;
            case NULL:
                return 0;
            case CHAR:
            case SHORT:
                return 2;
            default:
                return -1;
        }
    }

    DataType currentFieldType_;
    int currentFieldId_;
    DataReflectMap map_;

    DataInputStream activeChild_;
    byte buffer_[] = new byte[1024];
}
