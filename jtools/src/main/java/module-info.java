module com.bw.jtools {
	// static: compile time but optional during runtime.
	// (only needed if the related functionality is used).
	requires static org.netbeans.swing.outline.RELEASE121;
	requires static org.apache.logging.log4j;
	requires static org.apache.logging.log4j.core;
	requires static jdk.management;
	requires static java.sql; // Needed by commons.csv
	// transitive: using modules are also allowed to read.
	requires static transitive java.json;
	requires static transitive java.desktop;
	requires transitive com.bw.jSVG;

	requires java.prefs;
	requires commons.csv;
	exports com.bw.jtools;
}
