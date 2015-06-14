/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

/*
 * Drawable.java
 *
 * Created on 13. Oktober 2001, 17:40
 */

package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.StringUtil;

/**
 * 
 * @author Markus
 */
public abstract class Drawable extends DrawableND {

	private boolean forceNoFill;

	/**
	 * Default stroke for this drawable
	 */
	protected GBasicStroke objStroke = EuclidianStatic.getDefaultStroke();
	/**
	 * Stroke for this drawable in case referenced geo is selected
	 */
	protected GBasicStroke selStroke = EuclidianStatic
			.getDefaultSelectionStroke();
	/**
	 * Stroke for decorations; always full
	 */
	protected GBasicStroke decoStroke = EuclidianStatic.getDefaultStroke();

	private int lineThickness = -1;
	private int lineType = -1;

	/**
	 * View in which this is drawn
	 */
	protected EuclidianView view;

	/**
	 * Referenced GeoElement
	 */
	protected GeoElement geo;
	/** x-coord of the label */
	public int xLabel;
	/** y-coord of the label */
	public int yLabel;
	/** for Previewables */
	int mouseX;
	/** for Previewables */
	int mouseY;
	/** label Description */
	public String labelDesc;
	private String oldLabelDesc;
	private boolean labelHasIndex = false;
	/** for label hit testing */
	protected GRectangle labelRectangle = AwtFactory.prototype.newRectangle(0,
			0);
	/**
	 * Stroked shape for hits testing of conics, loci ... with alpha = 0
	 */
	protected GShape strokedShape;
	/**
	 * Stroked shape for hits testing of hyperbolas
	 */
	protected GShape strokedShape2;

	private GArea shape;

	private int lastFontSize = -1;

	/** tracing */
	protected boolean isTracing = false;

	private ArrayList<GPaint> hatchPaint = null;

	// boolean createdByDrawList = false;

	@Override
	public abstract void update();

	/**
	 * Draws this drawable to given graphics
	 * 
	 * @param g2
	 *            graphics
	 */
	public abstract void draw(GGraphics2D g2);

	/**
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @return true if hit
	 */
	public abstract boolean hit(int x, int y, int hitThreshold);

	/**
	 * @param rect
	 *            rectangle
	 * @return true if the whole drawable is inside
	 */
	public abstract boolean isInside(GRectangle rect);

	/**
	 * @param rect
	 *            rectangle
	 * @return true if a part of this Drawable is within the rectangle
	 */
	public boolean intersectsRectangle(GRectangle rect) {
		GArea s = getShape();
		if (s == null) {
			return false;
		}
		if (isFilled()) {
			return s.intersects(rect);
		}
		return s.intersects(rect) && !s.contains(rect);
	}

	@Override
	public abstract GeoElement getGeoElement();

	/**
	 * @param geo
	 *            referenced geo
	 */
	public abstract void setGeoElement(GeoElement geo);

	@Override
	public double getxLabel() {
		return xLabel;
	}

	@Override
	public double getyLabel() {
		return yLabel;
	}

	/**
	 * Updates font size
	 */
	public void updateFontSize() {
		// do nothing, overriden in drawables
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 * 
	 * @return null when this Drawable is infinite or undefined
	 */
	public GRectangle getBounds() {
		return null;
	}

	/**
	 * Draws label of referenced geo
	 * 
	 * @param g2
	 *            graphics
	 */
	public final void drawLabel(GGraphics2D g2) {
		if (labelDesc == null)
			return;
		String label = labelDesc;

		// stripping off helper syntax from captions
		// assuming that non-caption labels will not contain
		// that helper syntax anyway
		int ind = label.indexOf("%style=");
		if (ind > -1) {
			label = label.substring(0, ind);
		}

		GFont oldFont = null;

		// allow LaTeX caption surrounded by $ $
		if ((label.charAt(0) == '$') && label.endsWith("$")
				&& label.length() > 1) {
			boolean serif = true; // nice "x"s
			if (geo.isGeoText())
				serif = ((GeoText) geo).isSerifFont();
			int offsetY = 10 + view.getFontSize(); // make sure LaTeX labels
													// don't go
													// off bottom of screen
			App app = view.getApplication();
			GDimension dim = app.getDrawEquation().drawEquation(
					geo.getKernel().getApplication(), geo, g2, xLabel,
					yLabel - offsetY, label.substring(1, label.length() - 1),
					g2.getFont(), serif, g2.getColor(), g2.getBackground(),
					true, false, null);
			labelRectangle.setBounds(xLabel, yLabel - offsetY, dim.getWidth(),
					dim.getHeight());
			return;
		}

		// label changed: check for bold or italic tags in caption
		if (oldLabelDesc != labelDesc || (labelDesc.charAt(0) == '<')) {
			boolean italic = false;

			// support for bold and italic tags in captions
			// must be whole caption
			if (label.startsWith("<i>") && label.endsWith("</i>")) {
				oldFont = g2.getFont();

				// use Serif font so that we can get a nice curly italic x
				g2.setFont(view.getApplication().getFontCommon(true,
						oldFont.getStyle() | GFont.ITALIC, oldFont.getSize()));
				label = label.substring(3, label.length() - 4);
				italic = true;
			}

			if (label.startsWith("<b>") && label.endsWith("</b>")) {
				oldFont = g2.getFont();

				g2.setFont(g2.getFont().deriveFont(
						GFont.BOLD + (italic ? GFont.ITALIC : 0)));
				label = label.substring(3, label.length() - 4);
			}
		}

		// no index in label: draw it fast
		int fontSize = g2.getFont().getSize();
		if (oldLabelDesc == labelDesc && !labelHasIndex
				&& lastFontSize == fontSize) {
			g2.drawString(label, xLabel, yLabel);
			labelRectangle.setLocation(xLabel, yLabel - fontSize);
		} else { // label with index or label has changed:
					// do the slower index drawing routine and check for indices
			oldLabelDesc = labelDesc;

			GPoint p = EuclidianStatic.drawIndexedString(view.getApplication(),
					g2, label, xLabel, yLabel, isSerif(), false);
			labelHasIndex = p.y > 0;
			labelRectangle.setBounds(xLabel, yLabel - fontSize, p.x, fontSize
					+ p.y);
			lastFontSize = fontSize;
		}

		if (oldFont != null)
			g2.setFont(oldFont);
	}

	/**
	 * Adapts xLabel and yLabel to make sure that the label rectangle fits fully
	 * on screen.
	 * 
	 * @param Xmultiplier
	 *            multiply the x size by it to ensure fitting (default: 1.0)
	 * @param Ymultiplier
	 *            multiply the y size by it to ensure fitting (default: 1.0)
	 */
	private void ensureLabelDrawsOnScreen(double Xmultiplier, double Ymultiplier) {
		// draw label and
		int widthEstimate = (int) labelRectangle.getWidth();
		int heightEstimate = (int) labelRectangle.getHeight();

		GFont font = view.getApplication().getPlainFontCommon();

		if (oldLabelDesc != labelDesc || lastFontSize != font.getSize()) {
			if (labelDesc.startsWith("$")) {
				// for LaTeX we need proper repaint
				drawLabel(view.getTempGraphics2D(font));
				widthEstimate = (int) labelRectangle.getWidth();
				heightEstimate = (int) labelRectangle.getHeight();
			} else {
				// if we use name = value, this may still be called pretty
				// often.
				// Hence use heuristic here instead of measurement
				heightEstimate = (int) (StringUtil.prototype.estimateHeight(labelDesc,
						font) * Ymultiplier);
				widthEstimate = (int) (StringUtil.prototype.estimateLengthHTML(labelDesc,
						font) * Xmultiplier);
			}
		}
		// make sure labelRectangle fits on screen horizontally
		if (xLabel < 3)
			xLabel = 3;
		else
			xLabel = Math.min(xLabel, view.getWidth() - widthEstimate - 3);
		if (yLabel < heightEstimate)
			yLabel = heightEstimate;
		else
			yLabel = Math.min(yLabel, view.getHeight() - 3);

		// update label rectangle position
		labelRectangle.setLocation(xLabel, yLabel - view.getFontSize());
	}

	/**
	 * @param g2
	 *            graphics
	 * @param font
	 *            font
	 * @param fgColor
	 *            text color
	 * @param bgColor
	 *            background color
	 */
	public final void drawMultilineLaTeX(GGraphics2D g2, GFont font,
			GColor fgColor, GColor bgColor) {
		labelRectangle.setBounds(EuclidianStatic.drawMultilineLaTeX(
				view.getApplication(), view.getTempGraphics2D(font), geo, g2,
				font, fgColor, bgColor, labelDesc, xLabel, yLabel, isSerif(),
				null));
	}

	/**
	 * @return true if serif font is used for GeoText
	 */
	final boolean isSerif() {
		return geo.isGeoText() ? ((GeoText) geo).isSerifFont() : false;
	}

	/**
	 * @param g2
	 *            graphics
	 * @param textFont
	 */
	protected final void drawMultilineText(GGraphics2D g2, GFont textFont) {

		if (labelDesc == null)
			return;

		// no index in text
		if (oldLabelDesc == labelDesc && !labelHasIndex) {

			labelRectangle.setBounds(EuclidianStatic.drawMultiLineText(
					view.getApplication(), labelDesc, xLabel, yLabel, g2,
					isSerif(), textFont));
		} else {
			int lines = 0;
			int fontSize = textFont.getSize();
			float lineSpread = fontSize * 1.5f;

			int xoffset = 0, yoffset = 0;
			// text with indices
			// label description has changed, search for possible indices
			oldLabelDesc = labelDesc;

			// draw text line by line
			int lineBegin = 0;
			int length = labelDesc.length();
			xoffset = 0;
			yoffset = 0;
			for (int i = 0; i < length - 1; i++) {
				if (labelDesc.charAt(i) == '\n') {
					// end of line reached: draw this line

					// iOS (bug?) - bold text needs font setting for each line
					g2.setFont(textFont);
					GPoint p = EuclidianStatic.drawIndexedString(
							view.getApplication(), g2,
							labelDesc.substring(lineBegin, i), xLabel, yLabel
									+ lines * lineSpread, isSerif(), true);
					if (p.x > xoffset)
						xoffset = p.x;
					if (p.y > yoffset)
						yoffset = p.y;
					lines++;
					lineBegin = i + 1;
				}
			}

			float ypos = yLabel + lines * lineSpread;

			// iOS (bug?) - bold text needs font setting for each line
			g2.setFont(textFont);
			GPoint p = EuclidianStatic.drawIndexedString(view.getApplication(),
					g2, labelDesc.substring(lineBegin), xLabel, ypos,
					isSerif(), true);
			if (p.x > xoffset)
				xoffset = p.x;
			if (p.y > yoffset)
				yoffset = p.y;
			labelHasIndex = yoffset > 0;
			int height = (int) ((lines + 1) * lineSpread);
			labelRectangle.setBounds(xLabel - 3, yLabel - fontSize - 3,
					xoffset + 6, height + 6);
		}
	}

	/**
	 * Adds geo's label offset to xLabel and yLabel.
	 * 
	 * @return whether the label fits on screen
	 */
	final protected boolean addLabelOffset() {
		if (geo.labelOffsetX == 0 && geo.labelOffsetY == 0)
			return false;

		int x = xLabel + geo.labelOffsetX;
		int y = yLabel + geo.labelOffsetY;

		// don't let offset move label out of screen
		int xmax = view.getWidth() - 15;
		int ymax = view.getHeight() - 5;
		if (x < 5 || x > xmax)
			return false;
		if (y < 15 || y > ymax)
			return false;

		xLabel = x;
		yLabel = y;
		return true;
	}

	/**
	 * Adds geo's label offset to xLabel and yLabel.
	 * 
	 */
	public final void addLabelOffsetEnsureOnScreen() {
		addLabelOffsetEnsureOnScreen(1.0, 1.0);
	}

	/**
	 * Adds geo's label offset to xLabel and yLabel.
	 * 
	 * @param Xmultiplier
	 *            multiply the x size by it to ensure fitting
	 * @param Ymultiplier
	 *            multiply the y size by it to ensure fitting
	 * 
	 */
	public final void addLabelOffsetEnsureOnScreen(double Xmultiplier,
			double Ymultiplier) {
		// MAKE SURE LABEL STAYS ON SCREEN
		xLabel += geo.labelOffsetX;
		yLabel += geo.labelOffsetY;

		// change xLabel and yLabel so that label stays on screen
		ensureLabelDrawsOnScreen(Xmultiplier, Ymultiplier);
	}

	/**
	 * Was the label clicked at? (mouse pointer location (x,y) in screen coords)
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @return true if hit
	 */
	public boolean hitLabel(int x, int y) {
		return labelRectangle.contains(x, y);
	}

	private boolean forcedLineType;

	private HatchingHandler hatchingHandler;

	/**
	 * Set fixed line type and ignore line type of the geo. Needed for
	 * inequalities.
	 * 
	 * @param type
	 *            line type
	 */
	public final void forceLineType(int type) {
		forcedLineType = true;
		lineType = type;
	}

	final public void updateStrokes(GeoElementND fromGeo) {
		updateStrokes(fromGeo, 0);
	}

	/**
	 * Update strokes (default,selection,deco) accordingly to geo
	 * 
	 * @param fromGeo
	 *            geo whose style should be used for the update
	 */
	final public void updateStrokes(GeoElementND fromGeo, int minThickness) {
		strokedShape = null;
		strokedShape2 = null;

		if (lineThickness != fromGeo.getLineThickness()) {
			lineThickness = Math.max(minThickness, fromGeo.getLineThickness());
			if (!forcedLineType)
				lineType = fromGeo.getLineType();

			float width = lineThickness / 2.0f;
			objStroke = EuclidianStatic.getStroke(width, lineType);
			decoStroke = EuclidianStatic.getStroke(width,
					EuclidianStyleConstants.LINE_TYPE_FULL);
			selStroke = EuclidianStatic.getStroke(width
					+ EuclidianStyleConstants.SELECTION_ADD,
					EuclidianStyleConstants.LINE_TYPE_FULL);
		} else if (lineType != fromGeo.getLineType()) {
			if (!forcedLineType)
				lineType = fromGeo.getLineType();

			float width = lineThickness / 2.0f;
			objStroke = EuclidianStatic.getStroke(width, lineType);
		}
	}

	/**
	 * Update strokes (default,selection,deco) accordingly to geo; ignores line
	 * style
	 * 
	 * @param fromGeo
	 *            geo whose style should be used for the update
	 */
	public final void updateStrokesJustLineThickness(GeoElement fromGeo) {
		strokedShape = null;
		strokedShape2 = null;

		if (lineThickness != fromGeo.lineThickness) {
			lineThickness = fromGeo.lineThickness;

			float width = lineThickness / 2.0f;
			objStroke = org.geogebra.common.factories.AwtFactory.prototype
					.newBasicStroke(width, objStroke.getEndCap(),
							objStroke.getLineJoin(), objStroke.getMiterLimit(),
							objStroke.getDashArray(), 0.0f);
			decoStroke = org.geogebra.common.factories.AwtFactory.prototype
					.newBasicStroke(width, objStroke.getEndCap(),
							objStroke.getLineJoin(), objStroke.getMiterLimit(),
							decoStroke.getDashArray(), 0.0f);
			selStroke = org.geogebra.common.factories.AwtFactory.prototype
					.newBasicStroke(width
							+ EuclidianStyleConstants.SELECTION_ADD,
							objStroke.getEndCap(), objStroke.getLineJoin(),
							objStroke.getMiterLimit(),
							selStroke.getDashArray(), 0.0f);

		}
	}

	/**
	 * Fills given shape
	 * 
	 * @param g2
	 *            graphics
	 * @param fillShape
	 *            shape to be filled
	 * @param usePureStroke
	 *            true to use pure stroke
	 */
	protected void fill(GGraphics2D g2, GShape fillShape, boolean usePureStroke) {
		if (isForceNoFill())
			return;
		if (geo.isHatchingEnabled()) {
			// use decoStroke as it is always full (not dashed/dotted etc)
			if (hatchPaint == null) {
				hatchPaint = new ArrayList<GPaint>();
			}
			GPaint gpaint = getHatchingHandler().setHatching(g2, decoStroke,
					geo.getObjectColor(), geo.getBackgroundColor(),
					geo.getAlphaValue(), geo.getHatchingDistance(),
					geo.getHatchingAngle(), geo.getFillType(),
					geo.getFillSymbol(), geo.getKernel().getApplication());

			if (!hatchPaint.contains(gpaint)) {
				hatchPaint.add(gpaint);
			}
			g2.setPaint(hatchPaint.get(hatchPaint.size() - 1));

			if (!geo.getKernel().getApplication().isHTML5Applet()) {
				if (usePureStroke)
					g2.fillWithValueStrokePure(fillShape);
				else
					g2.fill(fillShape);
			} else {
				// take care of filling after the image is loaded
				EuclidianStatic.fillAfterImageLoaded(fillShape, g2,
						getHatchingHandler().getSubImage(), geo.getKernel()
								.getApplication());
			}

		} else if (geo.getFillType() == GeoElement.FillType.IMAGE) {
			getHatchingHandler().setTexture(g2, geo, geo.getAlphaValue());
			g2.fill(fillShape);
		} else if (geo.getAlphaValue() > 0.0f) {
			g2.setPaint(geo.getFillColor());
			// magic for switching off dash emulation moved to GGraphics2DW
			g2.fill(fillShape);
		}

	}

	private HatchingHandler getHatchingHandler() {
		if (hatchingHandler == null) {
			hatchingHandler = new HatchingHandler();
		}

		return hatchingHandler;
	}

	/**
	 * @param forceNoFill
	 *            the forceNoFill to set
	 */
	public void setForceNoFill(boolean forceNoFill) {
		this.forceNoFill = forceNoFill;
	}

	/**
	 * @return the forceNoFill
	 */
	public boolean isForceNoFill() {
		return forceNoFill;
	}

	/**
	 * @param shape
	 *            the shape to set
	 */
	public void setShape(GArea shape) {
		this.shape = shape;
	}

	/**
	 * @return the shape
	 */
	public GArea getShape() {
		return shape;
	}

	/**
	 * @return true if trace is on
	 */
	public boolean isTracing() {
		return isTracing;
	}

	/**
	 * draw trace of this geo into given Graphics2D
	 * 
	 * @param g2
	 *            graphics
	 */
	protected void drawTrace(GGraphics2D g2) {
		// do nothing, overridden where needed
	}

	/**
	 * @return whether the to-be-drawn geoElement is filled, meaning the
	 *         alpha-value is greater zero, or hatching is enabled.
	 */
	public boolean isFilled() {
		return (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled());
	}

	/**
	 * @return view in which this is drawn
	 */
	public EuclidianView getView() {
		return view;
	}

	public void resetHatch() {
		this.hatchPaint = null;
	}

	public boolean isEuclidianVisible() {
		return geo.isEuclidianVisible();
	}

	/**
	 * @return If the {@code GeoElement} has line opacity then a {@code GColor}
	 *         object with the alpha value set, else the original {@code GColor}
	 *         .
	 */
	protected GColor getObjectColor() {
		GColor color = geo.getObjectColor();
		if (geo.hasLineOpacity()) {
			color = AwtFactory.prototype.newColor(color.getRed(),
					color.getGreen(), color.getBlue(), geo.getLineOpacity());
		}
		return color;
	}
}
