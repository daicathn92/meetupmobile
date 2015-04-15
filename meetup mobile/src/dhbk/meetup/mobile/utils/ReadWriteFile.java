package dhbk.meetup.mobile.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

public class ReadWriteFile {

	@SuppressLint("NewApi")
	public static String readFile (String path, String name, long maxBytes) {
		Bitmap bm = null;
		try {
			if(name.endsWith(".jpg") || name.endsWith(".JPG") || 
					name.endsWith(".jpeg") || name.endsWith(".JPEG") ||
					name.endsWith(".png") || name.endsWith(".PNG")) {
				bm = BitmapFactory.decodeFile(path + name);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if(name.endsWith(".png") || name.endsWith(".PNG"))
					 bm.compress(CompressFormat.PNG, 100, baos);
				else bm.compress(CompressFormat.JPEG, 100, baos);
				
				byte[] __bytes = baos.toByteArray();
				System.out.println("SIZEaaaa : " + baos.size());
				int quality = 100;
				while (__bytes.length > maxBytes) {
					quality -= 5;
					if(quality < 5)
						return null;
					baos = null;
					baos = new ByteArrayOutputStream();
					if(name.endsWith(".png") || name.endsWith(".PNG"))
						 bm.compress(CompressFormat.PNG, quality, baos);
					else bm.compress(CompressFormat.JPEG, quality, baos);
					__bytes = null;
					__bytes = baos.toByteArray();
					System.out.println("WHILE : " + quality + " : " + __bytes.length);
				}
				System.out.println("QUALITY : " + quality);
				StringBuffer _buff = new StringBuffer();
				System.out.println("SIZE : " + baos.size());
				for(byte b : __bytes) {
//					System.out.println(b);
					try {
						_buff.append(b);
						_buff.append(" ");
//						System.out.println(_buff.length());
					} catch (OutOfMemoryError e ) {
						e.printStackTrace();
						System.out.println("BUFF LENGTH : " + _buff.length() + " : " + _buff.capacity() );
//						System.gc();
//						bm.recycle();
						return null;
					}
					
				}
				System.out.println("BUFFF : " + _buff.length());
				return _buff.toString();
			} else if (name.endsWith(".txt") || name.endsWith(".TXT") ||
					name.endsWith(".doc") || name.endsWith(".DOC") ||
					name.endsWith(".docx") || name.endsWith(".DOCX") ||
					name.endsWith(".pdf") || name.endsWith(".PDF") ||
					name.endsWith(".meetup") || name.endsWith(".MEETUP")) {
				BufferedReader br = new BufferedReader(new FileReader(path + name));
				StringBuffer buff = new StringBuffer();
				String s = "";
				while ((s = br.readLine()) != null) {
					buff.append(s);
					buff.append("\n");
					
				}
				return buff.toString();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.gc();
			bm.recycle();
		}
		
		return null;
	}
	
	public static boolean writeFile (String name, String content) {
		FileOutputStream fos = null;
		try {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
			File dir = new File(path);
			if(!dir.exists())
				dir.mkdirs();
			
			String __name = "";
			File file = new File(path + name);
			if(file.exists()) {
				int dot = name.lastIndexOf(".");
				__name = name.substring(0, dot) + System.currentTimeMillis() + name.substring(dot, name.length());
			} else {
				__name = name;
			}
			
			fos = new FileOutputStream(path + __name);
			if(name.endsWith(".jpg") || name.endsWith(".JPG") || 
					name.endsWith(".jpeg") || name.endsWith(".JPEG") ||
					name.endsWith(".png") || name.endsWith(".PNG")) {
				String[] ss = content.split(" ");
				byte[] bytes = new byte[ss.length];
				for(int i = 0; i < ss.length; i++) {
					bytes[i] = Byte.parseByte(ss[i]);
				}
				fos.write(bytes);
				fos.close();
			} else if (name.endsWith(".txt") || name.endsWith(".TXT") ||
					name.endsWith(".doc") || name.endsWith(".DOC") ||
					name.endsWith(".docx") || name.endsWith(".DOCX") ||
					name.endsWith(".pdf") || name.endsWith(".PDF") || 
					name.endsWith(".meetup") || name.endsWith(".MEETUP")) {
				fos.write(content.getBytes());
				fos.close();
			}
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(fos != null)
					fos.close();
			} catch (Exception e){}
		}
			
		return false;
	}
	
	public static String[] parsePathFile (Context context, Uri uri) {
		String[] path = new String[2];
		
		try {
			String pathfile = getPath(context, uri);
			if(pathfile == null) {
				String lastPathSegment = uri.getLastPathSegment();
				if(lastPathSegment == null || lastPathSegment.equals("")) {
					return null;
				} else {
					String lastPath = lastPathSegment.substring("primary:".length(), lastPathSegment.length());
					int __index = lastPath.lastIndexOf("/");
					String __path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
					String __name = "";
					if(__index < 0) {
						__name = lastPath;
					} else {
						__path += lastPath.substring(0, __index + 1);
						__name = lastPath.substring(__index + 1, lastPath.length());;
					}
					path[0] = __path;
					path[1] = __name;
				}
			} else {
				int index = pathfile.lastIndexOf("/");
				path[0] = pathfile.substring(0, index + 1);
				path[1] = pathfile.substring(index + 1, pathfile.length());
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(path[1].endsWith(".jpg") || path[1].endsWith(".JPG") || 
				path[1].endsWith(".jpeg") || path[1].endsWith(".JPEG") ||
				path[1].endsWith(".png") || path[1].endsWith(".PNG") ||
				path[1].endsWith(".txt") || path[1].endsWith(".TXT") ||
				path[1].endsWith(".doc") || path[1].endsWith(".DOC") ||
				path[1].endsWith(".docx") || path[1].endsWith(".DOCX") ||
				path[1].endsWith(".pdf") || path[1].endsWith(".PDF")) 
			return path;
		
		return null;
	}
	
	public static String getPath(Context context, Uri uri) throws URISyntaxException {
	    if ("content".equalsIgnoreCase(uri.getScheme())) {
	        String[] projection = { "_data" };
	        Cursor cursor = null;

	        try {
	            cursor = context.getContentResolver().query(uri, projection, null, null, null);
	            int column_index = cursor.getColumnIndexOrThrow("_data");
	            if (cursor.moveToFirst()) {
	                return cursor.getString(column_index);
	            }
	        } catch (Exception e) {
	            // Eat it
	        }
	    } else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}
}
