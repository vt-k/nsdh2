/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh;
import nsdh.model.OptimizerInput;
import nsdh.model.script_models.*;
import nsdh.model.*;
import nsdh.controller.*;
/**
 *
 * @author vojteq
 */
public class NsdhModel {
    public Network_settings network_settings;
    public Network_structure network_structure;
    public Scenario scenario;
    public Optimizer_input optimizer_input;
    public Settings settings;
    public Results results;
    public Sequences sequences;
    public Ns2Runner ns2Runner;
    

    public NsdhModel(){
        network_settings = new Network_settings();
        network_structure = new Network_structure();
        scenario = new Scenario();
        settings = new Settings();
        results = new Results();
        sequences = new Sequences();
        optimizer_input = new Optimizer_input();
    }

}
