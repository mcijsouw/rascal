package org.rascalmpl.library.vis.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.rascalmpl.library.vis.Figure;
import org.rascalmpl.library.vis.FigureApplet;
import org.rascalmpl.library.vis.graphics.GraphicsContext;
import org.rascalmpl.library.vis.graphics.SWTGraphicsContext;
import org.rascalmpl.library.vis.util.Rectangle;

public class OverlapCanvas extends Canvas implements PaintListener {

	private Figure fig;
	private double xOffset;
	private double yOffset;
	
	public OverlapCanvas(Composite parent) {
		super(parent, SWT.NORMAL);
		this.addPaintListener(this);
		setSize(100,100);
		setLocation(0, 0);
	}
	
	public void setOverlap(Figure fig,Rectangle overlap){
		System.out.printf("Setting overlap canvas %s\n",overlap);
		this.fig = fig;
		setSize(FigureApplet.round(overlap.getWidth()),FigureApplet.round(overlap.getHeight()));
		setLocation(FigureApplet.round(overlap.getX()),FigureApplet.round(overlap.getY()));
		xOffset = -overlap.getX();
		yOffset = -overlap.getY();
	}

	@Override
	public void paintControl(PaintEvent e) {
		GraphicsContext gc = new SWTGraphicsContext(e.gc);
		gc.translate(xOffset,yOffset);
		fig.draw(gc);
		e.gc.dispose();
		
	}
	
	

}