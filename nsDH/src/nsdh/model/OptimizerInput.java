/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model;
import java.util.*;
/**
 *
 * @author vtq
 */
public class OptimizerInput {
    public String targetFunctionExpression = ""; //funkcja celu
    public HashMap<String,String> functionArgumentList = new HashMap<String,String>(); //mapowanie listy argumentow funkcji celu y0, y1 itd. na wartosc z modelu wynikow np. server_client_connection_name\AvgDelay

    //mapowanie listy zmiennych x0, x1 itd. na wartosc z modelu np. queue_policy:polityka1\queue:kolejka1\scheduler_param:priority lub
    //queue_policy:polityka1/queue:kolejka1/subqueue:0/param:min_thereshold
    public HashMap<String,String> variableList = new HashMap<String,String>(); 
    public HashMap<String,Double> variableMinList = new HashMap<String,Double>(); //lista ograniczen min zmiennych
    public HashMap<String,Double> variableMaxList = new HashMap<String,Double>(); //lista ograniczen max zmiennych
    public HashMap<String,Double> startingPoint = new HashMap<String,Double>(); //punkt początkowy
    public String maxIter = ""; //maksymalna liczba iteracji
    public String numberOfStartRandomPoints =""; //liczba losowanych punktow na samym poczatku optymalizacji
    public String numberOfStepRandomPoints = ""; //liczba losowanych punktow w kroku optymalizacji szukajacym kolejnego punktu
    public String maxPTries = ""; //max liczba prob znalezienia P < M
}
