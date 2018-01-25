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
public class Network_settings {
    public String routing_type = "auto";
    public HashMap<String,Queue_policy> queue_policy_list = new HashMap<String,Queue_policy>();
    public HashMap<String,Service> service_list = new HashMap<String,Service>();
}
