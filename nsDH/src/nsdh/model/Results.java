/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model;
import java.util.*;

/**
 * Klasa reprezentujaca zagregowane wyniki ostatniej symulacji
 * @author Artur Wojtkowski
 */
public class Results {

    //słownik nazwy server_client_connection na flowID symulacji
    public HashMap<String,String> serverClientConnectionFlowIdDictionary = new HashMap<String,String>();

    //słownik nazwy pc lub routera na nodeID symulacji
    public HashMap<String,String> pcRouterNodeIdDictionary = new HashMap<String,String>();

    //zagregowane wyniki dla flow o podanym id
    public HashMap<String,FlowResults> flowResultsList = new HashMap<String,FlowResults>();

}
