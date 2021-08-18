package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public class TableValuesListColumn extends AbstractTableValuesColumn {

	private final GeoList list;

	public TableValuesListColumn(GeoList list) {
		super(list);
		this.list = list;
	}

	@Override
	protected double calculateValue(int row) {
		GeoElement element = list.get(row);
		return element.evaluateDouble();
	}

	@Override
	protected String getHeaderName() {
		return list.getLabelSimple();
	}
}

