package org.geogebra.common.properties.impl;

import java.util.ArrayList;

import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class RoundingProperty extends AbstractEnumerableProperty {

    private App app;
    private int figuresIndex;

    public RoundingProperty(App app, Localization localization) {
        super(localization, "Rounding");

        this.app = app;

        setupValues(localization);
    }

    private void setupValues(Localization localization) {
        String[] values = localization.getRoundingMenu();
        ArrayList<String> list = new ArrayList<>(values.length - 1);
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (value.equals(Localization.ROUNDING_MENU_SEPARATOR)) {
                figuresIndex = i;
            } else {
                list.add(value);
            }
        }

        String[] array = new String[list.size()];

        setValues(list.toArray(array));
    }

    @Override
    public int getCurrent() {
		return OptionsMenu.getMenuDecimalPosition(app.getKernel(), true);
    }

    @Override
    protected void setValueSafe(String value, int index) {
        boolean figures = index >= figuresIndex;
        OptionsMenu.setRounding(app, figures ? index + 1: index, figures);
    }
}
