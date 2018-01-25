/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.view;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import nsdh.*;
import nsdh.model.script_models.*;
import org.w3c.dom.Document;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.model.mxCell;

/**
 *
 * @author vojteq
 */
public class GraphPanel extends JPanel {
    
    protected final NsdhGraph graph;
    protected mxGraphComponent graphComponent;
    private mxRubberband rubberband;
    protected JLabel statusBar;

    //konsola ns-2
    protected JTextArea consoleTextArea;
    protected JScrollPane consoleScrollPane;
    protected JPanel graphConsolePanel;

    protected NsdhController nsdhController;

    //Flaga czy jest wlaczona widocznosc polaczen server-client
    protected Boolean isServerClientConnectionVisible;

    //listy z elementami sieci na grafie
    protected HashMap<String,Object> pcCellList;
    protected HashMap<String,Object> routerCellList;
    protected HashMap<String,Object> serverClientConnectonEdgeList;
    protected HashMap<String,Object> standardLinkEdgeList;
    protected HashMap<String,Object> edgeCoreLinkEdgeList;
    protected HashMap<String,Object> coreCoreLinkEdgeList;

    protected EditorToolBar editorToolBar;


    public GraphPanel(NsdhController nsdhController) {
        super();

        this.consoleTextArea  = new JTextArea();
        this.nsdhController = nsdhController;
        isServerClientConnectionVisible = true;
        graph = new NsdhGraph(nsdhController,this);
        graphComponent = new mxGraphComponent(graph);
        statusBar = createStatusBar();
        consoleScrollPane = new JScrollPane();
        graphConsolePanel = new JPanel();
        rubberband = new mxRubberband(graphComponent);

        //listy z elementami sieci na grafie
        pcCellList = new HashMap<String,Object>();
        routerCellList = new HashMap<String,Object>();
        serverClientConnectonEdgeList = new HashMap<String,Object>();
        standardLinkEdgeList = new HashMap<String,Object>();
        edgeCoreLinkEdgeList = new HashMap<String,Object>();
        coreCoreLinkEdgeList = new HashMap<String,Object>();
	
        initComponents();
    }

    /**
     * Inicjalizuje komponenty
     */
    protected void initComponents(){

        //parametry grafu
        graph.setCellsEditable(false);
        graph.setCellsDisconnectable(false);
        graph.setAllowDanglingEdges(false);
        graph.setCellsResizable(false);

        //parametru graphComponent
        graphComponent.setPageVisible(false);
        graphComponent.setGridVisible(false);
        graphComponent.setToolTips(false);
        graphComponent.getConnectionHandler().setCreateTarget(false);
        graphComponent.setBackground(Color.white);
        graphComponent.getViewport().setOpaque(false);


        // laduje style grafu
        mxCodec codec = new mxCodec();
        Document doc = mxUtils
                        .loadDocument(GraphPanel.class
                                        .getResource(
                                                        "/nsdh/resources/basic-style.xml")
                                        .toString());
        codec.decode(doc.getDocumentElement(), graph.getStylesheet());

        //ustawienia consoli
        consoleTextArea.setBackground(new Color(250,250,250));
        consoleTextArea.setEditable(false);
        consoleTextArea.setEnabled(true);
        
        
        // laczy wszystko razem
        setLayout(new BorderLayout());
        add(graphComponent, BorderLayout.CENTER);
        graphConsolePanel.setLayout(new BorderLayout());
        JLabel jLabelWyjscieNs2 = new JLabel("Konsola:");
        jLabelWyjscieNs2.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        graphConsolePanel.add(jLabelWyjscieNs2 , BorderLayout.NORTH);
        graphConsolePanel.add(consoleScrollPane, BorderLayout.CENTER);
        consoleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        consoleScrollPane.setViewportView(consoleTextArea);
        consoleScrollPane.setPreferredSize(new Dimension(800,100));
        graphConsolePanel.add(statusBar, BorderLayout.SOUTH);
        add(graphConsolePanel, BorderLayout.SOUTH);

        //instaluje obsluge myszy
        installHandlers();

    }

    /**
     * Instaluje obsluge myszy
     */
    protected void installHandlers()
    {
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
        {
          public void mousePressed (MouseEvent e)
          {
            if (e.isPopupTrigger())
            {
                if (graph.getSelectionCells().length>0){

                    //id zaznaczonego elementu
                    graph.getSelectionCells();

                    ArrayList<String> cellTypeList = new ArrayList<String>();
                    ArrayList<String> cellNameList = new ArrayList<String>();

                    //uzupelnij listy wartosciami parametrow z zaznaczonych komorek
                    for (int i=0; i<  graph.getSelectionCells().length; i++){

                        String cellId = ((mxCell)graph.getSelectionCells()[i]).getId();

                        //jako parametry wywolawcze PopUp wstaw nazwe i typ wyciagniete z id elementu
                        String cellType="";
                        String cellName="";

                        if(cellId!=null && cellId.split(";").length>1){

                            cellType=cellId.split(";")[0];
                            cellName = cellId.split(";")[1];
                        }

                        cellTypeList.add(i, cellType);
                        cellNameList.add(i, cellName);
                    }

                    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                                graphComponent);

                    EditorPopupMenu menu = new EditorPopupMenu(nsdhController,GraphPanel.this,cellTypeList,cellNameList);
                    menu.show(graphComponent, pt.x, pt.y);

                    e.consume();
                }
            }
          }

          public void mouseReleased (MouseEvent e)
          {
            if (e.isPopupTrigger()){
                if (graph.getSelectionCells().length>0){

                    //id zaznaczonego elementu
                    graph.getSelectionCells();

                    ArrayList<String> cellTypeList = new ArrayList<String>();
                    ArrayList<String> cellNameList = new ArrayList<String>();

                    //uzupelnij listy wartosciami parametrow z zaznaczonych komorek
                    for (int i=0; i<  graph.getSelectionCells().length; i++){

                        String cellId = ((mxCell)graph.getSelectionCells()[i]).getId();

                        //jako parametry wywolawcze PopUp wstaw nazwe i typ wyciagniete z id elementu
                        String cellType="";
                        String cellName="";

                        if(cellId!=null && cellId.split(";").length>1){

                            cellType=cellId.split(";")[0];
                            cellName = cellId.split(";")[1];
                        }

                        cellTypeList.add(i, cellType);
                        cellNameList.add(i, cellName);
                    }

                    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                                graphComponent);

                    EditorPopupMenu menu = new EditorPopupMenu(nsdhController,GraphPanel.this,cellTypeList,cellNameList);
                    menu.show(graphComponent, pt.x, pt.y);

                    e.consume();
                }
            }
          }
        });

    }


    /**
     * Metoda uzywana przez przyciski z ToolBar
     * @param name
     * @param action
     * @return
     */
    public Action bind(final Action action)
    {
        return bind(null, action, null);
    }

    /**
     * Metoda uzywana przez przyciski z ToolBar
     * @param name
     * @param action
     * @return
     */
    public Action bind(String name, final Action action, String iconUrl)
    {
        return new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
                        GraphPanel.class.getResource(iconUrl)) : null)
                {
                    public void actionPerformed(ActionEvent e)
                    {
                            action.actionPerformed(new ActionEvent(getGraphComponent(), e
                                            .getID(), e.getActionCommand()));
                    }
                };
    }

    /**
     * Dodaje nowa linie tekstu w konsoli ns-2 pod grafem
     * @param text
     */
    public void addConsoleText(String text)
    {
        int maxRows = 200;

        consoleTextArea.append("\n"+text);
        String [] consoleRowTable = consoleTextArea.getText().split("\\n");
        String outputString = "";

        //obetnij gore jesli wierszy powyzej maxRows
        if(consoleRowTable.length>maxRows){
            for(int i=consoleRowTable.length-maxRows; i < consoleRowTable.length && i >0; i++){
                outputString = outputString +  "\n" + consoleRowTable[i];
            }
            consoleTextArea.setText(outputString);
        }
    }

    /**
     * Ustawia tekst konsoli ns-2 po grafem
     * @param text
     */
    public void setConsoleText(String text)
    {
            this.consoleTextArea.setText(text);
    }

    /**
     * Pobiera tekst z konsoli ns-2 pod grafem
     * @return
     */
    public String getConsoleText()
    {
            return this.consoleTextArea.getText();
    }

    /**
     * Tworzy nowy statusBar
     * @return
     */
    protected JLabel createStatusBar()
    {
            statusBar = new JLabel(" ");
            statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

            return statusBar;
    }

    /**
     * Pokazuje tekst w statusBar przez okreslona ilosc czasu
     * @param text
     * @param delayTime
     */
    protected void showStutusBarTimeText(String text, int delayTime){
        statusBar.setText(text);
        javax.swing.Timer t = new javax.swing.Timer(delayTime, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              statusBar.setText(" ");
            }
        });
        t.start();
    }

    public mxGraphComponent getGraphComponent()
    {
            return graphComponent;
    }


        /**
         * Instaluje toolbar w panelGraph
         * @param nsdhGUI klasa nsdhGUI
         */
	protected void installToolBar(NsdhGUI nsdhGUI)
	{
            editorToolBar = new EditorToolBar(this, nsdhGUI, JToolBar.HORIZONTAL);
            add(editorToolBar, BorderLayout.NORTH);
	}

        protected void clearAll()
	{
            graph.selectAll();
            graph.removeCells();
	}

        /**
         * Uaktualnia PC i routery z modelu na grafie
         */
        protected void loadCellsFromNsdhModel(){
            //usun pc i routery z grafu
            graph.removeCells(pcCellList.values().toArray());
            graph.removeCells(routerCellList.values().toArray());
            
            pcCellList.clear();
            routerCellList.clear();


            //zaladuj je na nowo
            Object parent = graph.getDefaultParent();
            graph.getModel().beginUpdate();
            try
            {
                //laduj pc z modelu na graf (jako id przyjmij wartosc pc;pc_name)
                for(Pc pc: nsdhController.nsdhModel.network_structure.pc_list.values()){
                    Object pcCell = graph.insertVertex(parent, "pc;".concat(pc.name) , pc.name, Double.valueOf(pc.gui_x), Double.valueOf(pc.gui_y), 40,
                                            40, "shape=image;image=/nsdh/images/computer.png;verticalLabelPosition=bottom;verticalAlign=top");


                    pcCellList.put(pc.name, pcCell);
                }

                //laduj routery z modelu na graf
                for(Router router: nsdhController.nsdhModel.network_structure.router_list.values()){
                    Object routerCell = graph.insertVertex(parent, "router;".concat(router.name), router.name, Double.valueOf(router.gui_x), Double.valueOf(router.gui_y), 48,
                                                48, "shape=image;image=/nsdh/images/router.png;verticalLabelPosition=bottom;verticalAlign=top");


                    routerCellList.put(router.name, routerCell);
                }
                
            }
            finally
            {
                    graph.getModel().endUpdate();
            }
        }

        /**
         * Zapisuje wspolrzedne routerow i pc z grafu do modelu
         */
        protected void saveCellsToNsdhModel(){
            for(Pc pc: nsdhController.nsdhModel.network_structure.pc_list.values()){
                Object pcCell = pcCellList.get(pc.name);
                pc.gui_x=Double.toString(graph.getCellBounds((mxCell)pcCell).getX());
                pc.gui_y=Double.toString(graph.getCellBounds((mxCell)pcCell).getY());
            }

            for(Router router: nsdhController.nsdhModel.network_structure.router_list.values()){
                Object routerCell = routerCellList.get(router.name);
                router.gui_x=Double.toString(graph.getCellBounds((mxCell)routerCell).getX());
                router.gui_y=Double.toString(graph.getCellBounds((mxCell)routerCell).getY());
            }
        }

        /**
         * uaktualnia wszystkie krawedzie na grafie na podstawie modelu
         */
        protected void loadEdgesFromNsdhModel(){
            //usun wszystkie krawedzie z grafu
            graph.removeCells(serverClientConnectonEdgeList.values().toArray());
            graph.removeCells(standardLinkEdgeList.values().toArray());
            graph.removeCells(edgeCoreLinkEdgeList.values().toArray());
            graph.removeCells(coreCoreLinkEdgeList.values().toArray());

            serverClientConnectonEdgeList.clear();
            standardLinkEdgeList.clear();
            edgeCoreLinkEdgeList.clear();
            coreCoreLinkEdgeList.clear();

            Object parent = graph.getDefaultParent();

            graph.getModel().beginUpdate();
            try
            {

                //laduj server-client-connection z modelu na graf
                for(Server_client_connection server_client_connection: nsdhController.nsdhModel.network_structure.server_client_connection_list.values()){
                    Object v1 = pcCellList.get(server_client_connection.server_pc);
                    

                    Object v2 = pcCellList.get(server_client_connection.client_pc);
                    Object serverClientConnectonEdge = graph.insertEdge(parent, "server_client_connection;".concat(server_client_connection.name), server_client_connection.service, v1, v2,"strokeColor=orange;dashed=true;labelColor=red;endArrow=classic");


                    serverClientConnectonEdgeList.put(server_client_connection.name, serverClientConnectonEdge);
                }

                //laduj standard-link z modelu na graf
                for(Standard_link standard_link: nsdhController.nsdhModel.network_structure.standard_link_list.values()){
                    Object v1,v2;
                    if(pcCellList.containsKey(standard_link.node1)){
                        v1 = pcCellList.get(standard_link.node1);
                    }else{
                        v1 = routerCellList.get(standard_link.node1);
                    }
                    if(pcCellList.containsKey(standard_link.node2)){
                        v2 = pcCellList.get(standard_link.node2);
                    }else{
                        v2 = routerCellList.get(standard_link.node2);
                    }
                    Object standardLinkEdge = graph.insertEdge(parent,  "standard_link;".concat(standard_link.name), standard_link.bandwidth +"Mb\n "+standard_link.delay+"ms", v1, v2,"strokeColor=black");
                    

                    standardLinkEdgeList.put(standard_link.name, standardLinkEdge);
                }

                //laduj edge-core-link z modelu na graf
                for(Edge_core_link edge_core_link: nsdhController.nsdhModel.network_structure.edge_core_link_list.values()){
                    Object v1,v2;
                    v1 = routerCellList.get(edge_core_link.edge_router);
                    v2 = routerCellList.get(edge_core_link.core_router);
                    Object edgeCoreLinkEdge = graph.insertEdge(parent, "edge_core_link;".concat(edge_core_link.name), edge_core_link.bandwidth +"Mb\n "+edge_core_link.delay+"ms", v1, v2,"strokeColor=0066B3");

                    edgeCoreLinkEdgeList.put(edge_core_link.name, edgeCoreLinkEdge);
                }

                //laduj core-core-link z modelu na graf
                for(Core_core_link core_core_link: nsdhController.nsdhModel.network_structure.core_core_link_list.values()){
                    Object v1,v2;
                    v1 = routerCellList.get(core_core_link.core_router1);
                    v2 = routerCellList.get(core_core_link.core_router2);
                    Object coreCoreLinkEdge = graph.insertEdge(parent, "core_core_link;".concat(core_core_link.name), core_core_link.bandwidth +"Mb\n "+core_core_link.delay+"ms", v1, v2,"strokeColor=007D47");



                    coreCoreLinkEdgeList.put(core_core_link.name, coreCoreLinkEdge);
                }
            }
            finally
            {
                    graph.getModel().endUpdate();
            }
        }

        /**
         * Laduje wszystko z modelu na graf
         */
        protected void loadAllFromNsdhModel()
	{
            clearAll();
            loadCellsFromNsdhModel();
            loadEdgesFromNsdhModel();
	}

        /**
         * Pokazuje, gdy ukryte i ukrywa, gdy widoczne połączenia server-client na grafie
         */
        protected void showHideServerClientConnection()
	{
            if(isServerClientConnectionVisible){
                graph.clearSelection();
                graph.removeCells(serverClientConnectonEdgeList.values().toArray());
                isServerClientConnectionVisible = false;
            }
            else{
                graph.clearSelection();
                graph.addCells(serverClientConnectonEdgeList.values().toArray());
                isServerClientConnectionVisible = true;
            }
	}

        /**
         * Zwraca tablice String, gdzie String[0]to parametr X a String[1] to parametr Y pierwszego wolnego miejsca na grafie
         * @return
         */
        protected String[] getSparePlaceXY(){
            String[] sparePlaceXY = new String[2];

            int pointValue = 10;
            do{
                pointValue++;
            }while(pointValue < 1000 && graphComponent.getCellAt(pointValue, pointValue)!=null);

            sparePlaceXY[0] = new String(pointValue+".0");
            sparePlaceXY[1] = new String(pointValue+".0");
            
            return sparePlaceXY;
        }

}
