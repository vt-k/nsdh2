/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh;
import nsdh.view.*;

/**
 *
 * @author vojteq
 */
public class NsdhView {

    private NsdhController nsdhController;
    public NsdhGUI nsdhGUI;

    public NsdhView (NsdhController nsdhController){

        this.nsdhController = nsdhController;
        this.nsdhGUI = new NsdhGUI(nsdhController);
        
    }
}
