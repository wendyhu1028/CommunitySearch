package experiment;

import java.util.Random;

public class Test {

	public static void main(String[] args) {
		double size = 339;
		int min = (int)(size*0.1);
        int max = (int)(size*0.2);
        Random random = new Random();

        int s = random.nextInt(max)%(max-min+1) + min;
        System.out.print(s);
	}

}
