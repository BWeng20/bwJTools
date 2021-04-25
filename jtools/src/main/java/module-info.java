module com.bw.jtools {
	requires static transitive java.json;
	requires static transitive java.desktop;
	requires static org.apache.logging.log4j;
	requires static org.apache.logging.log4j.core;
	requires static jdk.management;
	requires java.prefs;
	requires commons.csv;
	requires static java.sql; // Needed by commons.csv
	exports com.bw.jtools;
}
