package nsdh.view;

import javax.swing.*;
import nsdh.NsdhController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


public class EditorPopupMenu extends JPopupMenu
{
        public ArrayList<String> cellTypeList;
        public ArrayList<String> cellNameList;
        public NsdhController nsdhController;
        public GraphPanel graphPanel;

        /**
         * Konstruktor okna Popup na grafie
         * @param nsdhController
         * @param graphPanel graphPanel na ktorym pojawi sie popup
         * @param cellTypeList lista typow zaznaczonych elementow
         * @param cellNameList lista nazw zaznaczonych elementow
         */
	public EditorPopupMenu(NsdhController nsdhController, GraphPanel graphPanel,ArrayList<String> cellTypeList, ArrayList<String> cellNameList)
	{
            super();

            this.cellTypeList = cellTypeList;
            this.cellNameList = cellNameList;
            this.nsdhController = nsdhController;
            this.graphPanel = graphPanel;

            JMenuItem deleteMenuItem = new JMenuItem("Usuń");
            JMenuItem propertiesMenuItem = new JMenuItem("Właściwości");

            //wlasciwosci - otwiera okno z wlasciwosciami krawedzi
            propertiesMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(EditorPopupMenu.this.cellTypeList.get(0).equals("pc")){
                            new PcSettingsFrame(EditorPopupMenu.this.nsdhController,EditorPopupMenu.this.graphPanel,EditorPopupMenu.this.cellNameList.get(0));
                        }else if (EditorPopupMenu.this.cellTypeList.get(0).equals("router")){
                            new RouterSettingsFrame(EditorPopupMenu.this.nsdhController,EditorPopupMenu.this.graphPanel,EditorPopupMenu.this.cellNameList.get(0));
                        }else{
                            new EdgeSettingsFrame(EditorPopupMenu.this.nsdhController,EditorPopupMenu.this.graphPanel,EditorPopupMenu.this.cellTypeList.get(0),EditorPopupMenu.this.cellNameList.get(0),null,null,null,null).setVisible(true);
                        }
                        
                    }
                }

            );

            //usuwanie w zaleznosci od cellType
            deleteMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        for(int i=0; i< EditorPopupMenu.this.cellTypeList.size(); i++){
                            if(EditorPopupMenu.this.cellTypeList.get(i).equals("pc")){
                                EditorPopupMenu.this.nsdhController.DeletePc(EditorPopupMenu.this.cellNameList.get(i));
                                EditorPopupMenu.this.graphPanel.loadAllFromNsdhModel();
                            }else if (EditorPopupMenu.this.cellTypeList.get(i).equals("router")){
                                EditorPopupMenu.this.nsdhController.DeleteRouter(EditorPopupMenu.this.cellNameList.get(i));
                                EditorPopupMenu.this.graphPanel.loadAllFromNsdhModel();
                            }else if(EditorPopupMenu.this.cellTypeList.get(i).equals("server_client_connection")){
                                EditorPopupMenu.this.nsdhController.DeleteServerClientConnection(EditorPopupMenu.this.cellNameList.get(i));
                                EditorPopupMenu.this.graphPanel.loadEdgesFromNsdhModel();
                            }else if(EditorPopupMenu.this.cellTypeList.get(i).equals("standard_link")){
                                EditorPopupMenu.this.nsdhController.nsdhModel.network_structure.standard_link_list.remove(EditorPopupMenu.this.cellNameList.get(i));
                                EditorPopupMenu.this.graphPanel.loadEdgesFromNsdhModel();
                            }else if(EditorPopupMenu.this.cellTypeList.get(i).equals("edge_core_link")){
                                EditorPopupMenu.this.nsdhController.nsdhModel.network_structure.edge_core_link_list.remove(EditorPopupMenu.this.cellNameList.get(i));
                                EditorPopupMenu.this.graphPanel.loadEdgesFromNsdhModel();
                            }else if(EditorPopupMenu.this.cellTypeList.get(i).equals("core_core_link")){
                                EditorPopupMenu.this.nsdhController.nsdhModel.network_structure.core_core_link_list.remove(EditorPopupMenu.this.cellNameList.get(i));
                                EditorPopupMenu.this.graphPanel.loadEdgesFromNsdhModel();
                            }
                        }
                        
                    }
                }
            );

            this.add(deleteMenuItem);
            this.add(propertiesMenuItem);
	}

}
