package Model.GameServer;

import java.io.InputStream;
import java.io.OutputStream;

public interface ClientHandler {
//    void handleIn(InputStream inFromclient);
//
//    void handleOut(OutputStream outToClient);
void handleClient(InputStream inputStream, OutputStream outputStream);
    void close();
}
