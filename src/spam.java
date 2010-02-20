import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Scanner;
import static java.lang.System.*;

public class spam {

	/**
	 * @param args
	 */
	public static String dataset="correus.txt";
	public static int training=70;
	public static int test=100-training;
	
	public static treemap spam = new treemap();
	public static treemap noSpam = new treemap();
	public static int nNoSpam=0;
	public static int nSpam=0;
	public static int nMails=0;
	public static int nTraining=0;
	public static int nTest=0;
	private static Scanner file;
	public static int[][] confusionMatrix = new int [2][2];

	public static void main(String[] args) {
		readFile(dataset);
		noSpam.setPrior((double)nNoSpam/(double)nMails);
		spam.setPrior((double)nSpam/(double)nMails);
		//noSpam.setPrior(0.2);
		//spam.setPrior(0.8);
		noSpam.addProbability(noSpam.root);
		spam.addProbability(spam.root);
		test();
		file.close();
		printMatrix();
	}
	
	private static void test() {
		 for (int linenr = 1; file.hasNextLine(); ++linenr){
			 double spamProb=0;
			 double nospamProb=0;
			 String line = file.nextLine();
	         String[] email = line.split (" ");
	         if (email.length > 0) {
	        	 for(int i=1; i<email.length; i++){
     				String[] words  = email[i].split ("\\:");
     				double probS=spam.get(Integer.parseInt(words[0]));
     				double probN=noSpam.get(Integer.parseInt(words[0]));
     				if(probS==0){
     					probS=1/(double)(spam.totalWords+noSpam.totalWords);
     				}
     				if(probN==0){
     					probN=1/(double)(spam.totalWords+noSpam.totalWords);
     				}
     				probS=Math.log(probS)/Math.log(2);
     				probN=Math.log(probN)/Math.log(2);
     				
     				probS=probS*Integer.parseInt(words[1]);
     				probN=probN*Integer.parseInt(words[1]);
     				spamProb=spamProb+probS;
     				nospamProb=nospamProb+probN;

     			}
	         }
	         //out.println("pob Spam: "+(spamProb+(Math.log(spam.prior)/Math.log(2))));
			 //out.println("pob NO Spam: "+(nospamProb+(Math.log(noSpam.prior)/Math.log(2))));
	         spamProb=spamProb+(Math.log(spam.prior)/Math.log(2));
			 nospamProb=nospamProb+(Math.log(noSpam.prior)/Math.log(2));
			 int clase=0;
			 if(spamProb>nospamProb) clase=-1;
			 else clase=1;
			 
			setMatrix(Integer.parseInt(email[0]),clase);
			 
		 }
		 
	}

	private static void setMatrix(int row, int col) {
		if(row==-1) row=0;
		if(col==-1) col=0;
		confusionMatrix[row][col]++;	
	}
	
	private static void printMatrix() {
		out.println("No Spams Classified CORRECT as No Spam: "+confusionMatrix[0][0]);
		out.println("No Spams Classified WRONG as Spam: "+confusionMatrix[0][1]);
		out.println("Spams Classified CORRECT as Spam: "+confusionMatrix[1][1]);
		out.println("Spams Classified WRONG as No Spam: "+confusionMatrix[1][0]);
		out.println("TOTAL Classified CORRECT: "+(confusionMatrix[0][0]+confusionMatrix[1][1]));
		out.println("TOTAL Classified WRONG: "+(confusionMatrix[0][1]+confusionMatrix[1][0]));
   }

	public static void readFile(String filename){
		try {
			LineNumberReader lineCounter = null;
			lineCounter = new LineNumberReader(new FileReader(filename));
			while ((lineCounter.readLine()) != null) { 
				continue;
			}
			nMails = lineCounter.getLineNumber();
			file = new Scanner (new File (filename));

	        for (int linenr = 1; linenr<=nMails*((float)training/100); ++linenr) {
	        	String line = file.nextLine();
	        	String[] email = line.split (" ");
	        	if (email.length > 0) {	
	        		nTraining++;
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
	        
		}catch (IOException error) {
	         //Misstage error de fitxer no trobat.
		}
	}

}
