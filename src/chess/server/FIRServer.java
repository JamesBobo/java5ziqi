package chess.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import javax.swing.JButton;

public class FIRServer extends Frame implements ActionListener {
	JButton clearMsgButton = new JButton("����б�");
    JButton serverStatusButton = new JButton("������״̬");
    JButton closeServerButton = new JButton("�رշ�����");
     Panel buttonPanel = new Panel();
     ServerMsgPanel serverMsgPanel = new ServerMsgPanel();
     ServerSocket serverSocket;
     Hashtable clientDataHash = new Hashtable(50); 
     Hashtable clientNameHash = new Hashtable(50); 
     Hashtable chessPeerHash = new Hashtable(50); 
     public FIRServer()
     {     //ʡ�Բ��ִ���
    	super("Java �����������");
 		setBackground(Color.LIGHT_GRAY);
 		buttonPanel.setLayout(new FlowLayout());
 		clearMsgButton.setSize(60, 25);
 		buttonPanel.add(clearMsgButton);
 		clearMsgButton.addActionListener(this);
 		serverStatusButton.setSize(75, 25);
 		buttonPanel.add(serverStatusButton);
 		serverStatusButton.addActionListener(this);
 		closeServerButton.setSize(75, 25);
 		buttonPanel.add(closeServerButton);
 		closeServerButton.addActionListener(this);
 		add(serverMsgPanel, BorderLayout.CENTER);
 		add(buttonPanel, BorderLayout.SOUTH);
 		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		pack();
		setVisible(true);
		setSize(400, 300);
		setResizable(false);
		validate();
         try{     
        	 createServer(4331, serverMsgPanel);
            }catch (Exception e){
            e.printStackTrace();
     }
}
	private void createServer(int port, ServerMsgPanel serverMsgPanel) throws IOException 
	{
		// TODO �Զ����ɵķ������
		Socket clientSocket; 
        long clientAccessNumber = 1;
        this.serverMsgPanel = serverMsgPanel;
        try{
             serverSocket = new ServerSocket(port);
             serverMsgPanel.msgTextArea.setText("�����������ڣ�"+
             InetAddress.getLocalHost()+":"+serverSocket.getLocalPort()+"\n");
             while (true)
            {
                   clientSocket = serverSocket.accept();
                   serverMsgPanel.msgTextArea.append("�������û���"+clientSocket+"\n");
                   
                   DataOutputStream outputData = new
                             DataOutputStream(clientSocket.getOutputStream());
                   clientDataHash.put(clientSocket, outputData);
                   clientNameHash.put(clientSocket, ("�����" + clientAccessNumber++));
                   FIRServerThread thread = new FIRServerThread(clientSocket,
                   clientDataHash, clientNameHash, chessPeerHash, serverMsgPanel);
                   thread.start();
           }
       }catch (IOException ex){ex.printStackTrace();
       	}

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO �Զ����ɵķ������
		if(e.getSource()==clearMsgButton){
			serverMsgPanel.msgTextArea.setText("");
		}
		if(e.getSource()==serverStatusButton)
		{
			try {
				serverMsgPanel.msgTextArea.append("��������Ϣ��"+InetAddress.getLocalHost()
						+":"+serverSocket.getLocalPort()+"\n"
						);
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			
		}
		if(e.getSource()==closeServerButton){
			System.exit(0);
		}
		
	}
	public static void main(String args[]){
		FIRServer firServer=new FIRServer();
	}


}
