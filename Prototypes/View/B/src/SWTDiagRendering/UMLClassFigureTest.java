package SWTDiagRendering;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class UMLClassFigureTest {
	
	public static void main(String args[]) {
		Display d = new Display();
		final Shell shell = new Shell(d);
		shell.setSize(650, 420);
		shell.setText("UMLClassFigure Test");
		LightweightSystem lws = new LightweightSystem(shell);
		Figure contents = GiveMeTheFigure();
		
		lws.setContents(contents);
		shell.open();
		while (!shell.isDisposed())
			while (!d.readAndDispatch())
				d.sleep();
	}
 
 public static Figure GiveMeTheFigure() {
	Figure contents = new Figure();
	final XYLayout contentsLayout = new XYLayout();
	contents.setLayoutManager(contentsLayout);
	
	final UMLClassFigure classFigure = new UMLClassFigure(":Table");
	final UMLClassFigure classFigure2 = new UMLClassFigure(":Column");
	final UMLClassFigure classFigure3 = new UMLClassFigure(":Column");
	
	classFigure.addAttribute("columns");
	classFigure.addAttribute("rows");
	classFigure.addEvent("my_event_1");
	classFigure.addEvent("my_event_2");
	classFigure.addEvent("my_event_3");
	classFigure.addEvent("my_event_4");
	classFigure.addEvent("my_event_5");
	classFigure.addEvent("my_event_6");
	classFigure.addEvent("my_event_7");
	classFigure.addEvent("my_event_8");
	classFigure.addEvent("my_event_9");
	classFigure.addEvent("my_event_10");
	classFigure2.addAttribute("columnID = 5");
	classFigure2.addAttribute("width = 20");
	classFigure2.addEvent("event_1");
	classFigure2.addEvent("event_2");
	classFigure3.addAttribute("columnID = 7");
	classFigure3.addAttribute("width = 20");
	classFigure3.addEvent("event_1");
	classFigure3.addEvent("event_2");
	
	//set locations of the figures
	contentsLayout.setConstraint(classFigure, new Rectangle(100,150,-1,-1));
	contentsLayout.setConstraint(classFigure2, new Rectangle(300, 240, -1, -1));
	contentsLayout.setConstraint(classFigure3, new Rectangle(410, 140, -1, -1));
	
	// create links
	PolylineConnection c1 = new PolylineConnection();
	ChopboxAnchor sourceAnchor1 = new ChopboxAnchor(classFigure);
	ChopboxAnchor targetAnchor1 = new ChopboxAnchor(classFigure2);
	c1.setSourceAnchor(sourceAnchor1);
	c1.setTargetAnchor(targetAnchor1);
	PolylineConnection c2 = new PolylineConnection();
	ChopboxAnchor sourceAnchor2 = new ChopboxAnchor(classFigure);
	ChopboxAnchor targetAnchor2 = new ChopboxAnchor(classFigure3);
	c2.setSourceAnchor(sourceAnchor2);
	c2.setTargetAnchor(targetAnchor2);
	
	Label notes = new Label("Notes to self:\n" +
			"- Editor? (not actually 'editing' model, although possible additional functionality?)\n" +
			"- Detach event pools?\n" +
			"- **Idea**: ignore any changes occurring to hidden elements (ie no need for choice of path since obviously not interested!)\n" +
			"- Problem: Do we also want to hide/show ALL of the attributes/events?\n" +
			"- Solution: Perhaps make just one [Edit] button per box, and then bring up lots of checkboxes etc? - much more flexible.\n" +
			"     -> But would this work with events? Can we divide it up into eg attribute changes, signals, etc and choose from those?");
	contentsLayout.setConstraint(notes, new Rectangle(10,10,-1,-1));
	contents.add(notes);
	contents.add(c1);
	contents.add(c2);
	contents.add(classFigure);
	contents.add(classFigure2);
	contents.add(classFigure3);
	
	return contents;
 }
}

/**
 * A test class to display a UMLFigure
 */
/*public class UMLClassFigureTest {
 public static void main(String args[]){
	Display d = new Display();
	final Shell shell = new Shell(d);
	shell.setSize(400, 400);
	shell.setText("UMLClassFigure Test");
	LightweightSystem lws = new LightweightSystem(shell);
	Figure contents = new Figure();
	XYLayout contentsLayout = new XYLayout();
	contents.setLayoutManager(contentsLayout);
	
	Font classFont = new Font(null, "Arial", 12, SWT.BOLD);
	Label classLabel1 = new Label("Table", new Image(d, 
		UMLClassFigureTest.class.getResourceAsStream("class_obj.gif")));
	classLabel1.setFont(classFont);
	
	Label classLabel2 = new Label("Column", new Image(d, 
	        UMLClassFigureTest.class.getResourceAsStream("class_obj.gif")));
	classLabel2.setFont(classFont);
	
	final UMLClassFigure classFigure = new UMLClassFigure(classLabel1);
	final UMLClassFigure classFigure2 = new UMLClassFigure(classLabel2);
	
	Label attribute1 = new Label("columns: Column[]", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("field_private_obj.gif")));
	Label attribute2 = new Label("rows: Row[]", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("field_private_obj.gif")));
	Label attribute3 = new Label("columnID: int", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("field_private_obj.gif")));
	Label attribute4 = new Label("items: List", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("field_private_obj.gif")));

	classFigure.getAttributesCompartment().add(attribute1);
	classFigure.getAttributesCompartment().add(attribute2);
	classFigure2.getAttributesCompartment().add(attribute3);
	classFigure2.getAttributesCompartment().add(attribute4);

	Label method1 = new Label("getColumns(): Column[]", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("methpub_obj.gif")));
	Label method2 = new Label("getRows(): Row[]", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("methpub_obj.gif")));
	Label method3 = new Label("getColumnID(): int", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("methpub_obj.gif")));
	Label method4 = new Label("getItems(): List", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("methpub_obj.gif")));

	classFigure.getMethodsCompartment().add(method1);
	classFigure.getMethodsCompartment().add(method2);
	classFigure2.getMethodsCompartment().add(method3);
	classFigure2.getMethodsCompartment().add(method4);
					
	contentsLayout.setConstraint(classFigure, new Rectangle(10,10,-1,-1));
	contentsLayout.setConstraint(classFigure2, new Rectangle(200, 200, -1, -1));
	
	// Creating the connection
	PolylineConnection c = new PolylineConnection();
	ChopboxAnchor sourceAnchor = new ChopboxAnchor(classFigure);
	ChopboxAnchor targetAnchor = new ChopboxAnchor(classFigure2);
	c.setSourceAnchor(sourceAnchor);
	c.setTargetAnchor(targetAnchor);
	
	// Creating the decoration
	PolygonDecoration decoration = new PolygonDecoration();
	PointList decorationPointList = new PointList();
	decorationPointList.addPoint(0,0);
	decorationPointList.addPoint(-2,2);
	decorationPointList.addPoint(-4,0);
	decorationPointList.addPoint(-2,-2);
	decoration.setTemplate(decorationPointList);
	c.setSourceDecoration(decoration);
	
	// Adding labels to the connection
	ConnectionEndpointLocator targetEndpointLocator = 
	        new ConnectionEndpointLocator(c, true);
	targetEndpointLocator.setVDistance(15);
	Label targetMultiplicityLabel = new Label("1..*");
	c.add(targetMultiplicityLabel, targetEndpointLocator);*

	ConnectionEndpointLocator sourceEndpointLocator = 
		new ConnectionEndpointLocator(c, false);
	sourceEndpointLocator.setVDistance(15);
	Label sourceMultiplicityLabel = new Label("1");
	c.add(sourceMultiplicityLabel, sourceEndpointLocator);

	ConnectionEndpointLocator relationshipLocator = 
		new ConnectionEndpointLocator(c,true);
	relationshipLocator.setUDistance(10);
	relationshipLocator.setVDistance(-20);
	Label relationshipLabel = new Label("contains");
	c.add(relationshipLabel,relationshipLocator);

	contents.add(classFigure);
	contents.add(classFigure2);
	contents.add(c);
	
	lws.setContents(contents);
	shell.open();
	while (!shell.isDisposed())
		while (!d.readAndDispatch())
			d.sleep();
 }
}
*/