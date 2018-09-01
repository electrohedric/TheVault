package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class FileUtil {

	/**
	 * Helper method to write to a file while handling all the IO errors
	 * @param path Path to the file to write to
	 * @param data String to write
	 */
	public static void writeTo(String path, String data) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			writer.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Helper method to read from a file while handling all the IO errors
	 * @param path Path to the file to read from
	 * @return
	 */
	public static String readFrom(String path) {
		String data = "";
	    try {
			data = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			if(e instanceof NoSuchFileException)
				Log.err(path + " does not exist");
			else
				e.printStackTrace();
		}
	    return data;
	}
}
