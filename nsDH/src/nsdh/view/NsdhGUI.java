/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.view;

import java.io.*;
import nsdh.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.xpath.XPathExpressionException;
import com.mxgraph.util.mxCellRenderer;
import java.awt.event.*;
import javax.imageio.ImageIO;
import com.mxgraph.swing.util.mxGraphActions;


/**
 *
 * @author vojteq
 */
public class NsdhGUI extends javax.swing.JFrame{

    protected NsdhController nsdhController;

    // Variables declaration - do not modify
    private javax.swing.JFileChooser ChooseLoadFromXml;
    private javax.swing.JFileChooser ChooseSaveToXml;
    private javax.swing.JFileChooser ChooseSaveToTcl;
    private javax.swing.JFileChooser ChooseSaveToPng;

    public OptimizerProgressFrame optimizerProgressFrame;

    private javax.swing.JLabel jLabelOtwartyPlik;
    private javax.swing.JScrollPane jScrollPaneTest;
    private javax.swing.JTextPane jTextPaneTest;

    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JMenuItem jMenuItemKonfiguracjaProgramu;
    private javax.swing.JMenuItem jMenuItemNetworkSettings;
    private javax.swing.JMenuItem jMenuItemScenarioSettings;
    private javax.swing.JMenu jMenuPlik;
    private javax.swing.JMenuItem jMenuItemEksportujDoTcl;
    private javax.swing.JMenuItem jMenuItemEksportujDoPng;
    private javax.swing.JMenuItem jMenuItemKoniec;
    private javax.swing.JMenuItem jMenuItemNowy;
    private javax.swing.JMenuItem jMenuItemOtworz;
    private javax.swing.JMenuItem jMenuItemZapisz;
    private javax.swing.JMenuItem jMenuItemZapiszJako;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;

    public GraphPanel graphPanel;
    
    /**
     * Konstruktor
     * @param nsdhController
     */
    public NsdhGUI(NsdhController nsdhController) {
        this.nsdhController = nsdhController;
        initComponents();
        this.setLocationRelativeTo(null);
        

        //zapisz przed wyjsciem
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e)
            {
                exitProgram();
            }
        });

        this.setVisible(true);
    }

    /**
     * Inicjalizacja komponentów
     */
    private void initComponents() {
        graphPanel = new GraphPanel(nsdhController);

        ChooseLoadFromXml = new javax.swing.JFileChooser();
        ChooseLoadFromXml.addChoosableFileFilter(new XmlFileFilter());

        ChooseSaveToXml = new javax.swing.JFileChooser();
        ChooseSaveToXml.addChoosableFileFilter(new XmlFileFilter());

        ChooseSaveToTcl = new javax.swing.JFileChooser();
        ChooseSaveToTcl.addChoosableFileFilter(new TclFileFilter());

        ChooseSaveToPng = new javax.swing.JFileChooser();
        ChooseSaveToPng.addChoosableFileFilter(new PngFileFilter());

        

        jLabelOtwartyPlik = new javax.swing.JLabel();
        jScrollPaneTest = new javax.swing.JScrollPane();
        jTextPaneTest = new javax.swing.JTextPane();

        jMenuBarMain = new javax.swing.JMenuBar();
        jMenuPlik = new javax.swing.JMenu();
        jMenuItemNowy = new javax.swing.JMenuItem();
        jMenuItemOtworz = new javax.swing.JMenuItem();
        jMenuItemZapisz = new javax.swing.JMenuItem();
        jMenuItemZapiszJako = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItemEksportujDoTcl = new javax.swing.JMenuItem();
        jMenuItemEksportujDoPng = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemKoniec = new javax.swing.JMenuItem();
        jMenuSettings = new javax.swing.JMenu();
        jMenuItemKonfiguracjaProgramu = new javax.swing.JMenuItem();
        jMenuItemNetworkSettings = new javax.swing.JMenuItem();

        ChooseSaveToXml.setDialogType(javax.swing.JFileChooser.CUSTOM_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ns-2 DiffServ Helper");
        setBounds(new java.awt.Rectangle(150, 200, 1000, 300));

        jLabelOtwartyPlik.setText("Otwarty plik: nowy");

        jScrollPaneTest.setViewportView(jTextPaneTest);



        jMenuPlik.setText("Plik");

        jMenuItemNowy.setText("Nowy");
        jMenuItemNowy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNowyActionPerformed(evt);
            }
        });
        jMenuPlik.add(jMenuItemNowy);

        jMenuItemOtworz.setText("Otwórz...");
        jMenuItemOtworz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOtworzActionPerformed(evt);
            }
        });
        jMenuPlik.add(jMenuItemOtworz);

        jMenuItemZapisz.setText("Zapisz");
        jMenuItemZapisz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemZapiszActionPerformed(evt);
            }
        });
        jMenuPlik.add(jMenuItemZapisz);

        jMenuItemZapiszJako.setText("Zapisz jako...");
        jMenuItemZapiszJako.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemZapiszJakoActionPerformed(evt);
            }
        });
        jMenuPlik.add(jMenuItemZapiszJako);
        jMenuPlik.add(jSeparator2);

        jMenuItemEksportujDoTcl.setText("Eksportuj do OTcl...");
        jMenuItemEksportujDoTcl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEksportujDoTclActionPerformed(evt);
            }
        });
        jMenuPlik.add(jMenuItemEksportujDoTcl);

        jMenuItemEksportujDoPng.setText("Eksportuj do PNG...");
        jMenuItemEksportujDoPng.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEksportujDoPngActionPerformed(evt);
            }
        });
        jMenuPlik.add(jMenuItemEksportujDoPng);


        jMenuPlik.add(jSeparator1);

        jMenuItemKoniec.setText("Koniec");
        jMenuItemKoniec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemKoniecActionPerformed(evt);
            }
        });
        jMenuPlik.add(jMenuItemKoniec);

        jMenuBarMain.add(jMenuPlik);


        JMenu jMenuEdit = new JMenu("Edycja");
        JMenuItem jMenuItemInsertPC = new javax.swing.JMenuItem("Wstaw PC");
        jMenuItemInsertPC.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        PcSettingsFrame pcSettingsFrame = new PcSettingsFrame(nsdhController, graphPanel);
                        pcSettingsFrame.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(NsdhGUI.this, "Błąd: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
        });
        jMenuEdit.add(jMenuItemInsertPC);
        JMenuItem jMenuItemInsertRouter = new javax.swing.JMenuItem("Wstaw Router");
        jMenuItemInsertRouter.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        RouterSettingsFrame routerSettingsFrame = new RouterSettingsFrame(nsdhController, graphPanel);
                        routerSettingsFrame.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(NsdhGUI.this, "Błąd: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
        });
        jMenuEdit.add(jMenuItemInsertRouter);
        jMenuBarMain.add(jMenuEdit);



        JMenu jMenuView = new JMenu("Widok");

        JMenuItem jMenuItemZoomIn = new javax.swing.JMenuItem();
        jMenuItemZoomIn.setAction(graphPanel.bind(mxGraphActions.getZoomInAction()));
        jMenuItemZoomIn.setText("Powiększ");
        jMenuView.add(jMenuItemZoomIn);

        JMenuItem jMenuItemActualSize = new javax.swing.JMenuItem();
        jMenuItemActualSize.setAction(graphPanel.bind(mxGraphActions.getZoomActualAction()));
        jMenuItemActualSize.setText("Aktualna Wielkość");
        jMenuView.add(jMenuItemActualSize);

        JMenuItem jMenuItemZoomOut = new javax.swing.JMenuItem();
        jMenuItemZoomOut.setAction(graphPanel.bind(mxGraphActions.getZoomOutAction()));
        jMenuItemZoomOut.setText("Pomniejsz");
        jMenuView.add(jMenuItemZoomOut);

        jMenuView.add(new JSeparator());

        JMenuItem jMenuItemConnectionShowHide = new javax.swing.JMenuItem();
        jMenuItemConnectionShowHide.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    graphPanel.showHideServerClientConnection();
                }
        });
        jMenuItemConnectionShowHide.setText("Pokaż/Ukryj Połączenia Serwer-Klient");
        jMenuView.add(jMenuItemConnectionShowHide);

        jMenuView.add(new JSeparator());
        
        JMenuItem jMenuItemSimulationResults = new javax.swing.JMenuItem();
        jMenuItemSimulationResults.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuWynikiActionPerformed(e);
                }
        });
        jMenuItemSimulationResults.setText("Wyniki Symulacji");
        jMenuView.add(jMenuItemSimulationResults);


        jMenuBarMain.add(jMenuView);

        jMenuSettings.setText("Ustawienia");

        jMenuItemNetworkSettings.setText("Ustawienia Sieci");
        jMenuItemNetworkSettings.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        NetworkSettingsFrame networkSettingsFrame = new NetworkSettingsFrame(NsdhGUI.this);
                        networkSettingsFrame.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(NsdhGUI.this, "Błąd: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
        });
        jMenuSettings.add(jMenuItemNetworkSettings);

        jMenuItemScenarioSettings = new javax.swing.JMenuItem();
        jMenuItemScenarioSettings.setText("Scenariusz Symulacji");
        jMenuItemScenarioSettings.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        ScenerioSettingsFrame scenerioSettingsFrame = new ScenerioSettingsFrame(nsdhController);
                        scenerioSettingsFrame.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(NsdhGUI.this, "Błąd: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
        });
        jMenuSettings.add(jMenuItemScenarioSettings);

        jMenuSettings.add(new JSeparator());

        jMenuItemKonfiguracjaProgramu.setText("Konfiguracja Programu...");
        jMenuItemKonfiguracjaProgramu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemKonfiguracjaProgramuActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemKonfiguracjaProgramu);

        jMenuBarMain.add(jMenuSettings);


        JMenu jMenuSimulation = new JMenu("Symulacja");

        JMenuItem jMenuItemRunSimulation = new javax.swing.JMenuItem();
        jMenuItemRunSimulation.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuItemUruchomSymulacjeActionPerformed(e);
                }
        });
        jMenuItemRunSimulation.setText("Uruchom Symulację");
        jMenuSimulation.add(jMenuItemRunSimulation);

        

        JMenuItem jMenuItemOptimization = new javax.swing.JMenuItem();
        jMenuItemOptimization.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuOptymalizacjaActionPerformed(e);
                }
        });
        jMenuItemOptimization.setText("Optymalizacja");
        jMenuSimulation.add(jMenuItemOptimization);
        

        jMenuBarMain.add(jMenuSimulation);

        setJMenuBar(jMenuBarMain);

        graphPanel.installToolBar(this);
        this.add(graphPanel);

        this.setSize(800, 600);
    }

    /**
     * Koniec programu
     * @param evt
     */
    private void jMenuItemKoniecActionPerformed(java.awt.event.ActionEvent evt) {
        exitProgram();
    }


    //zakoncz program, lecz wczesniej zapytaj czy zapisac
    private void exitProgram(){

        int value = JOptionPane.showConfirmDialog(NsdhGUI.this, "Czy zapisać plik przed wyjściem z programu?", "Komunikat", JOptionPane.YES_NO_CANCEL_OPTION);

            if(value == JOptionPane.CANCEL_OPTION){
                return;
            }else if (value == JOptionPane.NO_OPTION){
                System.exit(0);
            }else if (value == JOptionPane.YES_OPTION){
                try{
                    saveToXML();
                }catch (SavedException e){
                    System.exit(0);
                }
            }

    }

    /**
     * Nowy plik
     * @param evt
     */
    protected void jMenuItemNowyActionPerformed(java.awt.event.ActionEvent evt) {
        setTitle("ns-2 DiffServ Helper");
        nsdhController.ClearModel();
        graphPanel.clearAll();
    }

    /**
     * Zapisz jako
     * @param evt
     */
    private void jMenuItemZapiszJakoActionPerformed(java.awt.event.ActionEvent evt) {

        try{
            saveToXML();
        }catch (SavedException e){
            
        }
        
    }


    //wyjatek - zapisano plik
    public class SavedException extends Exception{
        public SavedException(){
            super();
        }
    }

    //zapisz plik XML
    private void saveToXML() throws SavedException{
        int returnVal = ChooseSaveToXml.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try{
                File file = ChooseSaveToXml.getSelectedFile();

                //jesli filtr xml, zmien rozszerzenie
                if (ChooseSaveToXml.getFileFilter().getDescription().equals("*.xml") && !FileFilterUtils.getExtension(file).equals("xml")){
                    file = new File(file.getPath()+".xml");
                }

                //sprawdz czy nadpisac plik
                if(file.exists()){
                    int value = JOptionPane.showConfirmDialog(this, "Czy nadpisać istniejący plik?");

                    if(value == JOptionPane.NO_OPTION || value == JOptionPane.CANCEL_OPTION){
                        return;
                    }
                }

                graphPanel.saveCellsToNsdhModel();
                nsdhController.SaveToXmlFile(file.getPath());
                setTitle("ns-2 DiffServ Helper - " + nsdhController.nsdhModel.settings.openedXmlFilePath);
                graphPanel.showStutusBarTimeText("Plik zapisany pomyślnie.", 5000);
                throw new SavedException();

            }catch(SavedException e){
                throw new SavedException();
            }catch(IOException e){
                JOptionPane.showMessageDialog(this, "Błąd odczytu pliku: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }catch(Exception e){
                JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }

        
    }

    /**
     * Otwórz plik
     * @param evt
     */
    protected void jMenuItemOtworzActionPerformed(java.awt.event.ActionEvent evt) {
        int returnVal = ChooseLoadFromXml.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = ChooseLoadFromXml.getSelectedFile();

            try{
                nsdhController.LoadFromXmlFile(file.getPath());
                graphPanel.loadAllFromNsdhModel();
                setTitle("ns-2 DiffServ Helper - " + nsdhController.nsdhModel.settings.openedXmlFilePath);
                graphPanel.showStutusBarTimeText("Plik załadowany pomyślnie.", 5000);


            }catch(ParserConfigurationException e){
                JOptionPane.showMessageDialog(this, "Błąd parsera XML: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }catch(SAXException e){
                JOptionPane.showMessageDialog(this, "Błąd SAX: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }catch(XPathExpressionException e){
                JOptionPane.showMessageDialog(this, "Błąd XPath: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }catch(IOException e){
                JOptionPane.showMessageDialog(this, "Błąd odczytu pliku: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }catch(Exception e){
                JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    protected void jMenuItemKonfiguracjaProgramuActionPerformed(java.awt.event.ActionEvent evt) {
        new ProgramConfigurationFrame(nsdhController);
    }

    /**
     * Zapisz plik
     * @param evt
     */
    protected void jMenuItemZapiszActionPerformed(java.awt.event.ActionEvent evt) {

        if(nsdhController.nsdhModel.settings.openedXmlFilePath.equals("")){

            jMenuItemZapiszJakoActionPerformed(evt);
        }
        else{

            try{
                graphPanel.saveCellsToNsdhModel();
                nsdhController.SaveToXmlFile(nsdhController.nsdhModel.settings.openedXmlFilePath);
                setTitle("ns-2 DiffServ Helper - " + nsdhController.nsdhModel.settings.openedXmlFilePath);
                graphPanel.showStutusBarTimeText("Plik zapisany pomyślnie.", 5000);
                //JOptionPane.showMessageDialog(this, "Plik zapisano pomyślnie.");
            }catch(IOException e){
                JOptionPane.showMessageDialog(this, "Błąd odczytu pliku: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }catch(Exception e){
                JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    /**
     * Uruchamia symulacje symulatorem ns-2
     * @param evt
     */
    protected void jMenuItemUruchomSymulacjeActionPerformed(java.awt.event.ActionEvent evt) {

        graphPanel.setConsoleText("Rozpoczęcie symulacji...");
        
        try{
            //uruchom symulacje
            nsdhController.RunNs2Simulation();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    //wczytaj wyniki symulacji
                    try{
                        nsdhController.UploadResults(true);
                    }catch(Exception e){
                        NsdhGUI.this.graphPanel.addConsoleText("Błąd wczytywania wyników: "+e.getMessage());
                    }

                    graphPanel.addConsoleText("Koniec symulacji.");

                    if (!nsdhController.nsdhModel.ns2Runner.ns2Output.equals("")){
                        graphPanel.addConsoleText("\nWyjście standardowe ns-2: \n");
                        graphPanel.addConsoleText(nsdhController.nsdhModel.ns2Runner.ns2Output);
                    }

                    if (!nsdhController.nsdhModel.ns2Runner.ns2Error.equals("")){
                        graphPanel.addConsoleText("\nBłędy ns-2: \n");
                        graphPanel.addConsoleText(nsdhController.nsdhModel.ns2Runner.ns2Error);
                    }

                    if (!nsdhController.nsdhModel.ns2Runner.ioExceptionMsg.equals("")){
                        graphPanel.addConsoleText("\nBłędy IO: \n");
                        graphPanel.addConsoleText(nsdhController.nsdhModel.ns2Runner.ioExceptionMsg);
                    }

                    if(!(!nsdhController.nsdhModel.ns2Runner.ns2Output.equals("") || !nsdhController.nsdhModel.ns2Runner.ns2Error.equals("") || !nsdhController.nsdhModel.ns2Runner.ioExceptionMsg.equals("")))
                    {
                        ResultFrame wyniki = new ResultFrame(nsdhController);
                        wyniki.setVisible(true);
                    }
                    
                }
            });

        }catch(Exception e){
                JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                graphPanel.addConsoleText("Koniec symulacji.");
        }

    }

    /**
     * Pokazuje wyniki ostatniej symulacji
     * @param evt
     */
    protected void jMenuWynikiActionPerformed(java.awt.event.ActionEvent evt) {
        try{
            ResultFrame wyniki = new ResultFrame(nsdhController);
            wyniki.setVisible(true);
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Uruchom symulacje przed wyswietleniem wynikow.");
        }
    }


    /**
     * optymalizacja
     * @param evt
     */
    protected void jMenuOptymalizacjaActionPerformed(java.awt.event.ActionEvent evt) {
        try{
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new OptimizerFrame(nsdhController,NsdhGUI.this);
            }
        });

            
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }


    /**
     * Eksport do OTcl
     * @param evt
     */
    private void jMenuItemEksportujDoTclActionPerformed(java.awt.event.ActionEvent evt) {
        int returnVal = ChooseSaveToTcl.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try{
                File file = ChooseSaveToTcl.getSelectedFile();
                //jesli filtr xml, zmien rozszerzenie
                if (ChooseSaveToTcl.getFileFilter().getDescription().equals("*.tcl") && !FileFilterUtils.getExtension(file).equals("tcl")){
                    file = new File(file.getPath()+".tcl");
                }

                //sprawdz czy nadpisac plik
                if(file.exists()){
                    int value = JOptionPane.showConfirmDialog(this, "Czy nadpisać istniejący plik?");

                    if(value == JOptionPane.NO_OPTION || value == JOptionPane.CANCEL_OPTION){
                        return;
                    }
                }

                nsdhController.SaveToTclFile(file.getPath());
                graphPanel.showStutusBarTimeText("Eksport zakończony pomyślnie.", 5000);

            }catch(IOException e){
                JOptionPane.showMessageDialog(this, "Błąd odczytu pliku: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }catch(Exception e){
                JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Eksport do PNG
     * @param evt
     */
    private void jMenuItemEksportujDoPngActionPerformed(java.awt.event.ActionEvent evt) {
        int returnVal = ChooseSaveToPng.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            try{
                File file = ChooseSaveToPng.getSelectedFile();

                //jesli filtr png, zmien rozszerzenie
                if (ChooseSaveToPng.getFileFilter().getDescription().equals("*.png") && !FileFilterUtils.getExtension(file).equals("png")){
                    file = new File(file.getPath()+".png");
                }

                //sprawdz czy nadpisac plik
                if(file.exists()){
                    int value = JOptionPane.showConfirmDialog(this, "Czy nadpisać istniejący plik?");

                    if(value == JOptionPane.NO_OPTION || value == JOptionPane.CANCEL_OPTION){
                        return;
                    }
                }

                BufferedImage image = mxCellRenderer
                        .createBufferedImage(graphPanel.graph, null, 1, graphPanel.graphComponent.getBackground(),
										graphPanel.graphComponent.isAntiAlias(), null,
										graphPanel.graphComponent.getCanvas());
                if(image!=null){
                    ImageIO.write(image, "png", file);
                }else{
                    JOptionPane.showMessageDialog(this, "Nie można wygenerować PNG z pustego grafu.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                }

                graphPanel.showStutusBarTimeText("Eksport zakończony pomyślnie.", 5000);

            }catch(IOException e){
                JOptionPane.showMessageDialog(this, "Błąd zapisu pliku: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }catch(Exception e){
                JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }

        }
    }


}
