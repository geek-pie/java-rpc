package com.slowlizard.rpc;

import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.primitives.Bytes;

public class FileWriter2 {
	public static void main(String[] args) throws Exception {
		List<Entry<String, Integer>> end = new ArrayList<>();
		// File file = new File("/Users/xujianxing/Desktop/file.txt");
		//// if (!file.exists()) {
		//// file.createNewFile();
		//// }
		// FileOutputStream out = new FileOutputStream(file);
		// Random raRandom = new Random();
		//
		// double k = Math.pow(2, 29);
		// for (int i = 0; i < k; i++) {
		// String s = new String(raRandom.nextInt(10000000) + "");
		//// System.out.println(s.getBytes().length);
		// out.write(s.getBytes());
		// out.write("\n".getBytes());
		// out.flush();
		// }
		// out.close();

		FileInputStream fis = new FileInputStream("/Users/xujianxing/Desktop/file.txt");
		FileChannel fc = fis.getChannel();
		long fileSize = fc.size();
		int blockSize = 128 * 1024 * 1024;
		MappedByteBuffer mbb = null;
		long offset = 0;
		do {
			if (offset + blockSize > fileSize && offset < fileSize) {
				mbb = fc.map(FileChannel.MapMode.READ_ONLY, offset, fileSize - offset);
			} else if (offset + blockSize < fileSize) {
				mbb = fc.map(FileChannel.MapMode.READ_ONLY, offset, blockSize);
			} else {
				break;
			}
			Map<String, Integer> record = new HashMap<String, Integer>();
			List<Byte> strBytes = new ArrayList<Byte>();
			for (int i = 0; i < blockSize; i++) {
				if (mbb.hasRemaining()) {
					byte b = mbb.get();
					// 不能用"\n".equals((char) b) 判断。
					if ('\n' == (char) b) {
						byte[] arr = Bytes.toArray(strBytes);
						strBytes.clear();
						String str = new String(arr);
						Integer count = record.get(str);
						if (count == null) {
							count = 0;
						}
						record.put(str, count + 1);
					} else {
						strBytes.add(b);
					}
				}
			}
			offset = offset + blockSize;
			// 对Map进行排序
			List<Entry<String, Integer>> sortedList = MapSort.sort(record, 10);  
			end.addAll(sortedList);
			System.out.println("单次出现次数最多的Key:" + sortedList.get(0).getKey());
			System.out.println("单次出现次数最多的Key的次数:" + sortedList.get(0).getValue());
			System.gc();
		} while (null != mbb);
		end = MapSort.sort(end);  //各个key可能分散出现在不同的map中间。所以直接排序是不妥的。
		System.out.println("出现次数最多的Key:" + end.get(0).getKey());
		System.out.println("出现次数最多的Key的次数:" + end.get(0).getValue());
	}

}
