/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model.script_models;
import java.util.*;

/**
 *
 * @author Artur Wojtkowski
 */
public class Queue {

    public String name = "";
    public String policer = "";
    public HashMap<String,Policer_entry> policer_entry_list = new HashMap<String,Policer_entry>();
    public Scheduler_params scheduler_params = new Scheduler_params();
    public ArrayList<Subqueue> subqueue_list = new ArrayList<Subqueue>();
}
