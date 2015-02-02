package edu.csupomona.cs585.ibox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;







import edu.csupomona.cs585.ibox.sync.FileSyncManager;
import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;
import static org.mockito.Mockito.*;
import junit.framework.TestCase;


/**
 * Placeholder for unit test
 */


public class AppUnitTest extends TestCase {

   
    public void testApp() throws IOException
    {
    	
	    Path dir = Paths.get(System.getProperty("user.home"));
	    Path pathBU = Paths.get(System.getProperty("user.home"), "test.txt");
	    Path pathAU = Paths.get(System.getProperty("user.home"), "testUpdate.txt");
		String path = pathBU.toString();
	   
		com.google.api.services.drive.model.File file = new com.google.api.services.drive.model.File();
		FileSyncManager fileSyn = mock(GoogleDriveFileSyncManager.class);		
		WatchDir watch = new WatchDir(dir,fileSyn);

		ProcessFile processFile = new ProcessFile(watch);
		processFile.start();
		File fileBU = new File(path);
		fileBU.createNewFile();	
		Files.move(pathBU, pathBU.resolveSibling("testUpdate.txt"));
        FileWriter fileWriter = new FileWriter(path ,true);
        BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
        fileWriter.append("Wahed Alharbi");
        bufferFileWriter.close();
		File fileAU = new File (pathAU.toString());
		fileBU.delete();
		fileAU.delete();
		
		verify(fileSyn, atLeastOnce()).addFile(fileBU);
		verify(fileSyn).updateFile(fileAU);
		verify(fileSyn).deleteFile(fileBU);
		
		processFile.interrupt();
    }
}

class ProcessFile extends Thread {

	WatchDir watch;
	
	public ProcessFile( WatchDir watchDir){
    	watch = watchDir;
    	
    }
    public void run() {
    	while (!this.isInterrupted()) {
    		watch.processEvents();
    	}
		
    }
    
}
