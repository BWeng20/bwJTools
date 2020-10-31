package com.bw.jtools.collections;

import java.util.HashMap;

/**
 * A simple String Pool
 */
public class StringPool {

    private int idGenerator = 0;
    private HashMap<String, StringId> stringpool = new HashMap<>(10069);

    protected final static class StringId {
        public final String string;
        public final Integer id;

        StringId(final String s, int id) {
            string = s;
            this.id = id;
        }
    }

    /**
     * Get an unique Id for a string.
     *
     * @param str The string.
     * @return The Id.
     */
    public Integer getStringId(String str) {
        synchronized (stringpool) {
            StringId id = stringpool.get(str);
            if (id == null) {
                id = addString( str, ++idGenerator );
            }
            return id.id;
        }
    }

    /**
     * Get pool string.
     *
     * @param str The string.
     * @return The string.
     */
    public String getString(String str) {
        synchronized (stringpool) {
            StringId id = stringpool.get(str);
            if (id == null) {
                id = addString( str, ++idGenerator );
            }
            return id.string;
        }
    }

    protected StringId addString( String str, int id ) {
        StringId sid = new StringId( str, ++idGenerator );
        stringpool.put(str, sid);
        return sid;
    }


}
