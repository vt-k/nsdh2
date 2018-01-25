/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model.script_models;

/**
 *
 * @author vtq
 */
public class Server_agent_TCP_params {
    public String window = "";
    public String windowInit = "";
    public String windowOption = "";
    public String windowConstant = "";
    public String windowThresh = "";
    public String overhead = "";
    public String ecn = "";
    public String packetSize = "";
    public String bugFix = "";
    public String slow_start_restart = "";
    public String tcpTick = "";
    public Server_agent_TCP_NewReno_params server_agent_TCP_NewReno_params = new Server_agent_TCP_NewReno_params();
}
