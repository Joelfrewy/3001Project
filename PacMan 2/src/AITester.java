import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



public class AITester {
	public static void main(String[] args){
		for(int j = 0;j<1;j++){
			System.out.println("New Test");
			int i = 0;
			double a1 = Math.random();
			System.out.println("a1: " + a1);
			double a2 = Math.random()*2-1;
			System.out.println("a2: " + a2);
			while(i<5){
				GameTester gt = new GameTester();
				//set AI coefficients
				for(Ghost g:gt.m.getGhosts()){
					g.a1 = a1;
					g.a2 = a2;
				}
				gt.mv.keypress();
				i++;
			}
		}
	}
	
	public static int print(double a1,double a2, double average){
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data.txt", true)))) {
		    out.println(a1 + " " + a2 + " " + average);
		    return 1;
		}catch (IOException e) {
		    return 0;
		}
	}
}
