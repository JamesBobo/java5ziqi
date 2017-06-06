package chess.pad;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class FIRPointWhite extends Canvas{
	FIRPad padBelonged;
	public FIRPointWhite(FIRPad padBelonged){
		setSize(20,20);
		this.padBelonged=padBelonged;
	}
	public void paint(Graphics g){
		g.setColor(Color.white);
		g.fillOval(0, 0, 14, 14);
	}
}

