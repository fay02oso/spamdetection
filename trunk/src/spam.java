import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static java.lang.System.*;

public class spam {

	/**
	 * @param args
	 */
	public static String dataset="correus2.txt";
	public static int training=80;
	public static int test=100-training;
	
	public static treemap spam = new treemap();
	public static treemap noSpam = new treemap();
	public static int nNoSpam=0;
	public static int nSpam=0;
	public static int nMails=0;

	
	public static void main(String[] args) {
		readFile(dataset);
		noSpam.setPrior((double)nNoSpam/(double)nMails);
		spam.setPrior((double)nSpam/(double)nMails);
		out.println("Prior Spam: "+spam.prior);
		out.println("Prior No Spam: "+noSpam.prior);
		noSpam.addProbability(noSpam.root);
		spam.addProbability(spam.root);
		out.println("Probabilidad que la palabra 9 este en Spam:" +spam.get(9));
		out.println("Probabilidad que la palabra 9 este en No Spam: "+noSpam.get(9));
	}
	
	public static void readFile(String filename){
		try {
			Scanner file = new Scanner (new File (filename));
	        for (int linenr = 1; file.hasNextLine(); ++linenr) {
	        	String line = file.nextLine();
	        	String[] email = line.split (" ");
	        	if (email.length > 0) {	
	        		nMails++;
	        		if(Integer.parseInt(email[0]) == 1){
						//NO ES SPAM
	        			nNoSpam++;
	        			for(int i=1; i<email.length; i++){
	        				String[] words  = email[i].split ("\\:");
	        				noSpam.totalWords=noSpam.totalWords + Integer.parseInt(words[1]);
	        				noSpam.put(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
	        			}
	        		}
	        		if(Integer.parseInt(email[0]) == -1){
	        			//ES SPAM
	        			nSpam++;
	        			for(int i=1; i<email.length; i++){
	        				String[] words  = email[i].split ("\\:");
	        				spam.totalWords=spam.totalWords + Integer.parseInt(words[1]);
	        				spam.put(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
	        			}
	        		}
	        	} 
	        }
	        file.close();
	        
	        
		}catch (IOException error) {
	         //Misstage error de fitxer no trobat.
		}
	}

}
