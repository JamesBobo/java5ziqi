package chess.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import chess.gui.UserChatPad;
import chess.gui.UserControlPad;
import chess.gui.UserInputPad;
import chess.gui.UserListPad;
import chess.pad.FIRPad;

public class FIRClient extends Frame implements ActionListener, KeyListener {
	
	Socket clientSocket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    String chessClientName = null;
    String host = null;
     int port = 4331;
     boolean isOnChat = false; // 是否在聊天
     boolean isOnChess = false; // 是否在下棋
     boolean isGameConnected = false; // 游戏是否进行中
     boolean isCreator = false; // 是否为游戏创建者
     boolean isParticipant = false; // 是否为游戏加入者
    UserListPad userListPad = new UserListPad(); // 用户列表区
    UserChatPad userChatPad = new UserChatPad(); // 用户聊天区
    UserControlPad userControlPad = new UserControlPad(); // 用户操作区
    UserInputPad userInputPad = new UserInputPad(); // 用户输入区
    FIRPad firPad = new FIRPad(); // 下棋区
    Panel southPanel = new Panel(); // 面板区
    Panel northPanel = new Panel();
    Panel centerPanel = new Panel();
    Panel eastPanel = new Panel();
    public FIRClient()   
    {
         super("Java 五子棋客户端");
         setLayout(new BorderLayout());
         host = userControlPad.ipInputted.getText();
         eastPanel.setLayout(new BorderLayout());
         eastPanel.add(userListPad, BorderLayout.NORTH);
         eastPanel.add(userChatPad, BorderLayout.CENTER);
         eastPanel.setBackground(Color.LIGHT_GRAY);
         userInputPad.contentInputted.addKeyListener(this);
         firPad.host = userControlPad.ipInputted.getText();
         centerPanel.add(firPad, BorderLayout.CENTER);
         centerPanel.add(userInputPad, BorderLayout.SOUTH);
         centerPanel.setBackground(Color.LIGHT_GRAY);
         userControlPad.connectButton.addActionListener(this);
         userControlPad.createButton.addActionListener(this);
         userControlPad.joinButton.addActionListener(this);
         userControlPad.cancelButton.addActionListener(this);
         userControlPad.exitButton.addActionListener(this);

         userControlPad.createButton.setEnabled(false);
         userControlPad.joinButton.setEnabled(false);
         userControlPad.cancelButton.setEnabled(false);

         southPanel.add(userControlPad, BorderLayout.CENTER);
         southPanel.setBackground(Color.LIGHT_GRAY);
         addWindowListener(new WindowAdapter()
         {
                   public void windowClosing(WindowEvent e)
                   {
                          if (isOnChat){  // 聊天中
                                   try{          // 关闭客户端套接口
                                        clientSocket.close();
                                 }   catch (Exception ed){}
                         }
                          if (isOnChess || isGameConnected){ // 下棋中
                                  try{           // 关闭下棋端口
                                       firPad.chessSocket.close();
                                }
                                  catch (Exception ee){}
                          }
                          System.exit(0);
                     }
         });
         add(eastPanel, BorderLayout.EAST);
         add(centerPanel, BorderLayout.CENTER);
         add(southPanel, BorderLayout.SOUTH);
         pack();
         setSize(670, 560);
         setVisible(true);
         setResizable(false);
          this.validate();
   }
    public boolean connectToServer(String serverIP, int   serverPort) throws Exception{
        try {
                clientSocket = new Socket(serverIP, serverPort);
                inputStream = new 
                           DataInputStream(clientSocket.getInputStream());
                outputStream = new 
                           DataOutputStream(clientSocket.getOutputStream());
                FIRClientThread clientthread = new FIRClientThread(this);
                clientthread.start();
                isOnChat = true;    return true;
         }
          catch (IOException ex){
                 userChatPad.chatTextArea.setText("不能连接!\n");
         }
          return false;
 }



	@Override
	public void keyTyped(KeyEvent e) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO 自动生成的方法存根
        TextField inputwords = (TextField) e.getSource();
        if (e.getKeyCode() == KeyEvent.VK_ENTER){    
              if (userInputPad.userChoice.getSelectedItem().
                           equals("所有用户")){ // 给所有人发信息
                       try{  // 发送信息
                           outputStream.writeUTF(inputwords.getText());
                           inputwords.setText("");
                     }catch (Exception ea){
                            userChatPad.chatTextArea
                                               .setText("不能连接到服务器!\n");
                            userListPad.userList.removeAll();
                            userInputPad.userChoice.removeAll();
                            inputwords.setText("");
                            userControlPad.connectButton.setEnabled(true);
                     }
               }
               else
               { // 给指定人发信息
                   try{
                         outputStream.writeUTF("/" +  
                                     userInputPad.userChoice.getSelectedItem()
                                     + " " + inputwords.getText());
                         inputwords.setText("");
                  }    catch (Exception ea)       {
                  userChatPad.chatTextArea.setText("不能连接到服务器!\n");
                  userListPad.userList.removeAll();
                  userInputPad.userChoice.removeAll();
                  inputwords.setText("");
                   userControlPad.connectButton.setEnabled(true);
               }
             }
           }

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		if (e.getSource() == userControlPad.connectButton)
	     { // 连接到主机按钮单击事件
	             host = firPad.host = userControlPad.ipInputted.getText(); 
	              try   {
	                     if (connectToServer(host, port)){   
	                          userChatPad.chatTextArea.setText("");
	                          userControlPad.connectButton.setEnabled(false);
	                          userControlPad.createButton.setEnabled(true);
	                          userControlPad.joinButton.setEnabled(true);
	                          firPad.statusText.setText("连接成功，请等待!");
	                     }
	             }
	              catch (Exception ei)  {
	                       userChatPad.chatTextArea.setText("不能连接!\n");
	            }
	     }
		if (e.getSource() == userControlPad.exitButton){ 
	           if (isOnChat) {  // 若用户处于聊天状态中
	                        try  { // 关闭客户端套接口
	                              clientSocket.close();
	                       }
	                         catch (Exception ed){}
	            }
	            if (isOnChess || isGameConnected)
	           { // 若用户处于游戏状态中
	                        try   { // 关闭游戏端口
	                              firPad.chessSocket.close();
	                      }
	                       catch (Exception ee){}
	            }
	            System.exit(0);
		 }
		if (e.getSource() == userControlPad.joinButton)
		{ // 加入游戏按钮单击事件
		         String selectedUser = 
		                    (String)userListPad.userList.getSelectedItem(); 
		         if (selectedUser == null || 
		                        selectedUser.startsWith("[inchess]") ||
		                       selectedUser.equals(chessClientName))   { 
		                 firPad.statusText.setText("必须选择一个用户!");
		          }else{ // 执行加入游戏的操作
		                   try  {
		                            if (!isGameConnected){ // 若游戏套接口未连接
		             if (firPad.connectServer(firPad.host, firPad.port)) { 
		                  isGameConnected = true;
		                  isOnChess = true;
		                  isParticipant = true;
		                  userControlPad.createButton.setEnabled(false);
		                  userControlPad.joinButton.setEnabled(false);
		                  userControlPad.cancelButton.setEnabled(true);
		                  firPad.firThread.sendMessage("/joingame "
		                          + (String)userListPad.userList.getSelectedItem() + " "
		                          + chessClientName);
		                  }
		            } else{ // 若游戏端口连接中
		                     isOnChess = true;         isParticipant = true;
		                     userControlPad.createButton.setEnabled(false);
		                     userControlPad.joinButton.setEnabled(false);
		                     userControlPad.cancelButton.setEnabled(true);
		                     firPad.firThread.sendMessage("/joingame "
		                               + (String)userListPad.userList.getSelectedItem() + " "
		                               + chessClientName);
		           }
		       }catch (Exception ee){     isGameConnected = false;   isOnChess = false;
		             isParticipant = false;     userControlPad.createButton.setEnabled(true);
		             userControlPad.joinButton.setEnabled(true);
		             userControlPad.cancelButton.setEnabled(false);
		             userChatPad.chatTextArea.setText("不能连接: \n" + ee);   }     }
		       }
		if (e.getSource() == userControlPad.createButton){ 
	        try  {
	               if (!isGameConnected)  { // 若游戏端口未连接
	                       if (firPad.connectServer(firPad.host, firPad.port)){ 
	                             isGameConnected = true;
	                             isOnChess = true;
	                             isCreator = true;
	                             userControlPad.createButton.setEnabled(false);
	                             userControlPad.joinButton.setEnabled(false);
	                             userControlPad.cancelButton.setEnabled(true);
	                            firPad.firThread.sendMessage("/creatgame "
	                                                   + "[inchess]" + chessClientName);
	                      }
	            }else{ // 若游戏端口连接中
	                    isOnChess = true;     isCreator = true;
	                    userControlPad.createButton.setEnabled(false);
	                    userControlPad.joinButton.setEnabled(false);
	                    userControlPad.cancelButton.setEnabled(true);
	                    firPad.firThread.sendMessage("/creatgame "
                                + "[inchess]" + chessClientName);
	            	}
	        	}
				        	catch (Exception ec)
			  {
			         isGameConnected = false;
			         isOnChess = false;
			         isCreator = false;
			         userControlPad.createButton.setEnabled(true);
			         userControlPad.joinButton.setEnabled(true);
			         userControlPad.cancelButton.setEnabled(false);
			         ec.printStackTrace();
			         userChatPad.chatTextArea.setText("不能连接: \n"+ ec);
			  }
		}
		if (e.getSource() == userControlPad.cancelButton){ 
            if (isOnChess){ // 游戏中
                 firPad.firThread.sendMessage("/giveup " + chessClientName);
                 firPad.setVicStatus(-1 * firPad.chessColor);
                 userControlPad.createButton.setEnabled(true);
                 userControlPad.joinButton.setEnabled(true);
                 userControlPad.cancelButton.setEnabled(false);
                 firPad.statusText.setText("请创建或加入游戏!");
           }
            if (!isOnChess){ // 非游戏中
                 userControlPad.createButton.setEnabled(true);
                 userControlPad.joinButton.setEnabled(true);
                 userControlPad.cancelButton.setEnabled(false);
                firPad.statusText.setText("请创建或加入游戏!");
          }
          isParticipant = isCreator = false;
      }
	




	}
    public static void main(String args[])
   {
           FIRClient chessClient = new FIRClient();
   }

}
