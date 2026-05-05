/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package serverrest;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;



/**
 * Server REST per la calcolatrice
 * 
 * @author marce
 */
public class ServerRest {

    /**
     * Avvia il server REST sulla porta specificata
     * 
     * @param porta la porta su cui avviare il server
     */
    public static void avviaServer(int porta) {
        try {
            // Crea il server sulla porta specificata
            HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
            
            // 1. Arnie
            server.createContext("/api/arnie/get", new GetArnieHandler());
            server.createContext("/api/arnie/post", new PostArnieHandler());

            // 2. Users / Utenti
            server.createContext("/api/users/get", new GetUsersHandler());
            server.createContext("/api/users/post", new PostUsersHandler());
            server.createContext("/api/utenti/get", new GetUtentiHandler());
            server.createContext("/api/utenti/post", new PostUtentiHandler());

            // 3. Notifiche
            server.createContext("/api/notifiche/get", new GetNotificheHandler());
            server.createContext("/api/notifiche/post", new PostNotificheHandler());

            // 4. Tipi Rilevazione
            server.createContext("/api/tipirilevazione/get", new GetTipiRilevazioneHandler());
            server.createContext("/api/tipirilevazione/post", new PostTipiRilevazioneHandler());

            // 5. Rilevazioni
            server.createContext("/api/rilevazioni/get", new GetRilevazioniHandler());
            server.createContext("/api/rilevazioni/post", new PostRilevazioniHandler());

            // 6. Sensori Arnia
            server.createContext("/api/sensoriarnia/get", new GetSensoriArniaHandler());
            server.createContext("/api/sensoriarnia/post", new PostSensoriArniaHandler());

            // 7. Sensori
            server.createContext("/api/sensori/get", new GetSensoriHandler());
            server.createContext("/api/sensori/post", new PostSensoriHandler());

            // 8. Apiari
            server.createContext("/api/apiari/get", new GetApiariHandler());
            server.createContext("/api/apiari/post", new PostApiariHandler());
            
            // Endpoint di benvenuto
            server.createContext("/", ServerRest::gestisciBenvenuto);
            
            // Avvia il server
            server.setExecutor(null); // Usa il default executor
            server.start();
            
            // Messaggi di conferma
            System.out.println("==============================================");
            System.out.println("  Server REST con GSON avviato!");
            System.out.println("==============================================");
            System.out.println("Porta: " + porta);
            System.out.println();
            System.out.println("Endpoint disponibili:");
            System.out.println("  - POST: http://localhost:" + porta + "/api/calcola/post");
            System.out.println("  - GET:  http://localhost:" + porta + "/api/calcola/get");
            System.out.println("  - Info: http://localhost:" + porta + "/");
            System.out.println();
            System.out.println("Operatori supportati:");
            System.out.println("  SOMMA, SOTTRAZIONE, MOLTIPLICAZIONE, DIVISIONE");
            System.out.println();
            System.out.println("Premi Ctrl+C per fermare il server");
            System.out.println("==============================================");
            
        } catch (IOException e) {
            System.err.println("Errore nell'avvio del server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gestisce l'endpoint di benvenuto che fornisce informazioni sull'API
     * 
     * @param exchange l'oggetto HttpExchange per gestire la richiesta/risposta
     * @throws IOException in caso di errori durante la comunicazione
     */
    private static void gestisciBenvenuto(HttpExchange exchange) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        Map info = new HashMap<>();
        info.put("messaggio", "benvenuto nell'arnia digitale");
        info.put("versione", "1.0.0");
        info.put("tecnologia", "Java + GSON");
        
        Map endpoints = new HashMap<>();
        endpoints.put("POST", "/api/calcola/post");
        endpoints.put("GET", "/api/calcola/get?operando1=X&operando2=Y&operatore=OP");
        
        info.put("endpoints", endpoints);
        
        Map operatori = new HashMap<>();
        operatori.put("somma", "SOMMA o +");
        operatori.put("sottrazione", "SOTTRAZIONE o -");
        operatori.put("moltiplicazione", "MOLTIPLICAZIONE o * o X");
        operatori.put("divisione", "DIVISIONE o /");
        info.put("operatori_supportati", operatori);
        
        String jsonRisposta = gson.toJson(info);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = jsonRisposta.getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }
}