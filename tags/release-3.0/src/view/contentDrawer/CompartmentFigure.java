package view.contentDrawer;

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
import org.eclipse.swt.graphics.Font;

import view.ClickableLabel;

public class CompartmentFigure extends Figure {
	
	final Label showHideLabel = new ClickableLabel();
	
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
		showHideLabel.setFont(new Font(null, "Arial", 7, SWT.NORMAL));
		showHideLabel.setForegroundColor(new Color(null,100,100,100));
		showHideLabel.addMouseListener(new MouseListener(){
			public void mousePressed(MouseEvent me) {
				if (showHideLabel.getText().equals("[Hide]")) {
					hide();
				} else if (showHideLabel.getText().equals("[Show]")) {
					show();
				}
			}
			public void mouseReleased(MouseEvent me) {}
			public void mouseDoubleClicked(MouseEvent me) {}
		});
	}
	private List<Figure> attributeList;
	public void hide() {
		attributeList = new ArrayList<Figure>(getChildren());
		if (attributeList.size()==0) return;
		removeAll();
		add(showHideLabel);
		showHideLabel.setText("[Show]");
		setCursor(new Cursor(null, SWT.CURSOR_ARROW));
		revalidate();
	}
	public void show() {
		removeAll();
		for (Figure fig : attributeList) add(fig);
		add(showHideLabel);
		showHideLabel.setText("[Hide]");
		setCursor(new Cursor(null, SWT.CURSOR_ARROW));
		revalidate();
	}
}
