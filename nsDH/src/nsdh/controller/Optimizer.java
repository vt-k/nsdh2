/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.controller;

import nsdh.model.OptimizerInput;
import nsdh.*;
import nsdh.model.*;
import nsdh.view.*;
import java.util.*;
import com.graphbuilder.math.*;
import javax.swing.SwingWorker;


/**
 *
 * @author vtq
 */
public class Optimizer extends SwingWorker<Void,String>{


    
    NsdhController nsdhController;

    public OptimizerInput optimizerInput;
    public OptimizerOutput optimizerOutput = new OptimizerOutput();

    Boolean debugMode = false; //czy wlaczyc tryb debug - wiecej informacji w system.out

    VarMap mespVarMap; //mapowanie listy zmiennych i argumentow f. celu dla biblioteki MESP

    
    public class CalculatedFunctionPoint{
        public Double targetFunctionValue; //wartosc funckji
        public HashMap<String,Double> optimizationVariableToValueList = new HashMap<String,Double>(); //mapowanie stringu np. "x0" na wartosc double
        public HashMap<String,Double> targetFunctionArguementToValueList = new HashMap<String,Double>();
    }


    /**
     * wyniki optymalizacji
     */
    public class OptimizerOutput{

        //lista punktow inicjalizujacych A (w tym punkt poczatkowy)
        public ArrayList<CalculatedFunctionPoint> initialRandomPointsList= new ArrayList<CalculatedFunctionPoint>();

        //kolejne kroki iteracji
        public ArrayList<IterationResults> iterationResultsList= new ArrayList<IterationResults>();
        
        //wyjscie tekstowe, np blad
        public String textOutput="";

    }


    /**
     * Wyniki optymalizacji - wyniki jednej iteracji, punkty L(minimum z A), old_M (max z A) oraz new_M(czyli obliczony krok kolejny P)
     */
    public class IterationResults{
        public CalculatedFunctionPoint crs2_L;
        public CalculatedFunctionPoint crs2_M;
        public CalculatedFunctionPoint crs2_P;
    }

    //konstruktor
    public Optimizer(NsdhController nsdhController,OptimizerInput optimizerInput){
        this.nsdhController = nsdhController;
        this.optimizerInput=optimizerInput;
    }



    /**
     * Wykonuje symulacje i oblicza wartosc funkcji celu w punkcie
     * @param arguementToValueList
     * @return
     * @throws Exception
     */
    private CalculatedFunctionPoint FunctionCalculation(HashMap<String,Double> arguementToValueList) throws OptimizationEndException,Exception{
        CalculatedFunctionPoint calculatedFunctionPoint = new CalculatedFunctionPoint();
        calculatedFunctionPoint.optimizationVariableToValueList = arguementToValueList; //wynik do return
        calculatedFunctionPoint.targetFunctionArguementToValueList = new HashMap<String,Double>();

        

        //zaktualizuj model argumentami

        //zainicjalizowanie zmiennych - prametrow symulacji wedlug wyrazenia w optimizerInput.variableList
        for(String arguementName: arguementToValueList.keySet()){
            String variableModelExpression = optimizerInput.variableList.get(arguementName); //wyrazenie odnoszace sie do parametru symulacji, ktory bedzie "krecony"
            String variableNewValue = arguementToValueList.get(arguementName).toString(); //wartosc zmiennej ktora trzeba zainicjalizowac parametr symulacji

            //podzielenie sciezki typu queue_policy:polityka1/queue:kolejka1/subqueue:0/param:min_thereshold
            String[] splittedVariableModelExpression = variableModelExpression.split("/");

            if (splittedVariableModelExpression[0].split(":")[0].equals("queue_policy")){ //nazwa polityki koljkowania
                String queue_policy_name = splittedVariableModelExpression[0].split(":")[1];
                
                if (splittedVariableModelExpression[1].split(":")[0].equals("param")){ //srednia wielkosc pakietu
                    if (splittedVariableModelExpression[1].split(":")[1].equals("mean_packet_size")){
                        nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).mean_packet_size = variableNewValue;
                    }
                    
                }else if (splittedVariableModelExpression[1].split(":")[0].equals("queue")){ //nazwa kolejki

                    String queue_name = splittedVariableModelExpression[1].split(":")[1];
                    int queueOrdinal;
                    for(queueOrdinal = 0; queueOrdinal<nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.size();queueOrdinal++ ){
                        if(nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).name.equals(queue_name)){
                            break;
                        }
                    }

                    if(splittedVariableModelExpression[2].split(":")[0].equals("scheduler_param")){ //parametry schedulera

                        if(nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).scheduler.equals("PRI")){ //PRI
                             if(splittedVariableModelExpression[2].split(":")[1].equals("rate")){
                                nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).scheduler_params.rate= variableNewValue;
                            }
                        }//WRR,RR,WIRR
                        else if(nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).scheduler.equals("WRR") || nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).scheduler.equals("WIRR") ||nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).scheduler.equals("RR") ){
                             if(splittedVariableModelExpression[2].split(":")[1].equals("weight")){
                                nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).scheduler_params.weight= variableNewValue;
                            }
                        }

                    }
                    else if(splittedVariableModelExpression[2].split(":")[0].equals("service")){

                        String service_name = splittedVariableModelExpression[2].split(":")[1];

                        

                        if(splittedVariableModelExpression[3].split(":")[0].equals("policer_param")){ //parametry policera
                            if(nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer.equals("TSW2CM")){ //TSW2CM
                                 if(splittedVariableModelExpression[3].split(":")[1].equals("CIR")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).CIR= variableNewValue;
                                }
                            }else if(nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer.equals("TSW3CM")){ //TSW3CM
                                 if(splittedVariableModelExpression[3].split(":")[1].equals("CIR")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).CIR= variableNewValue;
                                }else if(splittedVariableModelExpression[3].split(":")[1].equals("PIR")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).PIR= variableNewValue;
                                }
                            }else if(nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer.equals("TokenBucket")){ //TokenBucket
                                 if(splittedVariableModelExpression[3].split(":")[1].equals("CIR")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).CIR= variableNewValue;
                                }else if(splittedVariableModelExpression[3].split(":")[1].equals("CBS")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).CBS= variableNewValue;
                                }
                            }else if(nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer.equals("srTCM")){ //srTCM
                                 if(splittedVariableModelExpression[3].split(":")[1].equals("CIR")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).CIR= variableNewValue;
                                }else if(splittedVariableModelExpression[3].split(":")[1].equals("CBS")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).CBS= variableNewValue;
                                }else if(splittedVariableModelExpression[3].split(":")[1].equals("EBS")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).EBS= variableNewValue;
                                }
                            }else if(nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer.equals("trTCM")){ //trTCM
                                 if(splittedVariableModelExpression[3].split(":")[1].equals("CIR")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).CIR= variableNewValue;
                                }else if(splittedVariableModelExpression[3].split(":")[1].equals("CBS")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).CBS= variableNewValue;
                                }else if(splittedVariableModelExpression[3].split(":")[1].equals("PIR")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).PIR= variableNewValue;
                                }else if(splittedVariableModelExpression[3].split(":")[1].equals("PBS")){
                                    nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).policer_entry_list.get(service_name).PBS= variableNewValue;
                                }
                            }
                        }

                    }
                    else if(splittedVariableModelExpression[2].split(":")[0].equals("subqueue")){ //parametry podkolejek

                        int subqueue_ordinal = Integer.parseInt(splittedVariableModelExpression[2].split(":")[1]); //numer podkolejki

                        if(splittedVariableModelExpression[3].split(":")[0].equals("param")){
                            if(splittedVariableModelExpression[3].split(":")[1].equals("min_threshold")){
                                nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).subqueue_list.get(subqueue_ordinal).min_threshold=variableNewValue;
                            }else if(splittedVariableModelExpression[3].split(":")[1].equals("max_threshold")){
                                nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).subqueue_list.get(subqueue_ordinal).max_threshold=variableNewValue;
                            }else if(splittedVariableModelExpression[3].split(":")[1].equals("probability_dropping")){
                                nsdhController.nsdhModel.network_settings.queue_policy_list.get(queue_policy_name).queue_list.get(queueOrdinal).subqueue_list.get(subqueue_ordinal).probability_dropping=variableNewValue;
                            }
                        }
                        
                    }
                }
            }
        }
        

        //wykonaj symulacje
        nsdhController.lock.lock();
        if(debugMode) System.out.println("Zapisywanie modelu do pliku");
        try{
            nsdhController.nsdhModel.ns2Runner = new Ns2Runner(nsdhController, nsdhController.nsdhModel.settings.ns2InputFilePath);
            nsdhController.SaveToTclFile(nsdhController.nsdhModel.settings.ns2InputFilePath);

        }finally{
            if(debugMode) System.out.println("Koniec zapisywania modelu do pliku");
            nsdhController.lock.unlock();
        }

        //uruchom symulacje
        nsdhController.nsdhModel.ns2Runner.start();
        nsdhController.nsdhModel.ns2Runner.join();

        nsdhController.lock.lock();
        //wczytaj wyniki
        if(debugMode) System.out.println("Wczytywanie wynikow");
        try{
            nsdhController.UploadResults(false);
        }finally{
            if(debugMode) System.out.println("Koniec wczytywania wynikow");
            nsdhController.lock.unlock();
        }


        //zaktualizuj zmienne funkcji celu
        nsdhController.lock.lock();
        if(debugMode) System.out.println("Obliczanie funkcji z modelu");
        try{
            //parsuj string funkcji
            Expression expression = ExpressionTree.parse(optimizerInput.targetFunctionExpression);

            //lista zmiennych
            mespVarMap = new VarMap(false /* case sensitive */);

            for(String arguementName: arguementToValueList.keySet()){
                mespVarMap.setValue(arguementName,arguementToValueList.get(arguementName));
            }

            try{
                //wczytanie wynikow symulacji jako argumenty parsera funkcji
                for(String functionArguementName: optimizerInput.functionArgumentList.keySet()){

                    double functionArguementValue = 0.0;
                    String functionArguementModelExpression = optimizerInput.functionArgumentList.get(functionArguementName);

                    //podzielenie sciezki typu nazwa_server_klient_connection/nazwa_parametru_wynikowego
                    String[] splittedfunctionArguementModelExpression = functionArguementModelExpression.split("/");

                    if(splittedfunctionArguementModelExpression.length >1){

                        if(splittedfunctionArguementModelExpression[1].split(":")[1].equals("avgDelay")){
                            functionArguementValue = nsdhController.nsdhModel.results.flowResultsList.get(nsdhController.nsdhModel.results.serverClientConnectionFlowIdDictionary.get(splittedfunctionArguementModelExpression[0].split(":")[1])).avgDelay;
                        }else if(splittedfunctionArguementModelExpression[1].split(":")[1].equals("avgJitter")){
                            functionArguementValue = nsdhController.nsdhModel.results.flowResultsList.get(nsdhController.nsdhModel.results.serverClientConnectionFlowIdDictionary.get(splittedfunctionArguementModelExpression[0].split(":")[1])).avgJitter;
                        }else if(splittedfunctionArguementModelExpression[1].split(":")[1].equals("avgThroughput")){
                            functionArguementValue = nsdhController.nsdhModel.results.flowResultsList.get(nsdhController.nsdhModel.results.serverClientConnectionFlowIdDictionary.get(splittedfunctionArguementModelExpression[0].split(":")[1])).avgThroughput;
                        }else if(splittedfunctionArguementModelExpression[1].split(":")[1].equals("packetLossRate")){
                            functionArguementValue = nsdhController.nsdhModel.results.flowResultsList.get(nsdhController.nsdhModel.results.serverClientConnectionFlowIdDictionary.get(splittedfunctionArguementModelExpression[0].split(":")[1])).packetLossRate;
                        }

                        mespVarMap.setValue(functionArguementName,functionArguementValue);
                        calculatedFunctionPoint.targetFunctionArguementToValueList.put(functionArguementName,functionArguementValue);

                    }
                    else{
                        throw new Exception ("Zle wyrazenie wynikowe");
                    }



                }

                }catch(Exception e){

                throw new Exception("Błąd symulacji\n" + e.getMessage());
            }

            // ladowanie funkcji mozliwych do uzycia
            FuncMap fm = new FuncMap();
            fm.loadDefaultFunctions();

            //oblicz funkcje
            calculatedFunctionPoint.targetFunctionValue = expression.eval(mespVarMap, fm);



        }catch(Exception e){

            throw new Exception("Błąd parsera funkcji matematycznej\n " + e.getMessage());
        }
        finally{
            if(debugMode) System.out.println("Koniec obliczania funkcji z modelu");
            nsdhController.lock.unlock();
        
            
        }

        //sprawdz czy nie zostala anulowana akcja

        if(isCancelled()){
            throw new  OptimizationEndException("Optimization stopped by user.");
        }

        //oblicz wynik funkcji

        return calculatedFunctionPoint;
    }

    //optymalizacja CRS2
    public void CRS2Optimization() throws Exception{

        
        int maxPTries = Integer.parseInt(optimizerInput.maxPTries); //liczba prob znalezienia P < M
        int maxIter = Integer.parseInt(optimizerInput.maxIter); //maksymalna liczba iteracji
        int crs2_N = Integer.parseInt(optimizerInput.numberOfStartRandomPoints); //ilosc punktow losowanych na poczatku algorytmu CRS2 z calej dziedziny funkcji (liczba punktow w A)
        int crs2_K = Integer.parseInt(optimizerInput.numberOfStepRandomPoints); //ilosc punktow losowanych z tablicy A do obliczania kolejnego kroku P
        ArrayList<CalculatedFunctionPoint> crs2_A = new ArrayList<CalculatedFunctionPoint>(); //tablica A przechowujaca wyniki obliczania funkcji algorytmem CRS3
        
        CalculatedFunctionPoint crs2_L = new CalculatedFunctionPoint();
        CalculatedFunctionPoint crs2_M = new CalculatedFunctionPoint();


        long startTime = System.currentTimeMillis(); //czas poczatkowy optymalizacji

        try{



            //wstaw punkt poczatkowy do A
            publish("--------- Add starting point to A  ----------");
            CalculatedFunctionPoint calculatedStartingPoint = FunctionCalculation(optimizerInput.startingPoint);
            crs2_A.add(calculatedStartingPoint);
            publish("Start point Optimization Variables: "+calculatedStartingPoint.optimizationVariableToValueList);
            publish("Start pointTarget Function Arguements: "+calculatedStartingPoint.targetFunctionArguementToValueList);
            publish("Start point Target Function Value: "+calculatedStartingPoint.targetFunctionValue);

            this.optimizerOutput.initialRandomPointsList.add(calculatedStartingPoint);
            


            //System.out.println("Wylosowane punkty:" );
            //losowanie zmiennych poczatkowych
            publish("--------- Randomize start points  ----------");
            for (int i=0; i < crs2_N-1; i++){
                
                HashMap<String,Double> arguementToValueList = new HashMap<String,Double>();

                for(String variableName: optimizerInput.variableList.keySet()){
                    Double minContraint = optimizerInput.variableMinList.get(variableName);
                    Double maxContraint = optimizerInput.variableMaxList.get(variableName);
                    Random random = new Random();
                    Double randomVariable = minContraint+(random.nextDouble()*(maxContraint-minContraint));
                    arguementToValueList.put(variableName, randomVariable);

                }

                CalculatedFunctionPoint calculatedFunctionPoint = FunctionCalculation(arguementToValueList);


                publish("Point "+(i+1)+" Optimization Variables: "+calculatedFunctionPoint.optimizationVariableToValueList);
                publish("Point "+(i+1)+" Target Function Arguements: "+calculatedFunctionPoint.targetFunctionArguementToValueList);
                publish("Point "+(i+1)+" Target Function Value: "+calculatedFunctionPoint.targetFunctionValue);

                this.optimizerOutput.initialRandomPointsList.add(calculatedFunctionPoint);


                crs2_A.add(calculatedFunctionPoint);
            }

            publish("--------- End of randomizing start points  ----------");



             publish("--------- Start Iteration  ----------");
            for(int iterationNumber =0; iterationNumber < maxIter; iterationNumber++){

                IterationResults iterationResults = new IterationResults(); //wynik iteracji

                //wyszukiwanie najlepszego punktu L (minimalnego) i najgorszego punktu M (maksymalnego)(biorac pod uwage wartosc funkcji w punkcie)
                for (int i=0;i < crs2_A.size(); i++){
                    if (i==0){
                        crs2_L = crs2_A.get(i);
                        crs2_M = crs2_A.get(i);
                    }
                    else {
                        if(crs2_L.targetFunctionValue > crs2_A.get(i).targetFunctionValue){
                            crs2_L = crs2_A.get(i);
                        }
                        if(crs2_M.targetFunctionValue < crs2_A.get(i).targetFunctionValue){
                            crs2_M = crs2_A.get(i);
                        }
                    }
                }




                publish("Iteration "+iterationNumber+": min point L Optimization Variables: "+crs2_L.optimizationVariableToValueList);
                publish("Iteration "+iterationNumber+": min point L Target Function Arguements: "+crs2_L.targetFunctionArguementToValueList);
                publish("Iteration "+iterationNumber+": min point L Target Function Value: "+crs2_L.targetFunctionValue);
                publish("Iteration "+iterationNumber+": max point M Optimization Variables: "+crs2_M.optimizationVariableToValueList);
                publish("Iteration "+iterationNumber+": max point M Target Function Arguements: "+crs2_M.targetFunctionArguementToValueList);
                publish("Iteration "+iterationNumber+": max point M Target Function Value: "+crs2_M.targetFunctionValue);


                iterationResults.crs2_L = crs2_L;
                iterationResults.crs2_M = crs2_M;



                publish("Iteration "+iterationNumber+": searching for P<M");
                //szukaj nowego M
                CalculatedFunctionPoint crs2_P = new CalculatedFunctionPoint();
                int i=0;
                do{
                    i++;
                    if(i > maxPTries){
                        publish("Iteration "+iterationNumber+": maxRandomizeTryNumber exceded when searching for new M");
                        //this.optimizerOutput.textOutput = this.optimizerOutput.textOutput + "Iteration "+iterationNumber+": maxRandomizeTryNumber exceded when searching for new M\n" ;
                        throw new  OptimizationEndException("Reached maxPTries at iteration: "+iterationNumber);
                    }

                    //wylosuj wspolrzedne punktu P, ktore spelniaja ograniczenia
                    crs2_P = Crs2PContraintsCalcultion(crs2_A,crs2_K,crs2_L);

                    //oblicz wartosc funkcji w punkcie P
                    crs2_P = FunctionCalculation(crs2_P.optimizationVariableToValueList);

                    //zamien P z M, jesli lepsze (mniejsze)
                    if(crs2_P.targetFunctionValue < crs2_M.targetFunctionValue){
                        crs2_A.set(crs2_A.indexOf(crs2_M), crs2_P);
                        publish("Iteration "+iterationNumber+": end of searching for P<M: "+crs2_P.targetFunctionValue+" - replacing M with P");
                        iterationResults.crs2_P = crs2_P;
                    }

                }while(crs2_P.targetFunctionValue >= crs2_M.targetFunctionValue);

                //wstaw wynik iteracji do listy wyjsciowej wynikow
                this.optimizerOutput.iterationResultsList.add(iterationResults);


            }

            throw new  OptimizationEndException("Reached maxIter");
        }catch(OptimizationEndException e){ //koniec optymalizacji
            //koncowe wyszukiwanie najlepszego punktu L (minimalnego) i najgorszego punktu M (maksymalnego)(biorac pod uwage wartosc funkcji w punkcie)
            for (int i=0;i < crs2_A.size(); i++){
                if (i==0){
                    crs2_L = crs2_A.get(i);
                    crs2_M = crs2_A.get(i);
                }
                else {
                    if(crs2_L.targetFunctionValue > crs2_A.get(i).targetFunctionValue){
                        crs2_L = crs2_A.get(i);
                    }
                    if(crs2_M.targetFunctionValue < crs2_A.get(i).targetFunctionValue){
                        crs2_M = crs2_A.get(i);
                    }
                }
            }

            //ustaw parametry minimum w modelu
            FunctionCalculation(crs2_L.optimizationVariableToValueList);

            long endTime = System.currentTimeMillis(); //czas poczatkowy optymalizacji

            publish("\n");
            publish("--------- End of optimization ---------");
            publish("min point L Optimization Variables: "+crs2_L.optimizationVariableToValueList);
            publish("min point L Target Function Arguements: "+crs2_L.targetFunctionArguementToValueList);
            publish("min point L Target Function Value: "+crs2_L.targetFunctionValue);
            publish("max point M Optimization Variables: "+crs2_M.optimizationVariableToValueList);
            publish("max point M Target Function Arguements: "+crs2_M.targetFunctionArguementToValueList);
            publish("max point M Target Function Value: "+crs2_M.targetFunctionValue);
            publish("\n");

            optimizerOutput.textOutput = optimizerOutput.textOutput + e.getMessage()+"\n";
            optimizerOutput.textOutput = optimizerOutput.textOutput + "--------- End of optimization ---------\n";
            optimizerOutput.textOutput = optimizerOutput.textOutput + "min point L Optimization Variables: "+crs2_L.optimizationVariableToValueList+"\n";
            optimizerOutput.textOutput = optimizerOutput.textOutput + "min point L Target Function Arguements: "+crs2_L.targetFunctionArguementToValueList+"\n";
            optimizerOutput.textOutput = optimizerOutput.textOutput + "min point L Target Function Value: "+crs2_L.targetFunctionValue+"\n";
            optimizerOutput.textOutput = optimizerOutput.textOutput + "execution time: "+((endTime-startTime)/1000)+" s\n";

            
        }


         
    }

    //losuje punkt P dla otymalizacji CRS2 bez sprawdzania ograniczen
    private CalculatedFunctionPoint Crs2PRandomCalculation(ArrayList<CalculatedFunctionPoint> crs2_A, int crs2_K, CalculatedFunctionPoint crs2_L) throws Exception{

         //losowanie punktow do tablicy R i obliczenie centroidy i P
        ArrayList<CalculatedFunctionPoint> crs2_R = new ArrayList<CalculatedFunctionPoint>(); //tablica R przechowujaca wyniki obliczania funkcji algorytmem CRS3
        crs2_R.add(crs2_L); //jako R(1) punkt L

        if(debugMode) System.out.println("Wylosowane punkty R:" );
        //losuje reszte punktow z A
        for (int i=0; i < crs2_K; i++){

            CalculatedFunctionPoint calculatedFunctionPoint;
            do{
                Random random = new Random();
                Integer sizeOfA = Integer.valueOf(crs2_A.size());

                int randomPosition = (int)(sizeOfA.doubleValue()*random.nextDouble());

                if(debugMode) System.out.println("crs2_N2: "+crs2_K);
                if(debugMode) System.out.println("randomPosition: "+randomPosition);

                calculatedFunctionPoint = crs2_A.get(randomPosition);

                if(debugMode) System.out.println("Wartosc: "+calculatedFunctionPoint.targetFunctionValue);
                if(debugMode) System.out.println("Argumenty: "+calculatedFunctionPoint.optimizationVariableToValueList);

            }while(crs2_R.contains(calculatedFunctionPoint)); //probuj dalej jesli punkty sie powtarzaja

            crs2_R.add(calculatedFunctionPoint);
        }

        if(debugMode) System.out.println();

        //obliczenie centroidy G
        CalculatedFunctionPoint crs2_G = new CalculatedFunctionPoint();

        for(String variableName: optimizerInput.variableList.keySet()){
            crs2_G.optimizationVariableToValueList.put(variableName, 0.0);
        }

        //dodaj do siebie wszystkie zmienne z wylosowanych punktow
        for (int i=0; i < crs2_R.size() -1; i++){

            for(String variableName: optimizerInput.variableList.keySet()){
                Double tempValue = crs2_G.optimizationVariableToValueList.get(variableName) + crs2_R.get(i).optimizationVariableToValueList.get(variableName);
                crs2_G.optimizationVariableToValueList.put(variableName, tempValue);

            }
        }

        //oblicz srednie wsp punktow dzielac przez n
        for(String variableName: optimizerInput.variableList.keySet()){
                Double tempValue = crs2_G.optimizationVariableToValueList.get(variableName)/(crs2_R.size()-1);
                crs2_G.optimizationVariableToValueList.put(variableName, tempValue);
        }

        if(debugMode) System.out.println("crs2_G:" );
        if(debugMode) System.out.println("Argumenty: "+crs2_G.optimizationVariableToValueList);
        if(debugMode) System.out.println();


        //obliczenie P
        CalculatedFunctionPoint crs2_P = new CalculatedFunctionPoint();

        if(debugMode) System.out.println("R(n+1):" );
        if(debugMode) System.out.println("Argumenty: "+crs2_R.get(crs2_R.size()-1).optimizationVariableToValueList);

        for(String variableName: optimizerInput.variableList.keySet()){
            Double tempValue = 2*crs2_G.optimizationVariableToValueList.get(variableName)-crs2_R.get(crs2_R.size()-1).optimizationVariableToValueList.get(variableName);
            crs2_P.optimizationVariableToValueList.put(variableName, tempValue);
        }

        if(debugMode) System.out.println("P:" );
        if(debugMode) System.out.println("Argumenty: "+crs2_P.optimizationVariableToValueList);

        return crs2_P;
    }


    //losuje P z uwzglednieniem ograniczen
    private CalculatedFunctionPoint Crs2PContraintsCalcultion(ArrayList<CalculatedFunctionPoint> crs2_A, int crs2_N2, CalculatedFunctionPoint crs2_L) throws Exception{
        //oblicz P
        Boolean pMeetsConstraintsFlag = false;
        CalculatedFunctionPoint crs2_P = new CalculatedFunctionPoint();
        //jesli P nie spelnia ograniczen, to oblicz jeszcze raz
        do{
            crs2_P = Crs2PRandomCalculation(crs2_A,crs2_N2, crs2_L);

            for(String variableName: optimizerInput.variableList.keySet()){
                Double minContraint = optimizerInput.variableMinList.get(variableName);
                Double maxContraint = optimizerInput.variableMaxList.get(variableName);

                if(crs2_P.optimizationVariableToValueList.get(variableName) < minContraint || crs2_P.optimizationVariableToValueList.get(variableName) > maxContraint){
                    pMeetsConstraintsFlag = false;
                    break;
                }
                else{
                    pMeetsConstraintsFlag = true;
                }
            }
        }
        while(!pMeetsConstraintsFlag);

        return crs2_P;
    }



    /**
     * Klasa reprezentujaca zakonczenie optymalizacji w wyniku osiagniecia punktu koncowego
     */
    private class OptimizationEndException extends Exception {
        OptimizationEndException(String message){
            super(message);
        }
    }

    //metoda swingworkera uruchamiajaca proces
    @Override
    protected Void doInBackground() {

        try{
            CRS2Optimization();
        }catch(Exception e){
            //System.out.println("Blad: " + e.getMessage());
        }

        return null;
    }

    //zmien tekst w konsoli
    @Override
    protected void process(List<String> messageList) {
        
        for(int i=0; i < messageList.size(); i++){
            nsdhController.nsdhView.nsdhGUI.graphPanel.addConsoleText(messageList.get(i));
        }

    }

    //zakoncz optymalizacje
    @Override
    protected void done() {
            nsdhController.nsdhView.nsdhGUI.optimizerProgressFrame.dispose();
            nsdhController.nsdhView.nsdhGUI.graphPanel.addConsoleText("Koniec optymalizacji");
            new OptimizerResultsFrame(nsdhController);
    }

}
