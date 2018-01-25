/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nsdh;

import nsdh.model.OptimizerInput;
import nsdh.model.*;
import java.io.*;
import java.lang.Math;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import javax.xml.xpath.*;
import nsdh.model.script_models.*;
import nsdh.controller.*;
import org.xml.sax.SAXParseException;
import javax.swing.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;

import java.util.concurrent.locks.*;


/**
 *
 * @author vojteq
 */
public class NsdhController {
    public NsdhModel nsdhModel;
    public NsdhView nsdhView;
    public Optimizer optimizer;
    public Lock lock = new ReentrantLock();

    public NsdhController (NsdhModel nsdhModel){
        this.nsdhModel = nsdhModel;
        
        OptimizerInput optimizerInput = new OptimizerInput();
        this.optimizer = new Optimizer(this,optimizerInput);
    }

    public void ClearModel(){
        nsdhModel.network_settings = new Network_settings();
        nsdhModel.network_settings.routing_type="auto";
        nsdhModel.network_structure = new Network_structure();
        nsdhModel.scenario = new Scenario();
        nsdhModel.settings.openedXmlFilePath = "";
        nsdhModel.results = new Results();
        nsdhModel.sequences = new Sequences();

        OptimizerInput optimizerInput = new OptimizerInput();
        this.optimizer = new Optimizer(this,optimizerInput);
    }


    /**
     * Wczytuje plik xml z ustawieniami sieci do obiektow modelua
     * @param filepath sciezka pliku do zaladowania
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public void LoadFromXmlFile (String filepath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException,Exception {

        nsdhModel = new NsdhModel();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        factory.setValidating(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

        // ustawia schemat xml - plik xsd do sprawdzania poprawnosci pliku xml
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", NsdhController.class.getResource("/nsdh/resources/settings.xsd").toString());
        
        DocumentBuilder builder = factory.newDocumentBuilder();

        //obsluga bledow parsera
        ErrorHandler handler = new ErrorHandler() {

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw new SAXException(exception.getMessage());
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw new SAXException(exception.getMessage());
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw new SAXException(exception.getMessage());
            }
        };
        builder.setErrorHandler(handler);

        //parsuj dokument
        Document document = builder.parse(filepath);
        
        XPath xpath = XPathFactory.newInstance().newXPath();

        //USTAWIENIA SIECI

        //routing_type
        nsdhModel.network_settings.routing_type = (String)xpath.evaluate("/nsdh_file/network_settings/routing_type", document, XPathConstants.STRING);

        //ladowanie listy queue_policy
        NodeList queue_policy = (NodeList)xpath.evaluate("/nsdh_file/network_settings/queue_policy", document, XPathConstants.NODESET);
        for (int i=0; i<queue_policy.getLength(); i++){
            Queue_policy queue_policy_item = new Queue_policy();
            queue_policy_item.name = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/@name", document, XPathConstants.STRING);
            queue_policy_item.scheduler = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/scheduler", document, XPathConstants.STRING);
            queue_policy_item.mean_packet_size = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/mean_packet_size", document, XPathConstants.STRING);
            nsdhModel.network_settings.queue_policy_list.put(queue_policy_item.name, queue_policy_item);

            
            //ladowanie listy queue
            NodeList queue = (NodeList)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) +"]/queue", document, XPathConstants.NODESET);
            for (int j=0; j<queue.getLength(); j++){
                Queue queue_item = new Queue();
                queue_item.name = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/@name", document, XPathConstants.STRING);
                queue_item.policer = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer", document, XPathConstants.STRING);
                //ladowanie listy policer_entry
                NodeList policer_entry = (NodeList)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) +"]/queue[" + (j+1) +"]/policer_entry", document, XPathConstants.NODESET);
                for (int k=0; k<policer_entry.getLength(); k++){
                    Policer_entry policer_entry_item = new Policer_entry();
                    policer_entry_item.service = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/@service", document, XPathConstants.STRING);

                    //ustaw parametry w zaleznosci od policera
                    if(queue_item.policer.equals("TSW2CM")){
                        policer_entry_item.CIR = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/CIR", document, XPathConstants.STRING);
                    }
                    else if(queue_item.policer.equals("TSW3CM")){
                        policer_entry_item.CIR = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/CIR", document, XPathConstants.STRING);
                        policer_entry_item.PIR = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/PIR", document, XPathConstants.STRING);
                        
                    }else if(queue_item.policer.equals("TokenBucket")){
                        policer_entry_item.CIR = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/CIR", document, XPathConstants.STRING);
                        policer_entry_item.CBS = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/CBS", document, XPathConstants.STRING);
                    }else if(queue_item.policer.equals("srTCM")){
                        policer_entry_item.CIR = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/CIR", document, XPathConstants.STRING);
                        policer_entry_item.CBS = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/CBS", document, XPathConstants.STRING);
                        policer_entry_item.EBS = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/EBS", document, XPathConstants.STRING);
                    
                    }else if(queue_item.policer.equals("srTCM")){
                        policer_entry_item.CIR = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/CIR", document, XPathConstants.STRING);
                        policer_entry_item.CBS = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/CBS", document, XPathConstants.STRING);
                        policer_entry_item.PBS = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/PBS", document, XPathConstants.STRING);
                        policer_entry_item.PIR = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/policer_entry[" + (k+1) + "]/PIR", document, XPathConstants.STRING);
                    }
                    
                    queue_item.policer_entry_list.put(policer_entry_item.service,policer_entry_item);
                }

                //ustawia parametry schedulera
                if(queue_policy_item.scheduler.equals("PRI")){
                    String priority = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/scheduler_params/priority", document, XPathConstants.STRING);
                    String rate = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/scheduler_params/rate", document, XPathConstants.STRING);
                    queue_item.scheduler_params.priority = priority;
                    queue_item.scheduler_params.rate = rate;
                }
                
                //ustawia parametry schedulera WRR WIRR lub RR
                if(queue_policy_item.scheduler.equals("WRR") || queue_policy_item.scheduler.equals("WIRR")  || queue_policy_item.scheduler.equals("RR")){
                    String weight = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/scheduler_params/weight", document, XPathConstants.STRING);
                    queue_item.scheduler_params.weight = weight;
                }
                
                //ladowanie listy subqueue
                NodeList subqueue = (NodeList)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) +"]/queue[" + (j+1) +"]/subqueue", document, XPathConstants.NODESET);
                for (int k=0; k<subqueue.getLength(); k++){
                    Subqueue subqueue_item = new Subqueue();
                    subqueue_item.ordinal = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/subqueue[" + (k+1) + "]/@ordinal", document, XPathConstants.STRING);
                    subqueue_item.max_threshold = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/subqueue[" + (k+1) + "]/max_threshold", document, XPathConstants.STRING);
                    subqueue_item.min_threshold = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/subqueue[" + (k+1) + "]/min_threshold", document, XPathConstants.STRING);
                    subqueue_item.probability_dropping = (String)xpath.evaluate("/nsdh_file/network_settings/queue_policy[" + (i+1) + "]/queue[" + (j+1) + "]/subqueue[" + (k+1) + "]/probability_dropping", document, XPathConstants.STRING);
                    queue_item.subqueue_list.add(subqueue_item);
                }

                nsdhModel.network_settings.queue_policy_list.get(queue_policy_item.name).queue_list.add(queue_item);
            }
        }
        
        //ladowanie listy service
        NodeList service = (NodeList)xpath.evaluate("/nsdh_file/network_settings/service", document, XPathConstants.NODESET);
        for (int i=0; i<service.getLength(); i++){
            Service service_item = new Service();
            service_item.name = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/@name", document, XPathConstants.STRING);

            //ladowanie server_transport
            //ladowanie parametrow zgodnych ze wszystkimi agentami
            service_item.server_transport.server_agent = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent", document, XPathConstants.STRING);
            service_item.server_transport.server_agent_params.fid = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/fid", document, XPathConstants.STRING);
            service_item.server_transport.server_agent_params.prio = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/prio", document, XPathConstants.STRING);
            service_item.server_transport.server_agent_params.flags = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/flags", document, XPathConstants.STRING);
            service_item.server_transport.server_agent_params.ttl = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/ttl", document, XPathConstants.STRING);

            //jesli Agent jest z rodziny TCP zaladuj dodatkowo parametry TCP
            if (service_item.server_transport.server_agent.equals("Agent/TCP") ||
                    service_item.server_transport.server_agent.equals("Agent/TCP/Reno") ||
                    service_item.server_transport.server_agent.equals("Agent/TCP/Newreno") ||
                    service_item.server_transport.server_agent.equals("Agent/TCP/Sack1") ||
                    service_item.server_transport.server_agent.equals("Agent/TCP/Vegas") ||
                    service_item.server_transport.server_agent.equals("Agent/TCP/Fack") ||
                    service_item.server_transport.server_agent.equals("Agent/TCP/Linux")
                    )
            {
                service_item.server_transport.server_agent_params.server_agent_TCP_params.window = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/window", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.windowInit = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/windowInit", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.windowOption = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/windowOption", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.windowConstant = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/windowConstant", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.windowThresh = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/windowThresh", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.overhead = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/overhead", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.ecn = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/ecn", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.packetSize = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/packetSize", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.bugFix = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/bugFix", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.slow_start_restart = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/slow_start_restart", document, XPathConstants.STRING);
                service_item.server_transport.server_agent_params.server_agent_TCP_params.tcpTick = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/tcpTick", document, XPathConstants.STRING);

                //jesli Agent to Agent/TCP/Newreno to ustaw dodatkowe paramatery NewReno oprocz TCP
                if (service_item.server_transport.server_agent.equals("Agent/TCP/Newreno")){
                    service_item.server_transport.server_agent_params.server_agent_TCP_params.server_agent_TCP_NewReno_params.newreno_changes = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_TCP_params/server_agent_TCP_NewReno_params/newreno_changes", document, XPathConstants.STRING);
                }
            }

            //jesli Agent jest typu UDP zaladuj dodatkowo parametry UDP
            if (service_item.server_transport.server_agent.equals("Agent/UDP")){
                service_item.server_transport.server_agent_params.server_agent_UDP_params.packetSize = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_transport/server_agent_params/server_agent_UDP_params/packetSize", document, XPathConstants.STRING);
            }

            //ladowanie server_application
            service_item.server_application.type = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/type", document, XPathConstants.STRING);

            //ladowanie parametrow gdy typ Application/Traffic/Exponential
            if(service_item.server_application.type.equals("Application/Traffic/Exponential")){
                service_item.server_application.application_Traffic_Exponential_params.packetSize = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_Exponential_params/packetSize", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_Exponential_params.burst_time = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_Exponential_params/burst_time", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_Exponential_params.idle_time = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_Exponential_params/idle_time", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_Exponential_params.rate = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_Exponential_params/rate", document, XPathConstants.STRING);
            }

            //ladowanie parametrow gdy typ Application/Traffic/Pareto
            if(service_item.server_application.type.equals("Application/Traffic/Pareto")){
                service_item.server_application.application_Traffic_Pareto_params.packetSize = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_Pareto_params/packetSize", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_Pareto_params.burst_time = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_Pareto_params/burst_time", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_Pareto_params.idle_time = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_Pareto_params/idle_time", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_Pareto_params.rate = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_Pareto_params/rate", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_Pareto_params.shape = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_Pareto_params/shape", document, XPathConstants.STRING);
            }

            //ladowanie parametrow gdy typ Application/Traffic/CBR
            if(service_item.server_application.type.equals("Application/Traffic/CBR")){
                service_item.server_application.application_Traffic_CBR_params.rate = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_CBR_params/rate", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_CBR_params.interval = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_CBR_params/interval", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_CBR_params.packetSize = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_CBR_params/packetSize", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_CBR_params.random = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_CBR_params/random", document, XPathConstants.STRING);
                service_item.server_application.application_Traffic_CBR_params.maxpkts = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Traffic_CBR_params/maxpkts", document, XPathConstants.STRING);
            }

            //ladowanie parametrow gdy typ Application/Telnet
            if(service_item.server_application.type.equals("Application/Telnet")){
                service_item.server_application.application_Telnet_params.interval = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_Telnet_params/interval", document, XPathConstants.STRING);
            }

            //ladowanie parametrow gdy typ Application/FTP
            if(service_item.server_application.type.equals("Application/FTP")){
                service_item.server_application.application_FTP_params.maxpkts = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/server_application/application_FTP_params/maxpkts", document, XPathConstants.STRING);
            }

            //ladowanie client_sink
            service_item.client_sink.client_agent = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/client_sink/client_agent", document, XPathConstants.STRING);

            service_item.client_sink.client_agent_params.fid = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/client_sink/client_agent_params/fid", document, XPathConstants.STRING);
            service_item.client_sink.client_agent_params.prio = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/client_sink/client_agent_params/prio", document, XPathConstants.STRING);
            service_item.client_sink.client_agent_params.flags = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/client_sink/client_agent_params/flags", document, XPathConstants.STRING);
            service_item.client_sink.client_agent_params.ttl = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/client_sink/client_agent_params/ttl", document, XPathConstants.STRING);


            //ladowanie parametrow gdy Agent jest z rodziny Agent/TCPSink
            if(service_item.client_sink.client_agent.equals("Agent/TCPSink") ||
                    service_item.client_sink.client_agent.equals("Agent/TCPSink/DelAck") ||
                    service_item.client_sink.client_agent.equals("Agent/TCPSink/Sack1") ||
                    service_item.client_sink.client_agent.equals("Agent/TCPSink/Sack1/DelAck")
                    )
            {
                service_item.client_sink.client_agent_params.client_agent_TCPSink_params.packetSize = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/client_sink/client_agent_params/client_agent_TCPSink_params/packetSize", document, XPathConstants.STRING);
                service_item.client_sink.client_agent_params.client_agent_TCPSink_params.maxSackBlocks = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/client_sink/client_agent_params/client_agent_TCPSink_params/maxSackBlocks", document, XPathConstants.STRING);

                //dodatkowe parametry gdy agent Agent/TCPSink/DelAck
                if(service_item.client_sink.client_agent.equals("Agent/TCPSink/DelAck")){
                    service_item.client_sink.client_agent_params.client_agent_TCPSink_params.client_agent_TCPSink_DelAck_params.interval = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/client_sink/client_agent_params/client_agent_TCPSink_params/client_agent_TCPSink_DelAck_params/interval", document, XPathConstants.STRING);
                }

                //dodatkowe parametry gdy agent Agent/TCPSink/Sack1/DelAck
                if(service_item.client_sink.client_agent.equals("Agent/TCPSink/Sack1/DelAck")){
                    service_item.client_sink.client_agent_params.client_agent_TCPSink_params.client_agent_TCPSink_Sack1_DelAck_params.interval = (String)xpath.evaluate("/nsdh_file/network_settings/service[" + (i+1) + "]/client_sink/client_agent_params/client_agent_TCPSink_params/client_agent_TCPSink_Sack1_DelAck_params/interval", document, XPathConstants.STRING);
                }

            }

            //dodaj skonfigurowany service do listy
            nsdhModel.network_settings.service_list.put(service_item.name, service_item);
        }

        //STRUKTURA SIECI

        //ladowanie listy router
        NodeList router = (NodeList)xpath.evaluate("/nsdh_file/network_structure/router", document, XPathConstants.NODESET);
        for (int i=0; i<router.getLength(); i++){
            Router router_item = new Router();
            router_item.name = (String)xpath.evaluate("/nsdh_file/network_structure/router[" + (i+1) + "]/@name", document, XPathConstants.STRING);
            router_item.gui_x = (String)xpath.evaluate("/nsdh_file/network_structure/router[" + (i+1) + "]/gui_x", document, XPathConstants.STRING);
            router_item.gui_y = (String)xpath.evaluate("/nsdh_file/network_structure/router[" + (i+1) + "]/gui_y", document, XPathConstants.STRING);
            //routing_table
            NodeList routing_table_row = (NodeList)xpath.evaluate("/nsdh_file/network_structure/router[" + (i+1) + "]/routing_table_row", document, XPathConstants.NODESET);
            for (int j=0; j<routing_table_row.getLength(); j++){
                Routing_table_row routing_table_row_item = new Routing_table_row();
                routing_table_row_item.next_hop=(String)xpath.evaluate("/nsdh_file/network_structure/router[" + (i+1) + "]/routing_table_row["+(j+1)+"]/next_hop", document, XPathConstants.STRING);
                routing_table_row_item.packet_destination=(String)xpath.evaluate("/nsdh_file/network_structure/router[" + (i+1) + "]/routing_table_row["+(j+1)+"]/packet_destination", document, XPathConstants.STRING);
                router_item.routing_table_row_list.add(routing_table_row_item);
            }
            nsdhModel.network_structure.router_list.put(router_item.name, router_item);
        }

        //ladowanie listy pc
        NodeList pc = (NodeList)xpath.evaluate("/nsdh_file/network_structure/pc", document, XPathConstants.NODESET);
        for (int i=0; i<pc.getLength(); i++){
            Pc pc_item = new Pc();
            pc_item.name = (String)xpath.evaluate("/nsdh_file/network_structure/pc[" + (i+1) + "]/@name", document, XPathConstants.STRING);
            pc_item.gui_x = (String)xpath.evaluate("/nsdh_file/network_structure/pc[" + (i+1) + "]/gui_x", document, XPathConstants.STRING);
            pc_item.gui_y = (String)xpath.evaluate("/nsdh_file/network_structure/pc[" + (i+1) + "]/gui_y", document, XPathConstants.STRING);
            nsdhModel.network_structure.pc_list.put(pc_item.name, pc_item);
        }

        //ladowanie listy server_client_connection
        NodeList server_client_connection = (NodeList)xpath.evaluate("/nsdh_file/network_structure/server_client_connection", document, XPathConstants.NODESET);
        for (int i=0; i<server_client_connection.getLength(); i++){
            Server_client_connection server_client_connection_item = new Server_client_connection();
            server_client_connection_item.name = (String)xpath.evaluate("/nsdh_file/network_structure/server_client_connection[" + (i+1) + "]/@name", document, XPathConstants.STRING);
            server_client_connection_item.server_pc = (String)xpath.evaluate("/nsdh_file/network_structure/server_client_connection[" + (i+1) + "]/server_pc", document, XPathConstants.STRING);
            server_client_connection_item.client_pc = (String)xpath.evaluate("/nsdh_file/network_structure/server_client_connection[" + (i+1) + "]/client_pc", document, XPathConstants.STRING);
            server_client_connection_item.service = (String)xpath.evaluate("/nsdh_file/network_structure/server_client_connection[" + (i+1) + "]/service", document, XPathConstants.STRING);
            nsdhModel.network_structure.server_client_connection_list.put(server_client_connection_item.name, server_client_connection_item);
        }

        //ladowanie listy standard_link
        NodeList standard_link = (NodeList)xpath.evaluate("/nsdh_file/network_structure/standard_link", document, XPathConstants.NODESET);
        for (int i=0; i<standard_link.getLength(); i++){
            Standard_link standard_link_item = new Standard_link();
            standard_link_item.name = (String)xpath.evaluate("/nsdh_file/network_structure/standard_link[" + (i+1) + "]/@name", document, XPathConstants.STRING);
            standard_link_item.node1 = (String)xpath.evaluate("/nsdh_file/network_structure/standard_link[" + (i+1) + "]/node1", document, XPathConstants.STRING);
            standard_link_item.node2 = (String)xpath.evaluate("/nsdh_file/network_structure/standard_link[" + (i+1) + "]/node2", document, XPathConstants.STRING);
            standard_link_item.delay = (String)xpath.evaluate("/nsdh_file/network_structure/standard_link[" + (i+1) + "]/delay", document, XPathConstants.STRING);
            standard_link_item.bandwidth = (String)xpath.evaluate("/nsdh_file/network_structure/standard_link[" + (i+1) + "]/bandwidth", document, XPathConstants.STRING);
            standard_link_item.queue_limit = (String)xpath.evaluate("/nsdh_file/network_structure/standard_link[" + (i+1) + "]/queue_limit", document, XPathConstants.STRING);
            nsdhModel.network_structure.standard_link_list.put(standard_link_item.name, standard_link_item);
        }

        //ladowanie listy edge_core_link
        NodeList edge_core_link = (NodeList)xpath.evaluate("/nsdh_file/network_structure/edge_core_link", document, XPathConstants.NODESET);
        for (int i=0; i<edge_core_link.getLength(); i++){
            Edge_core_link edge_core_link_item = new Edge_core_link();
            edge_core_link_item.name = (String)xpath.evaluate("/nsdh_file/network_structure/edge_core_link[" + (i+1) + "]/@name", document, XPathConstants.STRING);
            edge_core_link_item.queue_policy = (String)xpath.evaluate("/nsdh_file/network_structure/edge_core_link[" + (i+1) + "]/queue_policy", document, XPathConstants.STRING);
            edge_core_link_item.edge_router = (String)xpath.evaluate("/nsdh_file/network_structure/edge_core_link[" + (i+1) + "]/edge_router", document, XPathConstants.STRING);
            edge_core_link_item.core_router = (String)xpath.evaluate("/nsdh_file/network_structure/edge_core_link[" + (i+1) + "]/core_router", document, XPathConstants.STRING);
            edge_core_link_item.delay = (String)xpath.evaluate("/nsdh_file/network_structure/edge_core_link[" + (i+1) + "]/delay", document, XPathConstants.STRING);
            edge_core_link_item.bandwidth = (String)xpath.evaluate("/nsdh_file/network_structure/edge_core_link[" + (i+1) + "]/bandwidth", document, XPathConstants.STRING);
            edge_core_link_item.queue_limit = (String)xpath.evaluate("/nsdh_file/network_structure/edge_core_link[" + (i+1) + "]/queue_limit", document, XPathConstants.STRING);
            nsdhModel.network_structure.edge_core_link_list.put(edge_core_link_item.name, edge_core_link_item);
        }

        //ladowanie listy core_core_link
        NodeList core_core_link = (NodeList)xpath.evaluate("/nsdh_file/network_structure/core_core_link", document, XPathConstants.NODESET);
        for (int i=0; i<core_core_link.getLength(); i++){
            Core_core_link core_core_link_item = new Core_core_link();
            core_core_link_item.name = (String)xpath.evaluate("/nsdh_file/network_structure/core_core_link[" + (i+1) + "]/@name", document, XPathConstants.STRING);
            core_core_link_item.queue_policy = (String)xpath.evaluate("/nsdh_file/network_structure/core_core_link[" + (i+1) + "]/queue_policy", document, XPathConstants.STRING);
            core_core_link_item.core_router1 = (String)xpath.evaluate("/nsdh_file/network_structure/core_core_link[" + (i+1) + "]/core_router1", document, XPathConstants.STRING);
            core_core_link_item.core_router2 = (String)xpath.evaluate("/nsdh_file/network_structure/core_core_link[" + (i+1) + "]/core_router2", document, XPathConstants.STRING);
            core_core_link_item.delay = (String)xpath.evaluate("/nsdh_file/network_structure/core_core_link[" + (i+1) + "]/delay", document, XPathConstants.STRING);
            core_core_link_item.bandwidth = (String)xpath.evaluate("/nsdh_file/network_structure/core_core_link[" + (i+1) + "]/bandwidth", document, XPathConstants.STRING);
            core_core_link_item.queue_limit = (String)xpath.evaluate("/nsdh_file/network_structure/core_core_link[" + (i+1) + "]/queue_limit", document, XPathConstants.STRING);
            nsdhModel.network_structure.core_core_link_list.put(core_core_link_item.name, core_core_link_item);
        }

        //ladowanie scenario
        nsdhModel.scenario.total_time = (String)xpath.evaluate("/nsdh_file/scenario/total_time", document, XPathConstants.STRING);
        NodeList event = (NodeList)xpath.evaluate("/nsdh_file/scenario/event", document, XPathConstants.NODESET);
        for (int i=0; i<event.getLength(); i++){
            Event event_item = new Event();
            event_item.time = (String)xpath.evaluate("/nsdh_file/scenario/event[" + (i+1) + "]/time", document, XPathConstants.STRING);
            event_item.server_client_connection = (String)xpath.evaluate("/nsdh_file/scenario/event[" + (i+1) + "]/server_client_connection", document, XPathConstants.STRING);
            event_item.command = (String)xpath.evaluate("/nsdh_file/scenario/event[" + (i+1) + "]/command", document, XPathConstants.STRING);
            event_item.value = (String)xpath.evaluate("/nsdh_file/scenario/event[" + (i+1) + "]/value", document, XPathConstants.STRING);
            nsdhModel.scenario.eventList.add(event_item);
        }

        //ladowanie wejscia optymalizatora
        //zmienne optymalizacji
        NodeList optimization_variable = (NodeList)xpath.evaluate("/nsdh_file/optimizer_input/optimization_variable", document, XPathConstants.NODESET);
        for (int i=0; i<optimization_variable.getLength(); i++){
            Optimization_variable optimization_variable_item = new Optimization_variable();
            optimization_variable_item.name = (String)xpath.evaluate("/nsdh_file/optimizer_input/optimization_variable[" + (i+1) + "]/@name", document, XPathConstants.STRING);
            optimization_variable_item.mapping_expression = (String)xpath.evaluate("/nsdh_file/optimizer_input/optimization_variable[" + (i+1) + "]/mapping_expression", document, XPathConstants.STRING);
            optimization_variable_item.min_value = (String)xpath.evaluate("/nsdh_file/optimizer_input/optimization_variable[" + (i+1) + "]/min_value", document, XPathConstants.STRING);
            optimization_variable_item.max_value = (String)xpath.evaluate("/nsdh_file/optimizer_input/optimization_variable[" + (i+1) + "]/max_value", document, XPathConstants.STRING);
            optimization_variable_item.start_value = (String)xpath.evaluate("/nsdh_file/optimizer_input/optimization_variable[" + (i+1) + "]/start_value", document, XPathConstants.STRING);
            nsdhModel.optimizer_input.optimization_variable_list.put(optimization_variable_item.name, optimization_variable_item);
        }
        //arguemnty f celu
        NodeList target_function_arguement = (NodeList)xpath.evaluate("/nsdh_file/optimizer_input/target_function_arguement", document, XPathConstants.NODESET);
        for (int i=0; i<target_function_arguement.getLength(); i++){
            Target_function_arguement target_function_arguement_item = new Target_function_arguement();
            target_function_arguement_item.name = (String)xpath.evaluate("/nsdh_file/optimizer_input/target_function_arguement[" + (i+1) + "]/@name", document, XPathConstants.STRING);
            target_function_arguement_item.mapping_expression = (String)xpath.evaluate("/nsdh_file/optimizer_input/target_function_arguement[" + (i+1) + "]/mapping_expression", document, XPathConstants.STRING);
            nsdhModel.optimizer_input.target_function_arguement_list.put(target_function_arguement_item.name, target_function_arguement_item);
        }
        //inne parametry optymazliacji
        nsdhModel.optimizer_input.target_function = (String)xpath.evaluate("/nsdh_file/optimizer_input/target_function", document, XPathConstants.STRING);
        nsdhModel.optimizer_input.N_param = (String)xpath.evaluate("/nsdh_file/optimizer_input/N_param", document, XPathConstants.STRING);
        nsdhModel.optimizer_input.K_param = (String)xpath.evaluate("/nsdh_file/optimizer_input/K_param", document, XPathConstants.STRING);
        nsdhModel.optimizer_input.maxIter_param = (String)xpath.evaluate("/nsdh_file/optimizer_input/maxIter_param", document, XPathConstants.STRING);
        nsdhModel.optimizer_input.maxPTries_param = (String)xpath.evaluate("/nsdh_file/optimizer_input/maxPTries_param", document, XPathConstants.STRING);

        //ustawia sciezke aktualnie otwartego pliku
        nsdhModel.settings.openedXmlFilePath = filepath;
        
    }

    /**
     * Zapisuje dane z modeli do pliku xml
     * @param filepath sciezka pliku do zapisu
     * @throws java.io.IOException
     */
    public void SaveToXmlFile (String filepath) throws IOException{
        
        //znacznik nowej linii
        final String endl = System.getProperty("line.separator");

        //otworz strumien zapisu pliku
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath),"UTF8"));

        out.write("<nsdh_file>"+endl);
        out.write("\t<network_settings>"+endl);

        //routing_type
        out.write("\t\t<routing_type>"+ nsdhModel.network_settings.routing_type +"</routing_type>"+endl);

        //queue_policy
        for(Queue_policy queue_policy : nsdhModel.network_settings.queue_policy_list.values()){
            out.write("\t\t<queue_policy name=\""+ queue_policy.name +"\">"+endl);
            out.write("\t\t\t<scheduler>"+ queue_policy.scheduler +"</scheduler>"+endl);
            out.write("\t\t\t<mean_packet_size>"+ queue_policy.mean_packet_size +"</mean_packet_size>"+endl);

            

            //queue
            for(Queue queue : queue_policy.queue_list){
                out.write("\t\t\t<queue name =\""+ queue.name +"\">"+endl);

                //parametry schedulera PRI
                if(queue_policy.scheduler.equals("PRI")){
                    out.write("\t\t\t\t<scheduler_params>"+endl);
                    out.write("\t\t\t\t\t<priority>"+ queue.scheduler_params.priority +"</priority>"+endl);
                    out.write("\t\t\t\t\t<rate>"+ queue.scheduler_params.rate +"</rate>"+endl);
                    out.write("\t\t\t\t</scheduler_params>"+endl);
                }

                //parametry schedulera WRR WIRR lub RR
                if(queue_policy.scheduler.equals("WRR") || queue_policy.scheduler.equals("WIRR")  || queue_policy.scheduler.equals("RR")){
                    out.write("\t\t\t\t<scheduler_params>"+endl);
                    out.write("\t\t\t\t\t<weight>"+ queue.scheduler_params.weight +"</weight>"+endl);
                    out.write("\t\t\t\t</scheduler_params>"+endl);
                }

                //typ policera
                out.write("\t\t\t\t<policer>"+ queue.policer +"</policer>"+endl);


                for(Policer_entry policer_entry: queue.policer_entry_list.values()){
                    out.write("\t\t\t\t<policer_entry service=\""+policer_entry.service+"\">"+endl);
                    //wpisy policera w zaleznosci od typu
                    if(queue.policer.equals("TSW2CM")){
                        out.write("\t\t\t\t\t<CIR>"+ policer_entry.CIR +"</CIR>"+endl);
                    }
                    else if(queue.policer.equals("TSW3CM")){
                        out.write("\t\t\t\t\t<CIR>"+ policer_entry.CIR +"</CIR>"+endl);
                        out.write("\t\t\t\t\t<PIR>"+ policer_entry.PIR +"</PIR>"+endl);
                    }
                    else if(queue.policer.equals("TokenBucket")){
                        out.write("\t\t\t\t\t<CIR>"+ policer_entry.CIR +"</CIR>"+endl);
                        out.write("\t\t\t\t\t<CBS>"+ policer_entry.CBS +"</CBS>"+endl);
                    }else if(queue.policer.equals("srTCM")){
                        out.write("\t\t\t\t\t<CIR>"+ policer_entry.CIR +"</CIR>"+endl);
                        out.write("\t\t\t\t\t<CBS>"+ policer_entry.CBS +"</CBS>"+endl);
                        out.write("\t\t\t\t\t<EBS>"+ policer_entry.EBS +"</EBS>"+endl);
                    }else if(queue.policer.equals("trTCM")){
                        out.write("\t\t\t\t\t<CIR>"+ policer_entry.CIR +"</CIR>"+endl);
                        out.write("\t\t\t\t\t<CBS>"+ policer_entry.CBS +"</CBS>"+endl);
                        out.write("\t\t\t\t\t<PIR>"+ policer_entry.PIR +"</PIR>"+endl);
                        out.write("\t\t\t\t\t<PBS>"+ policer_entry.PBS +"</PBS>"+endl);
                    }
                    out.write("\t\t\t\t</policer_entry>"+endl);

                }

                //subqueue
                for(Subqueue subqueue : queue.subqueue_list){
                    out.write("\t\t\t\t<subqueue ordinal=\""+ subqueue.ordinal +"\">"+endl);
                    out.write("\t\t\t\t\t<min_threshold>"+ subqueue.min_threshold +"</min_threshold>"+endl);
                    out.write("\t\t\t\t\t<max_threshold>"+ subqueue.max_threshold +"</max_threshold>"+endl);
                    out.write("\t\t\t\t\t<probability_dropping>"+ subqueue.probability_dropping +"</probability_dropping>"+endl);
                    out.write("\t\t\t\t</subqueue>"+endl);
                }

                out.write("\t\t\t</queue>"+endl);
            }

            out.write("\t\t</queue_policy>"+endl);

        }

        //service
        for(Service service : nsdhModel.network_settings.service_list.values()){
            out.write("\t\t<service name=\""+ service.name +"\">"+endl);

            //server_transport
            out.write("\t\t\t<server_transport>"+endl);
                out.write("\t\t\t\t<server_agent>"+ service.server_transport.server_agent +"</server_agent>"+endl);
                out.write("\t\t\t\t<server_agent_params>"+endl);
                //parametry zgodne ze wszystkimi agentami
                out.write("\t\t\t\t\t<fid>"+ service.server_transport.server_agent_params.fid +"</fid>"+endl);
                out.write("\t\t\t\t\t<prio>"+ service.server_transport.server_agent_params.prio +"</prio>"+endl);
                out.write("\t\t\t\t\t<flags>"+ service.server_transport.server_agent_params.flags +"</flags>"+endl);
                out.write("\t\t\t\t\t<ttl>"+ service.server_transport.server_agent_params.ttl +"</ttl>"+endl);

                //jesli Agent jest z rodziny TCP zaladuj dodatkowo parametry TCP
                if (service.server_transport.server_agent.equals("Agent/TCP") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Reno") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Newreno") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Sack1") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Vegas") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Fack") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Linux")
                        )
                {
                    out.write("\t\t\t\t\t<server_agent_TCP_params>"+endl);
                    out.write("\t\t\t\t\t\t<window>"+ service.server_transport.server_agent_params.server_agent_TCP_params.window +"</window>"+endl);
                    out.write("\t\t\t\t\t\t<windowInit>"+ service.server_transport.server_agent_params.server_agent_TCP_params.windowInit +"</windowInit>"+endl);
                    out.write("\t\t\t\t\t\t<windowOption>"+ service.server_transport.server_agent_params.server_agent_TCP_params.windowOption +"</windowOption>"+endl);
                    out.write("\t\t\t\t\t\t<windowConstant>"+ service.server_transport.server_agent_params.server_agent_TCP_params.windowConstant +"</windowConstant>"+endl);
                    out.write("\t\t\t\t\t\t<windowThresh>"+ service.server_transport.server_agent_params.server_agent_TCP_params.windowThresh +"</windowThresh>"+endl);
                    out.write("\t\t\t\t\t\t<overhead>"+ service.server_transport.server_agent_params.server_agent_TCP_params.overhead +"</overhead>"+endl);
                    out.write("\t\t\t\t\t\t<ecn>"+ service.server_transport.server_agent_params.server_agent_TCP_params.ecn +"</ecn>"+endl);
                    out.write("\t\t\t\t\t\t<packetSize>"+ service.server_transport.server_agent_params.server_agent_TCP_params.packetSize +"</packetSize>"+endl);
                    out.write("\t\t\t\t\t\t<bugFix>"+ service.server_transport.server_agent_params.server_agent_TCP_params.bugFix +"</bugFix>"+endl);
                    out.write("\t\t\t\t\t\t<slow_start_restart>"+ service.server_transport.server_agent_params.server_agent_TCP_params.slow_start_restart +"</slow_start_restart>"+endl);
                    out.write("\t\t\t\t\t\t<tcpTick>"+ service.server_transport.server_agent_params.server_agent_TCP_params.tcpTick +"</tcpTick>"+endl);

                    //jesli Agent to Agent/TCP/Newreno to ustaw dodatkowe paramatery NewReno oprocz TCP
                    if (service.server_transport.server_agent.equals("Agent/TCP/Newreno")){
                        out.write("\t\t\t\t\t\t<server_agent_TCP_NewReno_params>"+endl);
                        out.write("\t\t\t\t\t\t\t<newreno_changes>"+ service.server_transport.server_agent_params.server_agent_TCP_params.server_agent_TCP_NewReno_params.newreno_changes +"</newreno_changes>"+endl);
                        out.write("\t\t\t\t\t\t</server_agent_TCP_NewReno_params>"+endl);
                    }

                    out.write("\t\t\t\t\t</server_agent_TCP_params>"+endl);
                }
                //jesli Agent jest typu UDP zaladuj dodatkowo parametry UDP
                if (service.server_transport.server_agent.equals("Agent/UDP")){
                    out.write("\t\t\t\t\t<server_agent_UDP_params>"+endl);
                    out.write("\t\t\t\t\t\t<packetSize>"+ service.server_transport.server_agent_params.server_agent_UDP_params.packetSize +"</packetSize>"+endl);
                    out.write("\t\t\t\t\t</server_agent_UDP_params>"+endl);
                }
                out.write("\t\t\t\t</server_agent_params>"+endl);
            out.write("\t\t\t</server_transport>"+endl);

            //server_application
            out.write("\t\t\t<server_application>"+endl);
            out.write("\t\t\t\t<type>"+ service.server_application.type +"</type>"+endl);

            //zapis parametrow gdy typ Application/Traffic/Exponential
            if(service.server_application.type.equals("Application/Traffic/Exponential")){
                out.write("\t\t\t\t<application_Traffic_Exponential_params>"+endl);
                out.write("\t\t\t\t\t<packetSize>"+ service.server_application.application_Traffic_Exponential_params.packetSize +"</packetSize>"+endl);
                out.write("\t\t\t\t\t<burst_time>"+ service.server_application.application_Traffic_Exponential_params.burst_time +"</burst_time>"+endl);
                out.write("\t\t\t\t\t<idle_time>"+ service.server_application.application_Traffic_Exponential_params.idle_time +"</idle_time>"+endl);
                out.write("\t\t\t\t\t<rate>"+ service.server_application.application_Traffic_Exponential_params.rate +"</rate>"+endl);
                out.write("\t\t\t\t</application_Traffic_Exponential_params>"+endl);
            }

            //zapis parametrow gdy typ Application/Traffic/Pareto
            if(service.server_application.type.equals("Application/Traffic/Pareto")){
                out.write("\t\t\t\t<application_Traffic_Pareto_params>"+endl);
                out.write("\t\t\t\t\t<packetSize>"+ service.server_application.application_Traffic_Pareto_params.packetSize +"</packetSize>"+endl);
                out.write("\t\t\t\t\t<burst_time>"+ service.server_application.application_Traffic_Pareto_params.burst_time +"</burst_time>"+endl);
                out.write("\t\t\t\t\t<idle_time>"+ service.server_application.application_Traffic_Pareto_params.idle_time +"</idle_time>"+endl);
                out.write("\t\t\t\t\t<rate>"+ service.server_application.application_Traffic_Pareto_params.rate +"</rate>"+endl);
                out.write("\t\t\t\t\t<shape>"+ service.server_application.application_Traffic_Pareto_params.shape +"</shape>"+endl);
                out.write("\t\t\t\t</application_Traffic_Pareto_params>"+endl);
            }

            //zapis parametrow gdy typ Application/Traffic/CBR
            if(service.server_application.type.equals("Application/Traffic/CBR")){
                out.write("\t\t\t\t<application_Traffic_CBR_params>"+endl);
                out.write("\t\t\t\t\t<rate>"+ service.server_application.application_Traffic_CBR_params.rate +"</rate>"+endl);
                out.write("\t\t\t\t\t<interval>"+ service.server_application.application_Traffic_CBR_params.interval +"</interval>"+endl);
                out.write("\t\t\t\t\t<packetSize>"+ service.server_application.application_Traffic_CBR_params.packetSize +"</packetSize>"+endl);
                out.write("\t\t\t\t\t<random>"+ service.server_application.application_Traffic_CBR_params.random +"</random>"+endl);
                out.write("\t\t\t\t\t<maxpkts>"+ service.server_application.application_Traffic_CBR_params.maxpkts +"</maxpkts>"+endl);
                out.write("\t\t\t\t</application_Traffic_CBR_params>"+endl);
            }

            //zapis parametrow gdy typ Application/Telnet
            if(service.server_application.type.equals("Application/Telnet")){
                out.write("\t\t\t\t<application_Telnet_params>"+endl);
                out.write("\t\t\t\t\t<interval>"+ service.server_application.application_Telnet_params.interval +"</interval>"+endl);
                out.write("\t\t\t\t</application_Telnet_params>"+endl);
            }

            //zapis parametrow gdy typ Application/FTP
            if(service.server_application.type.equals("Application/FTP")){
                out.write("\t\t\t\t<application_FTP_params>"+endl);
                out.write("\t\t\t\t\t<maxpkts>"+ service.server_application.application_FTP_params.maxpkts +"</maxpkts>"+endl);
                out.write("\t\t\t\t</application_FTP_params>"+endl);
            }

            out.write("\t\t\t</server_application>"+endl);

            //client_sink
            out.write("\t\t\t<client_sink>"+endl);
            out.write("\t\t\t\t<client_agent>"+ service.client_sink.client_agent +"</client_agent>"+endl);

            //client_agent_params
            out.write("\t\t\t\t<client_agent_params>"+endl);
            out.write("\t\t\t\t\t<fid>"+ service.client_sink.client_agent_params.fid +"</fid>"+endl);
            out.write("\t\t\t\t\t<prio>"+ service.client_sink.client_agent_params.prio +"</prio>"+endl);
            out.write("\t\t\t\t\t<flags>"+ service.client_sink.client_agent_params.flags +"</flags>"+endl);
            out.write("\t\t\t\t\t<ttl>"+ service.client_sink.client_agent_params.ttl +"</ttl>"+endl);

            //ladowanie parametrow gdy Agent jest z rodziny Agent/TCPSink
            if(service.client_sink.client_agent.equals("Agent/TCPSink") ||
                    service.client_sink.client_agent.equals("Agent/TCPSink/DelAck") ||
                    service.client_sink.client_agent.equals("Agent/TCPSink/Sack1") ||
                    service.client_sink.client_agent.equals("Agent/TCPSink/Sack1/DelAck")
                    )
            {
                out.write("\t\t\t\t\t<client_agent_TCPSink_params>"+endl);
                out.write("\t\t\t\t\t\t<packetSize>"+ service.client_sink.client_agent_params.client_agent_TCPSink_params.packetSize +"</packetSize>"+endl);
                out.write("\t\t\t\t\t\t<maxSackBlocks>"+ service.client_sink.client_agent_params.client_agent_TCPSink_params.maxSackBlocks +"</maxSackBlocks>"+endl);
                
                //dodatkowe parametry gdy agent Agent/TCPSink/DelAck
                if(service.client_sink.client_agent.equals("Agent/TCPSink/DelAck")){
                    out.write("\t\t\t\t\t\t<client_agent_TCPSink_DelAck_params>"+endl);
                    out.write("\t\t\t\t\t\t\t<interval>"+ service.client_sink.client_agent_params.client_agent_TCPSink_params.client_agent_TCPSink_DelAck_params.interval +"</interval>"+endl);
                    out.write("\t\t\t\t\t\t</client_agent_TCPSink_DelAck_params>"+endl);
                }

                //dodatkowe parametry gdy agent Agent/TCPSink/Sack1/DelAck
                if(service.client_sink.client_agent.equals("Agent/TCPSink/Sack1/DelAck")){
                    out.write("\t\t\t\t\t\t<client_agent_TCPSink_Sack1_DelAck_params>"+endl);
                    out.write("\t\t\t\t\t\t\t<interval>"+ service.client_sink.client_agent_params.client_agent_TCPSink_params.client_agent_TCPSink_Sack1_DelAck_params.interval +"</interval>"+endl);
                    out.write("\t\t\t\t\t\t</client_agent_TCPSink_Sack1_DelAck_params>"+endl);
                }

                out.write("\t\t\t\t\t</client_agent_TCPSink_params>"+endl);

            }
            out.write("\t\t\t\t</client_agent_params>"+endl);

            out.write("\t\t\t</client_sink>"+endl);

            out.write("\t\t</service>"+endl);
        }


        
        out.write("\t</network_settings>"+endl);
        
        out.write("\t<network_structure>"+endl);

        //pc
        for(Router router : nsdhModel.network_structure.router_list.values()){
            out.write("\t\t<router name=\""+ router.name +"\">"+endl);
            out.write("\t\t\t<gui_x>"+ router.gui_x +"</gui_x>"+endl);
            out.write("\t\t\t<gui_y>"+ router.gui_y +"</gui_y>"+endl);
            for(Routing_table_row routing_table_row : router.routing_table_row_list){
                out.write("\t\t\t<routing_table_row>"+endl);
                out.write("\t\t\t\t<packet_destination>"+ routing_table_row.packet_destination +"</packet_destination>"+endl);
                out.write("\t\t\t\t<next_hop>"+ routing_table_row.next_hop +"</next_hop>"+endl);
                out.write("\t\t\t</routing_table_row>"+endl);
            }

            out.write("\t\t</router>"+endl);
        }

        //pc
        for(Pc pc : nsdhModel.network_structure.pc_list.values()){
            out.write("\t\t<pc name=\""+ pc.name +"\">"+endl);
            out.write("\t\t\t<gui_x>"+ pc.gui_x +"</gui_x>"+endl);
            out.write("\t\t\t<gui_y>"+ pc.gui_y +"</gui_y>"+endl);
            out.write("\t\t</pc>"+endl);
        }

        //server_client_connection
        for(Server_client_connection server_client_connection : nsdhModel.network_structure.server_client_connection_list.values()){
            out.write("\t\t<server_client_connection name=\""+ server_client_connection.name +"\">"+endl);
            out.write("\t\t\t<server_pc>"+ server_client_connection.server_pc +"</server_pc>"+endl);
            out.write("\t\t\t<client_pc>"+ server_client_connection.client_pc +"</client_pc>"+endl);
            out.write("\t\t\t<service>"+ server_client_connection.service +"</service>"+endl);
            out.write("\t\t</server_client_connection>"+endl);
        }


        //edge_core_link
        for(Standard_link standard_link : nsdhModel.network_structure.standard_link_list.values()){
            out.write("\t\t<standard_link name=\""+ standard_link.name +"\">"+endl);
            out.write("\t\t\t<node1>"+ standard_link.node1 +"</node1>"+endl);
            out.write("\t\t\t<node2>"+ standard_link.node2 +"</node2>"+endl);
            out.write("\t\t\t<delay>"+ standard_link.delay +"</delay>"+endl);
            out.write("\t\t\t<bandwidth>"+ standard_link.bandwidth +"</bandwidth>"+endl);
            out.write("\t\t\t<queue_limit>"+ standard_link.queue_limit +"</queue_limit>"+endl);
            out.write("\t\t</standard_link>"+endl);
        }

        //edge_core_link
        for(Edge_core_link edge_core_link : nsdhModel.network_structure.edge_core_link_list.values()){
            out.write("\t\t<edge_core_link name=\""+ edge_core_link.name +"\">"+endl);
            out.write("\t\t\t<queue_policy>"+ edge_core_link.queue_policy +"</queue_policy>"+endl);
            out.write("\t\t\t<edge_router>"+ edge_core_link.edge_router +"</edge_router>"+endl);
            out.write("\t\t\t<core_router>"+ edge_core_link.core_router +"</core_router>"+endl);
            out.write("\t\t\t<delay>"+ edge_core_link.delay +"</delay>"+endl);
            out.write("\t\t\t<bandwidth>"+ edge_core_link.bandwidth +"</bandwidth>"+endl);
            out.write("\t\t\t<queue_limit>"+ edge_core_link.queue_limit +"</queue_limit>"+endl);
            out.write("\t\t</edge_core_link>"+endl);
        }

        //core_core_link
        for(Core_core_link core_core_link : nsdhModel.network_structure.core_core_link_list.values()){
            out.write("\t\t<core_core_link name=\""+ core_core_link.name +"\">"+endl);
            out.write("\t\t\t<queue_policy>"+ core_core_link.queue_policy +"</queue_policy>"+endl);
            out.write("\t\t\t<core_router1>"+ core_core_link.core_router1 +"</core_router1>"+endl);
            out.write("\t\t\t<core_router2>"+ core_core_link.core_router2 +"</core_router2>"+endl);
            out.write("\t\t\t<delay>"+ core_core_link.delay +"</delay>"+endl);
            out.write("\t\t\t<bandwidth>"+ core_core_link.bandwidth +"</bandwidth>"+endl);
            out.write("\t\t\t<queue_limit>"+ core_core_link.queue_limit +"</queue_limit>"+endl);
            out.write("\t\t</core_core_link>"+endl);
        }
        
        out.write("\t</network_structure>"+endl);

        //scenario
        out.write("\t<scenario>"+endl);
        out.write("\t\t<total_time>"+ nsdhModel.scenario.total_time +"</total_time>"+endl);

        for(Event event : nsdhModel.scenario.eventList){
            out.write("\t\t<event>"+endl);
            out.write("\t\t\t<time>"+ event.time +"</time>"+endl);
            out.write("\t\t\t<server_client_connection>"+ event.server_client_connection +"</server_client_connection>"+endl);
            out.write("\t\t\t<command>"+ event.command +"</command>"+endl);
            if(event.command.equals("produce") || event.command.equals("producemore") || event.command.equals("send")){
                out.write("\t\t\t<value>"+ event.value +"</value>"+endl);
            }
            out.write("\t\t</event>"+endl);
        }
        
        out.write("\t</scenario>"+endl);

        //wejscie optymalizatora
        out.write("\t<optimizer_input>"+endl);

        //zmienne optymalizacji
        for(Optimization_variable optimization_variable: nsdhModel.optimizer_input.optimization_variable_list.values()){
            out.write("\t\t<optimization_variable name=\""+optimization_variable.name+"\">"+endl);
            out.write("\t\t\t<mapping_expression>"+ optimization_variable.mapping_expression +"</mapping_expression>"+endl);
            out.write("\t\t\t<min_value>"+ optimization_variable.min_value +"</min_value>"+endl);
            out.write("\t\t\t<max_value>"+ optimization_variable.max_value +"</max_value>"+endl);
            out.write("\t\t\t<start_value>"+ optimization_variable.start_value +"</start_value>"+endl);
            out.write("\t\t</optimization_variable>"+endl);
        }

        //argumenty f. celu
        for(Target_function_arguement target_function_arguement: nsdhModel.optimizer_input.target_function_arguement_list.values()){
            out.write("\t\t<target_function_arguement name=\""+target_function_arguement.name+"\">"+endl);
            out.write("\t\t\t<mapping_expression>"+ target_function_arguement.mapping_expression +"</mapping_expression>"+endl);
            out.write("\t\t</target_function_arguement>"+endl);
        }

        //reszta parametrow
        out.write("\t\t<target_function>"+ nsdhModel.optimizer_input.target_function +"</target_function>"+endl);
        out.write("\t\t<N_param>"+ nsdhModel.optimizer_input.N_param +"</N_param>"+endl);
        out.write("\t\t<K_param>"+ nsdhModel.optimizer_input.K_param +"</K_param>"+endl);
        out.write("\t\t<maxIter_param>"+ nsdhModel.optimizer_input.maxIter_param +"</maxIter_param>"+endl);
        out.write("\t\t<maxPTries_param>"+ nsdhModel.optimizer_input.maxPTries_param +"</maxPTries_param>"+endl);
        
        out.write("\t</optimizer_input>"+endl);


        out.write("</nsdh_file>"+endl);

        //zamknij strumien
        out.close();

        //ustawia sciezke aktualnie otwartego pliku
        nsdhModel.settings.openedXmlFilePath = filepath;

    }


    /**
     * Zapisuje dane z modelu do pliku tcl wykonywalnego przez ns-2
     * @param filepath sciezka pliku do zapisu
     * @throws java.io.IOException
     */
    public void SaveToTclFile (String filepath) throws IOException,TclFileException{

        //wyczysc wyniki ostatniej symulacji
        nsdhModel.results = new Results();

        //znacznik nowej linii
        final String endl = System.getProperty("line.separator");

        //otworz strumien zapisu pliku
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath),"UTF8"));

        out.write("set ns [new Simulator]" +endl);
        out.write("set tracefile [open "+nsdhModel.settings.ns2OutputFilePath+" w]" +endl);
        out.write("$ns trace-all $tracefile" +endl);
        //plik dla nam
        if(nsdhModel.settings.generateNamFile){
            out.write("set namfile [open "+nsdhModel.settings.namFileName+" w]" +endl);
            out.write("$ns namtrace-all $namfile" +endl +endl);
        }
        
        //node ID routerow i pc
        int nodeID =0;

        //router
        for(Router router : nsdhModel.network_structure.router_list.values()){
            out.write("set "+router.name+" [$ns node]" +endl);
            //ustaw nodeID w slowniku wynikow
            nsdhModel.results.pcRouterNodeIdDictionary.put(router.name, Integer.toString(nodeID));
            nodeID++;
        }

        //pc
        for(Pc pc : nsdhModel.network_structure.pc_list.values()){
            out.write("set "+pc.name+" [$ns node]" +endl);
            //ustaw nodeID w slowniku wynikow
            nsdhModel.results.pcRouterNodeIdDictionary.put(pc.name, Integer.toString(nodeID));
            nodeID++;
        }

        out.write(endl);

        //standard_link
        for(Standard_link standard_link : nsdhModel.network_structure.standard_link_list.values()){
            out.write("$ns duplex-link $"+standard_link.node1+" $"+standard_link.node2+" "+standard_link.bandwidth+"Mb "+standard_link.delay+"ms DropTail" +endl);
            if(!standard_link.queue_limit.equals(""))
                out.write("$ns queue-limit $"+standard_link.node1+" $"+standard_link.node2+" "+standard_link.queue_limit +endl);
        }

        out.write(endl);

        //EDGE_CORE_LINK
        for(Edge_core_link edge_core_link : nsdhModel.network_structure.edge_core_link_list.values()){
            
            out.write("$ns simplex-link $"+edge_core_link.edge_router+" $"+edge_core_link.core_router+" "+edge_core_link.bandwidth+"Mb "+edge_core_link.delay+"ms dsRED/edge" +endl);
            out.write("$ns simplex-link $"+edge_core_link.core_router+" $"+edge_core_link.edge_router+" "+edge_core_link.bandwidth+"Mb "+edge_core_link.delay+"ms dsRED/core" +endl);

            if(!edge_core_link.queue_limit.equals("")){
                out.write("$ns queue-limit $"+edge_core_link.edge_router+" $"+edge_core_link.core_router+" "+edge_core_link.queue_limit +endl);
                out.write("$ns queue-limit $"+edge_core_link.core_router+" $"+edge_core_link.edge_router+" "+edge_core_link.queue_limit +endl);
            }

            Queue_policy queue_policy = nsdhModel.network_settings.queue_policy_list.get(edge_core_link.queue_policy);

            //tymczasowa nazwa kolejki na laczu wykorzystywana w generowaniu skryptu
            String queueTempName = edge_core_link.edge_router+edge_core_link.core_router;

            //ustawienie polityki kolejek
            out.write("set q"+queueTempName+" [[$ns link $"+edge_core_link.edge_router+" $"+edge_core_link.core_router+"] queue]" +endl);

            if(!queue_policy.scheduler.equals("None"))
                out.write("$q"+queueTempName+" setSchedularMode " + queue_policy.scheduler +endl);

            //mean_packet_size
            out.write("$q"+queueTempName+" meanPktSize "+ queue_policy.mean_packet_size +endl);

            //ilosc kolejek
            out.write("$q"+queueTempName+" set numQueues_ "+ queue_policy.queue_list.size() +endl);

            //ilosc subqueues
            out.write("$q"+queueTempName+" setNumPrec 3" +endl);

            //ustawienia kolejkowania
            for(int i=0; i< queue_policy.queue_list.size(); i++){
                Queue queue = queue_policy.queue_list.get(i);

                //codepoint kolejki
                int queue_codepoint=i;
                int queue_policer_codepoint=10+(i*10);

                //ustawia wagi lub priorytet
                if(queue_policy.scheduler.equals("PRI")){
                    //priorytet zalezy od codepointu kolejki
                    queue_codepoint = Integer.parseInt(queue.scheduler_params.priority);
                    queue_policer_codepoint = 10+(Integer.parseInt(queue.scheduler_params.priority)*10);
                    out.write("$q"+queueTempName+" addQueueRate " + queue_codepoint + " " + queue.scheduler_params.rate +endl);
                }
                if(queue_policy.scheduler.equals("WRR") || queue_policy.scheduler.equals("WIRR")  || queue_policy.scheduler.equals("RR")){
                    out.write("$q"+queueTempName+" addQueueWeights " + queue_codepoint + " " + queue.scheduler_params.weight +endl);
                }


                //markowanie policerem Null pakietow z service'ow kolejki
                if(queue.policer.equals("NullPolicer")){
                    for( Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                        for(Policer_entry policer_entry: queue.policer_entry_list.values()){
                            if(server_client_connection.service.equals(policer_entry.service)){
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.server_pc  +" id] [$"+ server_client_connection.client_pc +" id] Null "+queue_policer_codepoint  +endl);
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.client_pc  +" id] [$"+ server_client_connection.server_pc +" id] Null "+queue_policer_codepoint  +endl);
                            }
                        }
                    }
                }

                //markowanie policerem TSW2CM pakietow z service'ow kolejki
                if(queue.policer.equals("TSW2CM")){
                    for( Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                        for(Policer_entry policer_entry: queue.policer_entry_list.values()){
                            if(server_client_connection.service.equals(policer_entry.service)){
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.server_pc  +" id] [$"+ server_client_connection.client_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  +endl);
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.client_pc  +" id] [$"+ server_client_connection.server_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  +endl);
                            }
                        }
                    }
                }

                //markowanie policerem TSW3CM pakietow z service'ow kolejki
                else if(queue.policer.equals("TSW3CM")){
                    for( Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                        for(Policer_entry policer_entry: queue.policer_entry_list.values()){
                            if(server_client_connection.service.equals(policer_entry.service)){
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.server_pc  +" id] [$"+ server_client_connection.client_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  + " "+ policer_entry.PIR +endl);
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.client_pc  +" id] [$"+ server_client_connection.server_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  + " "+ policer_entry.PIR +endl);
                            }
                        }
                    }
                }


                //markowanie policerem TokenBucket pakietow z service'ow kolejki
                else if(queue.policer.equals("TokenBucket")){
                    for( Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                        for(Policer_entry policer_entry: queue.policer_entry_list.values()){
                            if(server_client_connection.service.equals(policer_entry.service)){
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.server_pc  +" id] [$"+ server_client_connection.client_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  + " "+ policer_entry.CBS +endl);
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.client_pc  +" id] [$"+ server_client_connection.server_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  + " "+ policer_entry.CBS +endl);
                            }
                        }
                    }
                }

                
                //markowanie policerem srTCM pakietow z service'ow kolejki
                else if(queue.policer.equals("srTCM")){
                    for( Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                        for(Policer_entry policer_entry: queue.policer_entry_list.values()){
                            if(server_client_connection.service.equals(policer_entry.service)){
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.server_pc  +" id] [$"+ server_client_connection.client_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  + " "+ policer_entry.CBS + " "+ policer_entry.EBS +endl);
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.client_pc  +" id] [$"+ server_client_connection.server_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  + " "+ policer_entry.CBS + " "+ policer_entry.EBS +endl);
                            }
                        }
                    }
                }


                //markowanie policerem trTCM pakietow z service'ow kolejki
                else if(queue.policer.equals("trTCM")){
                    for( Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                        for(Policer_entry policer_entry: queue.policer_entry_list.values()){
                            if(server_client_connection.service.equals(policer_entry.service)){
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.server_pc  +" id] [$"+ server_client_connection.client_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  + " "+ policer_entry.CBS + " "+ policer_entry.PIR + " "+ policer_entry.PBS +endl);
                                out.write("$q"+queueTempName+" addPolicyEntry [$"+ server_client_connection.client_pc  +" id] [$"+ server_client_connection.server_pc +" id] "+queue.policer+" "+queue_policer_codepoint+" "+ policer_entry.CIR  + " "+ policer_entry.CBS + " "+ policer_entry.PIR + " "+ policer_entry.PBS +endl);
                            }
                        }
                    }
                }


                //ustawienia gdy policery z 1 subqueue
                if(queue.policer.equals("NullPolicer") ){
                    if(queue.subqueue_list.size()==1){
                        out.write("$q"+queueTempName+" addPolicerEntry Null " + queue_policer_codepoint +endl);
                    }
                    else{
                        throw new TclFileException("Zła ilość subqueue: "+queue.subqueue_list.size()+" w kolejce: " + queue.name);
                    }
                }

                //ustawienia gdy policery z 2 subqueue
                if(queue.policer.equals("TSW2CM") || queue.policer.equals("TokenBucket") ){
                    if(queue.subqueue_list.size()==2){
                        out.write("$q"+queueTempName+" addPolicerEntry "+queue.policer+" " + queue_policer_codepoint + " " + (queue_policer_codepoint+1) +endl);
                    }
                    else{
                        throw new TclFileException("Zła ilość subqueue: "+queue.subqueue_list.size()+" w kolejce: " + queue.name);
                    }
                }

                //ustawienia gdy policery z 3 subqueue
                if(queue.policer.equals("TSW3CM") || queue.policer.equals("srTCM") || queue.policer.equals("trTCM")){
                    if(queue.subqueue_list.size()==3){
                        out.write("$q"+queueTempName+" addPolicerEntry "+queue.policer+" " + queue_policer_codepoint + " " + (queue_policer_codepoint+1) + " " + (queue_policer_codepoint+2) + endl);
                    }
                    else{
                        throw new TclFileException("Zła ilość subqueue w kolejce " + queue.name);
                    }
                }

                //ustawienia, gdy brak policera
                if(queue.policer.equals("None") ){
                        throw new TclFileException("W połączeniu Edge-Core musi byc zdefiniowana polityka z policerem.");
                }

                //ustawienia PHB subueue
                for(int j=0; j< queue.subqueue_list.size();j++){
                    Subqueue subqueue = queue.subqueue_list.get(j);
                    int subqueue_codepoint = j;
                    out.write("$q"+queueTempName+" addPHBEntry " + (queue_policer_codepoint+j) + " " + queue_codepoint + " "+ subqueue_codepoint +endl);
                    out.write("$q"+queueTempName+" configQ " + queue_codepoint + " "+ subqueue_codepoint + " "+ subqueue.min_threshold + " "+ subqueue.max_threshold + " "+ subqueue.probability_dropping +endl);
                }
            }

            out.write(endl);
            
            
            //EDGE_CORE_LINK W DRUGA STRONE
            
            //tymczasowa nazwa kolejki na laczu wykorzystywana w generowaniu skryptu
            queueTempName = edge_core_link.core_router+edge_core_link.edge_router;
            
            //ustawienie polityki kolejek
            out.write("set q"+queueTempName+" [[$ns link $"+edge_core_link.core_router+" $"+edge_core_link.edge_router+"] queue]" +endl);

            if(!queue_policy.scheduler.equals("None"))
                out.write("$q"+queueTempName+" setSchedularMode " + queue_policy.scheduler +endl);

            //mean_packet_size
            out.write("$q"+queueTempName+" meanPktSize "+ queue_policy.mean_packet_size +endl);

            //ilosc kolejek
            out.write("$q"+queueTempName+" set numQueues_ "+ queue_policy.queue_list.size() +endl);

            //ilosc subqueue
            out.write("$q"+queueTempName+" setNumPrec 3" +endl);


            //ustawienia kolejkowania
            for(int i=0; i< queue_policy.queue_list.size(); i++){
                Queue queue = queue_policy.queue_list.get(i);

                //codepoint kolejki
                int queue_codepoint=i;
                int queue_policer_codepoint=10+(i*10);

                //ustawia wagi lub priorytet
                if(queue_policy.scheduler.equals("PRI")){
                    //priorytet zalezy od codepointu kolejki
                    queue_codepoint = Integer.parseInt(queue.scheduler_params.priority);
                    queue_policer_codepoint = 10+(Integer.parseInt(queue.scheduler_params.priority)*10);
                    out.write("$q"+queueTempName+" addQueueRate " + queue_codepoint + " " + queue.scheduler_params.rate +endl);
                }
                if(queue_policy.scheduler.equals("WRR") || queue_policy.scheduler.equals("WIRR")  || queue_policy.scheduler.equals("RR")){
                    out.write("$q"+queueTempName+" addQueueWeights " + queue_codepoint + " " + queue.scheduler_params.weight +endl);
                }

                //ustawienia gdy policery z 2 subqueue
                if(queue.policer.equals("TSW2CM") || queue.policer.equals("TokenBucket") ){
                    if(queue.subqueue_list.size()!=2)
                        throw new TclFileException("Zła ilość subqueue: "+queue.subqueue_list.size()+" w kolejce: " + queue.name);
                }

                //ustawienia gdy policery z 3 subqueue
                if(queue.policer.equals("TSW3CM") || queue.policer.equals("srTCM") || queue.policer.equals("trTCM")){
                    if(queue.subqueue_list.size()!=3)
                        throw new TclFileException("Zła ilość subqueue: "+queue.subqueue_list.size()+" w kolejce: " + queue.name);
                }

                //ustawienia PHB subueue
                for(int j=0; j< queue.subqueue_list.size();j++){
                    Subqueue subqueue = queue.subqueue_list.get(j);
                    int subqueue_codepoint = j;
                    out.write("$q"+queueTempName+" addPHBEntry " + (queue_policer_codepoint+j) + " " + queue_codepoint + " "+ subqueue_codepoint +endl);
                    out.write("$q"+queueTempName+" configQ " + queue_codepoint + " "+ subqueue_codepoint + " "+ subqueue.min_threshold + " "+ subqueue.max_threshold + " "+ subqueue.probability_dropping +endl);
                }
            }

            out.write(endl);
            
        }


        //CORE_CORE_LINK
        for(Core_core_link core_core_link : nsdhModel.network_structure.core_core_link_list.values()){

            out.write("$ns simplex-link $"+core_core_link.core_router1+" $"+core_core_link.core_router2+" "+core_core_link.bandwidth+"Mb "+core_core_link.delay+"ms dsRED/core" +endl);
            out.write("$ns simplex-link $"+core_core_link.core_router2+" $"+core_core_link.core_router1+" "+core_core_link.bandwidth+"Mb "+core_core_link.delay+"ms dsRED/core" +endl);

            if(!core_core_link.queue_limit.equals("")){
                out.write("$ns queue-limit $"+core_core_link.core_router1+" $"+core_core_link.core_router2+" "+core_core_link.queue_limit +endl);
                out.write("$ns queue-limit $"+core_core_link.core_router2+" $"+core_core_link.core_router1+" "+core_core_link.queue_limit +endl);
            }

            Queue_policy queue_policy = nsdhModel.network_settings.queue_policy_list.get(core_core_link.queue_policy);

            //tymczasowa nazwa kolejki na laczu wykorzystywana w generowaniu skryptu
            String queueTempName = core_core_link.core_router1+core_core_link.core_router2;

            //ustawienie polityki kolejek
            out.write("set q"+queueTempName+" [[$ns link $"+core_core_link.core_router1+" $"+core_core_link.core_router2+"] queue]" +endl);

            if(!queue_policy.scheduler.equals("None"))
                out.write("$q"+queueTempName+" setSchedularMode " + queue_policy.scheduler +endl);

            //mean_packet_size
            out.write("$q"+queueTempName+" meanPktSize "+ queue_policy.mean_packet_size +endl);

            //ilosc kolejek
            out.write("$q"+queueTempName+" set numQueues_ "+ queue_policy.queue_list.size() +endl);

           out.write("$q"+queueTempName+" setNumPrec 3" +endl);


            //ustawienia kolejkowania
            for(int i=0; i< queue_policy.queue_list.size(); i++){
                Queue queue = queue_policy.queue_list.get(i);

                //codepoint kolejki
                int queue_codepoint=i;
                int queue_policer_codepoint=10+(i*10);

                //ustawia wagi lub priorytet
                if(queue_policy.scheduler.equals("PRI")){
                    //priorytet zalezy od codepointu kolejki
                    queue_codepoint = Integer.parseInt(queue.scheduler_params.priority);
                    queue_policer_codepoint = 10+(Integer.parseInt(queue.scheduler_params.priority)*10);
                    out.write("$q"+queueTempName+" addQueueRate " + queue_codepoint + " " + queue.scheduler_params.rate +endl);
                }
                if(queue_policy.scheduler.equals("WRR") || queue_policy.scheduler.equals("WIRR")  || queue_policy.scheduler.equals("RR")){
                    out.write("$q"+queueTempName+" addQueueWeights " + queue_codepoint + " " + queue.scheduler_params.weight +endl);
                }

                //ustawienia gdy policery z 2 subqueue
                if(queue.policer.equals("TSW2CM") || queue.policer.equals("TokenBucket") ){
                    if(queue.subqueue_list.size()!=2){
                        throw new TclFileException("Zła ilość subqueue: "+queue.subqueue_list.size()+" w kolejce: " + queue.name);
                    }
                }

                //ustawienia gdy policery z 3 subqueue
                if(queue.policer.equals("TSW3CM") || queue.policer.equals("srTCM") || queue.policer.equals("trTCM")){
                    if(queue.subqueue_list.size()!=3){
                        throw new TclFileException("Zła ilość subqueue w kolejce " + queue.name);
                    }
                }

                //ustawienia PHB subueue
                for(int j=0; j< queue.subqueue_list.size();j++){
                    Subqueue subqueue = queue.subqueue_list.get(j);
                    int subqueue_codepoint = j;
                    out.write("$q"+queueTempName+" addPHBEntry " + (queue_policer_codepoint+j) + " " + queue_codepoint + " "+ subqueue_codepoint +endl);
                    out.write("$q"+queueTempName+" configQ " + queue_codepoint + " "+ subqueue_codepoint + " "+ subqueue.min_threshold + " "+ subqueue.max_threshold + " "+ subqueue.probability_dropping +endl);
                }
            }

            out.write(endl);


            //CORE_CORE_LINK W DRUGA STRONE

            //tymczasowa nazwa kolejki na laczu wykorzystywana w generowaniu skryptu
            queueTempName = core_core_link.core_router2+core_core_link.core_router1;

            //ustawienie polityki kolejek
            out.write("set q"+queueTempName+" [[$ns link $"+core_core_link.core_router2+" $"+core_core_link.core_router1+"] queue]" +endl);

            if(!queue_policy.scheduler.equals("None"))
                out.write("$q"+queueTempName+" setSchedularMode " + queue_policy.scheduler +endl);

            //mean_packet_size
            out.write("$q"+queueTempName+" meanPktSize "+ queue_policy.mean_packet_size +endl);

            //ilosc kolejek
            out.write("$q"+queueTempName+" set numQueues_ "+ queue_policy.queue_list.size() +endl);

            out.write("$q"+queueTempName+" setNumPrec 3" +endl);

            //ustawienia kolejkowania
            for(int i=0; i< queue_policy.queue_list.size(); i++){
                Queue queue = queue_policy.queue_list.get(i);

                //codepoint kolejki
                int queue_codepoint=i;
                int queue_policer_codepoint=10+(i*10);

                //ustawia wagi lub priorytet
                if(queue_policy.scheduler.equals("PRI")){
                    //priorytet zalezy od codepointu kolejki
                    queue_codepoint = Integer.parseInt(queue.scheduler_params.priority);
                    queue_policer_codepoint = 10+(Integer.parseInt(queue.scheduler_params.priority)*10);
                    out.write("$q"+queueTempName+" addQueueRate " + queue_codepoint + " " + queue.scheduler_params.rate +endl);
                }
                if(queue_policy.scheduler.equals("WRR") || queue_policy.scheduler.equals("WIRR")  || queue_policy.scheduler.equals("RR")){
                    out.write("$q"+queueTempName+" addQueueWeights " + queue_codepoint + " " + queue.scheduler_params.weight +endl);
                }

                //ustawienia gdy policery z 2 subqueue
                if(queue.policer.equals("TSW2CM") || queue.policer.equals("TokenBucket") ){
                    if(queue.subqueue_list.size()!=2)
                        throw new TclFileException("Zła ilość subqueue: "+queue.subqueue_list.size()+" w kolejce: " + queue.name);
                }

                //ustawienia gdy policery z 3 subqueue
                if(queue.policer.equals("TSW3CM") || queue.policer.equals("srTCM") || queue.policer.equals("trTCM")){
                    if(queue.subqueue_list.size()!=3)
                        throw new TclFileException("Zła ilość subqueue: "+queue.subqueue_list.size()+" w kolejce: " + queue.name);
                }

                //ustawienia PHB subueue
                for(int j=0; j< queue.subqueue_list.size();j++){
                    Subqueue subqueue = queue.subqueue_list.get(j);
                    int subqueue_codepoint = j;
                    out.write("$q"+queueTempName+" addPHBEntry " + (queue_policer_codepoint+j) + " " + queue_codepoint + " "+ subqueue_codepoint +endl);
                    out.write("$q"+queueTempName+" configQ " + queue_codepoint + " "+ subqueue_codepoint + " "+ subqueue.min_threshold + " "+ subqueue.max_threshold + " "+ subqueue.probability_dropping +endl);
                }
            }

            out.write(endl);

        }
           
            

        //ustawia polaczenia miedzy PC - server_client_connection
        int flowID = 0; //flowID dla polaczen
        for(Server_client_connection server_client_connection : nsdhModel.network_structure.server_client_connection_list.values()){
            try{
                Service service = nsdhModel.network_settings.service_list.get(server_client_connection.service);
                flowID++;


                //server_transport
                out.write("set "+server_client_connection.name+"_transport [new "+service.server_transport.server_agent+"]" +endl);
                if(!service.server_transport.server_agent_params.prio.equals(""))
                    out.write("$"+server_client_connection.name+"_transport set prio_ "+service.server_transport.server_agent_params.prio +endl);
                if(!service.server_transport.server_agent_params.flags.equals(""))
                    out.write("$"+server_client_connection.name+"_transport set flags_ "+service.server_transport.server_agent_params.flags +endl);
                if(!service.server_transport.server_agent_params.ttl.equals(""))
                    out.write("$"+server_client_connection.name+"_transport set ttl_ "+service.server_transport.server_agent_params.ttl +endl);

                //ustaw flowID i dodaj do slownika
                out.write("$"+server_client_connection.name+"_transport set fid_ "+flowID +endl);
                nsdhModel.results.serverClientConnectionFlowIdDictionary.put(server_client_connection.name, Integer.toString(flowID));


                //jesli Agent jest z rodziny TCP zaladuj dodatkowo parametry TCP
                if (service.server_transport.server_agent.equals("Agent/TCP") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Reno") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Newreno") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Sack1") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Vegas") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Fack") ||
                        service.server_transport.server_agent.equals("Agent/TCP/Linux")
                        )
                {
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.window.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set window_ "+service.server_transport.server_agent_params.server_agent_TCP_params.window +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.windowInit.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set windowInit_ "+service.server_transport.server_agent_params.server_agent_TCP_params.windowInit +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.windowOption.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set windowOption_ "+service.server_transport.server_agent_params.server_agent_TCP_params.windowOption +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.windowConstant.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set windowConstant_ "+service.server_transport.server_agent_params.server_agent_TCP_params.windowConstant +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.windowThresh.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set windowThresh_ "+service.server_transport.server_agent_params.server_agent_TCP_params.windowThresh +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.overhead.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set overhead_ "+service.server_transport.server_agent_params.server_agent_TCP_params.overhead +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.ecn.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set ecn_ "+service.server_transport.server_agent_params.server_agent_TCP_params.ecn +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.packetSize.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set packetSize_ "+service.server_transport.server_agent_params.server_agent_TCP_params.packetSize +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.bugFix.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set bugFix_ "+service.server_transport.server_agent_params.server_agent_TCP_params.bugFix +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.slow_start_restart.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set slow_start_restart_ "+service.server_transport.server_agent_params.server_agent_TCP_params.slow_start_restart +endl);
                    if(!service.server_transport.server_agent_params.server_agent_TCP_params.tcpTick.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set tcpTick_ "+service.server_transport.server_agent_params.server_agent_TCP_params.tcpTick +endl);

                    //jesli Agent to Agent/TCP/Newreno to ustaw dodatkowe paramatery NewReno oprocz TCP
                    if (service.server_transport.server_agent.equals("Agent/TCP/Newreno")){
                        if(!service.server_transport.server_agent_params.server_agent_TCP_params.server_agent_TCP_NewReno_params.newreno_changes.equals(""))
                            out.write("$"+server_client_connection.name+"_transport set newreno_changes_ "+service.server_transport.server_agent_params.server_agent_TCP_params.server_agent_TCP_NewReno_params.newreno_changes +endl);
                    }
                }



                //jesli Agent jest typu UDP zaladuj dodatkowo parametry UDP
                if (service.server_transport.server_agent.equals("Agent/UDP")){
                    if(!service.server_transport.server_agent_params.server_agent_UDP_params.packetSize.equals(""))
                        out.write("$"+server_client_connection.name+"_transport set packetSize_ "+service.server_transport.server_agent_params.server_agent_UDP_params.packetSize +endl);
                }

                out.write(endl);

                //server_application
                out.write("set "+server_client_connection.name+"_application [new "+service.server_application.type+"]" +endl);

                //zapis parametrow gdy typ Application/Traffic/Exponential
                if(service.server_application.type.equals("Application/Traffic/Exponential")){
                    if(!service.server_application.application_Traffic_Exponential_params.packetSize.equals(""))
                        out.write("$"+server_client_connection.name+"_application set packetSize_ "+service.server_application.application_Traffic_Exponential_params.packetSize +endl);
                    if(!service.server_application.application_Traffic_Exponential_params.burst_time.equals(""))
                        out.write("$"+server_client_connection.name+"_application set burst_time_ "+service.server_application.application_Traffic_Exponential_params.burst_time +endl);
                    if(!service.server_application.application_Traffic_Exponential_params.idle_time.equals(""))
                        out.write("$"+server_client_connection.name+"_application set idle_time_ "+service.server_application.application_Traffic_Exponential_params.idle_time +endl);
                    if(!service.server_application.application_Traffic_Exponential_params.rate.equals(""))
                        out.write("$"+server_client_connection.name+"_application set rate_ "+service.server_application.application_Traffic_Exponential_params.rate +endl);
                }

                //zapis parametrow gdy typ Application/Traffic/Pareto
                if(service.server_application.type.equals("Application/Traffic/Pareto")){
                    if(!service.server_application.application_Traffic_Pareto_params.packetSize.equals(""))
                        out.write("$"+server_client_connection.name+"_application set packetSize_ "+service.server_application.application_Traffic_Pareto_params.packetSize +endl);
                    if(!service.server_application.application_Traffic_Pareto_params.burst_time.equals(""))
                        out.write("$"+server_client_connection.name+"_application set burst_time_ "+service.server_application.application_Traffic_Pareto_params.burst_time +endl);
                    if(!service.server_application.application_Traffic_Pareto_params.idle_time.equals(""))
                        out.write("$"+server_client_connection.name+"_application set idle_time_ "+service.server_application.application_Traffic_Pareto_params.idle_time +endl);
                    if(!service.server_application.application_Traffic_Pareto_params.rate.equals(""))
                        out.write("$"+server_client_connection.name+"_application set rate_ "+service.server_application.application_Traffic_Pareto_params.rate +endl);
                    if(!service.server_application.application_Traffic_Pareto_params.rate.equals(""))
                        out.write("$"+server_client_connection.name+"_application set shape_ "+service.server_application.application_Traffic_Pareto_params.shape +endl);
                }

                //zapis parametrow gdy typ Application/Traffic/CBR
                if(service.server_application.type.equals("Application/Traffic/CBR")){
                    if(!service.server_application.application_Traffic_CBR_params.rate.equals(""))
                        out.write("$"+server_client_connection.name+"_application set rate_ "+service.server_application.application_Traffic_CBR_params.rate +endl);
                    if(!service.server_application.application_Traffic_CBR_params.interval.equals(""))
                        out.write("$"+server_client_connection.name+"_application set interval_ "+service.server_application.application_Traffic_CBR_params.interval +endl);
                    if(!service.server_application.application_Traffic_CBR_params.packetSize.equals(""))
                        out.write("$"+server_client_connection.name+"_application set packetSize_ "+service.server_application.application_Traffic_CBR_params.packetSize +endl);
                    if(!service.server_application.application_Traffic_CBR_params.random.equals(""))
                        out.write("$"+server_client_connection.name+"_application set random_ "+service.server_application.application_Traffic_CBR_params.random +endl);
                    if(!service.server_application.application_Traffic_CBR_params.maxpkts.equals(""))
                        out.write("$"+server_client_connection.name+"_application set shape_ "+service.server_application.application_Traffic_CBR_params.maxpkts +endl);
                }

                //zapis parametrow gdy typ Application/Telnet
                if(service.server_application.type.equals("Application/Telnet")){
                    if(!service.server_application.application_Telnet_params.interval.equals(""))
                        out.write("$"+server_client_connection.name+"_application set interval_ "+service.server_application.application_Telnet_params.interval +endl);
                }

                //zapis parametrow gdy typ Application/FTP
                if(service.server_application.type.equals("Application/FTP")){
                    if(!service.server_application.application_FTP_params.maxpkts.equals(""))
                        out.write("$"+server_client_connection.name+"_application set interval_ "+service.server_application.application_FTP_params.maxpkts +endl);
                }

                out.write(endl);
                //client_sink
                out.write("set "+server_client_connection.name+"_sink [new "+service.client_sink.client_agent+"]" +endl);
//                if(!service.client_sink.client_agent_params.fid.equals(""))
//                    out.write("$"+server_client_connection.name+"_sink set fid_ "+service.client_sink.client_agent_params.fid +endl);
                if(!service.client_sink.client_agent_params.prio.equals(""))
                    out.write("$"+server_client_connection.name+"_sink set prio_ "+service.client_sink.client_agent_params.prio +endl);
                if(!service.client_sink.client_agent_params.flags.equals(""))
                    out.write("$"+server_client_connection.name+"_sink set flags_ "+service.client_sink.client_agent_params.flags +endl);
                if(!service.client_sink.client_agent_params.ttl.equals(""))
                    out.write("$"+server_client_connection.name+"_sink set ttl_ "+service.client_sink.client_agent_params.ttl +endl);

                //ladowanie parametrow gdy Agent jest z rodziny Agent/TCPSink
                if(service.client_sink.client_agent.equals("Agent/TCPSink") ||
                        service.client_sink.client_agent.equals("Agent/TCPSink/DelAck") ||
                        service.client_sink.client_agent.equals("Agent/TCPSink/Sack1") ||
                        service.client_sink.client_agent.equals("Agent/TCPSink/Sack1/DelAck")
                        )
                {
                    if(!service.client_sink.client_agent_params.client_agent_TCPSink_params.packetSize.equals(""))
                        out.write("$"+server_client_connection.name+"_sink set packetSize_ "+service.client_sink.client_agent_params.client_agent_TCPSink_params.packetSize +endl);
                    if(!service.client_sink.client_agent_params.client_agent_TCPSink_params.maxSackBlocks.equals(""))
                        out.write("$"+server_client_connection.name+"_sink set maxSackBlocks_ "+service.client_sink.client_agent_params.client_agent_TCPSink_params.maxSackBlocks +endl);

                    //dodatkowe parametry gdy agent Agent/TCPSink/DelAck
                    if(service.client_sink.client_agent.equals("Agent/TCPSink/DelAck")){
                        if(!service.client_sink.client_agent_params.client_agent_TCPSink_params.client_agent_TCPSink_DelAck_params.interval.equals(""))
                            out.write("$"+server_client_connection.name+"_sink set interval_ "+service.client_sink.client_agent_params.client_agent_TCPSink_params.client_agent_TCPSink_DelAck_params.interval +endl);
                    }

                    //dodatkowe parametry gdy agent Agent/TCPSink/Sack1/DelAck
                    if(service.client_sink.client_agent.equals("Agent/TCPSink/Sack1/DelAck")){
                        if(!service.client_sink.client_agent_params.client_agent_TCPSink_params.client_agent_TCPSink_Sack1_DelAck_params.interval.equals(""))
                            out.write("$"+server_client_connection.name+"_sink set interval_ "+service.client_sink.client_agent_params.client_agent_TCPSink_params.client_agent_TCPSink_Sack1_DelAck_params.interval +endl);
                    }
                }

                out.write(endl);
                //polacz wszystko razem
                out.write("$ns attach-agent $"+server_client_connection.server_pc+" $"+server_client_connection.name+"_transport" +endl);
                out.write("$"+server_client_connection.name+"_application attach-agent $"+server_client_connection.name+"_transport" +endl);
                out.write("$ns attach-agent $"+server_client_connection.client_pc+" $"+server_client_connection.name+"_sink" +endl);
                out.write("$ns connect $"+server_client_connection.name+"_transport $"+server_client_connection.name+"_sink" +endl);
                out.write(endl);

            }catch(NullPointerException e){throw new TclFileException("Błąd w generacji połączeń client-server");}
        }
        
        //funkcja finish wywolywana na koncu symulacji
        out.write("proc finish {} {" +endl);
        out.write("global ns tracefile namfile" +endl);
        out.write("$ns flush-trace" +endl);
        out.write("close $tracefile" +endl);

        if(nsdhModel.settings.generateNamFile){
            out.write("close $namfile" +endl);
        }

        //out.write("exec nam nam1.nam &" +endl);
        out.write("exit 0" +endl);
        out.write("}" +endl);

        out.write(endl);


        //manulny routing
        try{

            if(!nsdhModel.network_settings.routing_type.equals("auto")){

                //funkcja sprawdza czy istnieje polaczene do node'a next_hop, i zwraca do niego polaczenie "link",  jesli nie ma polaczenia to zwraca -1
                out.write("Node instproc nexthop2link { nexthop } {" +endl);
                out.write("\tset ns_ [Simulator instance]" +endl);
                out.write("\tforeach {index link} [$ns_ array get link_] {" +endl);
                out.write("\t\tset L [split $index :]" +endl);
                out.write("\t\tset src [lindex $L 0]" +endl);
                out.write("\t\tif {$src == [$self id]} {" +endl);
                out.write("\t\t\tset dst [lindex $L 1]" +endl);
                out.write("\t\t\tif {$dst == $nexthop} { " +endl);
                out.write("\t\t\t\treturn $link" +endl);
                out.write("\t\t\t}" +endl);
                out.write("\t\t}" +endl);
                out.write("\t}" +endl);
                out.write("\treturn -1" +endl);
                out.write("}" +endl);
                out.write(endl);

                //funkcja dodaje do tablicy routingu "routera" node sciezke do node'a dst przez node via (next_hop)
                out.write("proc addExplicitRoute {node dst via } {" +endl);
                out.write("\tset link2via [$node nexthop2link [$via node-addr]]" +endl);
                out.write("\tif {$link2via != -1} {" +endl);
                out.write("\t\t$node add-route [$dst node-addr] [$link2via head]" +endl);
                out.write("\t} else {" +endl);
                out.write("\t\tputs \"Warning: No link exists between node [$node node-addr] and [$via node-addr]. Explicit route not added.\"" +endl);
                out.write("\t}" +endl);
                out.write("}" +endl);
                out.write(endl);

                //ustawia sciezki do tablicy routingu poszczegolnych routerow
                for(Router router: nsdhModel.network_structure.router_list.values()){
                    for(Routing_table_row routing_table_row: nsdhModel.network_structure.router_list.get(router.name).routing_table_row_list){
                        out.write("$ns at 0 \"addExplicitRoute $"+ router.name + " $" + routing_table_row.packet_destination + " $" + routing_table_row.next_hop +"\""+endl);
                    }
                }
                out.write(endl);

            }



        }catch(NullPointerException e){throw new TclFileException("Błąd w generacji manualnego routingu");}

        try{
            //scenario
            for(Event event : nsdhModel.scenario.eventList){
                String connection_application = nsdhModel.network_structure.server_client_connection_list.get(event.server_client_connection).name;
                out.write("$ns at "+event.time+" \"$" + connection_application +"_application "+ event.command + " "+ event.value + "\"" +endl);
            }
            out.write("$ns at "+nsdhModel.scenario.total_time+" \"finish\"" +endl);
        }catch(NullPointerException e){throw new TclFileException("Błąd w generacji scenerio");}

        out.write("$ns run" +endl);

        //zamknij strumien
        out.close();

    }
        
    /**
     * Uruchamia skrypt przez program ns-2, wykorzystujac sciezke do programu i sciezke skryptu TCL z modelu
     * @throws java.io.IOException
     * @throws nsdh.controller.Ns2OutputException wyjatek zawierajacy zawartosc wyjscia ns-2
     */
    public synchronized void RunNs2Simulation() throws TclFileException, IOException {
        nsdhModel.ns2Runner = new Ns2Runner(this, nsdhModel.settings.ns2InputFilePath);
        SaveToTclFile(nsdhModel.settings.ns2InputFilePath);
        SwingUtilities.invokeLater(nsdhModel.ns2Runner);
    }

    /**
     * Wczytuje zagregowane wyniki symulacji (srednia przepustowosc, opoznienie, jitter, packet loss rate) do modelu
     * @param CountInstantStatsFlag - flaga czy ma liczyc instantJitter i instantThroughput dla wykresow
     * @throws FileNotFoundException
     * @throws IOException
     */
    public synchronized void UploadResults(Boolean countChartStatsFlag) throws FileNotFoundException, IOException {

        //wczytaj plik wynikowy ns2
        File file = new File(nsdhModel.settings.ns2OutputFilePath);
        BufferedReader input =  new BufferedReader(new FileReader(file));


        //oblicz statystyki
        try {
            //zmienne pomocnicze
            String line = null; //linia pliku
            String[] splittedLine; //linia z pliku podzielona

            //dane wyciagniete z linii
            String action; //drop, sent itp.
            double time; //czas akcji
            String src; //z ktorego node
            String dst; // do ktorego node
            long pktSize; //wielkosc pakietu
            String flowId; //flowID
            int pktId; //pktID

            //tic w sekundach co jaki czas ma byc samplowany wynik dla wykresu
            double tic = 1.0;
            
            //inicjalizuje wyniki dla kazdego flow
            for(Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                 nsdhModel.results.flowResultsList.put(nsdhModel.results.serverClientConnectionFlowIdDictionary.get(server_client_connection.name), new FlowResults());
            }

            //przechodzi przez kazda linie pliku
            while (( line = input.readLine()) != null){

                //wyciagnij dane z linii
                splittedLine = line.split(" ");
                action = splittedLine[0];
                time = Double.valueOf(splittedLine[1]);
                src = splittedLine[2];
                dst = splittedLine[3];
                pktSize = Long.valueOf(splittedLine[5]);
                flowId = splittedLine[7];
                pktId = Integer.valueOf(splittedLine[11]);

                //sprawdz czy linia odpowiada ktoremus z server-client-connection i oblicz wyniki
                for(Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                    
                    if(nsdhModel.results.serverClientConnectionFlowIdDictionary.get(server_client_connection.name).equals(flowId)){

                        //jesli obliczamy statystyki dla wykresow, ustaw czas poprzedni
                        if(countChartStatsFlag == true){
                            if (nsdhModel.results.flowResultsList.get(flowId).prevTime == 0){
                                nsdhModel.results.flowResultsList.get(flowId).prevTime = time;
                            }
                        }

                        //jesli drop pakietu
                        if(action.equals("d")){
                            nsdhModel.results.flowResultsList.get(flowId).droppedPackets++;
                        }
                        
                        //przechowuje czas nadania pakietu
                        else if(nsdhModel.results.pcRouterNodeIdDictionary.get(server_client_connection.server_pc).equals(src) &&
                            nsdhModel.results.flowResultsList.get(flowId).sendTimeList.get(pktId)==null &&
                            action.equals("+")
                            ){
                                if (time < nsdhModel.results.flowResultsList.get(flowId).startTime) {
                                        nsdhModel.results.flowResultsList.get(flowId).startTime = time;
                                }
                                nsdhModel.results.flowResultsList.get(flowId).sentPackets++;
                                nsdhModel.results.flowResultsList.get(flowId).sendTimeList.put(pktId, time);

                                break;
                         }
                         
                        //Zaktualizuj wielkosc odebranych pakietow i zachowaj czas przybycia pakietu
                        else if(nsdhModel.results.pcRouterNodeIdDictionary.get(server_client_connection.client_pc).equals(dst) &&
                            action.equals("r")
                            ){
                                if (time > nsdhModel.results.flowResultsList.get(flowId).stopTime) {
                                        nsdhModel.results.flowResultsList.get(flowId).stopTime = time;
                                }
                                nsdhModel.results.flowResultsList.get(flowId).receivedSize += pktSize;
                                nsdhModel.results.flowResultsList.get(flowId).receivedTimeList.put(pktId, time);

                                //jesli obliczamy statystyki dla wykresow
                                if(countChartStatsFlag == true){

                                    //oblicza chartStatsThroughput
                                    nsdhModel.results.flowResultsList.get(flowId).ticReceivedSize+=pktSize;

                                    if (time - nsdhModel.results.flowResultsList.get(flowId).prevTime >= 10*tic){
                                        nsdhModel.results.flowResultsList.get(flowId).chartStatsThroughput.put(nsdhModel.results.flowResultsList.get(flowId).prevTime+1.0, 0.0);
                                        nsdhModel.results.flowResultsList.get(flowId).chartStatsThroughput.put(time-1.0, 0.0);
                                    }

                                    nsdhModel.results.flowResultsList.get(flowId).currTime += (time - nsdhModel.results.flowResultsList.get(flowId).prevTime);

                                    if (nsdhModel.results.flowResultsList.get(flowId).currTime >= tic) {
                                        nsdhModel.results.flowResultsList.get(flowId).chartStatsThroughput.put(time, (nsdhModel.results.flowResultsList.get(flowId).ticReceivedSize/nsdhModel.results.flowResultsList.get(flowId).currTime)*(8.0/1000.0));

                                        nsdhModel.results.flowResultsList.get(flowId).currTime = 0.0;
                                        nsdhModel.results.flowResultsList.get(flowId).ticReceivedSize =0.0;
                                    }

                                    nsdhModel.results.flowResultsList.get(flowId).prevTime = time;
                                }
                         }

                        break;
                    }
                            
                }
            
                
            }

            //oblicza ostateczne wyniki srednie end-to-end (jitter, throughput, packet loss rate, opoznienie) dla kazdego flow
            for(String flowResultsId: nsdhModel.results.flowResultsList.keySet()){
                //oblicza packet loss rate
                nsdhModel.results.flowResultsList.get(flowResultsId).packetLossRate=nsdhModel.results.flowResultsList.get(flowResultsId).droppedPackets/nsdhModel.results.flowResultsList.get(flowResultsId).sentPackets;

                //srednia przepustowosc
                nsdhModel.results.flowResultsList.get(flowResultsId).avgThroughput=(nsdhModel.results.flowResultsList.get(flowResultsId).receivedSize/(nsdhModel.results.flowResultsList.get(flowResultsId).stopTime-nsdhModel.results.flowResultsList.get(flowResultsId).startTime))*(8D/1000D);
                
                //srednie opoznienie
                Double tempDelay = 0D; //zmienna pomocnicza delay
                for(Integer receivedPktId:nsdhModel.results.flowResultsList.get(flowResultsId).receivedTimeList.keySet()){
                    
                    tempDelay += nsdhModel.results.flowResultsList.get(flowResultsId).receivedTimeList.get(receivedPktId)-nsdhModel.results.flowResultsList.get(flowResultsId).sendTimeList.get(receivedPktId);
                    nsdhModel.results.flowResultsList.get(flowResultsId).receivedPackets++;
                }
                if(nsdhModel.results.flowResultsList.get(flowResultsId).receivedPackets>0.0001 || nsdhModel.results.flowResultsList.get(flowResultsId).receivedPackets<0.0001){
                    nsdhModel.results.flowResultsList.get(flowResultsId).avgDelay=(tempDelay/nsdhModel.results.flowResultsList.get(flowResultsId).receivedPackets)*1000.0;
                }

                //sredni jitter
                int processedPackets=0; //liczba przetworzonych pakietow
                double previousTime=0D; //poprzedni czas
                double previousE2eDelay=-1D,e2eDelay=0D, delay=0D; //opoznienie end2end
                double jitter = 0D; //ogolny jitter
                double ticJitter = 0D; //zmienna pomocnicza do obliczania danych do wykresu Jitter/czas
                double ticDelay = 0D; //zmienna pomocnicza do obliczania danych do wykresu Delay/czas
                double ticReceivedPackets = 0; //ilosc odebranych pakietow w ciagu jednego ticu
                double ticCurrTime = 0D; //aktualny czas - sprawdza czy liczyc nowy tic

                for(int i=0; processedPackets<nsdhModel.results.flowResultsList.get(flowResultsId).receivedPackets; i++){
                    double receivedTime=0D; //czas nadejscia aktualnie badanego pakietu
                    double sendTime=0D; //czas wyslania aktualnie badanego pakietu
                    if(nsdhModel.results.flowResultsList.get(flowResultsId).receivedTimeList.containsKey(i)){
                        receivedTime=nsdhModel.results.flowResultsList.get(flowResultsId).receivedTimeList.get(i);
                        sendTime = nsdhModel.results.flowResultsList.get(flowResultsId).sendTimeList.get(i);

                        ticReceivedPackets++;

                        if(previousTime!=0){
                            delay = receivedTime - previousTime;
                            if(delay < 0)
                                delay = 0;

                            e2eDelay = receivedTime - sendTime;

                            if(previousE2eDelay!=-1){
                                jitter += Math.abs(e2eDelay - previousE2eDelay);
                            }

                            //oblicz statystyki dla wykresow jesli jest ustawiona flaga
                            if(countChartStatsFlag==true){
                                if(previousE2eDelay!=-1){
                                    ticJitter += Math.abs(e2eDelay - previousE2eDelay);
                                    ticDelay += Math.abs(e2eDelay);
                                }

                                //statystyki dla wykresu
                                if(delay >= tic*10) {
                                    nsdhModel.results.flowResultsList.get(flowResultsId).chartStatsJitter.put(previousTime+1.0, 0.0);
                                    nsdhModel.results.flowResultsList.get(flowResultsId).chartStatsJitter.put(receivedTime-1.0, 0.0);
                                    nsdhModel.results.flowResultsList.get(flowResultsId).chartStatsDelay.put(previousTime+1.0, 0.0);
                                    nsdhModel.results.flowResultsList.get(flowResultsId).chartStatsDelay.put(receivedTime-1.0, 0.0);
                                }
                                ticCurrTime += delay;

                                if (ticCurrTime >= tic) {
                                    nsdhModel.results.flowResultsList.get(flowResultsId).chartStatsJitter.put(receivedTime, ticJitter*1000.0/ticReceivedPackets);
                                    nsdhModel.results.flowResultsList.get(flowResultsId).chartStatsDelay.put(receivedTime, ticDelay*1000.0/ticReceivedPackets);
                                    ticJitter = 0;
                                    ticDelay = 0;
                                    ticCurrTime = 0;
                                    ticReceivedPackets = 0;
                                }
                            }


                            previousE2eDelay=e2eDelay;
                        }
                        previousTime=receivedTime;
                        processedPackets++;
                    }
                }

                //sredni jitter
                if (nsdhModel.results.flowResultsList.get(flowResultsId).receivedPackets!=0){
                    jitter = jitter*1000/nsdhModel.results.flowResultsList.get(flowResultsId).receivedPackets;
                    nsdhModel.results.flowResultsList.get(flowResultsId).avgJitter = jitter;
                }
                else{
                    nsdhModel.results.flowResultsList.get(flowResultsId).avgJitter = 0D;
                }


            }


        }
        finally {
            input.close();
        }





    }

    public void updateOptimizerInput() throws Exception{

        OptimizerInput optimizerInput = new OptimizerInput();

        optimizerInput.maxIter = this.nsdhModel.optimizer_input.maxIter_param;
        optimizerInput.maxPTries = this.nsdhModel.optimizer_input.maxPTries_param;
        optimizerInput.numberOfStartRandomPoints = this.nsdhModel.optimizer_input.N_param;
        optimizerInput.numberOfStepRandomPoints = this.nsdhModel.optimizer_input.K_param;
        optimizerInput.targetFunctionExpression = this.nsdhModel.optimizer_input.target_function;

        for(String variableName: this.nsdhModel.optimizer_input.optimization_variable_list.keySet()){
            optimizerInput.startingPoint.put(variableName, Double.valueOf(nsdhModel.optimizer_input.optimization_variable_list.get(variableName).start_value));
            optimizerInput.variableMinList.put(variableName, Double.valueOf(nsdhModel.optimizer_input.optimization_variable_list.get(variableName).min_value));
            optimizerInput.variableMaxList.put(variableName, Double.valueOf(nsdhModel.optimizer_input.optimization_variable_list.get(variableName).max_value));
            optimizerInput.variableList.put(variableName, nsdhModel.optimizer_input.optimization_variable_list.get(variableName).mapping_expression);
        }

        for(String arguementName: this.nsdhModel.optimizer_input.target_function_arguement_list.keySet()){

            optimizerInput.functionArgumentList.put(arguementName, nsdhModel.optimizer_input.target_function_arguement_list.get(arguementName).mapping_expression);

        }

        this.optimizer = new Optimizer(this,optimizerInput);
        //this.optimizer.start();
        
    }


    /**
     * Usuwa router z modelu razem z przylaczonymi do niego polaczeniami
     * @param routerName
     */
    public void DeleteRouter(String routerName){
        nsdhModel.network_structure.router_list.remove(routerName);

        HashSet<String> removeSet = new HashSet<String>(); //tymczasowa lista krawedzi do usuniecia

        //wybierz krawedzie do usuniecia z standard_link_list
        for(String keyName: nsdhModel.network_structure.standard_link_list.keySet()){
            if(nsdhModel.network_structure.standard_link_list.get(keyName).node1.equals(routerName) || nsdhModel.network_structure.standard_link_list.get(keyName).node2.equals(routerName)){
                removeSet.add(keyName);
            }
        }
        //usun wybrane krawedzie
        for(String keyToRemove: removeSet){
            nsdhModel.network_structure.standard_link_list.remove(keyToRemove);
        }


        removeSet = new HashSet<String>(); //tymczasowa lista krawedzi do usuniecia

        //wybierz krawedzie do usuniecia z edge_core_link_list
        for(String keyName: nsdhModel.network_structure.edge_core_link_list.keySet()){
            if(nsdhModel.network_structure.edge_core_link_list.get(keyName).edge_router.equals(routerName) || nsdhModel.network_structure.edge_core_link_list.get(keyName).core_router.equals(routerName)){
                removeSet.add(keyName);
            }
        }
        //usun wybrane krawedzie
        for(String keyToRemove: removeSet){
            nsdhModel.network_structure.edge_core_link_list.remove(keyToRemove);
        }

        
        removeSet = new HashSet<String>(); //tymczasowa lista krawedzi do usuniecia
        //wybierz krawedzie do usuniecia z edge_core_link_list
        for(String keyName :nsdhModel.network_structure.core_core_link_list.keySet()){
            if(nsdhModel.network_structure.core_core_link_list.get(keyName).core_router1.equals(routerName) || nsdhModel.network_structure.core_core_link_list.get(keyName).core_router2.equals(routerName)){
                removeSet.add(keyName);
            }
        }
        //usun wybrane krawedzie
        for(String keyToRemove: removeSet){
            nsdhModel.network_structure.core_core_link_list.remove(keyToRemove);
        }


        //usun wpisy w tablicy routingu routerow nawizujace do PC
        HashSet<Routing_table_row> routingTableRowsRemoveSet = new HashSet<Routing_table_row>(); //tablica wierszy do usuniecia
        for(Router router: nsdhModel.network_structure.router_list.values()){
            //wybierz wpisy do usuniecia
            for(int i=0; i < router.routing_table_row_list.size(); i++){
                if(router.routing_table_row_list.get(i).next_hop.equals(routerName)){
                    routingTableRowsRemoveSet.add(router.routing_table_row_list.get(i));
                }
            }
            //usun wpisy z tablicy routingu
            router.routing_table_row_list.removeAll(routingTableRowsRemoveSet);
            router.routing_table_row_list.trimToSize();
        }
    }

    /**
     * Usuwa pc z modelu razem z przylaczonymi do niego polaczeniami
     * @param pcName
     */
    public void DeletePc(String pcName){
        nsdhModel.network_structure.pc_list.remove(pcName); //usun pc z listy

        HashSet<String> removeSet = new HashSet<String>(); //tymczasowa lista krawedzi do usuniecia

        //wybierz krawedzie do usuniecia z standard_link_list
        for(String keyName: nsdhModel.network_structure.standard_link_list.keySet()){
            if(nsdhModel.network_structure.standard_link_list.get(keyName).node1.equals(pcName) || nsdhModel.network_structure.standard_link_list.get(keyName).node2.equals(pcName)){
                removeSet.add(keyName);
            }
        }
        //usun wybrane krawedzie
        for(String keyToRemove: removeSet){
            nsdhModel.network_structure.standard_link_list.remove(keyToRemove);
        }

        removeSet = new HashSet<String>(); //wyczysc liste
        //wybierz krawedzie do usuniecia z server_client_connection
        for(String keyName: nsdhModel.network_structure.server_client_connection_list.keySet()){
            if(nsdhModel.network_structure.server_client_connection_list.get(keyName).server_pc.equals(pcName) || nsdhModel.network_structure.server_client_connection_list.get(keyName).client_pc.equals(pcName)){
                removeSet.add(keyName);
            }
        }
        //usun wybrane krawedzie server-client connection
        for(String keyToRemove: removeSet){
            this.DeleteServerClientConnection(keyToRemove);
        }

         //usun wpisy w tablicy routingu routerow nawizujace do PC
        HashSet<Routing_table_row> routingTableRowsRemoveSet = new HashSet<Routing_table_row>(); //tablica wierszy do usuniecia
        for(Router router: nsdhModel.network_structure.router_list.values()){
            //wybierz wpisy do usuniecia
            for(int i=0; i < router.routing_table_row_list.size(); i++){
                if(router.routing_table_row_list.get(i).packet_destination.equals(pcName)){
                    routingTableRowsRemoveSet.add(router.routing_table_row_list.get(i));
                }
            }
            //usun wpisy z tablicy routingu
            router.routing_table_row_list.removeAll(routingTableRowsRemoveSet);
            router.routing_table_row_list.trimToSize();
        }




    }



    public void RenameRouter(String oldName, String newName) throws Exception{

        if(nsdhModel.network_structure.router_list.containsKey(oldName)){

            //zamien nazwy w standard_link_list
            for(String keyName: nsdhModel.network_structure.standard_link_list.keySet()){
                if(nsdhModel.network_structure.standard_link_list.get(keyName).node1.equals(oldName)) {
                    nsdhModel.network_structure.standard_link_list.get(keyName).node1 = newName;
                }

                if (nsdhModel.network_structure.standard_link_list.get(keyName).node2.equals(oldName)){
                    nsdhModel.network_structure.standard_link_list.get(keyName).node2 = newName;
                }
            }


            //zamien nazwy w edge_core_link_list
            for(String keyName: nsdhModel.network_structure.edge_core_link_list.keySet()){

                if(nsdhModel.network_structure.edge_core_link_list.get(keyName).edge_router.equals(oldName)) {
                    nsdhModel.network_structure.edge_core_link_list.get(keyName).edge_router = newName;
                }

                if (nsdhModel.network_structure.edge_core_link_list.get(keyName).core_router.equals(oldName)){
                    nsdhModel.network_structure.edge_core_link_list.get(keyName).core_router = newName;
                }
            }

            //zamien nazwy w core_core_link_list
            for(String keyName: nsdhModel.network_structure.core_core_link_list.keySet()){

                if(nsdhModel.network_structure.core_core_link_list.get(keyName).core_router1.equals(oldName)) {
                    nsdhModel.network_structure.core_core_link_list.get(keyName).core_router1 = newName;
                }

                if (nsdhModel.network_structure.core_core_link_list.get(keyName).core_router2.equals(oldName)){
                    nsdhModel.network_structure.core_core_link_list.get(keyName).core_router2 = newName;
                }
            }

            //zmien nazwy na tablicach routingu
            for(String keyName: nsdhModel.network_structure.router_list.keySet()){
                for(Routing_table_row routing_table_row: nsdhModel.network_structure.router_list.get(keyName).routing_table_row_list){
                    if(routing_table_row.next_hop.equals(oldName)){
                        routing_table_row.next_hop = newName;
                    }
                }

            }

            //podmien obiekt na liscie
            Router newRouter = nsdhModel.network_structure.router_list.get(oldName);
            newRouter.name=newName;
            nsdhModel.network_structure.router_list.remove(oldName);
            nsdhModel.network_structure.router_list.put(newName, newRouter);

        }else{
            throw new Exception("Nie mozna zmienic nazwy, routera nie ma na liscie");
        }


    }




    public void RenamePc(String oldName, String newName) throws Exception{

        if(nsdhModel.network_structure.pc_list.containsKey(oldName)){

            //zamien nazwy w standard_link_list
            for(String keyName: nsdhModel.network_structure.standard_link_list.keySet()){
                if(nsdhModel.network_structure.standard_link_list.get(keyName).node1.equals(oldName)) {
                    nsdhModel.network_structure.standard_link_list.get(keyName).node1 = newName;
                }

                if (nsdhModel.network_structure.standard_link_list.get(keyName).node2.equals(oldName)){
                    nsdhModel.network_structure.standard_link_list.get(keyName).node2 = newName;
                }
            }


            //zamien nazwy w server_client_connection_list
            for(String keyName: nsdhModel.network_structure.server_client_connection_list.keySet()){
                if(nsdhModel.network_structure.server_client_connection_list.get(keyName).server_pc.equals(oldName)) {
                    nsdhModel.network_structure.server_client_connection_list.get(keyName).server_pc = newName;
                }

                if(nsdhModel.network_structure.server_client_connection_list.get(keyName).client_pc.equals(oldName)) {
                    nsdhModel.network_structure.server_client_connection_list.get(keyName).client_pc = newName;
                }
            }

            //zmien nazwy na tablicach routingu
            for(String keyName: nsdhModel.network_structure.router_list.keySet()){
                for(Routing_table_row routing_table_row: nsdhModel.network_structure.router_list.get(keyName).routing_table_row_list){
                    if(routing_table_row.next_hop.equals(oldName)){
                        routing_table_row.next_hop = newName;
                    }else if(routing_table_row.packet_destination.equals(oldName)){
                        routing_table_row.packet_destination = newName;
                    }
                }

            }
            
            //podmien obiekt na liscie
            Pc newPc = nsdhModel.network_structure.pc_list.get(oldName);
            newPc.name=newName;
            nsdhModel.network_structure.pc_list.remove(oldName);
            nsdhModel.network_structure.pc_list.put(newName, newPc);

        }else{
            throw new Exception("Nie mozna zmienic nazwy, pc nie ma na liscie");
        }
    }


    public void RenameService(String oldServiceName, String newServiceName) throws Exception{

        //jesli nazwa stara i nowa jest ta sama to nic nie rob
        if(oldServiceName.equals(newServiceName)){
            return;
        }else{
            //sprawdz czy pusta nazwa lub juz istnieje taka na liscie
            if(newServiceName.equals("") || nsdhModel.network_settings.service_list.containsKey(newServiceName)){
                throw new Exception("Pusta nazwa lub usluga o tej nazwie juz istnieje");
            }
            else{
                //zamien nazwe w obiekcie i przepnij na liscie uslug
                Service tempService = nsdhModel.network_settings.service_list.get(oldServiceName);
                tempService.name = newServiceName;
                nsdhModel.network_settings.service_list.remove(oldServiceName);
                nsdhModel.network_settings.service_list.put(newServiceName,tempService);

                //zamien nazwe w polaczeniach i eventach scenariusza
                for(Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                    if(server_client_connection.service.equals(oldServiceName)){
                        server_client_connection.service = newServiceName;
                    }
                }

                //zmien nazwe w wpisach policera w kolejkach
                for(Queue_policy queue_policy: nsdhModel.network_settings.queue_policy_list.values()){
                    for(Queue queue: queue_policy.queue_list){
                        if(queue.policer_entry_list.containsKey(oldServiceName)){
                            Policer_entry tempPolicerEntry = queue.policer_entry_list.get(oldServiceName);
                            tempPolicerEntry.service = newServiceName;
                            queue.policer_entry_list.remove(oldServiceName);
                            queue.policer_entry_list.put(newServiceName,tempPolicerEntry);
                        }
                    }
                }

            }
        }

    }


    public void RenameServerClientConnection(String oldName, String newName) throws Exception{

        //jesli nazwa stara i nowa jest ta sama to nic nie rob
        if(oldName.equals(newName)){
            return;
        }else{
            //sprawdz czy pusta nazwa lub juz istnieje taka na liscie
            if(newName.equals("") || nsdhModel.network_settings.service_list.containsKey(newName)){
                throw new Exception("Pusta nazwa lub polaczenie o tej nazwie juz istnieje");
            }
            else{

                //zamien nazwe w obiekcie i przepnij na liscie uslug
                Server_client_connection tempServerClientConnection = nsdhModel.network_structure.server_client_connection_list.get(oldName);
                tempServerClientConnection.name = newName;
                nsdhModel.network_structure.server_client_connection_list.remove(oldName);
                nsdhModel.network_structure.server_client_connection_list.put(newName,tempServerClientConnection);

                //zamien nazwe w polaczeniach i eventach scenariusza
                for(Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
                    if(server_client_connection.service.equals(oldName)){
                        server_client_connection.service = newName;
                    }
                }

                for(Event event: nsdhModel.scenario.eventList){
                    if(event.server_client_connection.equals(oldName)){
                        event.server_client_connection = newName;
                    }
                }

            }
        }


    }


    public void DeleteServerClientConnection(String connectionName){

        HashSet<Event> eventRemoveSet = new HashSet<Event>(); //tymcasowa lista zdarzen ze scenariusza do usuniecia

        for(Event event: nsdhModel.scenario.eventList){
            if(event.server_client_connection.equals(connectionName)){
                eventRemoveSet.add(event);
            }
        }

        for(Event eventKeyToRemove: eventRemoveSet){
            nsdhModel.scenario.eventList.remove(eventKeyToRemove);
        }

        nsdhModel.network_structure.server_client_connection_list.remove(connectionName);

    }



    public void DeleteService(String serviceName){

        //usun polaczenia server-client ktore uzywaly usuwanej uslugi
        
        HashSet<String> connectionRemoveSet = new HashSet<String>(); //tymczasowa lista obiektow do usuniecia
        
        //polaczenia do usuniecia
        for(Server_client_connection server_client_connection: nsdhModel.network_structure.server_client_connection_list.values()){
            if(server_client_connection.service.equals(serviceName)){
                connectionRemoveSet.add(server_client_connection.name);
            }
        }

        //usun powiazane polaczenia i eventy ze scenariusza
        for(String connectionKeyToRemove: connectionRemoveSet){
            this.DeleteServerClientConnection(connectionKeyToRemove);
        }


        //usun usluge z kolejek
        for(Queue_policy queue_policy: nsdhModel.network_settings.queue_policy_list.values()){
            for(Queue queue: queue_policy.queue_list){
                //wybierz wpisy do usuniecia
                if(queue.policer_entry_list.containsKey(serviceName)){
                    queue.policer_entry_list.remove(serviceName);
                }
            }
        }

        nsdhModel.network_settings.service_list.remove(serviceName);

    }


    public void DeleteQueuePolicy(String queuePolicyName){

        //usun edge-core link polaczone z polityka
        HashSet<String> linkRemoveSet = new HashSet<String>(); //tymczasowa lista obiektow do usuniecia

        for(Edge_core_link edge_core_link: nsdhModel.network_structure.edge_core_link_list.values()){
            if(edge_core_link.queue_policy.equals(queuePolicyName)){
                linkRemoveSet.add(edge_core_link.name);
            }
        }
        for(String linkKeyToRemove: linkRemoveSet){
            nsdhModel.network_structure.edge_core_link_list.remove(linkKeyToRemove);
        }

        //usun core-core link polaczone z polityka
        linkRemoveSet = new HashSet<String>(); //tymczasowa lista obiektow do usuniecia

        for(Core_core_link core_core_link: nsdhModel.network_structure.core_core_link_list.values()){
            if(core_core_link.queue_policy.equals(queuePolicyName)){
                linkRemoveSet.add(core_core_link.name);
            }
        }
        for(String linkKeyToRemove: linkRemoveSet){
            nsdhModel.network_structure.core_core_link_list.remove(linkKeyToRemove);
        }

        nsdhModel.network_settings.queue_policy_list.remove(queuePolicyName);

    }

    public void RenameQueuePolicy(String oldName, String newName) throws Exception{

        //jesli nazwa stara i nowa jest ta sama to nic nie rob
        if(oldName.equals(newName)){
            return;
        }else{
            //sprawdz czy pusta nazwa lub juz istnieje taka na liscie
            if(newName.equals("") || nsdhModel.network_settings.queue_policy_list.containsKey(newName)){
                throw new Exception("Pusta nazwa lub polityka o tej nazwie juz istnieje");
            }
            else{
                //zamien nazwe w obiekcie i przepnij na liscie uslug
                Queue_policy tempQueuePolicy = nsdhModel.network_settings.queue_policy_list.get(oldName);
                tempQueuePolicy.name = newName;
                nsdhModel.network_settings.queue_policy_list.remove(oldName);
                nsdhModel.network_settings.queue_policy_list.put(newName,tempQueuePolicy);

                //zamien nazwy w edge_core_link_list
                for(String keyName: nsdhModel.network_structure.edge_core_link_list.keySet()){
                    if(nsdhModel.network_structure.edge_core_link_list.get(keyName).queue_policy.equals(oldName)) {
                        nsdhModel.network_structure.edge_core_link_list.get(keyName).queue_policy = newName;
                    }
                }

                //zamien nazwy w core_core_link_list
                for(String keyName: nsdhModel.network_structure.core_core_link_list.keySet()){
                    if(nsdhModel.network_structure.core_core_link_list.get(keyName).queue_policy.equals(oldName)) {
                        nsdhModel.network_structure.core_core_link_list.get(keyName).queue_policy = newName;
                    }
                }

                

            }
        }

    }


    /**
     * Sprawdza czy istnieje lacze (standardowe, edge-core lub core-core) o podanych krawedziach
     * @param node1
     * @param node2
     * @return lista nazw polaczen
     */
    public ArrayList<String> getExistingLinks(String node1, String node2){

        ArrayList<String> existingLinksList = new ArrayList<String>();

        //sprawdz krawedzie z standard_link_list
        for(String keyName: nsdhModel.network_structure.standard_link_list.keySet()){
            if((nsdhModel.network_structure.standard_link_list.get(keyName).node1.equals(node1) && nsdhModel.network_structure.standard_link_list.get(keyName).node2.equals(node2))
                    || (nsdhModel.network_structure.standard_link_list.get(keyName).node1.equals(node2) && nsdhModel.network_structure.standard_link_list.get(keyName).node2.equals(node1))){
                existingLinksList.add(keyName);
            }
        }

        //sprawdz krawedzie z edge_core_link_list
        for(String keyName: nsdhModel.network_structure.edge_core_link_list.keySet()){
            if((nsdhModel.network_structure.edge_core_link_list.get(keyName).edge_router.equals(node1) && nsdhModel.network_structure.edge_core_link_list.get(keyName).core_router.equals(node2))
                    || (nsdhModel.network_structure.edge_core_link_list.get(keyName).edge_router.equals(node2) && nsdhModel.network_structure.edge_core_link_list.get(keyName).core_router.equals(node1))){
                existingLinksList.add(keyName);
            }
        }

        //sprawdz krawedzie z core_core_link_list
        for(String keyName :nsdhModel.network_structure.core_core_link_list.keySet()){
            if((nsdhModel.network_structure.core_core_link_list.get(keyName).core_router1.equals(node1) && nsdhModel.network_structure.core_core_link_list.get(keyName).core_router2.equals(node2))
                    || (nsdhModel.network_structure.core_core_link_list.get(keyName).core_router1.equals(node2) && nsdhModel.network_structure.core_core_link_list.get(keyName).core_router2.equals(node1))){
                existingLinksList.add(keyName);
            }
        }

        return existingLinksList;

    }


    /**
     * Sprawdza czy istnieje polaczenie server-client o podanych krawedziach
     * @param serverName
     * @param clientName
     * @return lista z nazwami istniejacych polaczen
     */
    public ArrayList<String> getExistingServerClientConnections(String serverName, String clientName){

        ArrayList<String> existingConnectionsList = new ArrayList<String>();

        //sprawdz server_client_connection_list
        for(String keyName: nsdhModel.network_structure.server_client_connection_list.keySet()){
            if(nsdhModel.network_structure.server_client_connection_list.get(keyName).server_pc.equals(serverName) && nsdhModel.network_structure.server_client_connection_list.get(keyName).client_pc.equals(clientName)){
                existingConnectionsList.add(keyName);
            }
        }
        return existingConnectionsList;
    }


    /**
     * Zwraca liste node'ow polozonych w sieci obok routera podanego w nazwie
     * @param routerName
     * @return
     */
    public ArrayList<String> getNearbyNodes(String nodeName){

        ArrayList<String> returnNodesList = new ArrayList<String>();

        //sprawdz krawedzie z standard_link_list
        for(String keyName: nsdhModel.network_structure.standard_link_list.keySet()){
            if(nsdhModel.network_structure.standard_link_list.get(keyName).node1.equals(nodeName)){
                returnNodesList.add(nsdhModel.network_structure.standard_link_list.get(keyName).node2);
            }else if(nsdhModel.network_structure.standard_link_list.get(keyName).node2.equals(nodeName)){
                returnNodesList.add(nsdhModel.network_structure.standard_link_list.get(keyName).node1);
            }
        }

        //sprawdz krawedzie z edge_core_link_list
        for(String keyName: nsdhModel.network_structure.edge_core_link_list.keySet()){

            if(nsdhModel.network_structure.edge_core_link_list.get(keyName).edge_router.equals(nodeName)){
                returnNodesList.add(nsdhModel.network_structure.edge_core_link_list.get(keyName).core_router);
            }else if(nsdhModel.network_structure.edge_core_link_list.get(keyName).core_router.equals(nodeName)){
                returnNodesList.add(nsdhModel.network_structure.edge_core_link_list.get(keyName).edge_router);
            }
            
        }

        //sprawdz krawedzie z core_core_link_list
        for(String keyName :nsdhModel.network_structure.core_core_link_list.keySet()){

            if(nsdhModel.network_structure.core_core_link_list.get(keyName).core_router1.equals(nodeName)){
                returnNodesList.add(nsdhModel.network_structure.core_core_link_list.get(keyName).core_router2);
            }else if(nsdhModel.network_structure.core_core_link_list.get(keyName).core_router2.equals(nodeName)){
                returnNodesList.add(nsdhModel.network_structure.core_core_link_list.get(keyName).core_router1);
            }
        }

        return returnNodesList;


    }


    /**
     * Zwraca nr indeksu kolejki o nazwie queueName na liscie kolejek polityki Queue_policy, -1 gdy nie ma takiej nazwy
     * @param queue_policy
     * @param queueName
     * @return
     */
    public int getQueueIndexOnListInQueuePolicy(Queue_policy queue_policy, String queueName){

        for(int i=0; i< queue_policy.queue_list.size(); i++){
            if(queue_policy.queue_list.get(i).name.equals(queueName)){
                return i;
            }
        }

        return -1;

    }

}
