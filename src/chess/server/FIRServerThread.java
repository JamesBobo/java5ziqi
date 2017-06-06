package chess.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.omg.CORBA.PUBLIC_MEMBER;

import chess.server.ServerMsgPanel;


public class FIRServerThread extends Thread
{
	Socket clientSocket; // ����ͻ����׽ӿ���Ϣ
	Hashtable clientDataHash; // ����ͻ��˶˿����������Ӧ��Hash
	Hashtable clientNameHash; // ����ͻ����׽ӿںͿͻ�����Ӧ��Hash
	Hashtable chessPeerHash; // ������Ϸ�����ߺ���Ϸ�����߶�Ӧ��Hash
	ServerMsgPanel serverMsgPanel;
	boolean isClientClosed = false;
	
	public FIRServerThread(Socket clientSocket, Hashtable clientDataHash,
			Hashtable clientNameHash, Hashtable chessPeerHash,
			ServerMsgPanel server)
	{
		this.clientSocket = clientSocket;
		this.clientDataHash = clientDataHash;
		this.clientNameHash = clientNameHash;
		this.chessPeerHash = chessPeerHash;
		this.serverMsgPanel = server;
	}

	public void dealWithMsg(String msgReceived)
	{
		String clientName;
		String peerName;
		if (msgReceived.startsWith("/"))
		{
			if (msgReceived.equals("/list"))
			{ // �յ�����ϢΪ�����û��б�
				Feedback(getUserList());
			}
			else if (msgReceived.startsWith("/creatgame [inchess]"))
			{ // �յ�����ϢΪ������Ϸ
				String gameCreaterName = msgReceived.substring(20); //ȡ�÷�������
				synchronized (clientNameHash)
				{ // ���û��˿ڷŵ��û��б���
					clientNameHash.put(clientSocket, msgReceived.substring(11));
				}
				synchronized (chessPeerHash)
				{ // ����������Ϊ�ȴ�״̬
					chessPeerHash.put(gameCreaterName, "wait");
				}
				Feedback("/yourname " + clientNameHash.get(clientSocket));
				sendGamePeerMsg(gameCreaterName, "/OK");
				sendPublicMsg(getUserList());
			}
			else if (msgReceived.startsWith("/joingame "))
			{ // �յ�����ϢΪ������Ϸʱ
				StringTokenizer userTokens = new StringTokenizer(msgReceived, " ");
				String userToken;
				String gameCreatorName;
				String gamePaticipantName;
				String[] playerNames = { "0", "0" };
				int nameIndex = 0;
				while (userTokens.hasMoreTokens())
				{
					userToken = (String) userTokens.nextToken(" ");
					if (nameIndex >= 1 && nameIndex <= 2)
					{
						playerNames[nameIndex - 1] = userToken; // ȡ����Ϸ����
					}
					nameIndex++;
				}
				gameCreatorName = playerNames[0];
				gamePaticipantName = playerNames[1];
				if (chessPeerHash.containsKey(gameCreatorName)
						&& chessPeerHash.get(gameCreatorName).equals("wait"))
				{ // ��Ϸ�Ѵ���
					synchronized (clientNameHash)
					{ // ������Ϸ�����ߵ��׽ӿ������ƵĶ�Ӧ
						clientNameHash.put(clientSocket,
								("[inchess]" + gamePaticipantName));
					}
					synchronized (chessPeerHash)
					{ // ���ӻ��޸���Ϸ����������Ϸ�����ߵ����ƵĶ�Ӧ
						chessPeerHash.put(gameCreatorName, gamePaticipantName);
					}
					sendPublicMsg(getUserList());
					// ������Ϣ����Ϸ������
					sendGamePeerMsg(gamePaticipantName,
							("/peer " + "[inchess]" + gameCreatorName));
					// ������Ϸ����Ϸ������
					sendGamePeerMsg(gameCreatorName,
							("/peer " + "[inchess]" + gamePaticipantName));
				}
				else
				{ // ����Ϸδ������ܾ�������Ϸ
					sendGamePeerMsg(gamePaticipantName, "/reject");
					try
					{
						closeClient();
					}
					catch (Exception ez)
					{
						ez.printStackTrace();
					}
				}
			}
			else if (msgReceived.startsWith("/[inchess]"))
			{ // �յ�����ϢΪ��Ϸ��ʱ
				int firstLocation = 0, lastLocation;
				lastLocation = msgReceived.indexOf(" ", 0);
				peerName = msgReceived.substring((firstLocation + 1), lastLocation);
				msgReceived = msgReceived.substring((lastLocation + 1));
				if (sendGamePeerMsg(peerName, msgReceived))
				{
					Feedback("/error");
				}
			}
			else if (msgReceived.startsWith("/giveup "))
			{ // �յ�����ϢΪ������Ϸʱ
				String chessClientName = msgReceived.substring(8);
				if (chessPeerHash.containsKey(chessClientName)
						&& !((String) chessPeerHash.get(chessClientName))
								.equals("wait"))
				{ // ʤ����Ϊ��Ϸ�����ߣ�����ʤ����Ϣ
					sendGamePeerMsg((String) chessPeerHash.get(chessClientName),
							"/youwin");
					synchronized (chessPeerHash)
					{ // ɾ���˳���Ϸ���û�
						chessPeerHash.remove(chessClientName);
					}
				}
				if (chessPeerHash.containsValue(chessClientName))
				{ // ʤ����Ϊ��Ϸ�����ߣ�����ʤ����Ϣ
					sendGamePeerMsg((String) getHashKey(chessPeerHash,
							chessClientName), "/youwin");
					synchronized (chessPeerHash)
					{// ɾ���˳���Ϸ���û�
						chessPeerHash.remove((String) getHashKey(chessPeerHash,
								chessClientName));
					}
				}
			}
			else
			{ // �յ�����ϢΪ������Ϣʱ
				int lastLocation = msgReceived.indexOf(" ", 0);
				if (lastLocation == -1)
				{
					Feedback("��Ч����");
					return;
				}
			}
		}
		else
		{
			msgReceived = clientNameHash.get(clientSocket) + ">" + msgReceived;
			serverMsgPanel.msgTextArea.append(msgReceived + "\n");
			sendPublicMsg(msgReceived);
			serverMsgPanel.msgTextArea.setCaretPosition(serverMsgPanel.msgTextArea.getText()
					.length());
		}
	}

	// ���͹�����Ϣ
	public void sendPublicMsg(String publicMsg)
	{
		synchronized (clientDataHash)
		{
			for (Enumeration enu = clientDataHash.elements(); enu
					.hasMoreElements();)
			{
				DataOutputStream outputData = (DataOutputStream) enu.nextElement();
				try
				{
					outputData.writeUTF(publicMsg);
				}
				catch (IOException es)
				{
					es.printStackTrace();
				}
			}
		}
	}

	// ������Ϣ��ָ������Ϸ�е��û�
	public boolean sendGamePeerMsg(String gamePeerTarget, String gamePeerMsg)
	{
		for (Enumeration enu = clientDataHash.keys(); enu.hasMoreElements();)
		{ // ������ȡ����Ϸ�е��û����׽ӿ�
			Socket userClient = (Socket) enu.nextElement();
			if (gamePeerTarget.equals((String) clientNameHash.get(userClient))
					&& !gamePeerTarget.equals((String) clientNameHash
							.get(clientSocket)))
			{ // �ҵ�Ҫ������Ϣ���û�ʱ
				synchronized (clientDataHash)
				{
					// ���������
					DataOutputStream peerOutData = (DataOutputStream) clientDataHash
							.get(userClient);
					try
					{
						// ������Ϣ
						peerOutData.writeUTF(gamePeerMsg);
					}
					catch (Exception es)
					{
						es.printStackTrace();
					}
				}
				return false;
			}
		}
		return true;
	}

	// ���ͷ�����Ϣ�����ӵ���������
	public void Feedback(String feedBackMsg)
	{
		synchronized (clientDataHash)
		{
			DataOutputStream outputData = (DataOutputStream) clientDataHash
					.get(clientSocket);
			try
			{
				outputData.writeUTF(feedBackMsg);
			}
			catch (Exception eb)
			{
				eb.printStackTrace();
			}
		}
	}

	// ȡ���û��б�
	public String getUserList()
	{
		String userList = "/userlist";
		for (Enumeration enu = clientNameHash.elements(); enu.hasMoreElements();)
		{
			userList = userList + " " + (String) enu.nextElement();
		}
		return userList;
	}

	// ����valueֵ��Hashtable��ȡ����Ӧ��key
	public Object getHashKey(Hashtable targetHash, Object hashValue)
	{
		Object hashKey;
		for (Enumeration enu = targetHash.keys(); enu.hasMoreElements();)
		{
			hashKey = (Object) enu.nextElement();
			if (hashValue.equals((Object) targetHash.get(hashKey)))
				return hashKey;
		}
		return null;
	}

	// �����ӵ�����ʱִ�еķ���
	public void sendInitMsg()
	{
		sendPublicMsg(getUserList());
		Feedback("/yourname " + (String) clientNameHash.get(clientSocket));
		Feedback("Java ������ͻ���");
		Feedback("/list --�����û��б�");
		Feedback("/<username> <talk> --˽��");
		Feedback("ע�⣺�������������û�����");
	}

	public void closeClient()
	{
		serverMsgPanel.msgTextArea.append("�û��Ͽ�����:" + clientSocket + "\n");
		synchronized (chessPeerHash)
		{ //�������Ϸ�ͻ�������
			if (chessPeerHash.containsKey(clientNameHash.get(clientSocket)))
			{
				chessPeerHash.remove((String) clientNameHash.get(clientSocket));
			}
			if (chessPeerHash.containsValue(clientNameHash.get(clientSocket)))
			{
				chessPeerHash.put((String) getHashKey(chessPeerHash,
						(String) clientNameHash.get(clientSocket)),
						"tobeclosed");
			}
		}
		synchronized (clientDataHash)
		{ // ɾ���ͻ�����
			clientDataHash.remove(clientSocket);
		}
		synchronized (clientNameHash)
		{ // ɾ���ͻ�����
			clientNameHash.remove(clientSocket);
		}
		sendPublicMsg(getUserList());
		serverMsgPanel.statusLabel.setText("��ǰ������:" + clientDataHash.size());
		try
		{
			clientSocket.close();
		}
		catch (IOException exx)
		{
			exx.printStackTrace();
		}
		isClientClosed = true;
	}

	public void run()
	{
		DataInputStream inputData;
		synchronized (clientDataHash)
		{
			serverMsgPanel.statusLabel.setText("��ǰ������:" + clientDataHash.size());
		}
		try
		{	// �ȴ����ӵ���������Ϣ
			inputData = new DataInputStream(clientSocket.getInputStream());
			sendInitMsg();
			while (true)
			{
				String message = inputData.readUTF();
				dealWithMsg(message);
			}
		}
		catch (IOException esx){}
		finally
		{
			if (!isClientClosed)
			{
				closeClient();
			}
		}
	}
}