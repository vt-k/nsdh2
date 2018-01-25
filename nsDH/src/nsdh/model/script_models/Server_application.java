/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model.script_models;

/**
 *
 * @author vtq
 */
public class Server_application {
    public String type = "";
    public Application_Traffic_Exponential_params application_Traffic_Exponential_params = new Application_Traffic_Exponential_params();
    public Application_Traffic_Pareto_params application_Traffic_Pareto_params = new Application_Traffic_Pareto_params();
    public Application_Traffic_CBR_params application_Traffic_CBR_params = new Application_Traffic_CBR_params();
    public Application_Telnet_params application_Telnet_params = new Application_Telnet_params();
    public Application_FTP_params application_FTP_params = new Application_FTP_params();
}
