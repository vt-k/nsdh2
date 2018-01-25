package nsdh.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.mxgraph.swing.util.mxGraphActions;

/**
 * Klasa reprezentujaca ToolBar
 * @author Artur Wojtkowski
 */
public class EditorToolBar extends JToolBar
{
    /**
     * Konstruktor
     * @param graphPanel 
     * @param nsdhGUI
     * @param orientation
     */
    public EditorToolBar(final GraphPanel graphPanel, final NsdhGUI nsdhGUI, int orientation)
    {
        super(orientation);

        setBorder(BorderFactory.createCompoundBorder(BorderFactory
                        .createEmptyBorder(3, 3, 3, 3), getBorder()));
        setFloatable(false);
        setRollover(true);

        //Przycisk nowy plik
        JButton newButton = new JButton(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/new.gif")));
        newButton.setToolTipText("Nowy plik");
        newButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nsdhGUI.jMenuItemNowyActionPerformed(e);
                }
            }
        );
        this.add(newButton);

        //Przycisk otworz
        JButton openButton = new JButton(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/open.gif")));
        openButton.setToolTipText("Otwórz plik");
        openButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nsdhGUI.jMenuItemOtworzActionPerformed(e);
                }
            }
        );
        this.add(openButton);

        //Przycisk zapisz
        JButton saveButton = new JButton(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/save.gif")));
        saveButton.setToolTipText("Zapisz plik");
        saveButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nsdhGUI.jMenuItemZapiszActionPerformed(e);
                }
            }
        );
        this.add(saveButton);

        addSeparator();

        //Przycisk wstaw router
        JButton routerButton = new JButton();
        routerButton.setToolTipText("Wstaw router");
        routerButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/router_icon.png")));
        routerButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        RouterSettingsFrame routerSettingsFrame = new RouterSettingsFrame(nsdhGUI.nsdhController, graphPanel);
                        routerSettingsFrame.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(nsdhGUI, "Błąd: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        );
        this.add(routerButton);

        //Przysk wstaw PC
        JButton pcButton = new JButton();
        pcButton.setToolTipText("Wstaw PC");
        pcButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/computer_icon.png")));
        pcButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        PcSettingsFrame pcSettingsFrame = new PcSettingsFrame(nsdhGUI.nsdhController, graphPanel);
                        pcSettingsFrame.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(nsdhGUI, "Błąd: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        );
        this.add(pcButton);

//                addSeparator();
//                JButton deleteButton = new JButton();
//                deleteButton.setAction(graphPanel.bind(mxGraphActions.getDeleteAction()));
//                deleteButton.setToolTipText("Usuń zaznaczenie");
//                deleteButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/delete.gif")));
//                this.add(deleteButton);

        addSeparator();

        //Przycisk uslugi i kolejki
        JButton queueButton = new JButton();
        queueButton.setToolTipText("Ustawienia symulowanej sieci");
        queueButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/queue.gif")));
        queueButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        NetworkSettingsFrame networkSettingsFrame = new NetworkSettingsFrame(nsdhGUI);
                        networkSettingsFrame.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(nsdhGUI, "Błąd: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        );
        this.add(queueButton);

        //Przycisk scenariusz
        JButton scenarioButton = new JButton();
        scenarioButton.setToolTipText("Scenariusz symulacji");
        scenarioButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/scenario.png")));
        scenarioButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        ScenerioSettingsFrame scenerioSettingsFrame = new ScenerioSettingsFrame(nsdhGUI.nsdhController);
                        scenerioSettingsFrame.setVisible(true);
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(nsdhGUI, "Błąd: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        );
        this.add(scenarioButton);

        //Przycisk uruchom symulacje
        JButton runButton = new JButton();
        runButton.setToolTipText("Uruchom symulację");
        runButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/run.png")));
        runButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nsdhGUI.jMenuItemUruchomSymulacjeActionPerformed(e);
                }
            }
        );
        this.add(runButton);

        //Przycisk wyniki
        JButton resultsButton = new JButton();         
        resultsButton.setToolTipText("Wyniki ostatniej symulacji");
        resultsButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/results.png")));
        resultsButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nsdhGUI.jMenuWynikiActionPerformed(e);
                }
            }
        );
        this.add(resultsButton);
        //resultsButton.setEnabled(false);

        //optymalizacja
        JButton optimizationButton = new JButton();
        optimizationButton.setToolTipText("Optymalizacja parametrów sieci");
        optimizationButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/optimization.gif")));
        optimizationButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nsdhGUI.jMenuOptymalizacjaActionPerformed(e);
                }
            }
        );
        this.add(optimizationButton);

        addSeparator();

        //Przyciski lupy
        JButton zoomoutButton = new JButton();
        zoomoutButton.setAction(graphPanel.bind(mxGraphActions.getZoomOutAction()));
        zoomoutButton.setToolTipText("Pomniejsz");
        zoomoutButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/zoomout.gif")));
        this.add(zoomoutButton);

        JButton zoomactualButton = new JButton();
        zoomactualButton.setAction(graphPanel.bind(mxGraphActions.getZoomActualAction()));
        zoomactualButton.setToolTipText("Aktualna wielkość");
        zoomactualButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/zoomactual.gif")));
        
        this.add(zoomactualButton);

        JButton zoominButton = new JButton();
        zoominButton.setAction(graphPanel.bind(mxGraphActions.getZoomInAction()));
        zoominButton.setToolTipText("Powiększ");
        zoominButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/zoomin.gif")));
        this.add(zoominButton);

        //pokaz ukryj polaczenia server-client
        JButton connectionButton = new JButton();
        connectionButton.setToolTipText("Pokaż połączenia server-client (ON/OFF)");
        connectionButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/connection.png")));
        connectionButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    graphPanel.showHideServerClientConnection();
                }
            }
        );
        this.add(connectionButton);

        addSeparator();

        //konfiguracja programu
        JButton settingsButton = new JButton();
        settingsButton.setToolTipText("Konfiguracja programu");
        settingsButton.setIcon(new ImageIcon(EditorToolBar.class.getResource("/nsdh/images/settings.png")));
        settingsButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nsdhGUI.jMenuItemKonfiguracjaProgramuActionPerformed(e);
                }
            }
        );
        this.add(settingsButton);

    }
}
