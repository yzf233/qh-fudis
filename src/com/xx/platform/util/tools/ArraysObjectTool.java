package com.xx.platform.util.tools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.lucene.index.CorruptIndexException;

public class ArraysObjectTool {
	/**
	 * 把数组转化成对象
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static Object ArrayToObject(byte[] in) throws IOException {
		Object o = null;
		java.io.ByteArrayInputStream bi = new java.io.ByteArrayInputStream(in);
		java.io.ObjectInputStream oi = new java.io.ObjectInputStream(bi);
		try {
			o = oi.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			oi.close();
			bi.close();
		}
		return o;
	}

	/**
	 * 把对象转化成数组
	 * 
	 * @param dataObject
	 * @return
	 * @throws IOException
	 */
	public static byte[] ObjectToArrays(Object dataObject) throws IOException {
		java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutputStream objOut = new java.io.ObjectOutputStream(
				baout);
		objOut.writeObject(dataObject);
		byte[] data = baout.toByteArray();
		objOut.close();
		baout.close();
		return data;
	}

	/**
	 * 从2进制数组中获得文件
	 * 
	 * @param b
	 * @param outputFile
	 * @return
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		FileOutputStream fstream = null;
		try {
			file = new File(outputFile);
			fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}
	public static File getFileFromBytes(byte[] b, File file) {
		BufferedOutputStream stream = null;
		FileOutputStream fstream = null;
		try {
			fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}
	/**
	 * 文件变成2进制
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static byte[] fileToBytes(File f) throws IOException {
		if (f == null) {
			return null;
		}
		FileInputStream stream = null;
		ByteArrayOutputStream out = null;
		try {
			stream = new FileInputStream(f);
			out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1) {
				out.write(b, 0, n);
			}
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			if (stream != null) {
				stream.close();
			}
			if (out != null) {
				out.close();
			}
		}
		return null;
	}
/**
 * 压缩算法
 * @param input
 * @param offset
 * @param length
 * @return
 */
	public static byte[] compress(byte[] input, int offset, int length) {
		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);
		compressor.setInput(input, offset, length);
		compressor.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		try {
			compressor.setLevel(Deflater.BEST_COMPRESSION);
			compressor.setInput(input);
			compressor.finish();
			byte[] buf = new byte[1024];
			while (!compressor.finished()) {
				int count = compressor.deflate(buf);
				bos.write(buf, 0, count);
			}
		} finally {
			compressor.end();
		}
		return bos.toByteArray();
	}
/**
 * 解压缩算法
 * @param input
 * @return
 * @throws CorruptIndexException
 * @throws IOException
 */
	public static byte[] uncompress(final byte[] input)
			throws CorruptIndexException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
		Inflater decompressor = new Inflater();
		try {
			decompressor.setInput(input);
			byte[] buf = new byte[1024];
			while (!decompressor.finished()) {
				try {
					int count = decompressor.inflate(buf);
					bos.write(buf, 0, count);
				} catch (DataFormatException e) {
					CorruptIndexException newException = new CorruptIndexException(
							"field data are in wrong format: " + e.toString());
					newException.initCause(e);
					throw newException;
				}
			}
		} finally {
			decompressor.end();
		}
		return bos.toByteArray();
	}
	public static void main(String[] args) throws IOException{
		Properties sysPro=System.getProperties();
		Set keys=sysPro.keySet();
		for(Object key:keys){
			System.out.println(key.toString()+"				"+sysPro.getProperty((String)key));
		}
		System.out.println("=============================================================================");
		byte[] oldbt=ObjectToArrays(sysPro);
		System.out.println("长度==="+oldbt.length);
		byte[] newbt=compress(oldbt,0,oldbt.length);
		System.out.println("压缩后长度"+newbt.length);
		byte[] unbyte=uncompress(newbt);
		System.out.println("解压缩后长度"+unbyte.length);
		Properties p=(Properties)ArrayToObject(unbyte);
		System.out.println(p);
	}
}
