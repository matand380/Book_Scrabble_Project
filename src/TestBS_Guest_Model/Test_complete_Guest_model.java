package TestBS_Guest_Model;

import Model.BS_Guest_Model;

public class Test_complete_Guest_model {
  public static void main(String[] args) {
      BS_Guest_Model client1 = BS_Guest_Model.getModel();
		client1.openSocket("127.0.0.1", 65533);  //copy local server ip + server port
		client1.getCommunicationHandler().setCom();

//      BS_Guest_Model client2 = BS_Guest_Model.getModel();
//      client2.openSocket("127.0.0.1", 65533);  //copy local server ip + server port
//      client2.getCommunicationHandler().setCom();



    }
}

