/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.view;
import com.mxgraph.view.mxGraph;
import nsdh.*;
import com.mxgraph.model.mxCell;


/**
 *
 * @author notroot
 */
public class NsdhGraph extends mxGraph{

    public NsdhController nsdhController;
    public GraphPanel graphPanel;

    public NsdhGraph(NsdhController nsdhController, GraphPanel graphPanel){
        super();
        this.nsdhController = nsdhController;
        this.graphPanel = graphPanel;
    }

    @Override
    public void cellLabelChanged(Object cell, Object newValue, boolean autoSize) {
        //super.cellLabelChanged(cell, newValue, autoSize);
    }


    /**
     * Funkcja przeciazona pokazujaca okno z wlasciwosciami przy laczeniu 2 komorek grafu
     * @param parent
     * @param id
     * @param value
     * @param source
     * @param target
     * @return
     */
    @Override
    public Object insertEdge(Object parent, String id, Object value,
                Object source, Object target)
    {
            //pobierz id zrodlowej komorki na grafie
            String sourceCellId = ((mxCell)source).getId();
            String targetCellId = ((mxCell)target).getId();

            //jako parametry wywolawcze PopUp wstaw nazwe i typ wyciagniete z id elementu
            String sourceCellType="";
            String sourceCellName="";
            String targetCellType="";
            String targetCellName="";

            if(sourceCellId!=null && sourceCellId.split(";").length>1){

                sourceCellType=sourceCellId.split(";")[0];
                sourceCellName = sourceCellId.split(";")[1];
            }

             if(targetCellId!=null && targetCellId.split(";").length>1){

                targetCellType=targetCellId.split(";")[0];
                targetCellName = targetCellId.split(";")[1];
            }

            new EdgeSettingsFrame(this.nsdhController,this.graphPanel,null,null,sourceCellType,sourceCellName,targetCellType,targetCellName).setVisible(true);
            return null;
    }

    public Object insertNsdhModelEdge(Object parent, String id, Object value,Object source, Object target){
        return super.insertEdge(parent, id, value, source, target);
    }



}
