/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model;
import nsdh.*;
import java.io.*;

import java.util.concurrent.locks.*;

/**
 *
 * @author vtq
 */
public class Ns2Runner extends Thread {

    NsdhController nsdhController;
    
    String filepath = "";
    public String ns2Output = "";
    public String ns2Error = "";
    public String ioExceptionMsg = "";
    public String exceptionMsg = "";

    public Ns2Runner(NsdhController nsdhController, String filepath){
        this.nsdhController = nsdhController;
        this.filepath = filepath;
    }

    public void run() {

        String s = null;

        nsdhController.lock.lock();
        //System.out.println("Rozpoczecie symulacji");
        try{
            Process p = Runtime.getRuntime().exec(nsdhController.nsdhModel.settings.ns2Path + " " + filepath);

            BufferedReader stdInput = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                 InputStreamReader(p.getErrorStream()));

            //pobiera standardowe wyjscie ns-2
            ns2Output = "";
            while ((s = stdInput.readLine()) != null) {

                ns2Output = ns2Output + s + "\n";
            }

            //pobiera bledy ns-2
            ns2Error = "";
            while ((s = stdError.readLine()) != null) {
                ns2Error = ns2Error + s + "\n";
            }

            //czeka na zakonczenie ns
            p.waitFor();

            

        }catch(Exception e){
            ioExceptionMsg = e.getMessage();
        }finally{
            //System.out.println("Koniec symulacji");
            nsdhController.lock.unlock();
        }

        

    }
}
