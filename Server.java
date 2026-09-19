import java.io.*;
import java.net.*;

public class Server {
  private static final int PORT = 5000;

  public static void main(String[] args) {
    System.out.println("Server starting on port " + PORT + "...");

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("Server is listening...");

      while (true) {
        // Wait for a client to connect (blocks until connection)
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress());

        // Handle each client in a separate thread
        new Thread(new ClientHandler(clientSocket)).start();
      }
    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }

  // Handles communication with a single client
  static class ClientHandler implements Runnable {
    private final Socket socket;

    ClientHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try (
          BufferedReader in = new BufferedReader(
              new InputStreamReader(socket.getInputStream()));
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
        out.println("Welcome to the Echo Server! Type 'bye' to exit.");

        String message;
        while ((message = in.readLine()) != null) {
          System.out.println("Received: " + message);

          if (message.equalsIgnoreCase("bye")) {
            out.println("Goodbye!");
            break;
          }

          // Echo back (uppercase for demonstration)
          out.println("Echo: " + message.toUpperCase());
        }
      } catch (IOException e) {
        System.err.println("Client handler error: " + e.getMessage());
      } finally {
        try {
          socket.close();
          System.out.println("Client disconnected.");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
