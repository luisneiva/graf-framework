package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import agg.attribute.facade.impl.DefaultInformationFacade;
import agg.attribute.handler.AttrHandler;
import agg.convert.ConverterXML;
import agg.util.XMLHelper;
import agg.xt_basis.BaseFactory;
import agg.xt_basis.CompletionStrategySelector;
import agg.xt_basis.DefaultGraTraImpl;
import agg.xt_basis.GraGra;
import agg.xt_basis.GraTra;
import agg.xt_basis.GraTraEventListener;
import agg.xt_basis.Graph;
import agg.xt_basis.LayeredGraTraImpl;
import agg.xt_basis.MorphCompletionStrategy;
import agg.xt_basis.PriorityGraTraImpl;
import agg.xt_basis.Rule;
import agg.xt_basis.RuleSequencesGraTraImpl;
import agg.xt_basis.Type;

/**
 * Combines rule and graph with an AGG implementation
 * 
 * TODO this class can probably be deleted
 * 
 * 
 * @author Oscar Wood
 */
public class AGG {

	final private Type nodeType;
	final private Type edgeType;
	final private AttrHandler javaHandler;

	private GraGra gg;

	public AGG() {

		GraGra graphGrammar;

		graphGrammar = BaseFactory.theFactory().createGraGra();
		graphGrammar.setName("MyGraGra");

		nodeType = graphGrammar.createType(true);
		edgeType = graphGrammar.createType(true);
		nodeType.setStringRepr("");
		nodeType.setAdditionalRepr("[NODE]");
		edgeType.setStringRepr("");
		edgeType.setAdditionalRepr("[EDGE]");

		javaHandler = DefaultInformationFacade.self().getJavaHandler();
		nodeType.getAttrType().addMember(javaHandler,"String", "name");
		edgeType.getAttrType().addMember(javaHandler,"String", "name");

	}

	public AGG(String fileName, ArrayList<AGGAssignment> params) {
		this();
		gg = load(fileName);

		System.out.println(fileName + " contains " + gg.getCountOfGraphs() + " graphs and " + gg.getEnabledRules().size() + " rules");

		//setInputParameters(params);

	}

	
	public Graph getGraph() {
		return gg.getGraph();
	}

	public GraGra getGraGra() {
		return gg;
	}
	
	/*public void myTransform(GraTraEventListener l) {

		RuleSequencesGraTraImpl gt = new RuleSequencesGraTraImpl();

		gt.setGraGra(gg);
		gt.apply(gg.getRule(0));

	}*/
	

	
	
	/*
	 * the stuff below is modified from AGGBasicTest.java
	 */

	public void transform(GraTraEventListener l) {
		Boolean priority = false;
		Boolean layered = false;
		Boolean ruleSequence = false;
		GraTra gratra = new DefaultGraTraImpl();

		if(gg == null) return;
		
		if(gg.getGraTraOptions().contains("priority")) {
			gratra = new PriorityGraTraImpl();
			priority = true;
			System.out.println("Transformation by rule priority ...");
		}
		else if(gg.getGraTraOptions().contains("layered")) {
			gratra = new LayeredGraTraImpl();
			layered = true;
			System.out.println("Layered transformation ...");
		}
		else if(gg.getGraTraOptions().contains("ruleSequence")){
			gratra = new RuleSequencesGraTraImpl();
			ruleSequence = true;
			System.out.println("Transformation by rule sequences ...");
		}
		else {
			gratra = new DefaultGraTraImpl();
			System.out.println("Transformation  non-deterministically ...");
		}

		gratra.addGraTraListener(l);
		gratra.setGraGra(gg);
		gratra.setHostGraph(gg.getGraph());
		gratra.enableWriteLogFile(false);

		MorphCompletionStrategy 
		strategy = CompletionStrategySelector.getDefault();

		if(gg.getGraTraOptions().isEmpty()) {
			gg.setGraTraOptions(strategy);
			gratra.setCompletionStrategy(strategy);
		}
		else {
			if(gg.getGraTraOptions().contains("showGraphAfterStep"))
				gg.getGraTraOptions().remove("showGraphAfterStep");
			gratra.setGraTraOptions(gg.getGraTraOptions());
			System.out.println("Options:  "+gg.getGraTraOptions());
			System.out.println();
		}

		gg.destroyAllMatches();

		if(priority)
			((PriorityGraTraImpl)gratra).transform();
		else if(layered)
			((LayeredGraTraImpl)gratra).transform();
		else if(ruleSequence)
			((RuleSequencesGraTraImpl)gratra).transform();
		else
			((DefaultGraTraImpl)gratra).transform();
	}


	private static XMLHelper h = new XMLHelper(); 

	public static GraGra load(String fileName) {
		//System.out.println(fileName.endsWith(".ggx"));
		if (fileName.endsWith(".ggx")) {   
			h.read_from_xml(fileName);

			// create a gragra
			GraGra gra = (GraGra) BaseFactory.theFactory().createGraGra();
			h.getTopObject(gra);
			gra.setFileName(fileName);
			return gra;
		}
		else return null;
	}

	public static Graph importGraph(String filename)
	{
		//System.out.println(filename.endsWith(".gxl"));
		if(filename.endsWith(".ggx"))
			return importGraphGGX(filename);  
		else if(filename.endsWith(".gxl"))
			return importGraphGXL(filename);    

		else if(filename.endsWith(".gtxl"))	   
			return importGraphGTXL(filename);
		else if(filename.endsWith(".ecore"))	   
			return importGraphOMONDO_XMI(filename);
		else{
			String str = "Import failed!   < "+filename+" >  should be < .ggx > , < .gxl >  or  < .ecore >  file.";
			System.out.println(str);
			return null;
		}
	}

	private static Graph importGraphGGX(String filename){ 
		//System.out.println("importGraphGGX: "+filename);	 
		GraGra impGra = load(filename);
		if(impGra != null){
			//save(impGra, "_outImportGrammar.ggx");  //test
			return impGra.getGraph();
		}
		else
			return null;  
	}

	private static Graph importGraphGTXL(String filename)
	{
		return null;
	}


	private static Graph importGraphGXL(String filename){  
		String fd = ".";
		String fn = filename;
		String fnOut = "";
		File gxldtd = null;
		File gtsdtd = null;
		File source = null;
		File layout = null;
		String error = "";

		File f = new File(fn);
		if(f.exists()){
			if(f.isFile())
				fd = f.getParent();
		}
		if(fd != null)
			fd = fd+File.separator;
		else
			fd = "."+File.separator;
		//System.out.println("dir:  "+fd);
		//System.out.println("file:  "+fn);


		//  if(XMLHelper.hasGermanSpecialCh(fn)){
		//	  System.out.println("File name:  "+fn);
		//   	 System.out.println("\nRead file name exception occurred! "
		//     			  +"\nMaybe the German umlaut like ä, ö, ü or ß were used. "
		//    			  +"\nPlease replace it by ae, oe, ue or ss "
		//   			  +"\nand try again.");
		//	return null;
		//  }


		ConverterXML converter = new ConverterXML();
		fnOut = fn.substring(0, fn.length()-4) + "_gxl.ggx";
		source = converter.copyFile(fd, "gxl2ggx.xsl");
		gxldtd = converter.copyFile(fd, "gxl.dtd");
		gtsdtd = converter.copyFile(fd, "gts.dtd");
		layout = converter.copyFile(fd, "agglayout.dtd");
		if(source == null)
		{
			error = "Import is failed! File   < gxl2ggx.xsl >  is not found.";
			return null;
		}
		else if(gxldtd == null)
		{
			error = "Import is failed! File   < gxl.dtd >  is not found.";
			return null;
		}
		else if(gtsdtd == null)
		{
			error = "Import is failed! File   < gts.dtd >  is not found.";
			return null;
		}
		else if(layout == null)
		{
			error = "Import is failed! File   < agglayout.dtd >  is not found.";
			return null;
		}

		String in = fn;
		String out = fnOut;
		GraGra impGra = null;
		if(converter.gxl2ggx(in, out, fd+"gxl2ggx.xsl"))
		{
			if(out.endsWith(".ggx")) 
			{
				XMLHelper h = null;
				h = new XMLHelper();
				h.read_from_xml(out);
				impGra = (GraGra) h.getTopObject(BaseFactory.theFactory().createGraGra());

				if(impGra != null)
					return impGra.getGraph();
			}
		}
		error = "Import is failed! Please check format of the  GXL  file.";
		System.out.println(error);
		return null;
	}

	private static Graph importGraphOMONDO_XMI(String filename){  
		String fd = ".";
		String fn = filename;
		String fnOut = "";
		File gxldtd = null;
		File gtsdtd = null;
		File source = null;
		File layout = null;
		File omondo = null;
		String error = "";

		File f = new File(fn);
		if(f.exists()){
			if(f.isFile())
				fd = f.getParent();
		}
		if(fd != null)
			fd = fd+File.separator;
		else
			fd = "."+File.separator;
		//System.out.println("dir:  "+fd);
		//System.out.println("file:  "+fn);

		ConverterXML converter = new ConverterXML();	  

		fnOut = fn.substring(0, fn.length()-6) + "_ecore.ggx";
		source = converter.copyFile(fd, "gxl2ggx.xsl");
		gxldtd = converter.copyFile(fd, "gxl.dtd");
		gtsdtd = converter.copyFile(fd, "gts.dtd");	  
		layout = converter.copyFile(fd, "agglayout.dtd");
		omondo = converter.copyFile(fd, "omondoxmi2gxl.xsl");

		if(source == null){
			error = "Import failed! File   < gxl2ggx.xsl >  is not found.";
			return null;
		}
		else if(gxldtd == null){
			error = "Import failed! File   < gxl.dtd >  is not found.";
			return null;
		}
		else if(gtsdtd == null){
			error = "Import failed! File   < gts.dtd >  is not found.";
			return null;
		}
		else if(layout == null){
			error = "Import failed! File   < agglayout.dtd >  is not found.";
			return null;
		}
		else if(omondo == null){
			error = "Import failed! File   < omondoxmi2gxl.xsl >  is not found.";
			return null;
		}

		String in = fn;
		String out = fnOut;
		GraGra impGra = null;
		if(converter.omondoxmi2ggx(in, out, fd+"omondoxmi2gxl.xsl", fd+"gxl2ggx.xsl")){
			if(out.endsWith(".ggx")) {
				XMLHelper h = null;
				h = new XMLHelper();
				h.read_from_xml(out);
				impGra = (GraGra) h.getTopObject(BaseFactory.theFactory().createGraGra());

				if(impGra != null){            	  
					return impGra.getGraph();
				}
			}
		}
		error = "Import failed! Please check format of the  GXL  file.";
		System.out.println(error);
		return null;
	}

}
