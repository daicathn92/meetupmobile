package dhbk.meetup.mobile.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

public class Storage {

	public static ArrayList<String> getListFile (Context ct) {
		ArrayList<String> listFiles = new ArrayList<String>();
		File files[] = ct.getFilesDir().listFiles();
		for(File f : files) {
			if(f.getName().endsWith(".meetup"))
				listFiles.add(f.getName());
		}
		return listFiles;
	}
	
	public static boolean writerFile (String filename, String content, Context ct) {
		try {
			@SuppressWarnings("deprecation")
			FileOutputStream fOut = ct.openFileOutput(filename + ".meetup", Context.MODE_PRIVATE);
			fOut.write(content.getBytes());
			fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String readFile (String fileName, Context ct) {
		try {
			FileInputStream fIn = ct.openFileInput(fileName);
			int c;
			StringBuffer buff = new StringBuffer();
			while ((c = fIn.read()) != -1) {
				buff.append(Character.toString((char)c));
			}
			return buff.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
