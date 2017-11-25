package org.geogebra.web.web.gui.menubar;

import java.util.ArrayList;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.safehtml.shared.annotations.SuppressIsSafeHtmlCastCheck;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class AriaStackPanel extends ComplexPanel
		implements StackPanelInterface {
	private static final String DEFAULT_STYLENAME = "gwt-StackPanel";
	private static final String DEFAULT_ITEM_STYLENAME = DEFAULT_STYLENAME
			+ "Item";

	private int visibleStack = -1;
	private UListElement ul;
	private ArrayList<Widget> items = new ArrayList<Widget>();
	private ArrayList<Element> headers = new ArrayList<Element>();
	private ArrayList<Element> contents = new ArrayList<Element>();

	/**
	 * Creates an empty stack panel.
	 */
	public AriaStackPanel() {
		ul = Document.get().createULElement();
		setElement(ul);
		addStyleName("gwt-StackPanel");
		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
				| Event.ONFOCUS | Event.ONKEYDOWN);
	}

	@Override
	public void add(Widget w) {
		insert(w, getWidgetCount());
	}

	/**
	 * Adds a new child with the given widget and header.
	 *
	 * @param w
	 *            the widget to be added
	 * @param stackText
	 *            the header text associated with this widget
	 */
	@SuppressIsSafeHtmlCastCheck
	public void add(Widget w, String stackText) {
		add(w, stackText, false);
	}

	/**
	 * Adds a new child with the given widget and header, optionally
	 * interpreting the header as HTML.
	 *
	 * @param w
	 *            the widget to be added
	 * @param stackHtml
	 *            the header html associated with this widget
	 */
	public void add(Widget w, SafeHtml stackHtml) {
		add(w, stackHtml.asString(), true);

	}

	/**
	 * Adds a new child with the given widget and header, optionally
	 * interpreting the header as HTML.
	 *
	 * @param w
	 *            the widget to be added
	 * @param stackText
	 *            the header text associated with this widget
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as HTML
	 */
	public void add(Widget w, @IsSafeHtml String stackText, boolean asHTML) {
		add(w);
		items.add(w);
		setStackText(getWidgetCount() - 1, stackText);
	}

	/**
	 * Gets the currently selected child index.
	 *
	 * @return selected child
	 */
	public int getSelectedIndex() {
		return visibleStack;
	}

	public void insert(IsWidget w, int beforeIndex) {
		insert(asWidgetOrNull(w), beforeIndex);
	}

	public void insert(Widget w, int beforeIndex) {
		// header
		Element li = DOM.createElement("LI");
		// li.setClassName("gwt-StackPanelItem listMenuItem");
		li.setAttribute("role", "menuitem");

		getElement().appendChild(li);

		Element button = DOM.createElement("button");
		button.setAttribute("role", "menuitem");
		li.appendChild(button);

		headers.add(button);

		// UListElement ul1 = Document.get().createULElement();
		// ul1.setInnerHTML(w.getElement().getInnerHTML());
		// li.appendChild(ul1);
		Element content = DOM.createElement("DIV");

		items.add(beforeIndex, w);
		content.appendChild(w.getElement());
		contents.add(content);
		li.appendChild(content);
		// DOM indices are 2x logical indices; 2 dom elements per stack item
		ul.appendChild(li);

		// header styling
		setStyleName(button, DEFAULT_ITEM_STYLENAME, true);
		button.setPropertyInt("__owner", hashCode());
		button.setPropertyInt("__index", beforeIndex);
		content.setPropertyInt("__index", beforeIndex);
		w.getElement().setPropertyInt("__index", beforeIndex);

		updateIndicesFrom(beforeIndex);

		// body styling
		setStyleName(content, DEFAULT_STYLENAME + "Content", true);
		content.setPropertyString("height", "100%");


		// Correct visible stack for new location.
		if (visibleStack == -1) {
			showStack(0);
		} else {
			setStackVisible(beforeIndex, false);
			if (visibleStack >= beforeIndex) {
				++visibleStack;
			}
			// Reshow the stack to apply style names
			setStackVisible(visibleStack, true);
		}
	}

	// @Override
	// public void onBrowserEvent(Event event) {
	// if (DOM.eventGetType(event) == Event.ONCLICK) {
	// Element target = DOM.eventGetTarget(event);
	// int index = findDividerIndex(target);
	// if (index != -1) {
	// showStack(index);
	// }
	// }
	// super.onBrowserEvent(event);
	// }

	@Override
	public boolean remove(int index) {
		return remove(getWidget(index), index);
	}

	public boolean remove(Widget child) {
		return remove(child, getWidgetIndex(child));
	}

	@Override
	public Widget getWidget(int index) {
		return items.get(index);
	}
	/**
	 * Sets the text associated with a child by its index.
	 *
	 * @param index
	 *            the index of the child whose text is to be set
	 * @param text
	 *            the text to be associated with it
	 */
	@SuppressIsSafeHtmlCastCheck
	public void setStackText(int index, String text) {
		setStackText(index, text, false);
	}

	/**
	 * Sets the html associated with a child by its index.
	 *
	 * @param index
	 *            the index of the child whose text is to be set
	 * @param html
	 *            the html to be associated with it
	 */
	public void setStackText(int index, SafeHtml html) {
		setStackText(index, html.asString(), true);
	}

	/**
	 * Sets the text associated with a child by its index.
	 *
	 * @param index
	 *            the index of the child whose text is to be set
	 * @param text
	 *            the text to be associated with it
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as HTML
	 */
	public void setStackText(int index, @IsSafeHtml String text,
			boolean asHTML) {
		if (index >= getWidgetCount()) {
			return;
		}
		headers.get(index).setInnerHTML(text);
	}

	/**
	 * Shows the widget at the specified child index.
	 *
	 * @param index
	 *            the index of the child to be shown
	 */
	public void showStack(int index) {
		if ((index >= getWidgetCount()) || (index < 0)
				|| (index == visibleStack)) {
			return;
		}

		if (visibleStack >= 0) {
			setStackVisible(visibleStack, false);
		}

		visibleStack = index;
		setStackVisible(visibleStack, true);
	}


	/**
	 * Adds the {@code styleName} on the {@code 
	 * <tr>
	 * } for the header specified by {@code index}.
	 *
	 * @param index
	 *            the index of the header row to apply to the style to
	 * @param styleName
	 *            the name of the class to add
	 */
	public void addHeaderStyleName(int index, String styleName) {
		if (index >= getWidgetCount()) {
			return;
		}
		headers.get(index).addClassName(styleName);
	}

	/**
	 * Removes the {@code styleName} off the {@code 
	 * <tr>
	 * } for the header specified by {@code index}.
	 *
	 * @param index
	 *            the index of the header row to remove the style from
	 * @param styleName
	 *            the name of the class to remove
	 */
	public void removeHeaderStyleName(int index, String styleName) {
		if (index >= getWidgetCount()) {
			return;
		}
		headers.get(index).removeClassName(styleName);

	}

	@Override
	public int getWidgetCount() {
		return items.size();
	}

	protected int findDividerIndex(Element elem) {
		String expando = elem.getPropertyString("__index");
		if (expando == null) {
			expando = elem.getParentElement().getPropertyString("__index");
		}
		if (expando != null) {
			int index = headers.indexOf(elem);
			return index;
		}
		return -1;
	}

	private boolean remove(Widget child, int index) {
		// Make sure to call this before disconnecting the DOM.
		boolean removed = super.remove(child);
		headers.remove(index);
		contents.remove(index);
		return removed;
	}

	private void setStackVisible(int index, boolean visible) {
		Element header = headers.get(index);
		Element content = contents.get(index);
		setStyleName(header, DEFAULT_ITEM_STYLENAME + "-selected", visible);
		UIObject.setVisible(content, visible);
	}

	private void updateIndicesFrom(int beforeIndex) {
		Element header = headers.get(beforeIndex);
		if (beforeIndex == 0) {
			setStyleName(header, DEFAULT_ITEM_STYLENAME + "-first", true);
		} else {
			setStyleName(header, DEFAULT_ITEM_STYLENAME + "-first", false);
		}
	}

	/** Close all stacks */
	public void closeAll() {
		setStackVisible(visibleStack, false);
		visibleStack = -1;
	}

	public int getContentIndex(Element target) {
		return target.getParentElement().getPropertyInt("__index");
	}

}

