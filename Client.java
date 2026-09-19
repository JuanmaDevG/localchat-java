import java.io.*;
import java.net.*;

public class Client {
  private static final String SERVER_HOST = "localhost";
  private static final int SERVER_PORT = 5000;
  private static String username = null;
  private static Socket socket;

  public static void main(String[] args) {
    // TODO: add TUI with username
    BufferedReader consoleInput = new BufferedReader(
        new InputStreamReader(System.in));
    socket = new Socket(SERVER_HOST, SERVER_PORT);
    try (
        DataInputStream receiver = new DataInputStream(socket.getInputStream());
        DataOutputStream sender = new DataOutputStream(socket.getOutputStream());

        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {

      System.out.println("Connected to server at "
          + SERVER_HOST + ":" + SERVER_PORT);

      // Receive username
      username = receiver.readUTF();

      // Receiver thread, sender thread
      Thread tSender = new Thread(Client::initSender);
      tSender.start();

      while (true) {
        String msg = receiver.readUTF();
        System.out.println(msg);
      }

      String serverMessage = in.readLine();
      System.out.println("Server: " + serverMessage);

      String userInput;
      while (true) {
        System.out.print("You: ");
        userInput = consoleInput.readLine();

        if (userInput == null)
          break;

        // Send message to server
        out.println(userInput);

        // Read response from server
        serverMessage = in.readLine();
        System.out.println("Server: " + serverMessage);

        if (userInput.equalsIgnoreCase("bye")) {
          break;
        }
      }

      System.out.println("Connection closed.");
    } catch (UnknownHostException e) {
      System.err.println("Unknown host: " + SERVER_HOST);
    } catch (IOException e) {
      System.err.println("I/O error: " + e.getMessage());
    }
  }

  public static void initSender() {
  }
}
