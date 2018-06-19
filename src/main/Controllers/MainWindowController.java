package main.Controllers;

import main.Alerts.AlertsFXML;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import main.Logic.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class MainWindowController extends SceneGenerator implements Initializable {

    public ListView<String> listView;
    public Button searchButton;
    public TextArea patternTextArea;
    public MenuBar menuBar;
    public ComboBox<String> searchAlgorithmComboBox, modeComboBox;
    public Accordion accordion;
    public TitledPane pane1, pane2, pane3, textPane;
    public Label textFileSizeLabel, textFileNameLabel, textCharactersLabel,
            patternFileSizeLabel, patternFileNameLabel, patternCharactersLabel, patternTerminalsLabel,
            grammarFileNameLabel, grammarTerminalsLabel, grammarNonTerminalsLabel, grammarRulesLabel, grammarSizeLabel,
            allPermutationsLabel, validPermutationsLabel, matchesLabel;
    private static Set<String> unique = new HashSet<>();
    private static HashMap<Integer, String> matchedPermutations = new HashMap<>();

    @FXML
    public void addGrammar() {
        File grammarFile = getFileFromFileChooser("src\\resources\\GrammarFiles", "Grammar files", "grm");
        if (grammarFile != null) {
            GrammarParser.parseGrammarFile(grammarFile.getPath()).displayGrammar();
            Core.getInstance().setGrammar(GrammarParser.parseGrammarFile(grammarFile.getPath()));
            Core.getInstance().setTerminalsToPermute(null);
            XMLParser.modifyTag("grammar", System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", grammarFile.getPath());
            fillAcordion();
        }
    }

    @FXML
    public void loadPattern() {
        File patternFile = getFileFromFileChooser("src\\resources\\SampleTexts", "Text Files", "txt");
        if (patternFile != null) {
            patternTextArea.setText(TextManipulation.getStringFromFile(patternFile));
            XMLParser.modifyTag("pattern", System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", patternFile.getPath());
            Core.getInstance().setPattern(patternTextArea.getText());
            fillAcordion();
        }
    }

    @FXML
    public void loadText() {
        File textFile = getFileFromFileChooser("src\\resources\\SampleTexts", "Text Files", "txt");
        if (textFile != null) {
            XMLParser.modifyTag("text", System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", textFile.getPath());
            Core.getInstance().setText(TextManipulation.getStringFromFile(textFile));
            fillAcordion();
        }
    }

    @FXML
    public void chooseTerminals() {
        if (Core.getInstance().getGrammar() != null) {
            try {
                generateModal("../../resources/Views/ChooseTerminal.fxml", "Terminal symbols selector");
            } catch (IOException e) {
                AlertsFXML.errorAlert("Error", "Something went wrong while loading ChooseTerminal.fxml");
            }
        } else
            AlertsFXML.errorAlert("Grammar not specified", "Please select a grammar to determine which terminals you want to permute");
    }

    private File getFileFromFileChooser(String path, String description, String extension) {
        JSystemFileChooser chooser = new JSystemFileChooser();
        chooser.setCurrentDirectory(new File(path));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                description, extension);
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());
            return chooser.getSelectedFile();
        }
        return null;
    }

    @FXML
    public void searchForPattern() {
        unique.clear();
        matchedPermutations.clear();

        String text = Core.getInstance().getText();
        String algorithm = Core.getInstance().getAlgorithm();

        if (patternTextArea.getText() != null && !patternTextArea.getText().isEmpty()) {
            String pattern = patternTextArea.getText().replaceAll("\n", System.getProperty("line.separator"));

            ArrayList<String> validPermutations = new ArrayList<>();
            int mode = Core.getInstance().getMode();
            PatternMatcher pm = new PatternMatcher(Core.getInstance().getGrammar());

            switch (mode) {
                //permuting terminals + CYK verification
                case 1: {
                    if (verifyPattern(pattern)) {
                        if (Core.getInstance().getGrammar() != null) {
                            validPermutations = getValidPermutationsFromArray(pm.splitTerminals(pattern, Core.getInstance().getGrammar()), pm, true);
                            processValidPermutations(text, pattern, validPermutations);
                        } else
                            AlertsFXML.errorAlert("Grammar is not defined", "Please load grammar file to proceed in this mode");
                    } else
                        AlertsFXML.errorAlert("Pattern cannot be permuted in this mode", "Pattern consists of characters that are not terminals! Please remove invalid characters or change specified grammar file.");
                    break;
                }
                //permuting terminals without CYK verification
                case 2: {
                    if (verifyPattern(pattern)) {
                        validPermutations = getValidPermutationsFromArray(pm.splitTerminals(pattern, Core.getInstance().getGrammar()), pm, false);
                        processValidPermutations(text, pattern, validPermutations);
                    } else
                        AlertsFXML.errorAlert("Pattern cannot be permuted in this mode", "Pattern consists of characters that are not terminals!  Please remove invalid characters or change specified grammar file.");
                    break;
                }
                //permuting words with separator defined in config.xml (without CYK verification)
                case 3: {
                    validPermutations = getValidPermutationsWithSeparator(pattern, pm, Core.getInstance().getWordSeparator(), false);
                    processValidPermutations(text, pattern, validPermutations);
                    break;
                }
                //permuting chosen terminals
                case 4: {
                    if (verifyPattern(pattern)) {
                        if (Core.getInstance().getTerminalsToPermute() == null) {
                            chooseTerminals();
                        }
                        else {
                            validPermutations = permuteChosenTerminals(pattern, Core.getInstance().getTerminalsToPermute(), Core.getInstance().getGrammar(), pm);
                            processValidPermutations(text, pattern, validPermutations);
                        }
                    } else
                        AlertsFXML.errorAlert("Pattern cannot be permuted in this mode", "Pattern consists of characters that are not terminals!  Please remove invalid characters or change specified grammar file.");
                    break;
                }
                case 5: {
                    if (verifyPattern(pattern)){
                        validPermutations.add(pattern);
                        processValidPermutations(text, pattern, validPermutations);
                    }
                }
                default: {
                    System.out.println("Undefined mode");
                    break;
                }
            }
        }
        pane3.setExpanded(true);
    }

    private void processValidPermutations(String text, String pattern, ArrayList<String> validPermutations) {
        //removing separators from pattern
        pattern = pattern.replaceAll("" + Core.getInstance().getWordSeparator(), "");
        ObservableList<String> items = FXCollections.observableArrayList();
        int counter = 0;
        //finally
        for (String validPermutation : validPermutations) {
            counter++;
            ArrayList<Integer> part = new ArrayList<>();
            validPermutation = validPermutation.replaceAll("\n", System.getProperty("line.separator"));
            switch (Core.getInstance().getAlgorithm()) {
                case "Knuth-Morriss-Pratt": {
                    part = PatternSearchingUtil.KnuthMorissPratt(text, validPermutation);
                    break;
                }
                case "Boyer-Moore": {
                    part = PatternSearchingUtil.BoyerMoore(text, validPermutation);
                    break;
                }
                case "Karp-Rabin": {
                    part = PatternSearchingUtil.KarpRabin(text, validPermutation);
                    break;
                }
                case "Naive Backwards": {
                    part = PatternSearchingUtil.NaiveBackwards(text, validPermutation);
                    break;
                }
                default: {
                    part = PatternSearchingUtil.Naive(text, validPermutation);
                    break;
                }
            }

            for (Integer fragment : part) {
                //items.add(text.substring(fragment, fragment + pattern.length()) + " | " + fragment + "-" + (fragment + pattern.length()));
                items.add("Permutation number " +  counter + " found at " + fragment + "-" + (fragment + pattern.length()));
                matchedPermutations.put(fragment, text.substring(fragment, fragment + pattern.length()));
                unique.add(text.substring(fragment, fragment + pattern.length()));

            }
        }
        listView.setItems(items);
        fillStatistics(String.valueOf(TextManipulation.getPermutationsFromFile(
                new File(System.getProperty("user.dir") + "\\src\\resources\\PermutationFiles\\Permutations.txt")).size()), String.valueOf(validPermutations.size()), String.valueOf(items.size()));
    }

    private ArrayList<String> getValidPermutationsWithSeparator(String pattern, PatternMatcher pm, String separator,
                                                                boolean useCyk) {
        TextManipulation.permuteWordsHeap(pattern, separator, pm);
        return verifyPermutations(pm, useCyk);
    }

    private ArrayList<String> getValidPermutationsFromArray(ArrayList<String> pattern, PatternMatcher pm,
                                                            boolean useCyk) {
        File permutationFile = new File(System.getProperty("user.dir") + "\\src\\resources\\PermutationFiles\\Permutations.txt");
        try {
            PrintWriter writer = new PrintWriter(permutationFile, "UTF-8");
            TextManipulation.heapsAlgorithm(pattern.size(), pattern, writer);
            writer.close();
        } catch (FileNotFoundException e) {
            AlertsFXML.errorAlert("File not found", "The specified filepath is invalid: " + permutationFile.getPath());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            AlertsFXML.errorAlert("File is not supported", "Unsupported encoding in file: " + permutationFile.getPath());
            e.printStackTrace();
        }
        return verifyPermutations(pm, useCyk);
    }

    private ArrayList<String> removeDuplicates(ArrayList<String> permutations) {
        Set<String> hs = new HashSet<>(permutations);
        permutations.clear();
        permutations.addAll(hs);
        return permutations;
    }

    private ArrayList<String> verifyPermutations(PatternMatcher pm, boolean useCyk) {
        ArrayList<String> validPermutations = new ArrayList<>();
        ArrayList<String> uniquePermutations = removeDuplicates(TextManipulation.getPermutationsFromFile(
                new File(System.getProperty("user.dir") + "\\src\\resources\\PermutationFiles\\Permutations.txt")));

        for (String permutation : uniquePermutations) {
            if (useCyk) {
                if (pm.checkString(permutation)) {
                    System.out.println(permutation + " -> " + true);
                    validPermutations.add(permutation);
                } else System.out.println(permutation + " -> " + false);
            } else validPermutations.add(permutation);
        }
        return validPermutations;
    }

    private boolean verifyPattern(String pattern) {
        for (String terminal : Core.getInstance().getGrammar().getTerminals()) {
            pattern = pattern.replaceAll(terminal, "");
        }
        return pattern.length() == 0;
    }

    @FXML
    public void endSession() {
        Stage current = (Stage) menuBar.getScene().getWindow();
        current.close();
    }

    @FXML
    public void displayInfo() throws IOException {
        generateModal("../../resources/Views/About.fxml", "About");
    }

    private void initListeners() {
        searchAlgorithmComboBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            Core.getInstance().setAlgorithm(newValue);
            XMLParser.modifyTag("algorithm", System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", searchAlgorithmComboBox.getValue());
        });
        modeComboBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            switch (newValue) {
                case "Permute terminals with CYK verification":
                    Core.getInstance().setMode(1);
                    XMLParser.modifyTag("mode", System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", "1");
                    break;
                case "Permute terminals without CYK verification":
                    Core.getInstance().setMode(2);
                    XMLParser.modifyTag("mode", System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", "2");
                    break;
                case "Permute words":
                    Core.getInstance().setMode(3);
                    XMLParser.modifyTag("mode", System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", "3");
                    break;
                case "Permute chosen terminals":
                    Core.getInstance().setMode(4);
                    XMLParser.modifyTag("mode", System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", "4");
                    break;
            }
        });
    }

    private ArrayList<String> permuteChosenTerminals(String pattern, ArrayList<String> chosenTerminals, Grammar grammar, PatternMatcher pm) {
        ArrayList<String> terminalsInPattern = pm.splitTerminals(pattern, grammar);
        ArrayList<String> finalPermutations = new ArrayList<>();
        ArrayList<String> toPermute = new ArrayList<>();
        //adding every instance of chosenTerminals in pattern to array
        for (String terminal : terminalsInPattern) {
            if (chosenTerminals.contains(terminal)) {
                toPermute.add(terminal);
            }
        }
        //replacing chosenTerminals in pattern to empty string
        for (String toRemove : chosenTerminals) {
            Collections.replaceAll(terminalsInPattern, toRemove, "");
        }
        //permuting instances of chosenTerminals in pattern
        ArrayList<String> validPermutations = getValidPermutationsFromArray(toPermute, pm, false);
        //replacing empty strings with permuted chosenTerminals
        for (String validPermutation : validPermutations) {
            StringBuilder line = new StringBuilder();
            int offset = 0;
            for (String terminalInPattern : terminalsInPattern) {
                if (terminalInPattern.isEmpty()) {
                    line.append(validPermutation, offset, offset + 1);
                    offset++;
                } else line.append(terminalInPattern);
            }
            finalPermutations.add(line.toString());
        }
        return finalPermutations;
    }

    private void initComponents() {
        listView.setFocusTraversable(false);
        accordion.setExpandedPane(textPane);

        ArrayList<String> supportedSearchingAlgorithms = new ArrayList<>(Arrays.asList("Naive", "Karp-Rabin", "Knuth-Morriss-Pratt", "Boyer-Moore"));
        ObservableList<String> supportedAlgorithmList = FXCollections.observableArrayList(supportedSearchingAlgorithms);
        searchAlgorithmComboBox.setItems(supportedAlgorithmList);

        ArrayList<String> supportedModes = new ArrayList<>(Arrays.asList("Permute terminals with CYK verification", "Permute terminals without CYK verification", "Permute words", "Permute chosen terminals", "Default"));
        ObservableList<String> supportedModesList = FXCollections.observableArrayList(supportedModes);
        modeComboBox.setItems(supportedModesList);

        searchAlgorithmComboBox.getSelectionModel().select(Core.getInstance().getAlgorithm());
        modeComboBox.getSelectionModel().select(Core.getInstance().getMode() - 1);

    }


    private void initCore() {
        XMLParser.parseSettingsFile(System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml");
        //Core.getInstance().getGrammar().displayGrammar();
        patternTextArea.setText(Core.getInstance().getPattern());
        //System.out.println(TextManipulation.getStringFromFile(new File("C:\\Users\\paw\\IdeaProjects\\FX_PatternSearching_1.0\\src\\resources.SampleTexts\\pattern.txt")));
    }

    private void fillStatistics(String allPermutations, String validPermutations, String matches) {
        allPermutationsLabel.setText(allPermutations);
        validPermutationsLabel.setText(validPermutations);
        matchesLabel.setText(matches);
    }

    private void fillAcordion() {
        //text pane
        File textFile = new File(((Objects.requireNonNull(XMLParser.getValue((System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml"), "text")))));
        if (textFile.exists()) {
            textFileNameLabel.setText(textFile.getName().replaceFirst("[.][^.]+$", ""));
            textFileSizeLabel.setText(String.valueOf(textFile.length()) + " bytes");
            textCharactersLabel.setText(String.valueOf(TextManipulation.countCharacters(textFile)));
        } else {
            textFileNameLabel.setText("Text not specified");
            textFileSizeLabel.setText("Text not specified");
            textCharactersLabel.setText("Text not specified");
        }

        //pattern pane
        File patternFile = new File(((Objects.requireNonNull(XMLParser.getValue((System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml"), "pattern")))));
        if (patternFile.exists()) {
            patternFileNameLabel.setText(patternFile.getName().replaceFirst("[.][^.]+$", ""));
            patternFileSizeLabel.setText(String.valueOf(patternFile.length()) + " bytes");
            patternCharactersLabel.setText(String.valueOf(TextManipulation.countCharacters(patternFile)));
            if (Core.getInstance().getGrammar() != null) {
                patternTerminalsLabel.setText(String.valueOf(Core.getInstance().getGrammar().getTerminals().size()));
            }
        } else {
            patternFileNameLabel.setText("Pattern  not specified");
            patternFileSizeLabel.setText("Pattern not specified");
            patternCharactersLabel.setText("Pattern not specified");
            patternTerminalsLabel.setText("Pattern not specified");
        }

        //grammar pane
        File grammarFile = new File(((Objects.requireNonNull(XMLParser.getValue((System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml"), "grammar")))));
        if (grammarFile.exists()) {
            grammarFileNameLabel.setText(grammarFile.getName().replaceFirst("[.][^.]+$", ""));
            grammarTerminalsLabel.setText(String.valueOf(Core.getInstance().getGrammar().getTerminals().size()));
            grammarNonTerminalsLabel.setText(String.valueOf(Core.getInstance().getGrammar().getNonTerminals().size()));
            grammarRulesLabel.setText(String.valueOf(Core.getInstance().getGrammar().getNumberOfRules()));
            grammarSizeLabel.setText(String.valueOf(Core.getInstance().getGrammar().grammarSize()));
        } else {
            grammarFileNameLabel.setText("Grammar not specified");
            grammarTerminalsLabel.setText("Grammar not specified");
            grammarNonTerminalsLabel.setText("Grammar not specified");
            grammarRulesLabel.setText("Grammar not specified");
            grammarSizeLabel.setText("Grammar not specified");
        }

        //stats pane
        allPermutationsLabel.setText("Not available");
        validPermutationsLabel.setText("Not available");
        matchesLabel.setText("Not available");
    }

    private Document createTables(Document document, PdfFont boldFont) {
        Table table = new Table(new float[]{261, 261}) // in points
                .setWidth(100) //100 pt
                .setFixedLayout();
        table.setTextAlignment(TextAlignment.CENTER);

        //text info
        Paragraph centre = new Paragraph((new Text("Text information")).setFont(boldFont).setFontSize(15));
        centre.setTextAlignment(TextAlignment.CENTER);
        document.add(centre);
        table.addCell("Text file");
        table.addCell(XMLParser.getValue(System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", "text"));
        table.addCell("Text file size");
        table.addCell(textFileSizeLabel.getText());
        table.addCell("Characters in text");
        table.addCell(textCharactersLabel.getText());
        document.add(table);

        //pattern info
        centre = new Paragraph((new Text("Pattern information")).setFont(boldFont).setFontSize(15));
        centre.setTextAlignment(TextAlignment.CENTER);
        document.add(centre);
        table = new Table(new float[]{261, 261}) // in points
                .setWidth(100) //100 pt
                .setFixedLayout();
        table.setTextAlignment(TextAlignment.CENTER);
        table.addCell("Pattern file");
        table.addCell(XMLParser.getValue(System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", "pattern"));
        table.addCell("Pattern file size");
        table.addCell(patternFileSizeLabel.getText());
        table.addCell("Characters in pattern");
        table.addCell(patternCharactersLabel.getText());
        table.addCell("Terminals in pattern");
        table.addCell(patternTerminalsLabel.getText());
        document.add(table);

        //grammar info
        centre = new Paragraph((new Text("Grammar Information")).setFont(boldFont).setFontSize(15));
        centre.setTextAlignment(TextAlignment.CENTER);
        document.add(centre);
        table = new Table(new float[]{261, 261}) // in points
                .setWidth(100) //100 pt
                .setFixedLayout();
        table.setTextAlignment(TextAlignment.CENTER);
        Cell cell = new Cell();
        table.addCell("Grammar file");
        table.addCell(XMLParser.getValue(System.getProperty("user.dir") + "\\src\\resources\\Settings\\Config.xml", "grammar"));
        table.addCell("Non-Terminals in grammar");
        table.addCell(grammarNonTerminalsLabel.getText());
        table.addCell("Terminals in grammar");
        table.addCell(grammarTerminalsLabel.getText());
        table.addCell("Number of rules in grammar");
        table.addCell(grammarRulesLabel.getText());
        table.addCell("Grammar size");
        table.addCell(grammarSizeLabel.getText());
        document.add(table);

        //statistics
        centre = new Paragraph((new Text("Summary")).setFont(boldFont).setFontSize(15));
        centre.setTextAlignment(TextAlignment.CENTER);
        document.add(centre);
        table = new Table(new float[]{261, 261}) // in points
                .setWidth(100) //100 pt
                .setFixedLayout();
        table.setTextAlignment(TextAlignment.CENTER);
        table.addCell("All permutations");
        table.addCell(allPermutationsLabel.getText());
        table.addCell("Valid Permutations");
        table.addCell(validPermutationsLabel.getText());
        table.addCell("Matches found");
        table.addCell(matchesLabel.getText());
        document.add(table);

        //new page
        document.add(new AreaBreak());

        return document;
    }

    private String getDateTimeAsString(){
        StringBuilder dateTimeString = new StringBuilder();

        LocalDateTime dateTime = LocalDateTime.now();
        int day = dateTime.getDayOfMonth();
        int month = dateTime.getMonthValue();
        int year = dateTime.getYear();

        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int seconds = dateTime.getSecond();


        dateTimeString.append(day).append("-").append(month).append("-").append(year).append(" ").append(hour).append("-").append(minute).append("-").append(seconds);
        return dateTimeString.toString();
    }


    @FXML
    public void generatePDF() {
        String filePath = System.getProperty("user.dir") + "\\src\\resources\\Results\\" + getDateTimeAsString() + ".pdf";

        ObservableList<String> list = listView.getItems();
        ArrayList<Integer> permutationOffsets = new ArrayList<>();
        PdfWriter writer = null;

        try {
            writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            PageSize pageSize = PageSize.A4;
            Document document = new Document(pdf, pageSize);
            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            PdfFont boldFont = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
            PdfFont boldItalicFont = PdfFontFactory.createFont(FontConstants.TIMES_BOLDITALIC);
            document.setFont(font);

            if (!list.isEmpty()) {
                document = createTables(document, boldFont);
                Paragraph preface = new Paragraph((new Text("Matches")).setFont(boldFont).setFontSize(20));
                preface.setTextAlignment(TextAlignment.CENTER);
                document.add(preface);


                for (String permutation : unique) {
                    Paragraph centre = new Paragraph().add(new Text("Permutation: ")).add(new Text(permutation).setFont(boldItalicFont)).add(" was located in text at: ");
                    //centre.setTextAlignment(TextAlignment.CENTER);
                    document.add(centre);
                    //getting all the offsets of given permutation
                    for (Integer offset : matchedPermutations.keySet()) {
                        String key = offset.toString();
                        String value = matchedPermutations.get(offset);
                        if (value.equals(permutation)) permutationOffsets.add(offset);
                    }

                    //sorting and exporting
                    permutationOffsets.sort(Comparator.comparingInt(s -> s));
                    for (Integer sortedOffset : permutationOffsets) {
                        document.add(new Paragraph(sortedOffset + "-" + (sortedOffset + permutation.length())).setTextAlignment(TextAlignment.CENTER));
                    }
                    permutationOffsets.clear();
                }
                //opening created file
                try {
                    Desktop.getDesktop().open(new File(filePath));
                } catch (IOException e) {
                    System.out.println("No application registered for pdfs");
                }
            } else {
                document.add(new Paragraph(""));
                AlertsFXML.infoAlert("Export results", "No matches found");
            }
            document.close();
        } catch (IOException e) {
            System.out.println("Something went wrong when exporting data to PDF");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCore();
        initComponents();
        initListeners();
        fillAcordion();


        //label1.setText(String.valueOf(Core.getInstance().getText().length()));
    }
}
