package Model.GameLogic;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public interface ClientHandler {
//    void handleIn(InputStream inFromclient);
//
//    void handleOut(OutputStream outToClient);
void handleClient(ObjectInputStream inputStream, ObjectOutputStream outputStream);
    void close();
}
