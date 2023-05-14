package ViewModel;

import Model.BS_Host_Model;

import java.util.Observable;
import java.util.Observer;

public class BS_ViewModel extends Observable implements Observer {

    String winner;

    @Override
    public void update(Observable o, Object arg) {
        if (o == BS_Host_Model.getModel())
            if (arg instanceof String) {
                String key = (String) arg;

                switch (key) {
                    case "try successful":
                        //update the host view

                        break;
                }
            }


    }
}
