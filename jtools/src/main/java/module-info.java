module com.bw.jtools {

    // static: compile time but optional during runtime.
    // (only needed if the related functionality is used).
    // transitive: using modules are also allowed to read.
    
    requires static jdk.management;
    requires java.json;
    requires transitive java.desktop;
    requires transitive com.bw.jSVG;

    requires transitive org.netbeans.swing.outline;
    requires transitive commons.csv;
    requires static org.apache.logging.log4j.core;
    requires static org.apache.logging.log4j;

    requires java.prefs;
    exports com.bw.jtools;
}
