package edu.csupomona.cs585.ibox;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;






import junit.framework.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import edu.csupomona.cs585.ibox.sync.FileSyncManager;
import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;
import edu.csupomona.cs585.ibox.WatchDir;


public class AppIntegrationTest {

	private Drive googleDriveClient;
	
	
	
	
	@Before
	public void initGoogleDriveServices() throws IOException {
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		try{
		GoogleCredential credential = new GoogleCredential.Builder()
		.setTransport(httpTransport)
		.setJsonFactory(jsonFactory)
		.setServiceAccountId("2626844638-u5nvq58q5s71rcpedcvgv23mcokk57rj@developer.gserviceaccount.com")
		.setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
		.setServiceAccountPrivateKeyFromP12File(new File("WahedProject-19aca7eca55f.p12"))
		.build();

		googleDriveClient = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("ibox").build(); 
		}catch(GeneralSecurityException e){
		e.printStackTrace();
		}

		}

	@Test
	public void testIntergration() throws IOException{

		Path dir = Paths.get(System.getProperty("user.home"));
	    Path pathAfterUpdate = Paths.get(System.getProperty("user.home"), "testUpdate.txt");
		String path = dir.toString() + "\\test.txt";
		System.out.println(pathAfterUpdate.toString());
		GoogleDriveFileSyncManager fileSyncManager = new GoogleDriveFileSyncManager(
				googleDriveClient);
		
		ProcessFile process = new ProcessFile(new WatchDir(dir, fileSyncManager));
		process.start();

		File file = new File(path);
		File fileAfterUpdate = new File(pathAfterUpdate.toString());
		file.createNewFile();
		Assert.assertNotNull(fileSyncManager.getFileId("test.txt"));
		file.renameTo(fileAfterUpdate);
		Assert.assertNotNull(fileSyncManager.getFileId("testUpdate.txt"));
		System.out.print("FILE NAME " + file.getName());
		file.delete();
		fileAfterUpdate.delete();
		
		Assert.assertNull(fileSyncManager.getFileId("test..txt"));
		
		process.interrupt();
		

	}
	
}
