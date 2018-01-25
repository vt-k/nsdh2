/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model.script_models;

import java.util.*;

/**
 *
 * @author vtq
 */
public class Optimizer_input {

    public HashMap<String,Optimization_variable> optimization_variable_list = new HashMap<String,Optimization_variable>();
    public HashMap<String,Target_function_arguement> target_function_arguement_list = new HashMap<String,Target_function_arguement>();
    public String target_function = "";
    public String N_param = "";
    public String K_param = "";
    public String maxIter_param = "";
    public String maxPTries_param = "";
}
