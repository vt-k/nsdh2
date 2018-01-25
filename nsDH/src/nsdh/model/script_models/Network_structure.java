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
public class Network_structure {
    public HashMap<String,Router> router_list = new HashMap<String,Router>();
    public HashMap<String,Pc> pc_list = new HashMap<String,Pc>();
    public HashMap<String,Server_client_connection> server_client_connection_list = new HashMap<String,Server_client_connection>();
    public HashMap<String,Standard_link> standard_link_list = new HashMap<String,Standard_link>();
    public HashMap<String,Core_core_link> core_core_link_list = new HashMap<String,Core_core_link>();
    public HashMap<String,Edge_core_link> edge_core_link_list = new HashMap<String,Edge_core_link>();
}
