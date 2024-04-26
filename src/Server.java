import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("正在等待客户端连接...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("客户端已连接, IP地址为: " + socket.getInetAddress().getHostAddress());
                System.out.println("读取客户端数据：");
                socket.setSoTimeout(3000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println(reader.readLine());

                OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
                writer.write("已收到数据！\n");
                writer.flush();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
