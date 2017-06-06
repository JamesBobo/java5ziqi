package chess.gui;

import java.awt.BorderLayout;
import java.awt.List;
import java.awt.Panel;

public class UserListPad extends Panel {
public List userList = new List(10);

public UserListPad(){
	setLayout(new BorderLayout());
	for(int i=0;i<10;i++){
		userList.add(i+"."+"ÎÞÓÃ»§");
		
	}
	add(userList,BorderLayout.CENTER);
}

}
