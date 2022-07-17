/*
 * (c) copyright Bernd Wengenroth
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

package com.bw.jtools.io;

import com.bw.jtools.Log;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Collection of methods for JSON handling.<br>
 * Main reason for use of this class is to avoid additional checks and exception
 * handling if data need to be extracted.
 */
public final class JsonTool
{
	private static JsonWriterFactory jsonWriterFactory_ = null;
	private static JsonWriterFactory jsonWriterFactoryNonePretty_ = null;
	private static JsonGeneratorFactory jsonGeneratorFactory = null;
	private static JsonBuilderFactory jsonBuilderFactory_ = null;
	private static Map<String, Object> jsonDefaultConfig_ = null;

	/**
	 * Sets a new JsonConfiguration.<br>
	 * Currently used factories are cleared to force a re-creation.
	 *
	 * @param config The new configuration.
	 */
	public static synchronized void setJsonConfiguration(Map<String, Object> config)
	{
		jsonDefaultConfig_ = config;
		jsonBuilderFactory_ = null;
		jsonWriterFactory_ = null;
		jsonGeneratorFactory = null;
	}

	/**
	 * Get the current JsonConfiguration.<br>
	 * If not yet created the JsonConfiguration is created here with
	 * default "PRETTY_PRINTING=TRUE".<br>
	 *
	 * @return The current JsonConfiguration
	 */
	public static synchronized Map<String, Object> getJsonConfiguration()
	{
		if (jsonDefaultConfig_ == null)
		{
			jsonDefaultConfig_ = new HashMap<>();
			jsonDefaultConfig_.put(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
		}
		return jsonDefaultConfig_;
	}

	/**
	 * Get the current JsonGeneratorFactory.<br>
	 * If not yet created the JsonGeneratorFactory is created here.
	 *
	 * @return The current JsonGeneratorFactory
	 */
	public static synchronized JsonGeneratorFactory getJsonGeneratorFactory()
	{
		if (jsonGeneratorFactory == null)
		{
			jsonGeneratorFactory = Json.createGeneratorFactory(getJsonConfiguration());
		}
		return jsonGeneratorFactory;
	}

	/**
	 * Get the current JsonWriterFactory.<br>
	 * If not yet created the JsonWriterFactory is created here.<br>
	 * "PRETTY_PRINTING" is on per default. For a compact, none "pretty" version
	 * use getJsonWriterFactory(false).
	 *
	 * @return The current JsonWriterFactory
	 */
	public static synchronized JsonWriterFactory getJsonWriterFactory()
	{
		if (jsonWriterFactory_ == null)
		{
			jsonWriterFactory_ = Json.createWriterFactory(getJsonConfiguration());
		}
		return jsonWriterFactory_;
	}

	/**
	 * Get the current JsonWriterFactory with matching "pretty" flag.<br>
	 * A "pretty" output is more readable to humans, but consumes more space.
	 *
	 * @param pretty If true the "pretty" writer is returned.
	 * @return The current JsonWriterFactory with matching "pretty" setting.
	 */
	public static synchronized JsonWriterFactory getJsonWriterFactory(boolean pretty)
	{
		if (pretty) return getJsonWriterFactory();

		if (jsonWriterFactoryNonePretty_ == null)
		{
			Map<String, Object> jsonSimpleConfig = new HashMap<>();
			jsonWriterFactoryNonePretty_ = Json.createWriterFactory(jsonSimpleConfig);
		}
		return jsonWriterFactoryNonePretty_;
	}

	/**
	 * Get the current JsonBuilderFactory.<br>
	 * If not yet created the JsonBuilderFactory is created here.
	 *
	 * @return The current JsonBuilderFactory
	 */
	public static synchronized JsonBuilderFactory getJsonBuilderFactory()
	{
		if (jsonBuilderFactory_ == null)
		{
			jsonBuilderFactory_ = Json.createBuilderFactory(getJsonConfiguration());
		}
		return jsonBuilderFactory_;
	}

	/**
	 * Reads JSON from file.
	 *
	 * @param jsonFile The file to load.
	 * @return The parsed JSON-Object.
	 * @throws java.io.FileNotFoundException in case something went wrong.
	 */
	public static JsonObject readJson(File jsonFile) throws FileNotFoundException
	{
		JsonReader jsonReader = Json.createReader(new FileReader(jsonFile));
		return jsonReader.readObject();
	}

	/**
	 * Save getter method to extract an object from some JSON value.
	 *
	 * @param value The value to examine.
	 * @param name  The name of the member to extract.
	 * @return The extracted object or null.
	 */
	public static JsonObject getJsonObject(JsonValue value, String name)
	{
		if (value instanceof JsonObject)
		{
			JsonObject object = (JsonObject) value;
			try
			{
				return object.getJsonObject(name);
			}
			catch (Exception e)
			{
			}
		}
		return null;
	}

	/**
	 * Save getter method to extract a number from some JSON value.<br>
	 * If "value" is a JsonObject, a JsonNumber value with name "name" is extracted.<br>
	 * If "value" is an JsonNumber, it is cast and returned.
	 *
	 * @param value The value to examine.
	 * @param name  The name of the member to extract.
	 * @return The JsonNumber or null.
	 */
	public static JsonNumber getJsonNumber(JsonValue value, String name)
	{
		try
		{
			if (value instanceof JsonObject)
			{
				return ((JsonObject) value).getJsonNumber(name);
			}
			else if (value instanceof JsonNumber)
				return (JsonNumber) value;
		}
		catch (Exception e)
		{
		}
		return null;
	}

	/**
	 * Save getter method to extract a Boolean from some JSON value.<br>
	 * If "value" is a JsonObject, a Boolean value with name "name" is extracted.<br>
	 * If "value" is an JsonNumber, true is returned if the numeric value is != 0.
	 *
	 * @param value      The value to examine.
	 * @param name       Name of the member in case "value" is a JsonObject.
	 * @param defaultVal Default value in case no value was found.
	 * @return The found value or defaultVal.
	 */
	public static boolean getJsonBoolean(JsonValue value, String name, boolean defaultVal)
	{
		try
		{
			if (value instanceof JsonObject)
			{
				return ((JsonObject) value).getBoolean(name, defaultVal);
			}
			else if (value instanceof JsonNumber)
				return ((JsonNumber) value).intValue() != 0;
		}
		catch (Exception e)
		{
		}
		return defaultVal;
	}

	/**
	 * Save getter function to extract an integer from JSON value.
	 *
	 * @param value      The value to examine.
	 * @param name       Name of the member in case "value" is a JsonObject.
	 * @param defaultVal Default value in case no value was found.
	 * @return The found value or defaultVal.
	 */
	public static int getJsonInt(JsonValue value, String name, int defaultVal)
	{
		JsonNumber nb = getJsonNumber(value, name);
		return (nb != null) ? nb.intValue() : defaultVal;
	}

	/**
	 * Save getter function to extract a long value from JSON value.
	 *
	 * @param value      The value to examine.
	 * @param name       Name of the member in case "value" is a JsonObject.
	 * @param defaultVal Default value in case no value was found.
	 * @return The found value or defaultVal.
	 */
	public static long getJsonLong(JsonValue value, String name, long defaultVal)
	{
		JsonNumber nb = getJsonNumber(value, name);
		return (nb != null) ? nb.longValue() : defaultVal;
	}


	/**
	 * Save getter function to extract a string from JSON value.
	 *
	 * @param value The value to examine.
	 * @param name  Name of the member in case "value" is a JsonObject.
	 * @return The found value or null.
	 */
	public static String getJsonString(JsonValue value, String name)
	{
		if (value instanceof JsonObject)
		{
			JsonObject object = (JsonObject) value;
			try
			{
				return object.getString(name);
			}
			catch (Exception e)
			{
				if (object.containsKey(name))
				{
					return object.get(name)
								 .toString();
				}
			}
		}
		else if (value instanceof JsonString)
			return ((JsonString) value).getString();
		return null;
	}

	/**
	 * Save getter function to extract a string from JSON value.<br>
	 * If "value" is a JsonObject, a JsonValue member with name "name" is extracted.<br>
	 *
	 * @param value The value to examine.
	 * @param name  Name of the member to extract.
	 * @return The found JsonValue or null.
	 */
	public static JsonValue getJsonValue(JsonValue value, String name)
	{
		if (value instanceof JsonObject)
		{
			JsonObject object = (JsonObject) value;
			try
			{
				return object.get(name);
			}
			catch (Exception e)
			{
			}
		}
		return null;
	}

	/**
	 * Save getter function to extract an JsonArray string from some JSON value.<br>
	 * If "value" is a JsonObject, a JsonArray member with name "name" is extracted.<br>
	 * If "value" is a JsonArray, it is cast and returned.
	 *
	 * @param value The value to examine.
	 * @param name  Name of the member to extract.
	 * @return The found JsonArray or null.
	 */
	public static JsonArray getJsonArray(JsonValue value, String name)
	{
		if (name != null && value instanceof JsonObject)
		{
			JsonObject object = (JsonObject) value;
			try
			{
				return object.getJsonArray(name);
			}
			catch (Exception e)
			{
			}
		}
		else if (value instanceof JsonArray)
			return (JsonArray) value;
		return null;
	}

	/**
	 * A utility to retrieve a rectangle added by addJsonBounds from a JSON object.<br>
	 * Can be used to easily store 2D-information in some JSON object.
	 * <br>
	 * See {@link #addJsonBounds(javax.json.JsonObjectBuilder, java.lang.String, java.awt.Rectangle) }.
	 *
	 * @param value The JsonValie that contains the rectangle.
	 * @param name  The name of the rectangle JsonObject inside "value".
	 * @return The rectangle or null.
	 */
	public static Rectangle getJsonBounds(JsonValue value, String name)
	{
		Rectangle bounds = null;
		JsonObject boundObj = getJsonObject(value, name);
		if (boundObj != null)
		{
			JsonNumber x = getJsonNumber(boundObj, "x");
			JsonNumber y = getJsonNumber(boundObj, "y");
			JsonNumber w = getJsonNumber(boundObj, "w");
			JsonNumber h = getJsonNumber(boundObj, "h");

			if (x != null && y != null && w != null && h != null)
			{
				bounds = new Rectangle(x.intValue(), y.intValue(), w.intValue(), h.intValue());
			}
		}
		return bounds;
	}

	/**
	 * A utility to add a rectangle from a JSON object.<br>
	 * Can be used to easily store 2D-information in some JSON object.<br>
	 * See {@link #getJsonBounds(javax.json.JsonValue, java.lang.String)}
	 *
	 * @param obj  The builder to add the object to.
	 * @param name The name of the JsonObject to store the values .
	 * @param rect The Rectangle to store.
	 */
	public static void addJsonBounds(JsonObjectBuilder obj, String name, Rectangle rect)
	{
		if (rect != null && obj != null)
		{
			JsonObjectBuilder bounds = getJsonBuilderFactory().createObjectBuilder();
			bounds.add("x", rect.x);
			bounds.add("y", rect.y);
			bounds.add("w", rect.width);
			bounds.add("h", rect.height);
			obj.add(name, bounds);
		}
	}

	/**
	 * Parse a JSON object definition.<br>
	 * The source needs to define an object via some "{...}".
	 *
	 * @param json The JSON text containing an object.
	 * @return The parsed object or null.
	 */
	public static JsonObject parseJson(String json)
	{
		JsonReader jsonReader = Json.createReader(new StringReader(json));
		return jsonReader.readObject();
	}

	/**
	 * Parse a JSON array definition.<br>
	 * The source needs to define an object via some "[...]".
	 *
	 * @param jsonArray The JSON text containing an array.
	 * @return The parsed array or null.
	 */
	public static JsonArray parseJsonArray(String jsonArray)
	{
		JsonReader jsonReader = Json.createReader(new StringReader(jsonArray));
		return jsonReader.readArray();
	}

	/**
	 * Writes JSON to some file, using the current writer-factory.<br>
	 * {@link #getJsonWriterFactory() }
	 *
	 * @param data The JSON object to write.
	 * @param file The file to write.
	 * @return true if nothing went wrong.
	 */
	public static boolean writeJson(JsonObject data, File file)
	{
		try
		{
			try (JsonWriter jsonWriter = JsonTool.getJsonWriterFactory()
												 .createWriter(new FileWriter(file)))
			{
				jsonWriter.writeObject(data);
			}
			return true;
		}
		catch (Exception ex)
		{
			Log.error("Failed to write " + file, ex);
			return false;
		}
	}
}
