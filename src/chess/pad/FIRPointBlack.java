package chess.pad;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class FIRPointBlack extends Canvas{
	FIRPad padBelonged;
	public FIRPointBlack(FIRPad padBelonged){
		setSize(20,20);
		this.padBelonged=padBelonged;
	}
	public void paint(Graphics g){
		g.setColor(Color.BLACK);
		g.fillOval(0, 0, 14, 14);
	}
}
