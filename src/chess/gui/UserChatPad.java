package chess.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class UserChatPad extends JPanel{
	public JTextArea chatTextArea=new JTextArea();
	public UserChatPad(){
		setLayout(new BorderLayout());
		chatTextArea.setAutoscrolls(true);
		chatTextArea.setLineWrap(true);
		add(chatTextArea,BorderLayout.CENTER);
	}

}
