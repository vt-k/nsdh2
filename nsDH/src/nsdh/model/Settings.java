/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh.model;

/**
 *
 * @author vojteq
 */
public class Settings {
    public String ns2Path; //sciezka uruchomieniowa ns-2
    public String ns2OutputFilePath; //sciezka pliku z wynikiem wykonania skryptu ns-2
    public String ns2InputFilePath; //sciezka pliku do uruchomienia przez ns-2
    public String openedXmlFilePath; //aktualnie otwarty plik, "" gdy nowy
    public Boolean generateNamFile; //czy generowac plik dla NAM
    public String namFileName; //nazwa pliku dla NAM

    public Settings(){
        ns2Path = "ns";
        ns2OutputFilePath = "ns2output.txt";
        ns2InputFilePath = "ns2input.tcl";
        openedXmlFilePath = "";
        generateNamFile = false;
        namFileName = "nam_input.nam";

    }
}


