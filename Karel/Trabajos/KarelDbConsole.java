import java.util.Scanner;

public class KarelDbConsole {
    

    public static void main(String[] args) {
        
        KarelDbClient dbclient = new KarelDbClient();
        Scanner sc = new Scanner(System.in);

        dbclient.startConnection("localhost", 2025);


        while (true) {
            
            String query = sc.nextLine();

            System.out.println(dbclient.sendMessage(query));
            try {
                
            } catch (Exception e) {
                System.out.println(e.toString());// TODO: handle exception
            }
        }
    }
}
