package geogebra.common.main;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AbstractApplication {
public static final String LOADING_GIF = "http://www.geogebra.org/webstart/loading.gif";
	
	public static final String WIKI_OPERATORS = "Predefined Functions and Operators";
	public static final String WIKI_MANUAL = "Manual:Main Page";
	public static final String WIKI_TUTORIAL = "Tutorial:Main Page";
	public static final String WIKI_EXPORT_WORKSHEET = "Export_Worksheet_Dialog";
	public static final String WIKI_ADVANCED = "Advanced Features";
	public static final String WIKI_TEXT_TOOL = "Insert Text Tool";
	
	public static final int VIEW_NONE = 0;
	public static final int VIEW_EUCLIDIAN = 1;
	public static final int VIEW_ALGEBRA = 2;
	public static final int VIEW_SPREADSHEET = 4;
	public static final int VIEW_CAS = 8;
	public static final int VIEW_EUCLIDIAN2 = 16;
	public static final int VIEW_CONSTRUCTION_PROTOCOL = 32;
	public static final int VIEW_PROBABILITY_CALCULATOR = 64;
	public static final int VIEW_FUNCTION_INSPECTOR = 128;
	public static final int VIEW_INSPECTOR = 256;
    public static final int VIEW_EUCLIDIAN3D = 512;
    public static final int VIEW_EUCLIDIAN_FOR_PLANE = 1024;
    public static final int VIEW_PLOT_PANEL = 2048;
    public static final int VIEW_TEXT_PREVIEW = 4096;
	public static final int VIEW_PROPERTIES = 4097;

	// For eg Hebrew and Arabic. 	
		public static char unicodeDecimalPoint = '.';
		public static char unicodeComma = ','; // \u060c for Arabic comma
		public static char unicodeZero = '0';
	
		public enum CasType { NO_CAS, MATHPIPER, MAXIMA, MPREDUCE };
		
	public abstract ResourceBundle initAlgo2IntergeoBundle();
	public abstract ResourceBundle initAlgo2CommandBundle();
	public abstract String getCommand(String cmdName);
	public abstract String getPlain(String cmdName);
	public abstract String getPlain(String cmdName,String param);
	public abstract String getPlain(String cmdName,String param,String param2);
	public abstract String getPlain(String cmdName,String param,String param2,String param3);
	public abstract String getPlain(String cmdName,String param,String param2,String param3,String param4);
	public abstract String getPlain(String cmdName,String param,String param2,String param3,String param4,String param5);
	public abstract String getMenu(String cmdName);
	public abstract String getError(String cmdName);
	public abstract boolean isRightToLeftReadingOrder();
	public abstract void setTooltipFlag();
	public abstract void clearTooltipFlag();
	public abstract boolean isApplet();
	public abstract void storeUndoInfo();
	public abstract boolean isUsingFullGui();
	public abstract boolean showView(int view);
	public abstract void callJavaScript(String jsFunction, Object [] args);
	public abstract boolean isUsingLocalizedLabels();
	public abstract Locale getLocale();
	public abstract boolean languageIs(Locale l,String s);
	public abstract boolean letRedefine();
	public abstract String translationFix(String s);
	public abstract void traceToSpreadsheet(Object o);
	public abstract void resetTraceColumn(Object o);
	public abstract boolean isReverseNameDescriptionLanguage();
	public abstract boolean isBlockUpdateScripts();
	public abstract void setBlockUpdateScripts(boolean flag);
	public abstract String getScriptingLanguage();
	public abstract void setScriptingLanguage(String lang);
	public abstract String getInternalCommand(String s);
	public abstract void showError(String s);
	public abstract boolean isScriptingDisabled();
	public abstract boolean useBrowserForJavaScript();
	public abstract void initJavaScriptViewWithoutJavascript();
	public abstract Object getTraceXML(Object geoElement);
	public abstract void removeSelectedGeo(Object geoElement, boolean b);
	public abstract void changeLayer(Object ge, int layer, int layer2);
	public abstract Object getExternalImage(String fileName);
	public abstract boolean freeMemoryIsCritical();
	public abstract long freeMemory();
	public abstract int getLabelingStyle();
	public abstract String getOrdinalNumber(int i);
	public abstract double getXmin();
	public abstract double getXmax();
	public abstract double getXminForFunctions();
	public abstract double getXmaxForFunctions();
	public abstract double countPixels(double min, double max);
	public abstract int getMaxLayerUsed();
	public abstract Object getAlgebraView();
	public abstract void debugNotStatic(Object s);
	public abstract Object createEuclidianViewForPlane(Object o);
	public abstract boolean isRightToLeftDigits();
	public abstract boolean isShowingEuclidianView2();
	
}
