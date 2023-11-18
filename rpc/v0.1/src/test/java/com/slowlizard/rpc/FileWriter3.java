package com.slowlizard.rpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Bytes;

public class FileWriter3 {

	private static Map<String, FileOutputStream> streams = new HashMap<>();

	public static void main(String[] args) throws Exception {
		initFiles();
		final int BLOCK_SIZE = 128 * 1024 * 1024;
		FileInputStream fis = new FileInputStream("/Users/xujianxing/Desktop/file.txt");
		FileChannel fc = fis.getChannel();
		long fileSize = fc.size();
		MappedByteBuffer mbb = null;
		long offset = 0;
		do {
			if (offset + BLOCK_SIZE > fileSize && offset < fileSize) {
				mbb = fc.map(FileChannel.MapMode.READ_ONLY, offset, fileSize - offset);
			} else if (offset + BLOCK_SIZE < fileSize) {
				mbb = fc.map(FileChannel.MapMode.READ_ONLY, offset, BLOCK_SIZE);
			} else {
				break;
			}
			List<Byte> strBytes = new ArrayList<Byte>();
			for (int i = 0; i < BLOCK_SIZE; i++) {
				if (mbb.hasRemaining()) {
					byte b = mbb.get();
					// 不能用"\n".equals((char) b) 判断。
					if ('\n' == (char) b) {
						byte[] arr = Bytes.toArray(strBytes);
						strBytes.clear();
						String str = new String(arr);
						if(str!=null && str.length()>0){
						FileOutputStream stream = getStream(str);
						stream.write(arr);
						stream.write('\n');
						stream.flush();
						}
					} else {
						strBytes.add(b);
					}
				}
			}
			offset = offset + BLOCK_SIZE;
		} while (null != mbb);

	}

	public static void initFiles() {
		for (int i = 0; i < 10; i++) {
			File file = new File("/Users/xujianxing/Desktop/sort/" + i + ".txt");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static FileOutputStream getStream(String str) {
		String remainder = str.substring(str.length() - 1, str.length());
		FileOutputStream stream = streams.get(remainder);
		if (stream == null) {
			try {
				stream = new FileOutputStream(new File("/Users/xujianxing/Desktop/sort/" + remainder + ".txt"));
				streams.put(remainder, stream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return stream;
	}
}
