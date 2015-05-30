import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



public class AITester {
	public static void main(String[] args){
		for(int j = 0;j<10;j++){
			System.out.println("New Test");
			GameTester gt = new GameTester();
			//set AI coefficients
			gt.mv.keypress();
		}
	}
	
	public static int print(double average){
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data.txt", true)))) {
		    out.println(average);
		    return 1;
		}catch (IOException e) {
		    return 0;
		}
	}
}
