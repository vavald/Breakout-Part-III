package breakout.gui;

import java.awt.EventQueue;
import javax.swing.JFrame;

import breakout.BlockState;
import breakout.BreakoutFacade;
import breakout.BreakoutState;
import breakout.PaddleState;
import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;

public class BreakoutApplication {

	public static final String initMap = """
##########
###!######
##########
SSS!###!#S
     o

     =

""";

	public static final String initMap2 = """
#!##R#####
###!##SS##
##########
RSSRR##!!R
     o

     =

""";

	public static final String initMap3 = """
##########
##########
##########
##########
     o

     =

""";
	
	
	public static void main(String[] args) {

		
		
		BreakoutFacade facade = new BreakoutFacade();
		BreakoutState state = facade.createStateFromDescription(initMap2); //return to 2
//		BreakoutState state = GameMap.someAlphas();
		EventQueue.invokeLater(() -> {
			GameView mazeView = new GameView(state);
			JFrame frame = new JFrame("Breakout");
			frame.getContentPane().add(mazeView);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}

}
