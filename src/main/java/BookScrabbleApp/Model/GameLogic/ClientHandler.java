package BookScrabbleApp.Model.GameLogic;

import java.io.InputStream;
import java.io.OutputStream;

public interface ClientHandler {

void handleClient(InputStream inputStream, OutputStream outputStream);
    void close();
}
