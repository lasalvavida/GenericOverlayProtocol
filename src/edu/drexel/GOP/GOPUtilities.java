package edu.drexel.GOP;

public final class GOPUtilities {
	/**
	 ** Converts an integer into an array of 4 bytes.
	 ** Credit: http://stackoverflow.com/questions/6374915/java-convert-int-to-byte-array-of-4-bytes
	 **/
	public static final byte[] intToByteArray(int value) {
		return new byte[] {
			(byte)(value >>> 24),
			(byte)(value >>> 16),
			(byte)(value >>> 8),
			(byte)value
		};
	}
	/**
	 ** Converts a byte array into an integer.
	 ** Credit: http://stackoverflow.com/questions/5399798/byte-array-and-int-conversion-in-java
	 **/
	public static final int byteArrayToInt(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i+offset] & 0x000000FF) << shift;
		}
		return value;
	}
	public static byte[] combine(byte[] first, byte[] second) {
    		byte[] ret = new byte[first.length + second.length];
    		for(int i=0; i<first.length; i++) {
    			ret[i] = first[i];
		}
		for(int i=0; i<second.length; i++) {
			ret[first.length+i] = second[i];
		}
		return ret;
	}
}
