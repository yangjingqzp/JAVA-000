package learn.geektime.jvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Hello 类加载器，用于加载 Hello.xlass 中的 Hello 类
 *
 * @author yangjing
 * @date 2020/10/19 8:40 PM
 * @version v1.0
 */
public class HelloClassLoader extends ClassLoader {
	@Override
	protected Class<?> findClass(String name) {
		String path = "Week_01/Hello.xlass";
		byte[] bytes = convertByteArray(readBytesFromFile(path));

		return defineClass(name, bytes, 0, bytes.length);
	}

	private byte[] convertByteArray(byte[] bytes) {
		for (int i=0; i<bytes.length; i++) {
			bytes[i] = (byte)(bytes[i] ^ 0xFFFFFFFF);
		}
		return bytes;
	}

	private byte[] readBytesFromFile(String path) {
		File file = new File(path);
		byte[] classBytes = new byte[(int)file.length()];
		try (FileInputStream in = new FileInputStream(file)) {
			int offset = 0;
			int numRead = 0;
			while ((offset < classBytes.length) && (
				(numRead = in.read(classBytes, offset, classBytes.length - offset)) >= 0)) {
				offset+= numRead;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return classBytes;
	}
}
