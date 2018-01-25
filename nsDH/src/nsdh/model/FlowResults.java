/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model;
import java.util.*;

/**
 * Klasa reprezentujaca wyniki symulacji dla danego flow
 * @author vtq
 */
public class FlowResults {

    public double avgDelay =0; //srednie opoznienie
    public double avgJitter =0; //sredni Jitter
    public double avgThroughput =0; //srednia przepustowosc
    public double packetLossRate=0; //straty pakietu

    //dane do wykresow ThroughPut, Jitter i Delay w czasie
    public HashMap<Double,Double> chartStatsThroughput = new HashMap<Double,Double>();
    public HashMap<Double,Double> chartStatsJitter = new HashMap<Double,Double>();
    public HashMap<Double,Double> chartStatsDelay = new HashMap<Double,Double>();

    //zmienne pomocnicze
    public double droppedPackets=0; //ilosc odrzuconych pakietow
    public double sentPackets=0; //ilosc odrzuconych pakietow
    public double receivedPackets=0; //ilosc odebranych pakietow
    public double startTime = 1e6; //poczatek nadawania
    public double stopTime =0; //koniec nadawania
    public double receivedSize =0; //ilosc odebranych danych
    public double ticReceivedSize =0.0; //ilosc odebranych danych w ciagu jednogo ticu dla statystyk wykresu
    public double prevTime =0.0; //poprzedni czas pakietu
    public double currTime =0.0; //aktualny czas przegladu pakietow
    //lista czasow wyslanych i odebranych pakietow o podanym pktId
    public HashMap<Integer,Double> sendTimeList = new HashMap<Integer,Double>();
    public HashMap<Integer,Double> receivedTimeList = new HashMap<Integer,Double>();
    
    
}
