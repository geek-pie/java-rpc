package com.slowlizard.rpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class WriteProxy {

	private FileChannel fileChannel;
	private ByteBuffer buf;

	@SuppressWarnings("resource")
	public WriteProxy(File file, int capacity) {
		try {
			fileChannel = new FileOutputStream(file).getChannel();
			buf = ByteBuffer.allocate(capacity);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
    /**
     *  不采用递归是因为如果字符串过大而缓存区过小引发StackOverflowException
     * @param str
     * @throws IOException
     */
	public void write(String str) throws IOException {
		byte[] bytes = str.getBytes();
		int startPosition = 0;
		do {
			startPosition = write0(bytes, startPosition);
		} while (startPosition < str.length());

	}

	public int write0(byte[] bytes, int position) throws IOException {
		if (position >= bytes.length) {
			return position;
		}
		while (buf.hasRemaining() && position < bytes.length) {
			buf.put(bytes[position]);
			position++;
		}
		buf.flip();
		fileChannel.write(buf);
		buf.clear();
		return position;
	}

	/**
	 * 强制写入数据。并且关闭连接
	 */
	public void close() {
		try {
			fileChannel.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		File file = new File("/Users/xujianxing/Desktop/ab.txt");
		WriteProxy wp = new WriteProxy(file, 26);
		wp.write("abcdefghijklmnopqrstuvwxyzsssssabcdefghijklmnopqrstuvwxyz");
		wp.close();
	}
}
