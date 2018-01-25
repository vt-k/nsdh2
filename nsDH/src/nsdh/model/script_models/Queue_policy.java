/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model.script_models;
import java.util.*;
/**
 *
 * @author vojteq
 */
public class Queue_policy {
     public String name = "";
     public String scheduler = "";
     public String mean_packet_size = "";
     public ArrayList<Queue> queue_list = new ArrayList<Queue>();
}
