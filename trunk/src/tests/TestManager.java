package tests;

import java.io.File;

import junit.framework.TestCase;

/***
 * This test class performs basic system checks.
 * For example checking for graph transformation rules .xml files, dependent libraries, etc
 * 
 * @author Huy
 *
 */
public class TestManager extends TestCase {

	// tests that the required UML meta model xmi files exists in the project
	public void testHasUMLMetaModelXMI()
	{
		
		String[] expectedLocations = {"UMLMetamodel/08-05-06.xmi", "UMLMetamodel/08-05-07.xmi"};
		
		boolean hasFiles = true;
		for (String s : expectedLocations)
		{
			File metamodelXMI = new File(s);
			if (!metamodelXMI.exists())
			{
				hasFiles = false;
				break;
			}
			
		}
		assertTrue(hasFiles);		
	}
	
	// tests that the graph transformation rules xml file exists in the project
	public void testHasGTSRulesXML()
	{
		String expectedLocation = "GTSRules.xml";
		File gtsRulesXML = new File(expectedLocation); // double check if we need a try catch block...
		assertTrue(gtsRulesXML.exists());
	}
	
	// test that there exists a UMLGraph.jar file. This is required when generating the
	// javadoc.
	public void testHasUMLGraphJAR()
	{
		
	}
	
	// possible test...
	public void testHasInput()
	{
		
	}
}
