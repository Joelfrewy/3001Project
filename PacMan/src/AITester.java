import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



public class AITester {
	public static void main(String[] args){
		for(int j = 0;j<1;j++){
			System.out.println("New Test");
			double i = 1;
			double a1 = 599;//j*10+905;//Math.random()*1000;
			while(i<2){
				System.out.println("a1: " + (a1));
				GameTester gt = new GameTester();
				//set AI coefficients
				for(Ghost g:gt.m.getGhosts()){
					g.a1 = a1;
				}
				gt.mv.keypress();
				i++;
			}
		}
	}
	
	public static int print(double a1, double average){
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data.txt", true)))) {
		    out.println(a1 + " " + average);
		    return 1;
		}catch (IOException e) {
		    return 0;
		}
	}
}
