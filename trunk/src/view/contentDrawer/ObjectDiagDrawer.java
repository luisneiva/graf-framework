package view.contentDrawer;

import java.util.ArrayList;
import java.util.HashMap;

import model.TransitionAction;
import model.modelTransformer.objectDisplay.DisplayObject;
import model.modelTransformer.objectDisplay.ODAction;
import model.modelTransformer.objectDisplay.ODAttribute;
import model.modelTransformer.objectDisplay.ODEvent;
import model.modelTransformer.objectDisplay.ODLink;
import model.modelTransformer.objectDisplay.ODMethod;
import model.modelTransformer.objectDisplay.ODObject;
import model.modelTransformer.objectDisplay.ObjDiag;
import model.modelTransformer.objectDisplay.ObjectDisplay;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;

import view.ClickableLabel;
import view.View;

/** 
 * Draws object diagrams from ObjDiag class
 * 
 * @author Kevin O'Shea 
 */
public class ObjectDiagDrawer extends ContentDrawer {

	public ObjectDiagDrawer(View view) {
		super(view);
	}

	public void draw(ObjectDisplay objdisplay) {

		//This class only draws object diagrams
		if (!(objdisplay instanceof ObjDiag)) return;

		// get from model the object diagram representation of the graph.
		ObjDiag objDiag = (ObjDiag)objdisplay;
		ArrayList<DisplayObject> objects = objDiag.getODObjs();
		ArrayList<ODLink> odLinks = objDiag.getODLinks();

		if (objects.size()>0 && !(objects.get(0) instanceof ODObject)) return;

		HashMap<ODObject, ObjectDiagFigure> classFigureMap = new HashMap<ODObject, ObjectDiagFigure>();
		for (DisplayObject object : objects) {
			ODObject odObj = (ODObject)object;
			final ObjectDiagFigure classFigure = new ObjectDiagFigure(odObj);

			// draw methods in a fourth compartment of the object diagram boxes
			for(ODMethod method : odObj.getMethods())
			{
				classFigure.addMethod(method.getName());
			}

			for (ODAttribute attr : odObj.getAttributes()) {
				classFigure.addAttribute(attr.getName(), attr.getValue());
			}
			if (odObj.getEventMode()) {
				for (ODEvent event : odObj.getEventPool()) {
					classFigure.addEvent(event.getName(), transListener,
							new TransitionAction(odObj, "AcceptEventAction", event.getOccurence()));
				}
			} else {
				for (ODAction action : odObj.getActionPool()) {
					classFigure.addAction(action.getName(), transListener, action.getEnabled(),
							new TransitionAction(odObj, action.getType(), action.getName()));
				}
			}
			for (ODEvent extevent : odObj.getExternalEvents()) {
				classFigure.addExternalEvent(extevent.getName(), popupListener,
						new TransitionAction(odObj, "ActorSendSignal", extevent.getName()));
			}
			if (!odObj.isAttributesShowing()) classFigure.hideAttributes();
			if (!odObj.isRuntimePoolShowing()) classFigure.hideRuntimePool();
			classFigure.addMouseListener(new MouseListener() {
				public void mousePressed(MouseEvent me) {
					if (me.button == 3) {		//right-click: display popup menu
						Menu menu = view.createPopupMenu(new Point(me.x,me.y));
						classFigure.buildPopupMenu(menu);
					}
				}
				public void mouseReleased(MouseEvent me) {}
				public void mouseDoubleClicked(MouseEvent me) {}
			});
			classFigureMap.put(odObj, classFigure);
			contents.getLayoutManager().setConstraint(
					classFigure,
					new Rectangle(odObj.getLocation().x, odObj.getLocation().y,
							-1, -1));
			contents.add(classFigure);
		}
		// create links
		for (final ODLink odLink : odLinks) {
			PolylineConnection c = new PolylineConnection();
			ChopboxAnchor sourceAnchor = new ChopboxAnchor(classFigureMap
					.get(odLink.getLeftObj()));
			ChopboxAnchor targetAnchor = new ChopboxAnchor(classFigureMap
					.get(odLink.getRightObj()));
			c.setSourceAnchor(sourceAnchor);
			c.setTargetAnchor(targetAnchor);

			// Add labels to the connections
			ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(
					c, true);
			targetEndpointLocator.setUDistance(5); // distance from class box
			targetEndpointLocator.setVDistance(5); // distance from association

			Label targetMultiplicityLabel = new Label(odLink.getLinkLeftLabel());
			c.add(targetMultiplicityLabel, targetEndpointLocator);
			ConnectionEndpointLocator sourceEndpointLocator = new ConnectionEndpointLocator(
					c, false);
			sourceEndpointLocator.setUDistance(5);
			Label sourceMultiplicityLabel = new Label(odLink
					.getLinkRightLabel());
			c.add(sourceMultiplicityLabel, sourceEndpointLocator);
			ConnectionLocator middlelocator = new ConnectionLocator(c,
					ConnectionLocator.MIDDLE);
			middlelocator.setRelativePosition(PositionConstants.NORTH);
			middlelocator.setGap(5);

			ArrayList<ODAction> actions = odLink.getActions();
			for(int n = 0; n < actions.size(); n++) {
				
				ODAction odaction = actions.get(n);
				String newLines = "";
				for(int m = 0; m < n; m++)
					newLines = newLines + "\n";

				final Label relationshipLabel = new ClickableLabel(odLink.getLinkCentreLabel() + "\n" + odaction.getName() + newLines);
				relationshipLabel.addMouseListener(new MouseListener(){
					public void mousePressed(MouseEvent me) {
						relationshipLabel.setText(relationshipLabel.getText() + "1");
					}
					public void mouseReleased(MouseEvent me) {}
					public void mouseDoubleClicked(MouseEvent me) {}
				});

				c.add(relationshipLabel, middlelocator);

			}

			contents.add(c);
		}
	}
}
