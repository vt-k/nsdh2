/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model.script_models;

/**
 *
 * @author vtq
 */
public class Server_agent_params {
    public String fid = "";
    public String prio = "";
    public String flags = "";
    public String ttl = "";
    public Server_agent_TCP_params server_agent_TCP_params = new Server_agent_TCP_params();
    public Server_agent_UDP_params server_agent_UDP_params = new Server_agent_UDP_params();
}
