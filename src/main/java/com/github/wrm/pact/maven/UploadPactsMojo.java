package com.github.wrm.pact.maven;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.wrm.pact.domain.PactFile;
import com.github.wrm.pact.repository.RepositoryProvider;

/**
 * Verifies all pacts that can be found for this provider
 *
 * @goal upload-pacts
 * 
 * @phase test
 */
public class UploadPactsMojo extends AbstractPactsMojo {

	/**
	 * url of pact broker
	 * 
	 * @parameter
	 */
	private String brokerUrl;

	/**
	 * Location of pacts
	 * 
	 * @parameter expression="target/pacts"
	 */
	private String pacts;

	public void execute() throws MojoExecutionException, MojoFailureException {

		File folder = new File(pacts);
		getLog().info("loading pacts from " + pacts);
		try {
			List<PactFile> pactList = readPacts(folder);
			RepositoryProvider provider = createRepositoryProvider(brokerUrl);
			provider.uploadPacts(pactList);
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to read pacts", e);
		}
	}



	private List<PactFile> readPacts(File folder) throws FileNotFoundException {
		List<PactFile> pacts = new LinkedList<PactFile>();
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			String fileName = file.getName();
			if (fileName.endsWith("json")) {
				PactFile pactFile = PactFile.readPactFile(file);
				pacts.add(pactFile);
				getLog().info("found pact file: " + fileName);
			}
		}
		return pacts;
	}

	
}