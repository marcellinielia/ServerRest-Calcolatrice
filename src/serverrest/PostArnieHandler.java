/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package serverrest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;



/**
 *
 * @author marce
 */


public class PostArnieHandler implements HttpHandler {
    
    // Istanza Gson configurata per pretty printing
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        // 1. Verifica che sia una richiesta POST
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            inviaErrore(exchange, 405, "Metodo non consentito. Usa POST");
            return;
        }
        
        try {
            // 2. Legge il body della richiesta
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
            );
            
            // Convertiamo il JSON in una Mappa generica (più flessibile per ora)
            Map<String, Object> nuovaArnia = gson.fromJson(reader, Map.class);
            reader.close();
            
            // 3. Validazione minima
            if (nuovaArnia == null || nuovaArnia.isEmpty()) {
                inviaErrore(exchange, 400, "Dati arnia mancanti o formato JSON non valido");
                return;
            }

            if (!nuovaArnia.containsKey("codice")) {
                inviaErrore(exchange, 400, "Il campo 'codice' è obbligatorio");
                return;
            }
            
            // 4. LOGICA DI SALVATAGGIO
            // Qui dovresti aggiungere l'arnia alla tua lista statica.
            // Esempio: GetHandler.arnieDB.add(nuovaArnia);
            // (Assicurati che arnieDB in GetHandler sia public e static)
            
            System.out.println("Ricevuta nuova arnia: " + nuovaArnia.get("codice"));

            // 5. Risposta di successo
            Map<String, Object> response = new HashMap<>();
            response.put("stato", "successo");
            response.put("messaggio", "Arnia registrata correttamente");
            response.put("dati_ricevuti", nuovaArnia);
            
            String jsonRisposta = gson.toJson(response);
            inviaRisposta(exchange, 201, jsonRisposta); // 201 = Created
            
        } catch (JsonSyntaxException e) {
            inviaErrore(exchange, 400, "Errore di sintassi JSON: " + e.getMessage());
        } catch (Exception e) {
            inviaErrore(exchange, 500, "Errore interno del server: " + e.getMessage());
        }
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
    
    private void inviaErrore(HttpExchange exchange, int codice, String messaggio) throws IOException {
        Map<String, Object> errore = new HashMap<>();
        errore.put("errore", messaggio);
        errore.put("status", codice);
        inviaRisposta(exchange, codice, gson.toJson(errore));
    }
}
