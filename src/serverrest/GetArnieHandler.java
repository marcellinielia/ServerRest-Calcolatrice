/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package serverrest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 *
 * @author marce
 */


public class GetArnieHandler implements HttpHandler {
    
    // Istanza Gson configurata per pretty printing
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    // Database simulato: una lista di mappe che rappresentano le arnie
    private static final List<Map<String, Object>> arnieDB = new ArrayList<>();

    // Blocco statico per popolare dei dati di test all'avvio
    static {
        Map<String, Object> a1 = new HashMap<>();
        a1.put("id", 1);
        a1.put("codice", "ARN-001");
        a1.put("posizione", "Settore Nord");
        a1.put("stato", "Attiva");

        Map<String, Object> a2 = new HashMap<>();
        a2.put("id", 2);
        a2.put("codice", "ARN-002");
        a2.put("posizione", "Settore Sud");
        a2.put("stato", "In manutenzione");

        arnieDB.add(a1);
        arnieDB.add(a2);
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. Verifica che sia una richiesta GET
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            inviaErrore(exchange, 405, "Metodo non consentito. Usa GET");
            return;
        }
        
        try {
            // 2. Esegue l'azione (Recupero delle arnie)
            // Nota: arnieDB deve essere accessibile (es. public static in una classe comune)
            String jsonRisposta = gson.toJson(GetArnieHandler.arnieDB);
            
            // 3. Invia la risposta
            inviaRisposta(exchange, 200, jsonRisposta);
            
        } catch (Exception e) {
            inviaErrore(exchange, 500, "Errore interno del server: " + e.getMessage());
        }
    }

    
    private void gestisciGetArnie(HttpExchange exchange) throws IOException {
        // Trasforma l'intera lista DB in JSON
        String jsonRisposta = gson.toJson(arnieDB);
        inviaRisposta(exchange, 200, jsonRisposta);
    }
    
    /**
     * Invia una risposta di successo
     */
    private void inviaRisposta(HttpExchange exchange, int codice, String jsonRisposta) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        
        byte[] bytes = jsonRisposta.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(codice, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
    
    /**
     * Invia una risposta di errore in formato JSON
     */
    private void inviaErrore(HttpExchange exchange, int codice, String messaggio) throws IOException {
        Map<String, Object> errore = new HashMap<>();
        errore.put("errore", messaggio);
        errore.put("status", codice);
        
        inviaRisposta(exchange, codice, gson.toJson(errore));
    }
}
