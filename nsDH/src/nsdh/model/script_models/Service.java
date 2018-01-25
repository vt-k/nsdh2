/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model.script_models;

/**
 *
 * @author vojteq
 */
public class Service {
    public String name = "";
    public Server_transport server_transport = new Server_transport();
    public Server_application server_application = new Server_application();
    public Client_sink client_sink = new Client_sink();
}
