package net.sf.jlayercheck.ant;

import java.io.File;

import junit.framework.TestCase;

/**
 * Test case for ant task.
 * 
 * @author timo
 */
public class JLCTaskTest extends TestCase {

	public void testTask() throws Exception {

		// task to use
		JLCTask task = new JLCTask();

		// use config file from target dir
		File config = new File(System.getProperty("user.dir"),
				"target/classes/" + JLCTask.CONFIG);
		task.setConfig(config);

		// outDir is in target dir
		File outDir = new File(System.getProperty("user.dir"),
				"target/" + JLCTask.OUTDIR);
		task.setOutDir(outDir);

		task.execute();
	}

}
