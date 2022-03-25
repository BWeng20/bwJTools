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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Handles class meta-data for Data Input/Output.<br>
 * Class information is written to DataOutput as needed.
 * If multiple objects of the same class are written, the class-information is only written once.
 * References to the class-information is done via a internal managed class-id.<br>
 * Class-ids are always relative to the DataInput/Output-instance.
 * It is not guaranteed that the same class have the same id in different DataInput/Output-instance
 * and can't be used for unique identification.
 *
 * @see DataInput
 * @see DataOutput
 */
class DataReflectMap
{
	/**
	 * Sets the internal used start-value for the ids used to identify members. <br>
	 * Default is 3. Id 1 is used for class-information and Id 2 for the class-id.
	 * The value can be set to any legal field-id as the protocol allows duplicate ids.
	 */
	public void setFieldStartId(int fieldId)
	{
		fieldStartId_ = fieldId;
	}

	/**
	 * Adds the class with all classes used by members.
	 */
	protected void addClass(Class clazz) throws NoSuchFieldException
	{
		final String name = clazz.getName();
		if (!classes_.containsKey(name))
		{
			ClassInfo ci = new ClassInfo();
			ci.classId = classes_.size();
			ci.assignStaticInfo(getStaticClassInfo(clazz), fieldStartId_);

			classes_.put(name, ci);
			classesById_.put(Integer.valueOf(ci.classId), ci);
		}
	}

	/**
	 * Tries to detect the internal data-type that can be used to serialize a
	 * field of this type.
	 */
	public static DataType detectType(Class clazz)
	{
		if (clazz.isArray() || Iterable.class.isAssignableFrom(clazz))
			return DataType.ARRAY;
		else if (clazz.isPrimitive())
		{
			if (byte.class.isAssignableFrom(clazz))
				return DataType.BYTE;
			else if (short.class.isAssignableFrom(clazz))
				return DataType.SHORT;
			else if (int.class.isAssignableFrom(clazz))
				return DataType.INT;
			else if (long.class.isAssignableFrom(clazz))
				return DataType.LONG;
			else if (float.class.isAssignableFrom(clazz))
				return DataType.FLOAT;
			else if (double.class.isAssignableFrom(clazz))
				return DataType.DOUBLE;
		}
		else if (Number.class.isAssignableFrom(clazz))
		{
			if (Byte.class.isAssignableFrom(clazz))
				return DataType.BYTE;
			else if (Short.class.isAssignableFrom(clazz))
				return DataType.SHORT;
			else if (Integer.class.isAssignableFrom(clazz))
				return DataType.INT;
			else if (Long.class.isAssignableFrom(clazz))
				return DataType.LONG;
			else if (Float.class.isAssignableFrom(clazz))
				return DataType.FLOAT;
			else if (Double.class.isAssignableFrom(clazz))
				return DataType.DOUBLE;
		}
		else if (Character.class.isAssignableFrom(clazz))
			return DataType.CHAR;
		else if (Boolean.class.isAssignableFrom(clazz))
			return DataType.BOOLEAN;
		else if (CharSequence.class.isAssignableFrom(clazz))
			return DataType.STRING;
		else if (!clazz.getName()
					   .startsWith("java."))
			return DataType.OBJECT;
		else
			System.err.println("WARNING: Class " + clazz.getName() + " can not be serialized.");
		return DataType.NULL;
	}

	/**
	 * Get the member-map for the class. Maps field-name to field-information.
	 */
	public Map<String, FieldInfo> getFieldMap(Class clazz)
	{
		ClassInfo ci = classes_.get(clazz.getName());
		if (ci != null)
			return Collections.unmodifiableMap(ci.nameToInfo);
		else
			return null;
	}

	/**
	 * Get the member-map for the class. Maps field-Id to field-information.
	 */
	public Map<Integer, FieldInfo> getFieldIdMap(Class clazz)
	{
		ClassInfo ci = classes_.get(clazz.getName());
		if (ci != null)
			return Collections.unmodifiableMap(ci.idToInfo);
		else
			return null;
	}

	/**
	 * Field information independent of any actual stream.<br>
	 * This information is stored statically and re-used for
	 * different data-i/o-instances.
	 */
	protected static class StaticFieldInfo
	{
		Field field;
		String fieldName;
		DataType type;
		DataType elementType = DataType.NULL;

		public void assignStaticInfo(StaticFieldInfo sfi)
		{
			field = sfi.field;
			fieldName = sfi.fieldName;
			type = sfi.type;
			elementType = sfi.elementType;
		}

		@Override
		public boolean equals(Object other)
		{
			if (this == other)
				return true;
			else if (other instanceof StaticFieldInfo)
			{
				StaticFieldInfo oc = (StaticFieldInfo) other;
				return Objects.equals(oc.fieldName, fieldName) &&
						oc.type == type &&
						oc.elementType == elementType;
			}
			return false;
		}

	}

	/**
	 * Class and field information independent of any actual stream.<br>
	 * This information is stored statically and re-used for
	 * different data-i/o-instances.
	 */
	protected static final class StaticClassInfo
	{
		public Class clazz;
		public String name;
		public List<StaticFieldInfo> fields = new ArrayList<>();
		public Constructor ctor;
	}

	/**
	 * Field information with mapping to ids for the actual stream.
	 */
	protected static final class FieldInfo extends StaticFieldInfo
	{
		int id;

		@Override
		public boolean equals(Object other)
		{
			if (this == other)
				return true;
			else if (other instanceof FieldInfo)
			{
				FieldInfo oc = (FieldInfo) other;
				return oc.id == id && super.equals(other);
			}
			return false;
		}
	}

	/**
	 * Class and field information with mapping to ids for the actual stream.
	 */
	protected static final class ClassInfo
	{
		public int classId;
		public Class clazz;
		public String name;

		public Map<String, FieldInfo> nameToInfo = new HashMap<>();
		public Map<Integer, FieldInfo> idToInfo = new HashMap<>();
		public boolean written;

		public Constructor ctor;

		/**
		 * Copy values from static information and assign fieldIds,
		 * starting with fieldStartId.
		 */
		public void assignStaticInfo(StaticClassInfo info, int fieldStartId)
		{
			clazz = info.clazz;
			name = info.name;
			ctor = info.ctor;
			for (StaticFieldInfo sfi : info.fields)
			{
				FieldInfo f = new FieldInfo();
				f.id = fieldStartId++;
				f.assignStaticInfo(sfi);
				nameToInfo.put(f.fieldName, f);
				idToInfo.put(Integer.valueOf(f.id), f);
			}
		}

		/**
		 * Merge values from static information.
		 * Fields-ids are not assigned. This method is intended to be used
		 * to assign class information to a information-record that was read
		 * by a data-input.
		 */
		public void mergeStaticInfo(StaticClassInfo info)
		{
			if (info != null)
			{
				clazz = info.clazz;
				name = info.name;
				ctor = info.ctor;

				for (StaticFieldInfo sfi : info.fields)
				{
					FieldInfo fi = nameToInfo.get(sfi.fieldName);
					if (fi != null)
						fi.assignStaticInfo(sfi);
				}
			}
		}

		@Override
		public boolean equals(Object other)
		{
			if (this == other)
				return true;
			else if (other instanceof ClassInfo)
			{
				ClassInfo oc = (ClassInfo) other;
				return oc.name.equals(name) && oc.idToInfo.equals(idToInfo);
			}
			return false;
		}

		/**
		 * Reads class-info from a DataInput and merges it with the static class-info.
		 */
		protected void read(DataInput is, ClassNameCompressor compressor)
				throws IOException, ClassNotFoundException, NoSuchFieldException, NoSuchMethodException
		{
			while (is.hasNextField())
			{
				switch (is.getFieldId())
				{
					case 1:
						name = compressor.getUncompressed( is.readByteArray() );
						break;
					case 2:
						classId = is.readNumber().intValue();
						break;
					case 3:
					{
						DataInput l = is.startObject();
						while (l.hasNextField())
						{
							FieldInfo fi = new FieldInfo();
							fi.id = l.getFieldId();
							fi.fieldName = l.readString();

							idToInfo.put(Integer.valueOf(fi.id), fi);
							nameToInfo.put(fi.fieldName, fi);
						}
						break;
					}
					default:
						is.skip();
						break;
				}
			}

			mergeStaticInfo(getStaticClassInfo(name));

		}

		/**
		 * Write class-information to DataOutput.
		 */
		protected void write(DataOutput os, ClassNameCompressor compressor) throws IOException
		{
			os.writeArray(1, compressor.getCompressed(name));
			os.writeInt(2, classId);
			DataOutput l = os.startObject(3);
			for (Map.Entry<String, FieldInfo> i : nameToInfo.entrySet())
			{
				l.writeString(i.getValue().id, i.getKey());
			}
			os.finish();
		}
	}

	/**
	 * The field id starts by default with 3.
	 * Id 1 is used for meta info
	 * Id 2 is used for class-id.
	 */
	protected int fieldStartId_ = 3;

	/**
	 * Class name compressor (compresses redundant parts of package names).
	 */
	protected ClassNameCompressor compressor_ = new ClassNameCompressor();

	/**
	 * Maps class names to class-information.
	 */
	protected Map<String, ClassInfo> classes_ = new HashMap<>();

	/**
	 * Maps class-id to class-information (same instances as in {@link #classes_}).
	 * Ths class-ids are used to assign OBJECT-values to classes.
	 */
	protected Map<Integer, ClassInfo> classesById_ = new HashMap<>();

	/**
	 * Maps class-names to their static class-information.
	 */
	protected static Map<String, StaticClassInfo> staticClassInfo_ = new HashMap<>();



	/**
	 * Gets static class-information by class-name.
	 */
	protected static synchronized StaticClassInfo getStaticClassInfo(String name) throws ClassNotFoundException, NoSuchFieldException
	{
		StaticClassInfo ci = staticClassInfo_.get(name);
		if (ci == null)
		{
			ci = getStaticClassInfo(Class.forName(name));
		}
		return ci;
	}

	/**
	 * Gets static class-information by class.
	 */
	protected static synchronized StaticClassInfo getStaticClassInfo(Class clazz) throws NoSuchFieldException
	{
		StaticClassInfo ci = staticClassInfo_.get(clazz.getName());
		if (ci == null)
		{
			ci = new StaticClassInfo();
			ci.name = clazz.getName();
			ci.clazz = clazz;
			Constructor[] ctors = clazz.getDeclaredConstructors();
			for (int i = 0; i < ctors.length; ++i)
			{
				if (ctors[i].getGenericParameterTypes().length == 0)
				{
					ci.ctor = ctors[i];
					break;
				}
			}

			staticClassInfo_.put(ci.name, ci);

			for (Field f : clazz.getFields())
			{
				StaticFieldInfo fi = new StaticFieldInfo();
				fi.field = f;
				fi.fieldName = f.getName();
				fi.type = detectType(f.getType());
				ci.fields.add(fi);

				Class fc = f.getType();
				if (!(fc.isPrimitive() || fc.getPackageName()
											.startsWith("java.")))
				{
					getStaticClassInfo(fc);
				}
			}

			for (StaticFieldInfo fi : ci.fields)
			{
				fi.field = ci.clazz.getField(fi.fieldName);
				Class fieldClazz = fi.field.getType();
				fi.type = detectType(fieldClazz);
				if (fi.type == DataType.ARRAY)
				{
					if (fieldClazz.isArray())
						fi.elementType = detectType(fi.field.getType().getComponentType());
					else
						fi.elementType = DataType.OBJECT;
				}
			}
		}
		return ci;
	}

	/**
	 * Writes pending class information (OBJECT) with field-id "fieldId".
	 * If no class data is pending this field is not written.
	 * Then the class-id (INT) is written with field-id "fieldId+1".<br>
	 * The fieldId have to be the same as used in the corresponding {@link #readClass(DataInput, int)}.
	 */
	public Map<String, FieldInfo> writeClass(DataOutput os, Object o, int fieldId) throws IOException
	{
		final Class clazz = o.getClass();

		try
		{
			addClass(clazz);
		}
		catch (NoSuchFieldException e)
		{
			throw new IOException("Introspection of class " + clazz.getName() + " failed.", e);
		}

		boolean toWrite = false;
		for (ClassInfo c : classes_.values())
		{
			if (!c.written)
			{
				toWrite = true;
				break;
			}
		}
		if (toWrite)
		{
			DataOutput cs = os.startObject(fieldId);
			int fId = 1;
			for (ClassInfo c : classes_.values())
			{
				if (!c.written)
				{
					c.write(cs.startObject(fId++), compressor_);
					c.written = true;
				}
			}
			cs.finish();
		}

		ClassInfo ci = classes_.get(clazz.getName());
		os.writeInt(++fieldId, ci.classId);

		return getFieldMap(clazz);
	}

	/**
	 * Reads optional class information (OBJECT) with field-id "fieldId".
	 * Then the class-id (INT) is read with field-id "fieldId+1".<br>
	 * The fieldId have to be the same as used in the corresponding {@link #writeClass(DataOutput, Object, int)} call.
	 */
	public ClassInfo readClass(DataInput is, int fieldId) throws IOException
	{
		try
		{
			int id = is.getFieldId();
			if (fieldId == id)
			{
				//Read optional meta infos
				if (is.isFieldNull())
				{
					is.skip();
				}
				else
				{
					DataInput cs = is.startObject();
					while (cs.hasNextField())
					{
						ClassInfo c = new ClassInfo();
						c.read(cs.startObject(), compressor_);
						classes_.put(c.name, c);
						classesById_.put(Integer.valueOf(c.classId), c);
					}
				}
				id = is.getFieldId();
			}
			if ((fieldId + 1) != id)
				throw new IOException("Protocol sequence error in class information.");
			int classId = is.readNumber()
							.intValue();
			ClassInfo ci = classesById_.get(Integer.valueOf(classId));
			if (ci == null)
				throw new IOException("Protocol Error. Class id not found.");
			return ci;
		}
		catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e)
		{
			throw new IOException("Class could not be loaded.", e);
		}
	}

}
