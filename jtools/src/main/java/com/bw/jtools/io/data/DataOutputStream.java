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

import com.bw.jtools.collections.ClassNameCompressor;
import com.bw.jtools.collections.GenericIterator;
import com.bw.jtools.io.data.DataReflectMap.FieldInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * Data output that writes to an output stream.
 * @see DataOutput
 */
public class DataOutputStream implements DataOutput
{
	private OutputStream os_;
	DataOutputStream activeChild_;
	DataReflectMap map_;

	public DataOutputStream(OutputStream os)
	{
		os_ = os;
		map_ = new DataReflectMap();
	}

	public void writeNull(int fieldId) throws IOException
	{
		writeHeader(fieldId, DataType.NULL);
	}

	protected final class BitAccumulator
	{
		int bits = 0;
		int accu = 0;

		public void addBit( boolean v) throws IOException
		{
			if ( v )
				accu += (1 << bits);
			++bits;
			if ( bits == 8)
			{
				bits = 0;
				writeInternal((byte) accu);
				accu = 0;
			}

		}

		public void finish() throws IOException
		{
			if ( bits > 0)
				writeInternal((byte) accu);
		}

	}

	public void writeArray(int fieldId, boolean[] value) throws IOException
	{
		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader( fieldId, DataType.ARRAY );
			writeInternal(DataType.BOOLEAN.getId());
			writeInternalInt(value.length);
			BitAccumulator accu = new BitAccumulator();
			for ( boolean v : value )
			{
				accu.addBit(v);
			}
			accu.finish();
		}
	}

	public void writeArray(int fieldId, byte[] value) throws IOException
	{
		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader( fieldId, DataType.ARRAY );
			writeInternal(DataType.BYTE.getId());
			writeInternalInt(value.length);
			writeInternal(value);
		}
	}

	public void writeArray(int fieldId, short[] value) throws IOException
	{
		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader( fieldId, DataType.ARRAY );
			writeInternal(DataType.SHORT.getId());
			writeInternalInt(value.length);
			for ( short v : value )
				writeInternalShort(v);
		}
	}

	public void writeArray(int fieldId, char[] value) throws IOException
	{
		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader( fieldId, DataType.ARRAY );
			writeInternal(DataType.CHAR.getId());
			writeInternalInt(value.length);
			for ( char v : value )
				writeInternal((byte)(v>>8), (byte)v);
		}
	}

	public void writeArray(int fieldId, int[] value) throws IOException
	{
		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader( fieldId, DataType.ARRAY );
			writeInternal(DataType.INT.getId());
			writeInternalInt(value.length);
			for ( int v : value )
				writeInternalInt(v);
		}
	}

	public void writeArray(int fieldId, long[] value) throws IOException
	{
		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader( fieldId, DataType.ARRAY );
			writeInternal(DataType.LONG.getId());
			writeInternalInt(value.length);
			for ( long v : value )
			{
				writeInternalLong(v);
			}
		}
	}

	public void writeArray(int fieldId, float[] value) throws IOException
	{
		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader( fieldId, DataType.ARRAY );
			writeInternal(DataType.FLOAT.getId());
			writeInternalInt(value.length);
			for ( float v : value )
			{
				writeInternalInt(Float.floatToIntBits(v));
			}
		}
	}

	public void writeArray(int fieldId, double[] value) throws IOException
	{
		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader( fieldId, DataType.ARRAY );
			writeInternal(DataType.DOUBLE.getId());
			writeInternalInt(value.length);
			for ( double v : value )
			{
				final long bits = Double.doubleToLongBits(v);
				writeInternalLong(bits);
			}
		}
	}

	public void writeArray(int fieldId, String[] value) throws IOException
	{
		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader( fieldId, DataType.ARRAY );
			writeInternal(DataType.STRING.getId());
			writeInternalInt(value.length);
			for ( String v : value )
			{
				writeInternalString( v );
			}
		}
	}

	public void writeBoolean(int fieldId, boolean value) throws IOException
	{
		writeHeader( fieldId, DataType.BOOLEAN );
		writeInternalBoolean( value );
	}

	public void writeByte(int fieldId, int value) throws IOException
	{
		writeHeader( fieldId, DataType.BYTE );
		writeInternal((byte)value);
	}

	public void writeShort(int fieldId, int value) throws IOException
	{
		writeHeader( fieldId, DataType.SHORT );
		writeInternalShort( (short)value );
	}

	public void writeChar(int fieldId, char value ) throws IOException{
		writeHeader( fieldId, DataType.CHAR );
		writeInternal((byte)(value>>8), (byte)value);
	}

	public void writeInt(int fieldId, int value) throws IOException
	{
		writeHeader( fieldId, DataType.INT );
		writeInternalInt(value);
	}

	public void writeLong(int fieldId, long value) throws IOException{
		writeHeader( fieldId, DataType.LONG );
		writeInternalLong(value);
	}

	public void writeFloat(int fieldId, float value) throws IOException{
		writeHeader( fieldId, DataType.FLOAT );
		writeInternalInt(Float.floatToIntBits(value));
	}

	public void writeDouble(int fieldId, double value) throws IOException{
		writeHeader( fieldId, DataType.DOUBLE );
		writeInternalDouble( value );
	}

	public void writeString(int fieldId, CharSequence value) throws IOException{

		if (value == null)
			writeNull( fieldId );
		else
		{
			writeHeader(fieldId, DataType.STRING);
			writeInternalString(value);
		}
	}

	public DataOutput startObject(int fieldId) throws IOException {
		writeHeader(fieldId, DataType.OBJECT);
		activeChild_ = new DataOutputStream( this );
		return activeChild_;
	}

	protected void writeHeader(int fieldId, DataType type ) throws IOException
	{
		if ( fieldId <= 0 || fieldId > 65.535) throw new IOException("Illegal fieldId "+fieldId);

		if ( fieldId > ((2*2*2*2)-1))
		{
			writeInternal( (byte) (type.getId()<<4), (byte)(fieldId>>8), (byte)fieldId );
		}
		else
		{
			writeInternal( (byte) ((type.getId()<<4) | (fieldId)));
		}
	}

	protected void autoFinishChildStream() throws IOException
	{
		if ( activeChild_ != null )
		{
			activeChild_.finish();
			activeChild_ = null;
		}
	}

	@Override
	public void finish() throws IOException
	{
		if ( os_ != null )
			writeInternal( (byte)((DataType.END_OBJECT.getId() << 4) | 0x0F) );
		os_ = null;
	}

	protected void writeInternalInt(int v )  throws IOException
	{
		writeInternal( (byte)(v>>24), (byte)(v>>16), (byte)(v>>8), (byte)v );
	}

	protected void writeInternalShort(short v )  throws IOException
	{
		writeInternal((byte) (v >> 8), (byte)v);
	}

	protected void writeInternalLong(long v) throws IOException
	{
		writeInternalInt((int) (v >> 32));
		writeInternalInt((int) v);
	}

	private void writeInternalBoolean(boolean value ) throws IOException
	{
		writeInternal((byte) (value ? -1 : 0));
	}

	private void writeInternalDouble(double value ) throws IOException
	{
		writeInternalLong(Double.doubleToLongBits(value));
	}

	private void writeInternalString(CharSequence v ) throws IOException
	{
		if (v == null)
			writeInternalInt(0);
		else
		{
			byte[] bytes = v.toString().getBytes(StandardCharsets.UTF_8);
			if (bytes.length == 0)
			{
				writeInternalInt(1);
				writeInternal((byte) 0);
			}
			else
			{
				writeInternalInt(bytes.length);
				writeInternal(bytes);
			}
		}
	}

	protected void writeInternal(byte... data) throws IOException
	{
		writeInternal(data, 0,data.length);
	}

	protected void writeInternal(byte[] data, int off, int len) throws IOException
	{
		autoFinishChildStream();
		if ( os_ == null )
			throw new IOException("sub-data stream already closed");
		os_.write(data, off,len);
	}

	protected DataOutputStream(DataOutputStream os )
	{
		os_ = os.os_;
		map_= os.map_;
	}

	private void writeDynamicArray(int fieldId, Object value, DataType type) throws IOException
	{
		if ( value == null )
		{
			writeNull(fieldId);
			return;
		}
		final int N = GenericIterator.getLength(value);
		Iterator it = GenericIterator.createIterator( value );

		writeHeader( fieldId, DataType.ARRAY );
		writeInternal(type.getId());
		writeInternalInt(N);

		BitAccumulator accu = (type == DataType.BOOLEAN) ? new BitAccumulator() : null;

		while ( it.hasNext() )
		{
			Object v = it.next();
			switch ( type )
			{
				case BOOLEAN:
					accu.addBit((v!=null && (Boolean)v));
					break;
				case BYTE:
					writeInternal(((Number)v).byteValue());
					break;
				case SHORT:
					writeInternalShort(((Number)v).shortValue());
					break;
				case INT:
					writeInternalInt(((Number)v).intValue());
					break;
				case LONG:
					writeInternalLong( ((Number)v).longValue());
					break;
				case FLOAT:
					writeInternalInt(Float.floatToIntBits(((Number)v).floatValue()));
					break;
				case DOUBLE:
					writeInternalDouble(((Number)v).doubleValue());
					break;
				case STRING:
					writeInternalString(v.toString());
					break;
				case CHAR:
				{
					char cv = (Character) v;
					writeInternal((byte) (cv >> 8), (byte) cv);
					break;
				}
				default:
					throw new IOException("Unsupported array type "+type);
			}
		}
		if ( accu != null)
			accu.finish();
	}


	private void writeFieldValue(FieldInfo fi, Object v, boolean writeNullValues ) throws IOException
	{
		switch (fi.type)
		{
			case BOOLEAN:
				writeBoolean(fi.id, ((Boolean)v).booleanValue());
				break;
			case BYTE:
				writeByte(fi.id, ((Number)v).byteValue());
				break;
			case SHORT:
				writeShort(fi.id, ((Number)v).shortValue());
				break;
			case INT:
				writeInt(fi.id, ((Number)v).intValue());
				break;
			case CHAR:
				writeChar(fi.id, (Character)v);
				break;
			case LONG:
				writeLong(fi.id, ((Number)v).longValue());
				break;
			case STRING:
				writeString(fi.id, v.toString());
				break;
			case FLOAT:
				writeFloat(fi.id, ((Number)v).floatValue());
				break;
			case DOUBLE:
				writeDouble(fi.id, ((Number)v).doubleValue());
				break;
			case OBJECT:
				writeObject(fi.id, v, writeNullValues);
				break;
			case ARRAY:
				writeDynamicArray(fi.id, v, fi.elementType);
				break;
			case NULL:
				writeNull(fi.id);
				break;
			default:
				throw new IOException("Internal Error");
		};
	}

	@Override
	public void writeObject( int fieldId, Object o, boolean writeNullValues ) throws IOException
	{
		if ( o == null )
			writeNull(fieldId);
		else
		{
			try
			{
				DataOutputStream os = (DataOutputStream)startObject(fieldId);

				Map<String, FieldInfo> idMap = map_.writeClass(os, o, 1);

				if ( o instanceof Data)
				{
					((Data)o).write(os, 3);
				}
				else
				{
					for (FieldInfo fi : idMap.values())
					{
						Object v = fi.field.get(o);
						if (v == null)
						{
							if (writeNullValues)
								os.writeNull(fi.id);
						}
						else
							os.writeFieldValue(fi, v, writeNullValues);
					}
				}

				os.finish();
			}
			catch (IOException e)
			{
				throw e;
			}
			catch (Exception ee)
			{
				throw new IOException("Internal error in data mapping", ee);
			}
		}
	}



}
