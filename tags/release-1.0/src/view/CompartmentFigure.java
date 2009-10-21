package view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;

///////////////////////////////////////////////////////////////////////////////////////////////
//This class is part of the test view for initial demonstration 
///////////////////////////////////////////////////////////////////////////////////////////////

public class CompartmentFigure extends Figure {
	
	private final Label showHideLabel = new ClickableLabel();
	
	public CompartmentFigure() {
		ToolbarLayout layout = new ToolbarLayout();		//arranges components in a single column
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setStretchMinorAxis(false);
		layout.setSpacing(2);
		setLayoutManager(layout);
		setBorder(new CompartmentFigureBorder());
		setupShowHideLabel();
	}
	
	/** use given layout instead of ToolbarLayout */
	public CompartmentFigure(GridLayout layout) {
		layout.horizontalSpacing = 8;
		layout.verticalSpacing = 0;
		setLayoutManager(layout);
		setBorder(new CompartmentFigureBorder() {
			public Insets getInsets(IFigure figure) {
				return new Insets(1,0,0,0);
			}
		});
		setupShowHideLabel();
	}

	public class CompartmentFigureBorder extends AbstractBorder {
		public Insets getInsets(IFigure figure) {
			return new Insets(5, 5, 5, 5);
		}					//(1,0,0,0) (with the images)

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(),
					tempRect.getTopRight());
		}
	}
	
	public void add(IFigure figure, Object constraint, int index) {
		super.add(figure, constraint, index);
		if (getChildren().contains(showHideLabel)) {
			remove(showHideLabel);
		}
		super.add(showHideLabel, constraint, index);
	}
	
	private void setupShowHideLabel() {
		showHideLabel.setText("[Hide]");
		showHideLabel.setFont(UMLClassFigure.classTextFont);
		showHideLabel.setForegroundColor(new Color(null,100,100,100));
		showHideLabel.addMouseListener(new MouseListener(){
			private List<Figure> attributeList;
			public void mousePressed(MouseEvent me) {
				if (showHideLabel.getText().equals("[Hide]")) {
					attributeList = new ArrayList<Figure>(getChildren());
					removeAll();
					add(showHideLabel);
					showHideLabel.setText("[Show]");
					setCursor(new Cursor(null, SWT.CURSOR_ARROW));
				} else if (showHideLabel.getText().equals("[Show]")) {
					removeAll();
					add(showHideLabel);
					for (Figure fig : attributeList) {
						add(fig);
					}
					add(showHideLabel);
					showHideLabel.setText("[Hide]");
					setCursor(new Cursor(null, SWT.CURSOR_ARROW));
				}
				revalidate();
			}
			public void mouseReleased(MouseEvent me) {}
			public void mouseDoubleClicked(MouseEvent me) {}
		});
		//add(showHideLabel);
	}
}
