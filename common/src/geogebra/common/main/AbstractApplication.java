package geogebra.common.main;

import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.gui.view.spreadsheet.AbstractSpreadsheetTableModel;
import geogebra.common.gui.view.spreadsheet.SpreadsheetTraceManager;
import geogebra.common.io.layout.Perspective;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.AbstractUndoManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.MacroInterface;
import geogebra.common.kernel.View;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.cas.CASGenericInterface;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.commands.CommandsConstants;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.DebugPrinter;
import geogebra.common.util.LowerCaseDictionary;
import geogebra.common.util.Unicode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;

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
	public static final int VIEW_ASSIGNMENT = 8192;
	public static final int VIEW_TABLE_MODEL = 9000;
	private boolean showResetIcon = false;
	public boolean runningInFrame = false; // don't want to show resetIcon if
											// running in Frame
	
	// specialLanguageNames: Java does not show an English name for all
	// languages
	// supported by GeoGebra, so some language codes have to be treated
	// specially
	public static HashMap<String, String> specialLanguageNames = new HashMap<String, String>();
	static {
		specialLanguageNames.put("hy",
				"Armenian / \u0570\u0561\u0575\u0565\u0580\u0565\u0576");
		specialLanguageNames.put("bs",
				"Bosnian / \u0431\u043E\u0441\u0430\u043D\u0441\u043A\u0438");
		specialLanguageNames.put("zhCN",
				"Chinese Simplified / \u7B80\u4F53\u4E2D\u6587");
		specialLanguageNames.put("zhTW",
				"Chinese Traditional / \u7E41\u9AD4\u4E2D\u6587");
		specialLanguageNames.put("en", "English (US)");
		specialLanguageNames.put("enGB", "English (UK)");
		specialLanguageNames.put("enAU", "English (Australia)");
		specialLanguageNames.put("deAT",
				"German (Austria) / Deutsch (\u00D6sterreich)");
		specialLanguageNames.put("de", "German / Deutsch");
		specialLanguageNames.put("gl", "Galician / Galego");
		specialLanguageNames.put("noNO", "Norwegian / Bokm\u00e5l");
		specialLanguageNames.put("noNONY", "Norwegian / Nynorsk");
		specialLanguageNames.put("pt",
				"Portuguese (Brazil) / Portugu\u00EAs (Brasil)");
		specialLanguageNames.put("ptPT",
				"Portuguese (Portugal) / Portugu\u00EAs (Portugal)");
		specialLanguageNames.put("si",
				"Sinhala / \u0DC3\u0DD2\u0D82\u0DC4\u0DBD"); // better than
																// Sinhalese

		specialLanguageNames.put("sq", "Albanian / Gjuha Shqipe");
		specialLanguageNames.put("ar",
				"Arabic / \u0627\u0644\u0639\u0631\u0628\u064A\u0629 ");
		specialLanguageNames.put("eu", "Basque / Euskara");
		specialLanguageNames
				.put("bg",
						"Bulgarian / \u0431\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438 \u0435\u0437\u0438\u043A");
		specialLanguageNames.put("ca", "Catalan / Catal\u00E0");
		specialLanguageNames.put("hr", "Croatian / Hrvatska");
		specialLanguageNames.put("cs", "Czech / \u010Ce\u0161tina");
		specialLanguageNames.put("da", "Danish / Dansk");
		specialLanguageNames.put("nl", "Dutch / Nederlands");
		specialLanguageNames.put("et", "Estonian / Eesti keel");
		specialLanguageNames.put("fi", "Finnish / Suomi");
		specialLanguageNames.put("fr", "French / Fran\u00E7ais");
		specialLanguageNames
				.put("ka",
						"Georgian / \u10E5\u10D0\u10E0\u10D7\u10E3\u10DA\u10D8 \u10D4\u10DC\u10D0");
		specialLanguageNames.put("el",
				"Greek / \u0395\u03BB\u03BB\u03B7\u03BD\u03B9\u03BA\u03AC");
		specialLanguageNames.put("iw",
				"Hebrew / \u05E2\u05B4\u05D1\u05B0\u05E8\u05B4\u05D9\u05EA");
		specialLanguageNames
				.put("hi",
						"Hindi / \u092E\u093E\u0928\u0915 \u0939\u093F\u0928\u094D\u0926\u0940");
		specialLanguageNames.put("hu", "Hungarian / Magyar");
		specialLanguageNames.put("is", "Icelandic / \u00CDslenska");
		specialLanguageNames.put("in", "Indonesian / Bahasa Indonesia");
		specialLanguageNames.put("it", "Italian / Italiano");
		specialLanguageNames.put("ja", "Japanese / \u65E5\u672C\u8A9E");
		specialLanguageNames
				.put("kk",
						"Kazakh / \u049A\u0430\u0437\u0430\u049B \u0442\u0456\u043B\u0456");
		specialLanguageNames.put("ko", "Korean / \uD55C\uAD6D\uB9D0");
		specialLanguageNames.put("lt", "Lithuanian / Lietuvi\u0173 kalba");
		specialLanguageNames.put("ml",
				"Malayalam / \u0D2E\u0D32\u0D2F\u0D3E\u0D33\u0D02");
		specialLanguageNames
				.put("mk",
						"Macedonian / \u041C\u0430\u043A\u0435\u0434\u043E\u043D\u0441\u043A\u0438 \u0458\u0430\u0437\u0438\u043A");
		specialLanguageNames.put("mr",
				"Marathi / \u092E\u0930\u093E\u0920\u0940");
		specialLanguageNames.put("ms", "Malay / Bahasa Malaysia");
		specialLanguageNames.put("ne",
				"Nepalese / \u0928\u0947\u092A\u093E\u0932\u0940");
		specialLanguageNames.put("fa",
				"Persian / \u0641\u0627\u0631\u0633\u06CC");
		specialLanguageNames.put("pl", "Polish / J\u0119zyk polski");
		specialLanguageNames.put("ro", "Romanian /  Rom\u00E2n\u0103");
		specialLanguageNames
				.put("ru",
						"Russian / \u0420\u0443\u0441\u0441\u043A\u0438\u0439 \u044F\u0437\u044B\u043A");
		specialLanguageNames.put("sr",
				"Serbian / \u0441\u0440\u043F\u0441\u043A\u0438");
		specialLanguageNames.put("sk", "Slovakian / Slovensk\u00FD jazyk");
		specialLanguageNames.put("sl", "Slovenian / Sloven\u0161\u010Dina");
		specialLanguageNames.put("es", "Spanish / Espa\u00F1ol");
		specialLanguageNames.put("sv", "Swedish / Svenska");
		specialLanguageNames
				.put("ta", "Tamil / \u0BA4\u0BAE\u0BBF\u0BB4\u0BCD");
		specialLanguageNames.put("th",
				"Thai / \u0E20\u0E32\u0E29\u0E32\u0E44\u0E17\u0E22");
		specialLanguageNames.put("tr", "Turkish / T\u00FCrk\u00E7e");
		specialLanguageNames
				.put("uk",
						"Ukrainian / \u0423\u043A\u0440\u0430\u0457\u043D\u0441\u044C\u043A\u0430 \u043C\u043E\u0432\u0430");
		specialLanguageNames.put("vi", "Vietnamese / Ti\u1EBFng Vi\u1EC7t");
		specialLanguageNames.put("cy", "Welsh / Cymraeg");
		specialLanguageNames.put("ji",
				"Yiddish / \u05D9\u05D9\u05B4\u05D3\u05D9\u05E9");
	}
	
	private static String CASVersionString = "";

	public static void setCASVersionString(String string) {
		CASVersionString = string;

	}

	public static String getCASVersionString() {
		return CASVersionString;

	}
	// gui / menu fontsize (-1 = use appFontSize)
		private int guiFontSize = -1;
	// currently used application fonts
		private int appFontSize;
	// note: It is not necessary to use powers of 2 for view IDs

	// For eg Hebrew and Arabic.
	public static char unicodeDecimalPoint = '.';
	public static char unicodeComma = ','; // \u060c for Arabic comma
	public static char unicodeZero = '0';

	public enum CasType {
		NO_CAS, MATHPIPER, MAXIMA, MPREDUCE
	}

	// moved to Application from EuclidianView as the same value is used across
	// multiple EVs
	public int maxLayerUsed = 0;
	public int pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
	public int booleanSize = 13;
	public int rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;

	public boolean useJavaFontsForLaTeX = false;

	protected final ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();

	protected Kernel kernel;

	protected boolean isOnTheFlyPointCreationActive = true;
	protected Settings settings;
	
	public boolean useJavaFontsForLaTeX() {
		return useJavaFontsForLaTeX;

	}
	protected boolean antialiasing = true; 
	protected final boolean[] showAxes = { true, true };
	protected boolean showGrid = false;
	
	protected boolean labelDragsEnabled = true;//private

	public static final int SPREADSHEET_INI_COLS = 26;
	public static final int SPREADSHEET_INI_ROWS = 100;
	
	private HashMap<String, String> translateCommandTable,
	translateCommandTableScripting;
	// command dictionary
	private LowerCaseDictionary commandDict;
	private LowerCaseDictionary commandDictCAS;
	
	protected AbstractEuclidianView euclidianView;
	protected AbstractEuclidianController euclidianController;
	protected GeoElementSelectionListener currentSelectionListener;
	protected boolean showMenuBar = true;
	// array of dictionaries corresponding to the sub command tables
	private LowerCaseDictionary[] subCommandDict;
	
	private String oldScriptLanguage = null;

	private String scriptingLanguage;

	private void fillCommandDictScripting() {
		if ((scriptingLanguage == null)
				|| scriptingLanguage.equals(oldScriptLanguage)
				|| "null".equals(scriptingLanguage)) {
			return;
		}
		oldScriptLanguage = scriptingLanguage;
		initScriptingBundle();

		// translation table for all command names in command.properties
		if (translateCommandTableScripting == null) {
			translateCommandTableScripting = new HashMap<String, String>();
		}

		// command dictionary for all public command names available in
		// GeoGebra's input field

		translateCommandTableScripting.clear();


		for (Commands comm:Commands.values()) {
			String internal = comm.toString();
			// Application.debug(internal);
			if (!internal.equals("Command")) {
				String local = getScriptingCommand(internal);
				if (local != null) {
					local = local.trim();
					// case is ignored in translating local command names to
					// internal names!
					translateCommandTableScripting.put(local.toLowerCase(),
							internal);

				}
			}
		}

	}
	
	protected abstract boolean isCommandChanged();
	protected abstract void setCommandChanged(boolean b);
	protected abstract boolean isCommandNull();
	
	public void fillCasCommandDict() {
		// this method might get called during initialization, when we're not
		// yet
		// ready to fill the casCommandDict. In that case, we will fill the
		// dict during fillCommandDict :)

		if (!isCommandChanged()
				&& ((commandDictCAS != null) || isCommandNull())) {
			return;
		}
		CASGenericInterface cas = kernel.getGeoGebraCAS().getCurrentCAS();
		if (cas == null) {
			return;
		}

		setCommandChanged(false);

		commandDictCAS = new LowerCaseDictionary();
		subCommandDict[CommandsConstants.TABLE_CAS].clear();
		Set<String> nonCAScommands = kernel.getAlgebraProcessor().getPublicCommandSet();
		// iterate through all available CAS commands, add them (translated if
		// available, otherwise untranslated)
		for (String cmd : cas.getAvailableCommandNames()) {
			//skip commands which are also available as non-CAS
			if(nonCAScommands.contains(cmd))
				continue;
			try {
				String local = getCommand(cmd);
				if (local != null) {
					translateCommandTable.put(local.toLowerCase(), cmd);
					commandDictCAS.addEntry(local);
					subCommandDict[CommandsConstants.TABLE_CAS]
							.addEntry(local);
				} else {
					commandDictCAS.addEntry(cmd);
					subCommandDict[CommandsConstants.TABLE_CAS]
							.addEntry(cmd);
				}
			} catch (MissingResourceException mre) {
				commandDictCAS.addEntry(cmd);
				subCommandDict[CommandsConstants.TABLE_CAS]
						.addEntry(cmd);
			}
		}

	}
	
	public final LowerCaseDictionary getCommandDictionaryCAS() {
		fillCommandDict();
		fillCasCommandDict();
		return commandDictCAS;
	}
	
	/**
	 * Returns an array of command dictionaries corresponding to the categorized
	 * sub command sets created in CommandDispatcher.
	 */
	public final LowerCaseDictionary[] getSubCommandDictionary() {

		if (subCommandDict == null) {
			initTranslatedCommands();
		}
		if(isCommandChanged())
			updateCommandDictionary();

		return subCommandDict;
	}
	
	/**
	 * Initializes the translated command names for this application. Note: this
	 * will load the properties files first.
	 */
	final public void initTranslatedCommands() {
		if (isCommandNull() || subCommandDict == null) {
			getCommandResourceBundle();
			fillCommandDict();
			kernel.updateLocalAxesNames();
		}
	}
	public final LowerCaseDictionary getCommandDictionary() {
		fillCommandDict();
		return commandDict;
	}
	
	public abstract void getCommandResourceBundle();
	
	protected void fillCommandDict() {
		getCommandResourceBundle();

		if (!isCommandChanged()) {
			return;
		}
		

		// translation table for all command names in command.properties
		if (translateCommandTable == null) {
			translateCommandTable = new HashMap<String, String>();
		}

		// command dictionary for all public command names available in
		// GeoGebra's input field
		// removed check for null: commandDict.clear() removes keys, but they
		// are still available with commandDict.getIterator()
		// so change English -> French -> English doesn't work in the input bar
		// see AutoCompleteTextfield.lookup()
		// if (commandDict == null)
		commandDict = new LowerCaseDictionary();
		// else commandDict.clear();

		translateCommandTable.clear();


		Set<String> publicCommandNames = kernel.getAlgebraProcessor()
				.getPublicCommandSet();

		// =====================================
		// init sub command dictionaries
		
		if (subCommandDict == null) {
			subCommandDict = new LowerCaseDictionary[CommandDispatcher.tableCount];
			for (int i = 0; i < subCommandDict.length; i++) {
				subCommandDict[i] = new LowerCaseDictionary();
			}
		}
		for (int i = 0; i < subCommandDict.length; i++) {
			subCommandDict[i].clear();
			// =====================================
		}

		for (Commands comm : Commands.values()) {
			if(!tableVisible(comm.getTable()))
				continue;
			String internal = comm.name();
			// Application.debug(internal);
			String local = getCommand(internal);
			if (local != null) {
				local = local.trim();
				// case is ignored in translating local command names to
				// internal names!
				translateCommandTable.put(local.toLowerCase(), internal);

				// only add public commands to the command dictionary
				if (publicCommandNames.contains(internal)) {
					commandDict.addEntry(local);
				}

				// add public commands to the sub-command dictionaries
				subCommandDict[comm.getTable()].addEntry(local);
				

			}
			
		}

		// get CAS Commands
		if (kernel.isGeoGebraCASready()) {
			fillCasCommandDict();
		}
		addMacroCommands();
		setCommandChanged(false);
	}
	/**
	 * return true if commands of this table should be visible in input bar help
	 * and autocomplete
	 * @param table table number, see CommandConstants.TABLE_*
	 * @return true for visible tables
	 */
	protected boolean tableVisible(int table) {
		return !(table==CommandsConstants.TABLE_3D || table ==CommandsConstants.TABLE_ENGLISH);
	}

	/**
	 * translate command name to internal name. Note: the case of localname is
	 * NOT relevant
	 */
	final public String translateCommand(String localname) {
		if (localname == null) {
			return null;
		}
		if (translateCommandTable == null) {
			AbstractApplication.debug("translation not initialized");
			return localname;
		}

		// note: lookup lower case of command name!
		Object value = translateCommandTable.get(localname.toLowerCase());
		if (value == null) {
			fillCommandDictScripting();
			if (translateCommandTableScripting != null) {
				value = translateCommandTableScripting.get(localname
						.toLowerCase());
			}
		}
		if (value == null) {
			return localname;
		} else {
			return (String) value;
		}
	}

	
	public void updateCommandDictionary() {
		// make sure all macro commands are in dictionary
		fillCommandDict();
	}
	
	protected void addMacroCommands() {
		if ((commandDict == null) || (kernel == null) || !kernel.hasMacros()) {
			return;
		}

		ArrayList<MacroInterface> macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = macros.get(i).getCommandName();
			if (!commandDict.containsValue(cmdName)) {
				commandDict.addEntry(cmdName);
			}
		}
	}
	

	public void removeMacroCommands() {
		if ((commandDict == null) || (kernel == null) || !kernel.hasMacros()) {
			return;
		}

		ArrayList<MacroInterface> macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = macros.get(i).getCommandName();
			commandDict.removeEntry(cmdName);
		}
	}

	

	
	public abstract void initScriptingBundle();


	public abstract String getScriptingCommand(String internal);


	public abstract String getCommand(String cmdName);

	public abstract String getPlain(String cmdName);

	public abstract String getPlain(String cmdName, String param);

	public abstract String getPlain(String cmdName, String param, String param2);

	public abstract String getPlain(String cmdName, String param,
			String param2, String param3);

	public abstract String getPlain(String cmdName, String param,
			String param2, String param3, String param4);

	public abstract String getPlain(String cmdName, String param,
			String param2, String param3, String param4, String param5);

	public abstract String getMenu(String cmdName);

	public abstract String getError(String cmdName);



	public abstract void setTooltipFlag();

	public abstract boolean isApplet();

	public abstract void storeUndoInfo();

	public abstract boolean isUsingFullGui();

	public abstract boolean showView(int view);

	/**
	 * 
	 * @return 2 letter language name, eg "en"
	 */
	public abstract String getLanguage();

	public boolean languageIs(String lang) {
		return getLanguage().equals(lang);
	}

	public boolean letRename() {
		return true;
	}

	public boolean letDelete() {
		return true;
	}

	public boolean letRedefine() {
		return true;
	}


	/**
	 * In some languages, a properties file cannot completely describe
	 * translations. This method tries to rewrite a text to the correct form.
	 * 
	 * @param text
	 *            the translation text to fix
	 * @return text the fixed text
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 */
	public String translationFix(String text) {
		// Currently no other language is supported than Hungarian.
		String lang = getLanguage();
		if (!("hu".equals(lang))) {
			return text;
		}
		return translationFixHu(text);
	}

	/**
	 * Text fixer for the Hungarian language
	 * 
	 * @param text
	 *            the translation text to fix
	 * @return the fixed text
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 */

	private String translationFixHu(String text) {
		// Fixing affixes.

		// We assume that object names are usual object names like "P", "O_1"
		// etc.
		// FIXME: This will not work for longer object names, e.g. "X Triangle",
		// "mypoint". To solve this problem, we should check the whole word and
		// its vowels. Probably hunspell for JNA could help (but it can be
		// too big solution for us), http://dren.dk/hunspell.html.
		// TODO: The used method is not as fast as it could be, so speedup is
		// possible.
		String[] affixesList = { "-ra/-re", "-nak/-nek", "-ba/-be",
				"-ban/-ben", "-hoz/-hez", "-val/-vel" };
		String[] endE2 = { "10", "40", "50", "70", "90" };
		// FIXME: Numbers in endings which greater than 999 are not supported
		// yet.
		// Special endings for -val/-vel:
		String[] endO2 = { "00", "20", "30", "60", "80" };

		for (String affixes : affixesList) {
			int match;
			do {
				match = text.indexOf(affixes);
				// match > 0 can be assumed because an affix will not start the
				// text
				if ((match > -1) && (match > 0)) {
					// Affix found. Get the previous character.
					String prevChars = translationFixPronouncedPrevChars(text,
							match, 1);
					if (Unicode.translationFixHu_endE1.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match,
								affixes, "e", prevChars);
					} else if (Unicode.translationFixHu_endO1
							.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match,
								affixes, "o", prevChars);
					} else if (Unicode.translationFixHu_endOE1
							.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match,
								affixes, Unicode.translationFixHu_oe, prevChars);
					} else if (match > 1) {
						// Append the previous character.
						// TODO: This could be quicker: to add only the second
						// char beyond prevChars
						prevChars = translationFixPronouncedPrevChars(text,
								match, 2);
						boolean found2 = false;
						for (String last2fit : endE2) {
							if (!found2 && last2fit.equals(prevChars)) {
								text = translationFixHuAffixChange(text, match,
										affixes, "e", prevChars);
								found2 = true;
							}
						}

						// Special check for preparing -val/-vel:
						if (!found2) {
							for (String last2fit : endO2) {
								if (!found2 && last2fit.equals(prevChars)) {
									text = translationFixHuAffixChange(text,
											match, affixes, "o", prevChars);
									found2 = true;
								}
							}
						}

						if (!found2) {
							// Use heuristics:
							text = translationFixHuAffixChange(text, match,
									affixes, "o", prevChars);
						}

					} else {
						// Use heuristics:
						text = translationFixHuAffixChange(text, match,
								affixes, "o", prevChars);
					}
				}
			} while (match > -1);
		}

		return text;
	}
	
	/**
	 * Gets the previous "pronounced" characters from text before the match
	 * position for the given length. The returned text will be lowercased.
	 * 
	 * Example: translationFixPrevChars("ABC_{123}", 8, 4) gives "c123"
	 * 
	 * @param text
	 *            the text to pronounce
	 * @param match
	 *            starting position
	 * @param length
	 *            required length for the output
	 * @return lowercased output
	 */
	private String translationFixPronouncedPrevChars(String text, int match,
			int length) {
		String rettext = "";
		int rettextlen = 0;
		String thisChar;
		String ignoredChars = "_{}";

		while ((rettextlen < length) && (match > 0)) {
			thisChar = text.substring(match - 1, match);
			if (ignoredChars.indexOf(thisChar) == -1) {
				rettext = thisChar.toLowerCase() + rettext;
				rettextlen++;
			}
			match--;
		}
		return rettext;
	}

	
	/**
	 * Changes a set of possible affixes to the right one
	 * 
	 * @param text
	 *            the text to be corrected
	 * @param match
	 *            starting position of possible change
	 * @param affixes
	 *            possible affixes to change
	 * @param affixForm
	 *            abbreviation for the change type ("o"/"a"/"e")
	 * @param prevChars
	 * @return the corrected text
	 */
	private String translationFixHuAffixChange(String text, int match,
			String affixes, String affixForm, String prevChars) {

		String replace = "";

		if ("-ra/-re".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "ra";
			} else {
				replace = "re";
			}
		} else if ("-nak/-nek".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "nak";
			} else {
				replace = "nek";
			}
		} else if ("-ba/-be".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "ba";
			} else {
				replace = "be";
			}
		} else if ("-ban/-ben".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "ban";
			} else {
				replace = "ben";
			}
		} else if ("-hoz/-hez".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "hoz";
			} else if ("e".equals(affixForm)) {
				replace = "hez";
			} else {
				replace = Unicode.translationFixHu_hoez;
			}
		} else if ("-val/-vel".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "val";
			} else {
				replace = "vel";
			}

			// Handling some special cases:
			if (prevChars.length() == 1) {
				// f-fel, l-lel etc.
				String sameChars = "flmnrs";
				// y-nal, 3-mal etc.
				String valVelFrom = sameChars + "y356789";
				String valVelTo = sameChars + "nmtttcc";
				int index = valVelFrom.indexOf(prevChars);
				if (index > -1) {
					replace = valVelTo.charAt(index) + replace.substring(1);
				} else {
					// x-szel, 1-gyel etc.
					String valVelFrom2 = "x14";
					String[] valVelTo2 = { "sz", "gy", "gy" };
					index = valVelFrom2.indexOf(prevChars);
					if (index > -1) {
						replace = valVelTo2[index] + replace.substring(1);
					}
				}
			} else if ((prevChars.length() == 2)
					&& prevChars.substring(1).equals("0")) {
				// (Currently the second part of the conditional is
				// unnecessary.)
				// 00-zal, 10-zel, 30-cal etc.
				// FIXME: A_{00}-val will be replaced to A_{00}-zal currently,
				// because we silently assume that 00 is preceeded by another
				// number.
				String valVelFrom = "013456789";
				String valVelTo = "zzcnnnnnn";
				int index = valVelFrom.indexOf(prevChars.charAt(0));
				if (index > -1) {
					replace = valVelTo.charAt(index) + replace.substring(1);
				} else {
					// 20-szal
					if (prevChars.charAt(0) == '2') {
						replace = "sz" + replace.substring(1);
					}
				}
			}
		}

		if ("".equals(replace)) {
			// No replace.
			return text;
		} else {
			int affixesLength = affixes.length();
			// Replace.
			text = text.substring(0, match) + "-" + replace
					+ text.substring(match + affixesLength);
			return text;
		}
	}


	public abstract void traceToSpreadsheet(GeoElement o);

	public abstract void resetTraceColumn(GeoElement o);

	

	/**
	 * @return the blockUpdateScripts
	 */
	public boolean isBlockUpdateScripts() {
		return blockUpdateScripts;
	}

	/**
	 * @param blockUpdateScripts
	 *            the blockUpdateScripts to set
	 */
	public void setBlockUpdateScripts(boolean blockUpdateScripts) {
		this.blockUpdateScripts = blockUpdateScripts;
	}

	private boolean blockUpdateScripts = false;



	public abstract String getInternalCommand(String s);

	public abstract void showError(String s);



	private boolean useBrowserForJavaScript = true;

	public void setUseBrowserForJavaScript(boolean useBrowserForJavaScript) {
		this.useBrowserForJavaScript = useBrowserForJavaScript;
	}

	public boolean useBrowserForJavaScript() {
		return useBrowserForJavaScript;
	}

	public abstract void initJavaScriptViewWithoutJavascript();

	public abstract Object getTraceXML(GeoElement geoElement);

	

	public final void changeLayer(GeoElement ge, int layer, int layer2) {
		EuclidianViewInterfaceCommon ev = getActiveEuclidianView();// app.getEuclidianView();
		if (ev != null) {
			ev.changeLayer(ge, ge.layer, layer2);
		}

	}
	
	public boolean is3D() {
		return false;
	}
	
	String[] fontSizeStrings = null;

	public String[] getFontSizeStrings() {
		if (fontSizeStrings == null) {
			fontSizeStrings = new String[] { getPlain("ExtraSmall"),
					getPlain("VerySmall"), getPlain("Small"),
					getPlain("Medium"), getPlain("Large"),
					getPlain("VeryLarge"), getPlain("ExtraLarge") };
		}

		return fontSizeStrings;
	}
	
	/* selection handling */

	final public GeoElement getLastCreatedGeoElement() {
		return kernel.getConstruction().getLastGeoElement();
	}
	
	// used by PropertyDialogGeoElement and MenuBarImpl
		// for the Rounding Menus
		final public static int roundingMenuLookup[] = { 0, 1, 2, 3, 4, 5, 10, 15,
				-1, 3, 5, 10, 15 };
		final public static int decimalsLookup[] = { 0, 1, 2, 3, 4, 5, -1, -1, -1,
				-1, 6, -1, -1, -1, -1, 7 };
		final public static int figuresLookup[] = { -1, -1, -1, 9, -1, 10, -1, -1,
				-1, -1, 11, -1, -1, -1, -1, 12 };

		public String[] getRoundingMenu() {
			String[] strDecimalSpaces = {
					getPlain("ADecimalPlaces", "0"),
					getPlain("ADecimalPlace", "1"),
					getPlain("ADecimalPlaces", "2"),
					getPlain("ADecimalPlaces", "3"),
					getPlain("ADecimalPlaces", "4"),
					getPlain("ADecimalPlaces", "5"),
					getPlain("ADecimalPlaces", "10"),
					getPlain("ADecimalPlaces", "15"),
					"---", // separator
					getPlain("ASignificantFigures", "3"),
					getPlain("ASignificantFigures", "5"),
					getPlain("ASignificantFigures", "10"),
					getPlain("ASignificantFigures", "15") };

			// zero is singular in eg French
			if (!isZeroPlural(getLanguage())) {
				strDecimalSpaces[0] = getPlain("ADecimalPlace", "0");
			}

			return strDecimalSpaces;
		}
		
		/*
		 * in French, zero is singular, eg 0 dcimale rather than 0 decimal places
		 */
		public boolean isZeroPlural(String lang) {
			if (lang.startsWith("fr")) {
				return false;
			}
			return true;
		}
		
		

		final public static String[] strDecimalSpacesAC = { "0 decimals",
				"1 decimals", "2 decimals", "3 decimals", "4 decimals",
				"5 decimals", "10 decimals", "15 decimals", "", "3 figures",
				"5 figures", "10 figures", "15 figures" };

		// Rounding Menus end

		public void deleteSelectedObjects() {
			if (letDelete()) {
				Object[] geos = getSelectedGeos().toArray();
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = (GeoElement) geos[i];
					if (!geo.isFixed()) {
						geo.removeOrSetUndefinedIfHasFixedDescendent();
					}
				}

				// also delete just created geos if possible
				ArrayList<GeoElement> geos2 = getActiveEuclidianView()
						.getEuclidianController().getJustCreatedGeos();
				for (int j = 0; j < geos2.size(); j++) {
					GeoElement geo = geos2.get(j);
					if (!geo.isFixed()) {
						geo.removeOrSetUndefinedIfHasFixedDescendent();
					}
				}
				getActiveEuclidianView().getEuclidianController()
						.clearJustCreatedGeos();
				getActiveEuclidianView().getEuclidianController().clearSelections();
				storeUndoInfo();
			}

		}


	/**
	 * geos must contain GeoElement objects only.
	 * 
	 * @param geos
	 */
	final public void setSelectedGeos(ArrayList<GeoElement> geos) {
		clearSelectedGeos(false);
		if (geos != null) {
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = geos.get(i);
				addSelectedGeo(geo, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/*
	 * Michael Borcherds 2008-03-03 modified to select all of a layer pass
	 * layer==-1 to select all objects
	 */
	final public void selectAll(int layer) {
		clearSelectedGeos(false);

		Iterator<GeoElement> it = kernel.getConstruction()
				.getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if ((layer == -1) || (geo.getLayer() == layer)) {
				addSelectedGeo(geo, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void invertSelection() {

		Iterator<GeoElement> it = kernel.getConstruction()
				.getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selectedGeos.contains(geo)) {
				removeSelectedGeo(geo, false);
			} else {
				addSelectedGeo(geo, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void selectAllPredecessors() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllPredecessors();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				addSelectedGeo(it2.next(), false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void showHideSelection() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			geo.setEuclidianVisible(!geo.isEuclidianVisible());
			geo.update();
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void showHideSelectionLabels() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.update();
		}
		kernel.notifyRepaint();
		updateSelection();
	}
	
	final public void selectAllDescendants() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllChildren();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				addSelectedGeo(it2.next(), false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}
	
	public void getKeyboardXML(StringBuilder sb) {
		sb.append("<keyboard width=\"");
		sb.append(getSettings().getKeyboard().getKeyboardWidth());
		sb.append("\" height=\"");
		sb.append(getSettings().getKeyboard().getKeyboardHeight());
		sb.append("\" opacity=\"");
		sb.append(getSettings().getKeyboard().getKeyboardOpacity());
		sb.append("\" language=\"");
		sb.append(getSettings().getKeyboard().getKeyboardLocale());
		sb.append("\" show=\"");
		sb.append(getSettings().getKeyboard().isShowKeyboardOnStart());
		sb.append("\"/>");
	}

	public abstract boolean freeMemoryIsCritical();

	public abstract long freeMemory();


	/**
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 */
	StringBuilder sbOrdinal;

	/*
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 */
	public String getOrdinalNumber(int n) {
		String lang = getLanguage();
		
		if ("en".equals(lang)) return getOrdinalNumberEn(n);

		// check here for languages where 1st = 1
		if ("pt".equals(lang) || "ar".equals(lang) || "cy".equals(lang)
				|| "fa".equals(lang) || "ja".equals(lang) || "ko".equals(lang)
				|| "lt".equals(lang) || "mr".equals(lang) || "ms".equals(lang)
				|| "nl".equals(lang) || "si".equals(lang) || "th".equals(lang)
				|| "vi".equals(lang) || "zh".equals(lang)) {
			return n + "";
		}

		if (sbOrdinal == null) {
			sbOrdinal = new StringBuilder();
		} else {
			sbOrdinal.setLength(0);
		}

		// prefixes
		if ("in".equals(lang)) {
			sbOrdinal.append("ke-");
		} else if ("iw".equals(lang)) {
			// prefix and postfix for Hebrew
			sbOrdinal.append("\u200f\u200e");
		}

		sbOrdinal.append(n);

		if ("cs".equals(lang) || "da".equals(lang) || "et".equals(lang)
				|| "eu".equals(lang) || "hr".equals(lang) || "hu".equals(lang)
				|| "is".equals(lang) || "no".equals(lang) || "sk".equals(lang)
				|| "sr".equals(lang) || "tr".equals(lang)) {
			sbOrdinal.append('.');
		} else if ("de".equals(lang)) {
			sbOrdinal.append("th");
		} else if ("fi".equals(lang)) {
			sbOrdinal.append(":s");
		} else if ("el".equals(lang)) {
			sbOrdinal.append('\u03b7');
		} else if ("ro".equals(lang) || "ca".equals(lang) || "es".equals(lang)
				|| "it".equals(lang) || "pt".equals(lang)) {
			sbOrdinal.append(Unicode.FEMININE_ORDINAL_INDICATOR);
		} else if ("bs".equals(lang) || "sl".equals(lang)) {
			sbOrdinal.append("-ti");
		} else if ("sq".equals(lang)) {
			sbOrdinal.append("-te");
		} else if ("gl".equals(lang)) {
			sbOrdinal.append("ava");
		} else if ("mk".equals(lang)) {
			sbOrdinal.append("-\u0442\u0438");
		} else if ("ka".equals(lang)) {
			sbOrdinal.append("-\u10d4");
		} else if ("iw".equals(lang)) {
			sbOrdinal.append("\u200e\u200f");
		} else if ("ru".equals(lang) || "uk".equals(lang)) {
			sbOrdinal.append("-\u0433\u043e");
		} else if ("fr".equals(lang)) {
			if (n == 1) {
				sbOrdinal.append("er"); // could also be "re" for feminine...
			} else {
				sbOrdinal.append("e"); // could also be "es" for plural...
			}
		} else if ("sv".equals(lang)) {
			int unitsDigit = n % 10;
			if ((unitsDigit == 1) || (unitsDigit == 2)) {
				sbOrdinal.append(":a");
			} else {
				sbOrdinal.append(":e");
			}
		} 

		return sbOrdinal.toString();

	}

	
	/**
	 * given 1, return eg 1st (English only)
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 */
	public String getOrdinalNumberEn(int n) {
		/*
		 * http://en.wikipedia.org/wiki/Names_of_numbers_in_English If the
		 * tens digit of a number is 1, then write "th" after the number.
		 * For example: 13th, 19th, 112th, 9,311th. If the tens digit is not
		 * equal to 1, then use the following table: If the units digit is:
		 * 0 1 2 3 4 5 6 7 8 9 write this after the number th st nd rd th th
		 * th th th th
		 */

		int tensDigit = (n / 10) % 10;

		if (tensDigit == 1) {
			return n + "th";
		}

		int unitsDigit = n % 10;

		switch (unitsDigit) {
		case 1:
			return n + "st";
		case 2:
			return n + "nd";
		case 3:
			return n + "rd";
		default:
			return n + "th";
		}

	}
	
	/**
	 * Use localized digits.
	 */
	private boolean useLocalizedDigits = false;

	/**
	 * @return If localized digits are used for certain languages (Arabic,
	 *         Hebrew, etc).
	 */
	public boolean isUsingLocalizedDigits() {
		return useLocalizedDigits;
	}

	/**
	 * Use localized digits for certain languages (Arabic, Hebrew, etc).
	 * 
	 * Calls {@link #updateReverseLanguage(Locale)} to apply the change, but
	 * just if the new flag differs from the current.
	 */
	public void setUseLocalizedDigits(boolean useLocalizedDigits) {
		if (this.useLocalizedDigits == useLocalizedDigits) {
			return;
		}

		this.useLocalizedDigits = useLocalizedDigits;
		updateReverseLanguage(getLanguage());
		getKernel().updateConstruction();
		setUnsaved();

		if (euclidianView != null) {
			euclidianView.updateBackground();
		}
	}
	// for Basque and Hungarian you have to say "A point" instead of "point A"
		private boolean reverseNameDescription = false;
		private boolean isAutoCompletePossible = true;

		
		final public boolean isReverseNameDescriptionLanguage() {
			// for Basque and Hungarian
			return reverseNameDescription;
		}

		/**
		 * Returns whether autocomplete should be used at all. Certain languages
		 * make problems with auto complete turned on (e.g. Korean).
		 */
		final public boolean isAutoCompletePossible() {
			return isAutoCompletePossible;
		}

		// For Hebrew and Arabic. Guy Hed, 25.8.2008
		private boolean rightToLeftReadingOrder = false;

		final public boolean isRightToLeftReadingOrder() {
			return rightToLeftReadingOrder;
		}

		// For Persian and Arabic.
		private boolean rightToLeftDigits = false;

		final public boolean isRightToLeftDigits() {
			if (!Kernel.internationalizeDigits) {
				return false;
			}
			return rightToLeftDigits;
		}

		
	protected void updateReverseLanguage(String lang) {

		
		// reverseLanguage = "zh".equals(lang); removed Michael Borcherds
		// 2008-03-31
		reverseNameDescription = "eu".equals(lang) || "hu".equals(lang);

		// used for axes labels
		rightToLeftDigits = ("ar".equals(lang) || "fa".equals(lang));

		// Guy Hed, 25.8.2008
		// Guy Hed, 26.4.2009 - added Yiddish and Persian as RTL languages
		rightToLeftReadingOrder = ("iw".equals(lang) || "ar".equals(lang)
				|| "fa".equals(lang) || "ji".equals(lang));

		// Another option:
		// rightToLeftReadingOrder =
		// (Character.getDirectionality(getPlain("Algebra").charAt(1)) ==
		// Character.DIRECTIONALITY_RIGHT_TO_LEFT);

		// turn off auto-complete for Korean
		isAutoCompletePossible = true;// !"ko".equals(lang);

		// defaults
		unicodeDecimalPoint = '.';
		unicodeComma = ',';
		// unicodeThousandsSeparator=',';

		if (isUsingLocalizedDigits()) {
			if (lang.startsWith("ar")) { // Arabic
				unicodeZero = '\u0660'; // Arabic-Indic digit 0
				unicodeDecimalPoint = '\u066b'; // Arabic-Indic decimal point
				unicodeComma = '\u060c'; // Arabic comma
				// unicodeThousandsSeparator = '\u066c'; // Arabic Thousands
				// separator
			} else if (lang.startsWith("fa")) { // Persian
				unicodeZero = '\u06f0'; // Persian digit 0 (Extended
										// Arabic-Indic)
				unicodeDecimalPoint = '\u066b'; // Arabic comma
				unicodeComma = '\u060c'; // Arabic-Indic decimal point
				// unicodeThousandsSeparator = '\u066c'; // Arabic Thousands
				// separators
			} else if (lang.startsWith("ml")) {
				unicodeZero = '\u0d66'; // Malayalam digit 0
			} else if (lang.startsWith("th")) {
				unicodeZero = '\u0e50'; // Thai digit 0
			} else if (lang.startsWith("ta")) {
				unicodeZero = '\u0be6'; // Tamil digit 0
			} else if (lang.startsWith("sd")) {
				unicodeZero = '\u1bb0'; // Sudanese digit 0
			} else if (lang.startsWith("kh")) {
				unicodeZero = '\u17e0'; // Khmer digit 0
			} else if (lang.startsWith("mn")) {
				unicodeZero = '\u1810'; // Mongolian digit 0
			} else if (lang.startsWith("mm")) {
				unicodeZero = '\u1040'; // Mayanmar digit 0
			} else {
				unicodeZero = '0';
			}
		} else {
			unicodeZero = '0';
		}
	}

	public void updateRightAngleStyle() {
		if (rightAngleStyle != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE) {
			if (getLanguage().equals("de")
					|| getLanguage().equals("hu")) {
				rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT;
			} else {
				rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;
			}
		}
	}

	public double getXmin() {
		// TODO Auto-generated method stub
		return getEuclidianView().getXmin();
	}

	public double getXmax() {
		// TODO Auto-generated method stub
		return getEuclidianView().getXmax();
	}

	
	public double getXminForFunctions() {
		// TODO Auto-generated method stub
		return ((AbstractEuclidianView)getEuclidianView()).getXminForFunctions();
	}

	
	public double getXmaxForFunctions() {
		// TODO Auto-generated method stub
		return ((AbstractEuclidianView)getEuclidianView()).getXmaxForFunctions();
	}

	public int getMaxLayerUsed() {
		return maxLayerUsed;
	}

	
	public double countPixels(double min, double max) {
		AbstractEuclidianView ev = (AbstractEuclidianView)getEuclidianView();
		return ev.toScreenCoordXd(max) - ev.toScreenCoordXd(min);
	}


	public abstract AlgebraView getAlgebraView();

	public EuclidianViewInterfaceCommon getEuclidianView(){
		return euclidianView;
	}

	public abstract EuclidianViewInterfaceCommon getActiveEuclidianView();


	

	public abstract boolean isShowingEuclidianView2();

	public abstract AbstractImageManager getImageManager();
	
	public abstract GuiManager getGuiManager();

	// Michael Borcherds 2008-06-22
	public static void printStacktrace(String message) {
		try {

			throw new Exception(message);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public static void debug(Object s) {
		if (s == null) {
			doDebug("<null>", false, false, 0);
		} else {
			doDebug(s.toString(), false, false, 0);
		}
	}

	public static void debug(Object s[]) {
		debug(s, 0);
	}

	static StringBuilder debugSb = null;

	public static void debug(Object[] s, int level) {
		if (debugSb == null) {
			debugSb = new StringBuilder();
		} else {
			debugSb.setLength(0);
		}

		for (int i = 0; i < s.length; i++) {
			debugSb.append(s[i]);
			debugSb.append('\n');
		}

		debug(debugSb, level);
	}

	public static void debug(Object s, int level) {
		doDebug(s.toString(), false, false, level);
	}

	public static void debug(Object s, boolean showTime, boolean showMemory,
			int level) {
		doDebug(s.toString(), showTime, showMemory, level);
	}

	public static DebugPrinter dbg;
	private static Set<String> reportedImpls = new TreeSet<String>();

	public boolean isMacOS() {
		return false;
	}

	public boolean isWindows() {
		return false;
	}

	public boolean isWindowsVistaOrLater() {
		return false;
	}
				

	private static boolean miniPropertiesActive = true;
	// Michael Borcherds 2008-06-22
	private static void doDebug(String s, boolean showTime, boolean showMemory,
			int level) {

		String ss = s == null ? "<null>" : s;

		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();

		// String calleeMethod = elements[0].getMethodName();
		String callerMethodName = elements[2].getMethodName();
		String callerClassName = elements[2].getClassName();
		if(s.toLowerCase().equals("implementation needed")){
			if(reportedImpls.contains(callerClassName+callerMethodName))
				return;
			reportedImpls.add(callerClassName+callerMethodName);
		}
		StringBuilder sb = new StringBuilder("*** Message from ");
		sb.append("[");
		sb.append(callerClassName);
		sb.append(".");
		sb.append(callerMethodName);
		sb.append("]");

		if ((dbg != null) && showTime) {
			dbg.getTimeInfo(sb);
		}

		if ((dbg != null) && showMemory) {
			dbg.getMemoryInfo(sb);

		}
		if (dbg == null) {
			System.out.println(ss + sb.toString());
		} else {
			dbg.print(ss, sb.toString(), level);
		}

	}
	/**
	 * @return the scriptingLanguage
	 */
	public String getScriptingLanguage() {
		// in some files we stored language="null" accidentally
		if ("null".equals(scriptingLanguage)) {
			scriptingLanguage = null;
		}
		return scriptingLanguage;
	}
	
	/**
	 * @param scriptingLanguage
	 *            the scriptingLanguage to set
	 */
	public void setScriptingLanguage(String scriptingLanguage) {
		this.scriptingLanguage = scriptingLanguage;
	}

	@SuppressWarnings("deprecation")
	public static boolean isWhitespace(char charAt) {
		return Character.isSpace(charAt);
	}

	/**
	 * important to use this rather than String.toLowerCase() as this is overridden in 
	 * desktop.Application so that it uses  String.toLowerCase(Locale.US)
	 * so that the behavior is well defined whatever language we are running in
	 * NB does cause problems eg in Turkish
     * @param s 
     * @return the <code>String</code>, converted to lowercase.
     * @see     java.lang.String#toUpperCase(Locale)
	 */
	public String toLowerCase(String s) {
		return s.toLowerCase();
	}

	/**
	 * important to use this rather than String.toUpperCase() as this is overridden in 
	 * desktop.Application so that it uses  String.toUpperCase(Locale.US)
	 * so that the behavior is well defined whatever language we are running in
	 * NB does cause problems eg in Turkish
     * @param s 
     * @return the <code>String</code>, converted to uppercase.
     * @see     java.lang.String#toUpperCase(Locale)
	 */
	public String toUpperCase(String s) {
		return s.toUpperCase();
	}

	public abstract void evalScript(AbstractApplication app, String script,
			String arg);

	private int[] version = null;
	
	public boolean fileVersionBefore(int[] v) {
		if (this.version == null) {
			return true;
		}

		int length = version.length;
		if (v.length < length) {
			length = v.length;
		}

		for (int i = 0; i < length; i++) {
			if (version[i] < v[i]) {
				return true;
			} else if (version[i] > v[i]) {
				return false;
			}
		}

		return version.length < v.length;
	}

	public void setFileVersion(String version) {

		// AbstractApplication.debug("file version: " + version);

		if (version == null) {
			this.version = null;
			return;
		}

		this.version = getSubValues(version);
	}

	static final public int[] getSubValues(String version) {
		String[] values = version.split("\\.");
		int[] ret = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			ret[i] = Integer.parseInt(values[i]);
		}

		return ret;
	}

	
	public static boolean isMiniPropertiesActive() {
		return miniPropertiesActive;
	}

	public static void setMiniPropertiesActive(boolean active) {
		miniPropertiesActive = active;
		// Application.debug("miniprops active:"+miniPropertiesActive);
	}

	public abstract AbstractEuclidianView createEuclidianView();
	
	final public int getMode() {
		return this.createEuclidianView().getMode();
	}

	/**
	 * @deprecated added when refactoring
	 */
	@Deprecated
	public abstract void updateConstructionProtocol();

	public int getCurrentLabelingStyle() {
		if (getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
			if (isUsingFullGui()) {
				if ((getGuiManager() != null)
						&& getGuiManager().hasAlgebraView()) {
					return getAlgebraView().isVisible() ? ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS
							: ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF;
				}
				return ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF;
			}
			return ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS;
		}
		return getLabelingStyle();
	}


	public abstract String reverseGetColor(String colorName);

	public abstract String getColor(String string);

	public int getMD5folderLength(String fullPath) {
		return 32;
	}

	public abstract BufferedImage getExternalImageAdapter(String filename);

	public abstract String getCommandSyntax(String cmd);

	final public void clearSelectedGeos() {
		clearSelectedGeos(true);
	}

	public void clearSelectedGeos(boolean repaint) {
		int size = selectedGeos.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				GeoElement geo = selectedGeos.get(i);
				geo.setSelected(false);
			}
			selectedGeos.clear();
			if (repaint) {
				kernel.notifyRepaint();
			}
		}
		updateSelection();
	}


	final public boolean isLabelDragsEnabled() {
		return labelDragsEnabled;
	}

	/**
	 * Enables or disables label dragging in this application. This is useful
	 * for applets.
	 */
	public void setLabelDragsEnabled(boolean flag) {
		labelDragsEnabled = flag;
	}

	public void setScrollToShow(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setUnsaved() {
		// TODO Auto-generated method stub

	}

	public void setActiveView(int viewEuclidian2) {
		// TODO Auto-generated method stub

	}
	
	public void refreshViews() {
		getEuclidianView().updateBackground();
		if (hasEuclidianView2()) {
			getEuclidianView2().updateBackground();
		}
		kernel.notifyRepaint();
	}
	
	/**
	 * Switches the application to macro editing mode
	 * 
	 * @author Zbynek Konecny
	 * @version 2010-05-26
	 * @param macro
	 *            Tool to be edited
	 */
	public void openMacro(Macro macro) {
		String allXml = getXML();
		String header = allXml.substring(0, allXml.indexOf("<construction"));
		String footer = allXml.substring(allXml.indexOf("</construction>"),
				allXml.length());
		StringBuilder sb = new StringBuilder();
		macro.getXML(sb);
		String macroXml = sb.toString();
		String newXml = header
				+ macroXml.substring(macroXml.indexOf("<construction"),
						macroXml.indexOf("</construction>")) + footer;
		this.macro = macro;
		setXML(newXml, true);
	}
	
	private Macro macro;

	/**
	 * Returns macro if in macro editing mode.
	 * 
	 * @return macro being edited (in unchanged state)
	 */
	public Macro getMacro() {
		return macro;
	}

	private int labelingStyle = ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY;

	public boolean hasEuclidianView2() {
		// TODO Auto-generated method stub
		return false;
	}

	public abstract void showRelation(GeoElement geoElement,
			GeoElement geoElement2);

	public abstract void showError(MyError e);
	
	public String getXML(){
		//TODO unimplemented
		return "";
	}

	public abstract void showError(String string, String str);

	public View getView(int viewID) {

		// check for PlotPanel ID family first
		if ((getGuiManager() != null)
				&& (getGuiManager().getPlotPanelView(viewID) != null)) {
			return getGuiManager().getPlotPanelView(viewID);
		}

		else {
			switch (viewID) {
			case VIEW_EUCLIDIAN:
				return getEuclidianView();
			case VIEW_ALGEBRA:
				return getAlgebraView();
			case VIEW_SPREADSHEET:
				return getGuiManager().getSpreadsheetView();
			case VIEW_CAS:
				return getGuiManager().getCasView();
			case VIEW_EUCLIDIAN2:
				return hasEuclidianView2() ? getEuclidianView2() : null;
			case VIEW_CONSTRUCTION_PROTOCOL:
				return getGuiManager().getConstructionProtocolData();
			case VIEW_PROBABILITY_CALCULATOR:
				return getGuiManager().getProbabilityCalculator();
				// case VIEW_FUNCTION_INSPECTOR: return (View)getGuiManager()..
				// case VIEW_INSPECTOR: return
				// (View)getGuiManager().getSpreadsheetView();
				// case VIEW_EUCLIDIAN3D: return
				// (View)getGuiManager().getSpreadsheetView();
				// case VIEW_EUCLIDIAN_FOR_PLANE: return
				// (View)getGuiManager().getSpreadsheetView();
				// case VIEW_TEXT_PREVIEW: return
				// (View)getGuiManager().getSpreadsheetView();
			}
		}

		return null;
	}


	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save gui tag settings
		sb.append(getGuiXML(asPreference));

		// save euclidianView settings
		getEuclidianView().getXML(sb, asPreference);

		// save euclidian view 2 settings
		if (hasEuclidianView2()) {
			getEuclidianView2().getXML(sb, asPreference);
		} else if (asPreference && (getGuiManager() != null)) {
			getEuclidianView2().getXML(sb, true);
		}

		// save spreadsheetView settings
		if (getGuiManager().hasSpreadsheetView()) {
			getGuiManager().getSpreadsheetViewXML(sb, asPreference);
		}

		// save AlgebraView settings
		// if (getGuiManager().hasAlgebraView()){
		// getGuiManager().getAlgebraViewXML(sb);
		// }

		// save ProbabilityCalculator settings
		if (getGuiManager().hasProbabilityCalculator()) {
			getGuiManager().getProbabilityCalculatorXML(sb);
		}
		if (asPreference) {
			getKeyboardXML(sb);
		}
		// coord style, decimal places settings etc
		kernel.getKernelXML(sb, asPreference);
		getScriptingXML(sb, asPreference);
		// save cas view seeting and cas session
		// if (casView != null) {
		// sb.append(((geogebra.cas.view.CASView) casView).getGUIXML());
		// sb.append(((geogebra.cas.view.CASView) casView).getSessionXML());
		// }

		return sb.toString();
	}

	private void getScriptingXML(StringBuilder sb, boolean asPreference) {
		sb.append("<scripting");
		if (getScriptingLanguage() != null) {
			sb.append(" language=\"");
			sb.append(getScriptingLanguage());
			sb.append("\"");
		}
		sb.append(" blocked=\"");
		sb.append(isBlockUpdateScripts());

		if (!asPreference) {
			sb.append("\" disabled=\"");
			sb.append(isScriptingDisabled());
		}

		sb.append("\"/>\n");
	}

	final public Settings getSettings() {
		return settings;
	}

	


	


	public void setUniqueId(String uniqueId) {
		// TODO Auto-generated method stub
		
	}

	public void setShowAuxiliaryObjects(boolean auxiliaryObjects) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Sets labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 */
	public void setLabelingStyle(int labelingStyle) {
		this.labelingStyle = labelingStyle;
	}
	
	/**
	 * Returns labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 */
	public int getLabelingStyle() {
		return labelingStyle;
	}

	/**
	 * @return the scriptingDisabled
	 */
	public boolean isScriptingDisabled() {
		return scriptingDisabled;
	}

	/**
	 * @param sd
	 *            the scriptingDisabled to set
	 */
	public void setScriptingDisabled(boolean sd) {
		this.scriptingDisabled = sd;
	}

	private boolean scriptingDisabled = false;



	boolean reverseMouseWheel = false;

	public boolean isMouseWheelReversed() {
		return reverseMouseWheel;
	}

	public void reverseMouseWheel(boolean b) {
		reverseMouseWheel = b;
	}


	

	public void setPreferredSize(Dimension size) {
		// TODO Auto-generated method stub
		
	}

	public int getTooltipTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Use localized labels.
	 */
	private boolean useLocalizedLabels = true;

	/**
	 * @return If localized labels are used for certain languages.
	 */
	public boolean isUsingLocalizedLabels() {
		return useLocalizedLabels;
	}

	/**
	 * Use localized labels for certain languages.
	 */
	public void setUseLocalizedLabels(boolean useLocalizedLabels) {
		this.useLocalizedLabels = useLocalizedLabels;
	}

	public void setTooltipLanguage(String ttl) {
		// TODO Auto-generated method stub
		
	}
	
	public abstract DrawEquationInterface getDrawEquation();

	public void setShowConstructionProtocolNavigation(boolean show) {
		// TODO Auto-generated method stub
		
	}

	public void setTmpPerspectives(ArrayList<Perspective> tmp_perspectives) {
		// TODO Auto-generated method stub
		
	}

	public abstract void setShowConstructionProtocolNavigation(boolean show,
			boolean playButton, double playDelay, boolean showProtButton);

	

	public void setTooltipTimeout(int ttt) {
		// TODO Auto-generated method stub
		
	}

	

	public AbstractEuclidianView getEuclidianView2() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return width of the whole application (central panel)
	 * This is needed for Corner[6]
	 */
	public abstract double getWidth();
	
	public abstract double getHeight();

	public Font getFontCommon(boolean b, int i, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract Font getPlainFontCommon();

	public boolean isExporting() {
		// TODO Auto-generated method stub
		return false;
	}

	public final ArrayList<GeoElement> getSelectedGeos() {
		return selectedGeos;
	}

	public final void addSelectedGeo(GeoElement geo) {
		addSelectedGeo(geo, true);
	}

	public final void addSelectedGeo(GeoElement geo, boolean repaint) {
		if ((geo == null) || selectedGeos.contains(geo)) {
			return;
		}
	
		selectedGeos.add(geo);
		geo.setSelected(true);
		if (repaint) {
			((Kernel)kernel).notifyRepaint();
		}
		updateSelection();
	
	}

	public final void addSelectedGeos(ArrayList<GeoElement> geos, boolean repaint) {
	
		selectedGeos.addAll(geos);
		for (int i = 0; i < geos.size(); i++) {
			geos.get(i).setSelected(true);
		}
		if (repaint) {
			((Kernel)kernel).notifyRepaint();
		}
		updateSelection();
	}
	
	/**
	 * init the kernel (used for 3D)
	 */
	public void initKernel() {
		kernel = new Kernel(this);
	}

	/**
	 * init the EuclidianView (and EuclidianView3D for 3D)
	 */
	public void initEuclidianViews() {

		euclidianController = newEuclidianController( kernel);
		euclidianView = newEuclidianView(showAxes, showGrid);
		euclidianView.setAntialiasing(antialiasing);
	}
	abstract protected AbstractEuclidianView newEuclidianView(boolean[] showAxes,boolean showGrid);
	abstract protected AbstractEuclidianController newEuclidianController(Kernel kernel);
	/**
	 * @deprecated
	 * @param cons
	 * @return undo manager
	 */
	@Deprecated
	public abstract AbstractUndoManager getUndoManager(Construction cons);

	public abstract AbstractAnimationManager newAnimationManager(Kernel kernel2);

	public abstract GeoElementGraphicsAdapter newGeoElementGraphicsAdapter();

	public void repaintSpreadsheet() {
		// TODO Auto-generated method stub
		
	}
	public final boolean isOnTheFlyPointCreationActive() {
		return isOnTheFlyPointCreationActive;
	}

	

	public boolean isEqualsRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public abstract SpreadsheetTraceManager getTraceManager();

	public void setDefaultCursor() {
		// TODO Auto-generated method stub
		
	}

	public void setWaitCursor() {
		// TODO Auto-generated method stub
		
	}

	public abstract AlgoElement newAlgoShortestDistance(Construction cons,
			String label, GeoList list, GeoPointND start, GeoPointND end,
			GeoBoolean weighted);

	public abstract void updateStyleBars();

	/**
	 * @param element
	 */
	final public void toggleSelectedGeo(GeoElement geo) {
		toggleSelectedGeo(geo, true);
	}

	final public void toggleSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null) {
			return;
		}

		boolean contains = selectedGeos.contains(geo);
		if (contains) {
			selectedGeos.remove(geo);
			geo.setSelected(false);
		} else {
			selectedGeos.add(geo);
			geo.setSelected(true);
		}

		if (repaint) {
			kernel.notifyRepaint();
		}
		updateSelection();
	}


	public void setMoveMode() {
		setMode(EuclidianConstants.MODE_MOVE);
	}

	
	
	public abstract AbstractSpreadsheetTableModel getSpreadsheetTableModel();
	
	public void setMode(int mode) {
		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			currentSelectionListener = null;
		}

		if (getGuiManager() != null) {
			getGuiManager().setMode(mode);
		} else if (euclidianView != null) {
			euclidianView.setMode(mode);
		}
	}


	public void addToEuclidianView(GeoElement geo) {
		geo.addView(AbstractApplication.VIEW_EUCLIDIAN);
		getEuclidianView().add(geo);
	}

	public void removeFromEuclidianView(GeoElement geo) {
		geo.removeView(AbstractApplication.VIEW_EUCLIDIAN);
		getEuclidianView().remove(geo);
	}

	// TODO remove this after ggb v>=5 (replace with same from Application3D)
	public AbstractEuclidianView createEuclidianViewForPlane(Object plane) {
		return null;
	}

	public abstract void setXML(String string, boolean b);

	public abstract GgbAPI getGgbApi();

	public abstract SoundManager getSoundManager();

	public final Kernel getKernel() {
		return kernel;
	}

	abstract public CommandProcessor newCmdBarCode();

	public final int selectedGeosSize() {
		return selectedGeos.size();
	}

	public boolean isRightClick(AbstractEvent e) {
		return e.isRightClick();
	}
	
	public boolean isControlDown(AbstractEvent e) {
		return e.isControlDown();
	}
	
	public boolean isMiddleClick(AbstractEvent e) {
		return e.isMiddleClick();
	}

	public abstract boolean showAlgebraInput();

	public abstract GlobalKeyDispatcher getGlobalKeyDispatcher();

	public abstract void evalPythonScript(AbstractApplication app, String string,
			String arg);

	public abstract void callAppletJavaScript(String string, Object[] args);
 
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (currentSelectionListener != null) {
			currentSelectionListener.geoElementSelected(geo, addToSelection);
		}
	}

	private PropertiesView propertiesView;

	protected boolean shiftDragZoomEnabled = true;

	// used when a secondary language is being used for tooltips 
	protected boolean tooltipFlag = false;

	public void setPropertiesView(PropertiesView propertiesView) {
		this.propertiesView = propertiesView;
	}

	/**
	 * Sets a mode where clicking on an object will notify the given selection
	 * listener.
	 */
	public void setSelectionListenerMode(GeoElementSelectionListener sl) {
		currentSelectionListener = sl;
		if (sl != null) {
			setMode(EuclidianConstants.MODE_SELECTION_LISTENER);
		} else {
			setMoveMode();
		}
	}
	
	public void updateSelection() {
		if (!showMenuBar || !isUsingFullGui() || isIniting()) {
			return;
		}

		// put in to check possible bottleneck
		// Application.debug("Update Selection");

		getGuiManager().updateMenubarSelection();

		if (getEuclidianView().getMode() == EuclidianConstants.MODE_VISUAL_STYLE) {
			if (selectedGeos.size() > 0) {
				getEuclidianView().getStyleBar().applyVisualStyle(selectedGeos);
			}
		}

		if (getEuclidianView().getMode() == EuclidianConstants.MODE_MOVE) {
			updateStyleBars();
		}

		if (propertiesView != null) {
			propertiesView.updateSelection();
		}
	}
	final public boolean containsSelectedGeo(GeoElement geo) {
		return selectedGeos.contains(geo);
	}

	final public void removeSelectedGeo(GeoElement geo) {
		removeSelectedGeo(geo, true);
	}

	final public void removeSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null) {
			return;
		}

		selectedGeos.remove(geo);
		geo.setSelected(false);
		if (repaint) {
			kernel.notifyRepaint();
		}
		updateSelection();
	}

	final public void selectNextGeo() {

		TreeSet<GeoElement> tree = kernel.getConstruction()
				.getGeoSetLabelOrder();

		TreeSet<GeoElement> copy = new TreeSet<GeoElement>(tree);

		Iterator<GeoElement> it = copy.iterator();

		// remove geos that don't have isSelectionAllowed()==true
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed()) {
				tree.remove(geo);
			}
		}

		it = tree.iterator();

		// none selected, select first geo
		if (selectedGeos.size() == 0) {
			if (it.hasNext()) {
				addSelectedGeo(it.next());
			}
			return;
		}

		if (selectedGeos.size() != 1) {
			return;
		}

		// one selected, select next one
		GeoElement selGeo = selectedGeos.get(0);
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selGeo == geo) {
				removeSelectedGeo(selGeo);
				if (!it.hasNext()) {
					it = tree.iterator();
				}
				addSelectedGeo(it.next());
				break;
			}
		}
	}

	final public void selectLastGeo() {
		if (selectedGeos.size() != 1) {
			return;
		}
		GeoElement selGeo = selectedGeos.get(0);
		GeoElement lastGeo = null;
		TreeSet<GeoElement> tree = kernel.getConstruction()
				.getGeoSetLabelOrder();
		TreeSet<GeoElement> copy = new TreeSet<GeoElement>(tree);
		Iterator<GeoElement> it = copy.iterator();

		// remove geos that don't have isSelectionAllowed()==true
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed()) {
				tree.remove(geo);
			}
		}

		it = tree.iterator();
		while (it.hasNext()) {
			lastGeo = it.next();
		}

		it = tree.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selGeo == geo) {
				removeSelectedGeo(selGeo);
				addSelectedGeo(lastGeo);
				break;
			}
			lastGeo = geo;
		}
	}

	
	public abstract boolean isIniting();

	public abstract boolean isRightClickEnabled();

	public final boolean isShiftDragZoomEnabled() {
		return shiftDragZoomEnabled;
	}

	public final void setShiftDragZoomEnabled(boolean shiftDragZoomEnabled) {
		this.shiftDragZoomEnabled = shiftDragZoomEnabled;
	}

	public abstract void updateMenubar();

	public int getFontSize() {
		return appFontSize;
	}
	public void setFontSize(int points) {
		setFontSize(points, true);
	}
	public void setFontSize(int points, boolean update) {
		if (points == appFontSize) {
			return;
		}
		appFontSize = points;
		//isSaved = false;
		if (!update) {
			return;
		}

		resetFonts();

		updateUI();
	}
	public abstract void updateUI();
	
	public void clearTooltipFlag() {
		tooltipFlag = false;
	}
	
	public void resetFonts() {
		getFontManager().setFontSize(getGUIFontSize());
		updateFonts();
	}

	public int getGUIFontSize() {
		return guiFontSize == -1 ? getFontSize() : guiFontSize;
	}

	public void setGUIFontSize(int size) {
		guiFontSize = size;
		updateFonts();
		//isSaved = false;

		resetFonts();

		updateUI();
	}


	protected AbstractFontManager getFontManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateFonts() {
		if (euclidianView != null) {
			euclidianView.updateFonts();
		}

		if (getGuiManager() != null) {
			getGuiManager().updateFonts();
			if (hasEuclidianView2()) {
				getEuclidianView2().updateFonts();
			}
		}

	}
	
	/**
	 * Returns a font that can display testString.
	 */
	public Font getFontCanDisplay(String testString) {
		return getFontCanDisplay(testString, false, Font.PLAIN, getFontSize());
	}

	/**
	 * Returns a font that can display testString.
	 */
	public Font getFontCanDisplay(String testString, int fontStyle) {
		return getFontCanDisplay(testString, false, fontStyle, getFontSize());
	}

	/**
	 * Returns a font that can display testString.
	 */
	public Font getFontCanDisplay(String testString, boolean serif,
			int fontStyle, int fontSize) {
		return getFontManager().getFontCanDisplay(testString, serif, fontStyle,
				fontSize);
	}
	
	/**
	 * Returns gui settings in XML format
	 */
	public String getGuiXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();
		sb.append("<gui>\n");

		getWindowLayoutXML(sb,asPreference);
		// just save mouse settings as preference
		if (asPreference) {
			sb.append("\t<mouse reverseWheel=\"");
			sb.append(isMouseWheelReversed());
			sb.append("\"/>\n");
		}

		sb.append("\t<font ");
		sb.append(" size=\"");
		sb.append(getFontSize());
		sb.append("\"/>\n");

		if (asPreference) {
			sb.append("\t<menuFont ");
			sb.append(" size=\"");
			//sb.append(guiFontSize);
			sb.append("\"/>\n");

			sb.append("\t<tooltipSettings ");
			if (getTooltipLanguageString() != null) {
				sb.append(" language=\"");
				sb.append(getTooltipLanguageString());
				sb.append("\"");
			}
			sb.append(" timeout=\"");
			sb.append(getTooltipTimeout());
			sb.append("\"");

			sb.append("/>\n");
		}

		if (!asPreference) {
			sb.append("\t<graphicsSettings");
			sb.append(" javaLatexFonts=\"");
			sb.append(useJavaFontsForLaTeX());
			sb.append("\"/>\n");
		}

		sb.append(getConsProtocolXML());

		sb.append("</gui>\n");

		return sb.toString();
	}
	public String getConsProtocolXML() {
		if (getGuiManager() == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		// construction protocol
		if (getGuiManager().isUsingConstructionProtocol()) {
			getGuiManager().getConsProtocolXML(sb);
		}

		return sb.toString();
	}
	public abstract String getTooltipLanguageString();
	protected abstract void getWindowLayoutXML(StringBuilder sb, boolean asPreference);

	public abstract void reset();

	public abstract boolean hasPythonBridge();

	public abstract PythonBridge getPythonBridge();

	public abstract String getPlainTooltip(String string);

	public GeoElementSelectionListener getCurrentSelectionListener() {
		return currentSelectionListener;
	}

	public void setCurrentSelectionListener(GeoElementSelectionListener sl) {
		currentSelectionListener = sl;
	}
	public void setShowResetIcon(boolean flag) {
		if (flag != showResetIcon) {
			showResetIcon = flag;
			euclidianView.updateBackground();
		}
	}

	final public boolean showResetIcon() {
		return showResetIcon && !runningInFrame;
	}

	public boolean isUndoActive() {
		return kernel.isUndoActive();
	}

	public static native void console(String string) /*-{
		if ($wnd && $wnd.console) {
			$wnd.console.log(string);
		}
	}-*/;
	

}
