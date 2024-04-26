import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket(); Scanner scanner = new Scanner(System.in)) {
            socket.setKeepAlive(true);
            socket.connect(new InetSocketAddress("localhost", 8080));
            System.out.println("已连接到服务器！");
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write(scanner.nextLine() + "\n");
            writer.flush();
            System.out.println("已发送数据！等待服务器响应...");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("服务器响应：" + reader.readLine());
        } catch (IOException e) {
            System.out.println("连接服务器失败！");
            e.printStackTrace();
        }
    }
}
