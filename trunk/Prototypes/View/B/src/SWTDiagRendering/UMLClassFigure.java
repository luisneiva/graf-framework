package SWTDiagRendering;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class UMLClassFigure extends Figure {
	public static Color classColor = new Color(null, 255, 255, 206);
	private CompartmentFigure attributeFigure = new CompartmentFigure();
	private CompartmentFigure eventPoolFigure = new CompartmentFigure(new GridLayout(2,true));
	
	static final Font classNameFont = new Font(null, "Arial", 10, SWT.BOLD);
	static final Font classTextFont = new Font(null, "Arial", 7, SWT.NORMAL);
	
	public UMLClassFigure(String name) {
		ToolbarLayout layout = new ToolbarLayout();	//arranges components in a single column
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(classColor);
		setOpaque(true);
		
		Label nameLabel = new Label(name);
		nameLabel.setFont(classNameFont);
		add(nameLabel);					//(including the name) (a Label is a Figure)
		add(new Label("  {" + "current_state" + "}  "));
		add(attributeFigure);	//each of these are seperate figures (each in their own 'row' of the UMLClassFigure)
		add(eventPoolFigure);
		
		class MyMouseListener implements MouseListener {
			boolean mouseDown = false;
			int relStartX=0, relStartY=0;
			public void mousePressed(MouseEvent me) {
				mouseDown = true;
				relStartX = me.x - getBounds().x;
				relStartY = me.y - getBounds().y;
			}
			public void mouseReleased(MouseEvent me) {
				mouseDown = false;
			}
			public void mouseDoubleClicked(MouseEvent me) {}
		}
		final MyMouseListener mouseDownListener = new MyMouseListener();
		addMouseListener(mouseDownListener);
		addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent me) {
				if (mouseDownListener.mouseDown) {
					getParent().getLayoutManager().setConstraint(UMLClassFigure.this,
							new Rectangle(me.getLocation().x - mouseDownListener.relStartX,
									me.getLocation().y - mouseDownListener.relStartY,
									-1,  //getBounds().width,
									-1)  //getBounds().height)
					);
					revalidate();
				}
			}
			public void mouseEntered(MouseEvent me) {}
			public void mouseExited(MouseEvent me) {}
			public void mouseMoved(MouseEvent me) {}
			public void mouseHover(MouseEvent me) {}
		});
		
		System.out.println("The class figure size is apparently: " + getSize());
		new Thread() {public void run() {try{Thread.sleep(1500);}catch(Exception e){}
		System.out.println("The class figure size is actually: " + getSize());}}.start();
	}
	
	public void addAttribute(String name) {
		Label label = new Label(name);
		label.setFont(classTextFont);
		attributeFigure.add(label);
	}
	
	public void addEvent(String name) {
		CompartmentFigure.ClickableLabel label = new CompartmentFigure.ClickableLabel(name);
		label.setFont(classTextFont);
		eventPoolFigure.add(label);
	}
}
