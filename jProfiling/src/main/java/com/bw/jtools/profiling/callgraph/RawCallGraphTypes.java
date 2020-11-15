package com.bw.jtools.profiling.callgraph;

public interface RawCallGraphTypes
{
	public final static byte BOOL_TRUE = 't';
	public final static byte BOOL_FALSE = 'f';
	public final static byte BYTE  = 'B';
	public final static byte SHORT = 'S';
	public final static byte INT   = 'I';
	public final static byte LONG  = 'L';
	public final static byte STRING= '$';
	public final static byte NULL  = 'N';

	public final static int MAX_STRING_LENGTH = 1024;

}
