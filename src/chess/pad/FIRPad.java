package chess.pad;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JPanel;

import chess.pad.FIRPointBlack;
import chess.pad.FIRPointWhite;

public class FIRPad extends Panel implements MouseListener,ActionListener {
	public boolean isMouseEnabled= false;
	public boolean isWinned= false;
	public boolean isGameing=false;
	
	public int chessX_POS=-1;
	public int chessY_POS=-1;
	public int chessColor=1;
	public int chessBlack_XPOS[]=new int [200];
	public int chessBlack_YPOS[]=new int[200];
	public int chessWhite_XPOS[]=new int [200];
	public int chessWhite_YPOS[]=new int [200];
	
	public int chessBlackCount=0;
	public int chessWhiteCount=0;
	public int chessBlackVicTimes=0;
	public int chessWhiteVicTimes=0;
	
	public Socket chessSocket;
	public DataInputStream inputData;
	public DataOutputStream outputData;
	
	public String chessSelfName=null;
	public String chessPeerName=null;
	public String host=null;
	public int port=4331;
	
	
	
	public TextField statusText =new TextField("Please connect to the servar first!");
	public FIRThread firThread= new FIRThread(this);
	
	public FIRPad()
	{
	setSize(440, 440);
	setLayout(null);
	setBackground(Color.LIGHT_GRAY);
	addMouseListener( this);

	add(statusText);
	statusText.setBounds(new Rectangle(40, 5, 360, 24));
	statusText.setEditable(false);
	}
	
	
	public boolean connectServer(String ServerIP,int ServerPort) throws Exception{
		try {
			chessSocket=new Socket(ServerIP,ServerPort);
			
			inputData=new DataInputStream(chessSocket.getInputStream());
			
			outputData =new DataOutputStream(chessSocket.getOutputStream());
			 firThread.start();
			 return true;
		} catch (IOException e) {
			// TODO: handle exception
			statusText.setText("����ʧ�ܣ�\n");
		}
		return false;
	}
	
	public void setVicStatus(int vicChessColor)
	{
		
		this.removeAll();
		for(int i=0;i<=chessBlackCount;i++){
			chessBlack_XPOS[i]=0;
			chessBlack_YPOS[i]=0;
		}
		for(int i=0;i<=chessWhiteCount;i++){
			chessWhite_XPOS[i]=0;
			chessWhite_YPOS[i]=0;
		}
		chessBlackCount=0;
		chessWhiteCount=0;
		
		add(statusText);
		statusText.setBounds(40, 5, 360, 24);
		if(vicChessColor==1){
			chessBlackVicTimes++;
			statusText.setText("�ڷ�ʤ���ڣ���"+chessBlackVicTimes

+":"+chessWhiteVicTimes+",��Ϸ�������ȴ��׷�...");
			
		}
		else if(vicChessColor==-1){
			chessWhiteVicTimes++;
			statusText.setText("�׷�ʤ���ڣ���"+chessBlackVicTimes

+":"+chessWhiteVicTimes+",��Ϸ�������ȴ��ڷ�...");
		}
	}

	public void setLocation(int xPos,int yPos,int chessColor){
		if(chessColor==1){
			chessBlack_XPOS[chessBlackCount]=xPos*20;
			chessBlack_YPOS[chessBlackCount]=yPos*20;
			chessBlackCount++;
		}
		else if(chessColor==-1)
		{
			chessWhite_XPOS[chessWhiteCount]=xPos*20;
			chessWhite_YPOS[chessWhiteCount]=yPos*20;
			chessWhiteCount++;
		}
	}
	
	public boolean checkVicStatus(int xPos,int yPos,int chessColor)
	{
		int chessLinkedCount =1;
		int chessLinkedCompare=1;
		int chessToCompareIndex=0;
		int closeGrid=1;
		
		if(chessColor==1)
		{
			chessLinkedCount=1;
			
			//�����ж�
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessBlackCount;
						chessToCompareIndex++){
					if(((xPos+closeGrid)*20==chessBlack_XPOS

[chessToCompareIndex])&&((yPos)*20==chessBlack_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessBlackCount;
						chessToCompareIndex++){
					if(((xPos-closeGrid)*20==chessBlack_XPOS

[chessToCompareIndex])&&((yPos)*20==chessBlack_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			//�����ж�
			chessLinkedCompare=1;
			chessLinkedCount=1;
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessBlackCount;
						chessToCompareIndex++){
					if(((xPos)*20==chessBlack_XPOS[chessToCompareIndex])

&&((yPos+closeGrid)*20==chessBlack_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessBlackCount;
						chessToCompareIndex++){
					if(((xPos)*20==chessBlack_XPOS[chessToCompareIndex])

&&((yPos-closeGrid)*20==chessBlack_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			//���ϡ�����
			
			chessLinkedCompare=1;
			chessLinkedCount=1;
			
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessBlackCount;
						chessToCompareIndex++){
					if(((xPos+closeGrid)*20==chessBlack_XPOS

[chessToCompareIndex])&&((yPos+closeGrid)*20==chessBlack_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessBlackCount;
						chessToCompareIndex++){
					if(((xPos-closeGrid)*20==chessBlack_XPOS

[chessToCompareIndex])&&((yPos-closeGrid)*20==chessBlack_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			
			//���ϡ�����
			chessLinkedCompare=1;
			chessLinkedCount=1;
			
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessBlackCount;
						chessToCompareIndex++){
					if(((xPos-closeGrid)*20==chessBlack_XPOS

[chessToCompareIndex])&&((yPos+closeGrid)*20==chessBlack_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessBlackCount;
						chessToCompareIndex++){
					if(((xPos+closeGrid)*20==chessBlack_XPOS

[chessToCompareIndex])&&((yPos-closeGrid)*20==chessBlack_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			
		}
	
		if(chessColor==-1)
		{
			chessLinkedCount=1;
			
			//�����ж�
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessWhiteCount;
						chessToCompareIndex++){
					if(((xPos+closeGrid)*20==chessWhite_XPOS

[chessToCompareIndex])&&((yPos)*20==chessWhite_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
					
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessWhiteCount;
						chessToCompareIndex++){
					if(((xPos-closeGrid)*20==chessWhite_XPOS

[chessToCompareIndex])&&((yPos)*20==chessWhite_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			//�����ж�
			chessLinkedCompare=1;
			chessLinkedCount=1;
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessWhiteCount;
						chessToCompareIndex++){
					if(((xPos)*20==chessWhite_XPOS[chessToCompareIndex])

&&((yPos+closeGrid)*20==chessWhite_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessWhiteCount;
						chessToCompareIndex++){
					if(((xPos)*20==chessWhite_XPOS[chessToCompareIndex])

&&((yPos-closeGrid)*20==chessWhite_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			//���ϡ�����
			
			chessLinkedCompare=1;
			chessLinkedCount=1;
			
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessWhiteCount;
						chessToCompareIndex++){
					if(((xPos+closeGrid)*20==chessWhite_XPOS

[chessToCompareIndex])&&((yPos+closeGrid)*20==chessWhite_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessWhiteCount;
						chessToCompareIndex++){
					if(((xPos-closeGrid)*20==chessWhite_XPOS

[chessToCompareIndex])&&((yPos-closeGrid)*20==chessWhite_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			
			//���ϡ�����
			chessLinkedCompare=1;
			chessLinkedCount=1;
			
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessWhiteCount;
						chessToCompareIndex++){
					if(((xPos-closeGrid)*20==chessWhite_XPOS

[chessToCompareIndex])&&((yPos+closeGrid)*20==chessWhite_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			for(closeGrid=1;closeGrid<=4;closeGrid++)
			{
				for

(chessToCompareIndex=0;chessToCompareIndex<=chessWhiteCount;
						chessToCompareIndex++){
					if(((xPos+closeGrid)*20==chessWhite_XPOS

[chessToCompareIndex])&&((yPos-closeGrid)*20==chessWhite_YPOS[chessToCompareIndex])){
						chessLinkedCount=chessLinkedCount+1;
					
					if(chessLinkedCount==5){
						return true;
					}
					}
				}
					if(chessLinkedCount==(chessLinkedCompare+1)){
						chessLinkedCompare++;
					}
					else{
						break;
					}
				
			}
			
			
		}
	
		return false;
			
		
		 
		
		
	}
	
	
	
	public void paint(Graphics g)
	{
	for (int i = 40; i <= 380; i = i + 20)
	{
	g.drawLine(40, i, 400, i);
	}
	g.drawLine(40, 400, 400, 400);
	for (int j = 40; j <= 380; j = j + 20)
	{
	g.drawLine(j, 40, j, 400);
	}
	g.drawLine(400, 40, 400, 400);
	g.fillOval(97, 97, 6, 6);
	g.fillOval(337, 97, 6, 6);
	g.fillOval(97, 337, 6, 6);
	g.fillOval(337, 337, 6, 6);
	g.fillOval(217, 217, 6, 6);
	}

	public void paintFirPoint(int xPos, int yPos, int chessColor)
	{
	
		FIRPointBlack firPBlack= new FIRPointBlack(this);
		FIRPointWhite firPWhite=new FIRPointWhite(this);
		
		if (chessColor == 1 && isMouseEnabled)
		{ // ����
			// �������ӵ�λ��
			setLocation(xPos, yPos, chessColor);
			// ȡ�õ�ǰ����״̬
			isWinned = checkVicStatus(xPos, yPos, chessColor);
			if (isWinned == false)
			{ // ��ʤ��״̬
				firThread.sendMessage("/" + chessPeerName + " /chess "
						+ xPos + " " + yPos + " " + chessColor);
				this.add(firPBlack); // ��������ӵ�������
				firPBlack.setBounds(xPos * 20 - 7,
						yPos * 20 - 7, 16, 16); // �������ӱ߽�
				statusText.setText("��(��" + chessBlackCount + "��)"
						+ xPos + " " + yPos + ",�ֵ��׷�.");
				isMouseEnabled = false; // �������Ϊ������
			}
			else
			{ // ʤ��״̬
				firThread.sendMessage("/" + chessPeerName + " /chess "
						+ xPos + " " + yPos + " " + chessColor);
				this.add(firPBlack);
				firPBlack.setBounds(xPos * 20 - 7,
						yPos * 20 - 7, 16, 16);
				setVicStatus(1); // ����ʤ���������������Ϊ����ʤ��
				isMouseEnabled = false;
			}
		}
		else if(chessColor==-1&&isMouseEnabled)
		{
			setLocation(xPos, yPos, chessColor);
			isWinned=checkVicStatus(xPos, yPos, chessColor);
			if(isWinned==false){
				firThread.sendMessage("/"+chessPeerName+" /chess "
						+xPos+" "+yPos+" "+chessColor);
				this.add(firPWhite);
				firPWhite.setBounds(xPos*20-7, yPos*20-7, 16, 16);
				statusText.setText("��(��" + chessWhiteCount + "��)"
						+ xPos + " " + yPos + ",�ֵ��ڷ�.");
				isMouseEnabled=false;
			}
			else
			{
				firThread.sendMessage("/"+chessPeerName+" /chess "
						+xPos+" "+yPos+" "+chessColor);
				this.add(firPWhite);
				firPWhite.setBounds(xPos*20-7, yPos*20-7, 16, 16);
				setVicStatus(-1);
				isMouseEnabled=false;
			}
		}
	
	}
	
	public void paintNetFirPoint(int xPos, int yPos, int chessColor)
	{
		FIRPointBlack firPBlack = new FIRPointBlack(this);
		FIRPointWhite firPWhite = new FIRPointWhite(this);
		setLocation(xPos, yPos, chessColor);
		if (chessColor == 1)
		{
			isWinned = checkVicStatus(xPos, yPos, chessColor);
			if (isWinned == false)
			{
				this.add(firPBlack);
				firPBlack.setBounds(xPos * 20 - 7,
						yPos * 20 - 7, 16, 16);
				statusText.setText("��(��" + chessBlackCount + "��)"
						+ xPos + " " + yPos + ",�ֵ��׷�.");
				isMouseEnabled = true;
			}
			else
			{
				firThread.sendMessage("/" + chessPeerName + " /victory "
						+ chessColor);//djr
				this.add(firPBlack);
				firPBlack.setBounds(xPos * 20 - 7,
						yPos * 20 - 7, 16, 16);
				setVicStatus(1);
				isMouseEnabled = true;
			}
		}
		else if (chessColor == -1)
		{
			isWinned = checkVicStatus(xPos, yPos, chessColor);
			if (isWinned == false)
			{
				this.add(firPWhite);
				firPWhite.setBounds(xPos * 20 - 7,
						yPos * 20 - 7, 16, 16);
				statusText.setText("��(��" + chessWhiteCount + "��)"
						+ xPos + " " + yPos + ",�ֵ��ڷ�.");
				isMouseEnabled = true;
			}
			else
			{
				firThread.sendMessage("/" + chessPeerName + " /victory "
						+ chessColor);
				this.add(firPWhite);
				firPWhite.setBounds(xPos * 20 - 7,
						yPos * 20 - 7, 16, 16);
				setVicStatus(-1);
				isMouseEnabled = true;
			}
		}
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO �Զ����ɵķ������
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO �Զ����ɵķ������
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO �Զ����ɵķ������
		
		if(e.getModifiers()==InputEvent.BUTTON1_MASK){
			
			chessX_POS=(int)e.getX();
			chessY_POS=(int)e.getY();
			int a=(chessX_POS+10)/20,b=(chessY_POS+10)/20;
			if(chessX_POS/20<2||chessY_POS/20<2||chessX_POS/20>19||

chessY_POS/20>19){
				
			}
			else{
				paintFirPoint(a, b, chessColor);
			}
		}
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO �Զ����ɵķ������
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO �Զ����ɵķ������
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO �Զ����ɵķ������
		
	}

}
