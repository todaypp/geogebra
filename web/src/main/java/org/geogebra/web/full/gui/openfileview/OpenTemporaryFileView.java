package org.geogebra.web.full.gui.openfileview;

import java.util.Collection;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.main.exam.TempStorage;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.html5.main.AppW;

/**
 * View for browsing materials
 */
public class OpenTemporaryFileView extends HeaderFileView implements
		OpenFileListener {

	private final TempStorage tempStorage;
	private final FileViewCommon common;

	private final AppW app;

	/**
	 * @param app - application
	 */
	public OpenTemporaryFileView(AppW app) {
		this.app = app;
		app.registerOpenFileListener(this);
		common = new FileViewCommon(app, "Open", false);
		common.addStyleName("examTemporaryFiles");
		tempStorage = app.getExam().getTempStorage();
	}

	@Override
	public AnimatingPanel getPanel() {
		return common;
	}

	private Collection<Material> getMaterials() {
		return tempStorage.collectTempMaterials();
	}

	@Override
	public void loadAllMaterials(int offset) {
		clearMaterials();
		if (tempStorage.isEmpty()) {
			common.showEmptyListNotification();
		} else {
			common.clearContents();
			common.addContent();
			addTemporaryMaterials();
		}
	}

	@Override
	public void clearMaterials() {
		common.clearMaterials();
	}

	private void addTemporaryMaterials() {
		common.clearPanels();
		for (Material material : getMaterials()) {
			addMaterial(material);
		}
		common.addMaterialPanel();
	}

	@Override
	public void setLabels() {
		common.setLabels();
	}

	@Override
	public void addMaterial(Material material) {
		common.addMaterialCard(new TemporaryCard(material, app));
	}

	@Override
	public boolean onOpenFile() {
		setConstructionTitleAsMaterial();
		return false;
	}

	private void setConstructionTitleAsMaterial() {
		Material activeMaterial = app.getActiveMaterial();
		if (activeMaterial != null) {
			app.getKernel().getConstruction().setTitle(
					activeMaterial.getTitle());
		}
	}

	@Override
	public void closeAndSave(AsyncOperation<Boolean> callback) {
		close();
		app.checkSaved(callback);
	}
}