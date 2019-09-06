package com.ec.survey.tools;

import java.io.File;
import java.io.IOException;

public class FileUtils {
	public static String cleanFilename(String name) {
	    return name.replaceAll("[^a-zA-Z0-9.-]", "_").replaceAll("\\.\\.", "_");
	}
	
	public static void delete(java.io.File f) throws IOException {
		if (f.exists())
		{
		  if (f.isDirectory()) {
		    File[] listFiles = f.listFiles();
		    if (listFiles != null) {
				for (java.io.File c : listFiles)
				      delete(c);
		    }
		  }
		  f.delete();
		}
	}
}
