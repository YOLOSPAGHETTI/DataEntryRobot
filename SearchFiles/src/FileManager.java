import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {
	
	public FileManager() {
	}
	
	public ArrayList<String> searchFiles(String dir, String ext, String search, boolean starts) {
		ArrayList<String> validFiles = new ArrayList<String>();
		File dirFile = new File(dir);
		ArrayList<File> files = getFiles(dirFile, ext);
		for(File file : files) {
			if(read(file.getAbsolutePath(), search, starts)) {
				validFiles.add(file.getName());
			}
		}
		return validFiles;
	}
	
	private boolean read(String name, String search, boolean starts) {
		boolean found = false;
		try {
			FileReader reader = new FileReader(name);
			BufferedReader bufferedReader = new BufferedReader(reader);
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				if(starts) {
					if(line.startsWith(search)) {
						found = true;
						break;
					}
				}
				else {
					if(line.contains(search)) {
						found = true;
						break;
					}
				}
			}
			reader.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return found;
	}
	
	// Gets the files from a folder
    private ArrayList<File> getFiles(File dir, String ext) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getFiles(file, ext));
                } else {
                    String name = file.getName();
                    if (name.endsWith(ext)) {
                        inFiles.add(file);
                    }
                }
            }
        }
        return inFiles;
    }
}
