package wiseasy.android;

public class JBigCompress {
	static {

		System.loadLibrary("jbigcompress_jni");
	}
	
	public static native byte[] compress(byte[] toCompressBytes);
	public static native boolean decompress(byte[] toDecompressBytes, String path);
}

