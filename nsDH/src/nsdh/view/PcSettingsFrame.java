/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PcSettingsFrame.java
 *
 * Created on 2010-04-12, 19:13:43
 */

package nsdh.view;

import nsdh.*;
import nsdh.model.script_models.*;
import javax.swing.*;

/**
 *
 * @author vtq
 */
public class PcSettingsFrame extends javax.swing.JFrame {

    /**
     * Konstruktor tworzacy okno edycji ustawien pc
     * @param pcName - nazwa pc
     */
    public PcSettingsFrame(NsdhController nsdhController, GraphPanel graphPanel, String pcName) {
        super();
        initComponents();
        this.nsdhController = nsdhController;
        this.graphPanel = graphPanel;
        this.pcName = pcName;

        pcNameTextField.setText(pcName);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Konstruktor tworzacy okno dodawania nowego PC
     */
    public PcSettingsFrame(NsdhController nsdhController, GraphPanel graphPanel) {
        super();
        initComponents();
        this.nsdhController = nsdhController;
        this.graphPanel = graphPanel;
        this.pcName = null;

        pcNameTextField.setText("PC" + nsdhController.nsdhModel.sequences.pcSequence);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pcNameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Parametry PC");
        setAlwaysOnTop(true);
        setBounds(new java.awt.Rectangle(100, 50, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        jLabel1.setText("Nazwa:");

        okButton.setText("OK");
        okButton.setActionCommand("jButton1");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Anuluj");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pcNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelButton)))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pcNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

        try{
            graphPanel.saveCellsToNsdhModel();

            //tworzy pc na podstawie ekranu
            Pc pc = new Pc();

            //sprawdz czy nazwa sie nie powtarza
            if( !pcNameTextField.getText().equals(pcName) && nsdhController.nsdhModel.network_structure.pc_list.containsKey(pcNameTextField.getText())){
                throw new Exception("Pc o takiej nazwie juz istnieje, zmien nazwe.");
            }
            else if(pcNameTextField.getText()==null || pcNameTextField.getText().equals("")){
                throw new Exception("Nazwa krawedzi nie moze byc pusta");
            }else if (nsdhController.nsdhModel.network_structure.router_list.containsKey(pcNameTextField.getText())){
                throw new Exception("Nazwy węzłów w grafie nie mogą się powtarzać. Router o takiej nazwie już istnieje.");
            }
            else{
                pc.name = pcNameTextField.getText();
            }


            //pobiera wolne miejsce na grafie
            String[] sparePlaceXY = graphPanel.getSparePlaceXY();
            pc.gui_x = sparePlaceXY[0];
            pc.gui_y = sparePlaceXY[1];


            //gdy edycja obiektu
            if(pcName!=null && nsdhController.nsdhModel.network_structure.pc_list.containsKey(pcName)){

                //usun stary obiekt z modelu
                nsdhController.RenamePc(pcName, pc.name);
            }
            else{ //gdy dodanie nowego obiektu
                nsdhController.nsdhModel.network_structure.pc_list.put(pc.name, pc);

                //zwieksz numer sekwencyjny
            nsdhController.nsdhModel.sequences.pcSequence++;
            }

            //zaktualizuj graf
            graphPanel.loadAllFromNsdhModel();

            this.dispose();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ostrzeżenie", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField pcNameTextField;
    // End of variables declaration//GEN-END:variables
    private NsdhController nsdhController;
    private GraphPanel graphPanel;
    private String pcName;
}