/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh;


/**
 *
 * @author Artur Wojtkowski
 */
public class Main {

    private static NsdhModel nsdhModel;
    private static NsdhController nsdhController;
    private static NsdhView nsdhView;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {


            nsdhModel = new NsdhModel();
            nsdhController = new NsdhController(nsdhModel);
            nsdhView = new NsdhView(nsdhController);
            nsdhController.nsdhView = nsdhView;

    }

}
