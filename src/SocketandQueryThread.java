import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by yuxuanli on 10/9/17.
 */
public class SocketandQueryThread {
    public static int windowSize = 0;
    public static DGIM dgims;
    public static String lock[] = {"false"};

    // query thread
    private class QueryThread extends Thread {
        @Override
        public void run() {
            String prefix = "What is the number of ones for last";
            long queryNum = 0;
            try{
                while(true) {
//                    int key = System.in.read();
                    BufferedReader query = new BufferedReader(new InputStreamReader(System.in));
                    String line = "";
                    while ((line = query.readLine()) != null) {
//                    while(key != 0){
                        synchronized (lock) {
                            lock[0] = "true";
                        }
                        Thread.sleep(500);

                        if (!line.startsWith(prefix) && !line.contains("data")) {
                            System.out.print("Error!! query format should be:" + prefix + "<k> data?");
                            System.exit(0);
                        } else {
                            String[] temp = line.substring(prefix.length()).trim().split(" ");
                            if (temp == null || temp.length == 0) {
                                System.out.println("Error!! query format should be:" + prefix + "<k> data?");
                            }
                            queryNum = Long.valueOf(temp[0]);
                            System.out.println(line);

                            CalculateResult result = new CalculateResult(queryNum, dgims.dataStreams, dgims.bucketQueue);
                            String condition = "";
                            if(queryNum <= windowSize){
                                condition = "exactly";
                            }
                            else{
                                condition = "estimated";
                            }
                            System.out.println("The number of ones of last " + queryNum + " data is " + condition + " " + result.getResult());

                        }

                        Thread.sleep(5000);
                        synchronized (lock) {
                            lock[0] = "false";
                            lock.notify();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error!! No stdin input file");
                System.exit(0);
            } catch (InterruptedException e) {
                System.out.println("Error! Thread sleeping problems");
            } catch(NumberFormatException e){
                System.out.println("Error!! the number you entered is not valid integer");
                System.exit(0);
            }
        }
    }

    // socket thread
    private class InputThread extends Thread {
        private String host;
        private int port;

        InputThread(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            try{

                Socket socket = new Socket(this.host, this.port);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                int next_byte;
                while ((next_byte = reader.read()) != -1) {
                    if (next_byte == 10) {  // ignore the '\n'
                        continue;
                    }
                    char next_char = (char) next_byte;
                    System.out.print(next_char + " ");

                    //store the data by using boolean
                    if (next_char == 48) {
                        dgims.add(false);
                    } else {
                        dgims.add(true);
                    }

                    // lock this thread if the query is going on
                    synchronized (lock) {
                        if(lock[0].equals("true")){
                            lock.wait();
                        }
                    }
                }
            }catch (Exception e) {
                System.out.println("InputThread excpetion: " + e);
            }
        }
    }

    public static void main(String[] args){
        String host = "", portStr = "";
        int port = 0;

        int count = 0;
        try {
            while(count < 2) {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String s = in.readLine();
                if (count == 0) {
                    count++;
                    windowSize = Integer.parseInt(s);
                }
                else {
                    count++;
                    if (!s.contains(":")) {
                        System.out.println("Error!! the host and port should in format host:port ");
                        System.exit(0);
                    } else {
                        String[] temp = s.split(":");
                        host = temp[0].trim();
                        portStr = temp[1].trim();
                        for (int i = 0; i < portStr.length(); i++) {
                            if (!Character.isDigit(portStr.charAt(i))) {
                                System.out.println("Error!! port should be Integer value");
                                System.exit(0);
                            }
                        }
                        port = Integer.parseInt(portStr);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch(NumberFormatException e){
            System.out.println("Error!! The window size should be an integer.");
            System.exit(0);
        }

        dgims = new DGIM(windowSize);

        InputThread inputThread = new SocketandQueryThread().new InputThread(host, port);
        inputThread.start();

        QueryThread queryThread = new SocketandQueryThread().new QueryThread();
        queryThread.start();

    }
}
