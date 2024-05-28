import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * KarelDbClient
 */
public class KarelDbClient {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public KarelDbClient(){

    }

    public void startConnection(String ip, int port){

        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String sendMessage(String msg) {
        out.printf("%s\n",msg);
        String resp;
        try {
            resp = in.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
        return resp;

    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public String insert(String dbName, String tableName,JSONObject body){
        JSONObject req = new JSONObject();

        req.put("record", body);
        req.put("db_name", dbName);
        req.put("table_name",tableName);
        String msg = "INSERT " + req.toJSONString();
        System.out.println("Sent message " + msg);
        return this.sendMessage(msg);
            

    }

    @SuppressWarnings("unchecked")
    public String update(String dbName, String tableName, JSONObject record, JSONObject where) {
        JSONObject req = new JSONObject();
        req.put("db_name", dbName);
        req.put("table_name", tableName);
        req.put("record", record);
        req.put("where", where);
        String msg = "UPDATE " + req.toJSONString();
        System.out.println("Sent message " + msg);
        return this.sendMessage(msg);
    }

    @SuppressWarnings("unchecked")
    public String select(String dbName, String tableName, JSONObject where) {
        JSONObject req = new JSONObject();
        req.put("db_name", dbName);
        req.put("table_name", tableName);
        req.put("where", where);
        String msg = "SELECT " + req.toJSONString();
        System.out.println("Sent message " + msg);
        return this.sendMessage(msg);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        
        JSONParser parser = new JSONParser();
        try (FileReader fl = new FileReader("/home/danicracker/university/septimo_semestre/Sistemas Operativos/Projecto3/KarelDB/robotDb.json");
        ) {
            Object parsedDb = parser.parse(fl);

            JSONObject dObject = (JSONObject)parsedDb;

            
            
        
        KarelDbClient kb = new KarelDbClient();
        String msg = "CREATE " + dObject.toJSONString();
        System.out.println("Sent message " + msg);
        kb.startConnection("localhost", 2025);
        System.out.println(kb.sendMessage(msg));

        /*
        String robotData = "{\"db_name\": \"RobotDB\",\"table_name\":\"Robot\",\"record\": {\"tipoRobot\": \"Extractor\",\"encendido\":true, \"idRobot\": 1,\"calle\": 6,\"avenida\": 1,\"beepers\": 0,\"direccion\": \"South\",},}";
        
        JSONObject dJsonObject;
        dJsonObject = (JSONObject) parser.parse(robotData);

        

         */

         JSONObject jsonObject = new JSONObject();

         jsonObject.put("db_name", "RobotDB");
         jsonObject.put("table_name", "Robot");
 
         JSONObject where = new JSONObject();
         
         where.put("tipoRobot","Minero");
         jsonObject.put("where", where);

        msg = "SELECT " + jsonObject.toJSONString();
        System.out.println("Sent message " + msg);
        System.out.println(kb.sendMessage(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
