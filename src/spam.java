import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Scanner;
import static java.lang.System.*;


public class spam {
	// Parameters
	public static boolean CORPORA=true; 			//Change the dataset between the corpora and the UAB
	public static boolean DEFAULT_PRIORS=false; 	//Use the default priors 80% spam
	public static boolean NAIVE_BAYES=false;		//Naive Bayes Algorithm
	public static boolean KNN=true;					//K-Nearest Neighbor Algorithm
	/***************************/
	
	public static String dataset="correus3.txt";
	//public static String datadir="pu_corpora_public/pu1/";
	public static String datadir="pu1_encoded/stop/";
	public static int nTrainingCorpora=1000;		// Number of examples for training in the Corpora Dataset
	
	public static int training=50;				//Just for the UAB dataset
	public static int test=100-training; 		//Just for the UAB dataset
	
	public static int nNeighbors=2;				//Number of neighbors to predict on
	private static boolean RISK=false;			//To consider that it is more costful to predict wrong
												//a nonspam email as spam
	public static boolean WEIGHTED=true;		//To use the weighted k-nearest neighbor
	
	public static int nTraining=0;
	public static int nTest=0;
	public static float correctRatio=0;
	public static float wrongRatio=0;
	
	public static treemap spam = new treemap();
	public static treemap noSpam = new treemap();
	public static treemap general = new treemap();
	
	public static int nNoSpam=0;
	public static int nSpam=0;
	public static int nMails=0;
	private static Scanner file;
	public static int[][] confusionMatrix = new int [2][2];
	public static int best=3;
	public static treemap[] messages= new treemap[2000];
	public static short[] labels = new short[2000];

	public static void main(String[] args) throws IOException {
		if(CORPORA) readDir(datadir);
		else readFile(dataset);
		
		if(DEFAULT_PRIORS && NAIVE_BAYES){
			noSpam.setPrior(0.2);
			spam.setPrior(0.8);
		}else if(NAIVE_BAYES){
			noSpam.setPrior((double)nNoSpam/(nNoSpam+nSpam));
			spam.setPrior((double)nSpam/(nNoSpam+nSpam));
		}
		
		if(NAIVE_BAYES){
			noSpam.addProbability(noSpam.root);
			spam.addProbability(spam.root);
		}
		
		if(CORPORA && NAIVE_BAYES) testNaiveCorpora();
		else if(NAIVE_BAYES) testNaiveUAB();
		else if(KNN) testKNN();
		file.close();
		printMatrix();
		 
	}
	
	private static void testNaiveUAB() {
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
		 nTraining=(int) nMails*training/100;
		 nTest=(int) nMails*test/100;
	}
	
	private static void testNaiveCorpora() {
		File directory = new File(datadir+"part10/");
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return !name.startsWith(".") && !name.contains("unused") && !name.contains("part10");
		    }
		};
		File filename[] = directory.listFiles(filter);
		for (int i = 0; i < filename.length; i++) {
			if(filename[i].isFile()){
				
				try {
					file = new Scanner (filename[i]);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				double spamProb=0;
				double nospamProb=0;
				nTest++;
				while(file.hasNext()){
					String line = file.nextLine();
			        String[] email = line.split (" ");
			        if (email.length > 0) {
			        	for(int j=1; j<email.length; j++){
			        		double probS=spam.get(Integer.parseInt(email[j]));
		    				double probN=noSpam.get(Integer.parseInt(email[j]));
		    				if(probS==0){
		    					probS=1/(double)(spam.totalWords+noSpam.totalWords);
		    				}
		    				if(probN==0){
		    					probN=1/(double)(spam.totalWords+noSpam.totalWords);
		    				}
		    				probS=Math.log(probS)/Math.log(2);
		    				probN=Math.log(probN)/Math.log(2);
		    				
		    				spamProb=spamProb+probS;
		    				nospamProb=nospamProb+probN;
			        	}
			        }
				}
		        //out.println("pob Spam: "+(spamProb+(Math.log(spam.prior)/Math.log(2))));
				//out.println("pob NO Spam: "+(nospamProb+(Math.log(noSpam.prior)/Math.log(2))));
		        spamProb=spamProb+(Math.log(spam.prior)/Math.log(2));
				nospamProb=nospamProb+(Math.log(noSpam.prior)/Math.log(2));
				int clase=0;
				if(spamProb>nospamProb) clase=-1;
				else clase=1;
				int label;
				if(filename[i].getName().contains("spmsg")){
					label=-1;
				}
				else label=1;
				setMatrix(label,clase);
			}
		}
		nTraining=nMails;
		training=(int) (((float)nTraining/(nMails+nTest))*100);
		test=(int) (((float)nTest/(nMails+nTest))*100);
	}
	
	private static void testKNN() {
		double[] distances=new double[nMails];
		int[] minimums=new int[nNeighbors];
		File directory = new File(datadir+"part10/");
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return !name.startsWith(".") && !name.contains("unused") && !name.contains("part10");
		    }
		};
		File filename[] = directory.listFiles(filter);
		for (int i = 0; i < filename.length; i++) {
			if(filename[i].isFile()){
				
				try {
					file = new Scanner (filename[i]);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				nTest++;
				treemap newMessage=new treemap();
				while(file.hasNext()) {
		        	String line = file.nextLine();
		        	int startIndex=0;
		        	if(line.isEmpty()) continue;
		        	if(line.startsWith("Subject:")) startIndex=1;
		        	String[] email = line.split (" ");
		        	if (email.length > 0) {	
		        		for(int j=startIndex; j<email.length; j++){
		        			newMessage.put(Integer.parseInt(email[j]), 1);  			    
		        		}
		        			
		        	}
		        		
		        }
				for(int j=0; j<nMails; j++){
					distances[j]=newMessage.euclidianDistance(messages[j]);
				}
				double finalClass;
				if(RISK) finalClass=-(int)nNeighbors/4;
				else finalClass=0;

				for(int j=0; j<nNeighbors;j++){
					int index=minValue(distances);
					if(WEIGHTED) finalClass=finalClass+((labels[index])*(-1)*(1/distances[index]));
					else  finalClass=finalClass+labels[index];
				}
				int clase;
				if(finalClass>=0) clase=1;
				else clase=-1;
				
				int label;
				if(filename[i].getName().contains("spmsg")){
					label=-1;
				}
				else{
					label=1;
				}
				setMatrix(label,clase);
			}
		}
		nTraining=nMails;
		training=(int) (((float)nTraining/(nMails+nTest))*100);
		test=(int) (((float)nTest/(nMails+nTest))*100);
	}

	private static int minValue(double[] distances) {
		double min = distances[0];
		int index=0;
		for (int i=0; i<distances.length; i++) {
			if (distances[i]<min && distances[i]>0) {
				min = distances[i];
				index=i;
			}
		}
		distances[index]=distances[index]*-1;
		
		return index;
	}

	private static void setMatrix(int row, int col) {
		if(row==-1) row=0;
		if(col==-1) col=0;
		confusionMatrix[row][col]++;	
	}
	
	private static void printMatrix() {
		correctRatio=(((float)confusionMatrix[0][0]+confusionMatrix[1][1])/nTest)*100;
		wrongRatio=(((float)confusionMatrix[0][1]+confusionMatrix[1][0])/nTest)*100;
		out.println("Number of examples for training: "+nTraining);
		out.println("Number of examples for testing: "+nTest);
		out.println();
		out.println("NonSpam Classified CORRECT: "+confusionMatrix[0][0]);
		out.println("NonSpam Classified WRONG as Spam: "+confusionMatrix[0][1]);
		out.println();
		out.println("Spams Classified CORRECT as Spam: "+confusionMatrix[1][1]);
		out.println("Spams Classified WRONG as No Spam: "+confusionMatrix[1][0]);
		out.println();
		out.println("TOTAL Classified CORRECT: "+(confusionMatrix[0][0]+confusionMatrix[1][1])+" - "+correctRatio +"%");
		out.println("TOTAL Classified WRONG: "+(confusionMatrix[0][1]+confusionMatrix[1][0])+" - "+wrongRatio+"%");
   }

	public static void readFile(String filename){
		try {
			LineNumberReader lineCounter = null;
			lineCounter = new LineNumberReader(new FileReader(filename));
			while ((lineCounter.readLine()) != null) { 
				continue;
			}
			nMails = lineCounter.getLineNumber();
			labels=new short[nMails];
			messages=new treemap[nMails];
			file = new Scanner (new File (filename));

	        for (int linenr = 0; linenr<=nMails*((float)training/100); ++linenr) {
	        	String line = file.nextLine();
	        	String[] email = line.split (" ");
	        	treemap treeMessage=new treemap();
	        	if (email.length > 0) {	
	        		if(Integer.parseInt(email[0]) == 1){
						//NO ES SPAM
	        			labels[linenr]=1;
	        			nNoSpam++;
	        			for(int i=1; i<email.length; i++){
	        				String[] words  = email[i].split ("\\:");
	        				noSpam.totalWords=noSpam.totalWords + Integer.parseInt(words[1]);
	        				noSpam.put(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
	        				general.totalWords=general.totalWords + Integer.parseInt(words[1]);
	        				general.put(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
	        			    treeMessage.put(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
	        				
	        			}
	        		}
	        		if(Integer.parseInt(email[0]) == -1){
	        			//ES SPAM
	        			labels[linenr]=-1;
	        			nSpam++;
	        			for(int i=1; i<email.length; i++){
	        				String[] words  = email[i].split ("\\:");
	        				spam.totalWords=spam.totalWords + Integer.parseInt(words[1]);
	        				spam.put(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
	        				general.totalWords=general.totalWords + Integer.parseInt(words[1]);
	        				general.put(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
	        				treeMessage.put(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
	        	
	        			}
	        		}
	        	} 
	        	messages[linenr]=treeMessage;
	        }
	        
		}catch (IOException error) {
	         //Misstage error de fitxer no trobat.
		}	
	}
	
	public static void readDir(String dirname) throws IOException{
		File directory = new File(dirname);
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return !name.startsWith(".") && !name.contains("unused") && !name.contains("part10");
		    }
		};
		File filename[] = directory.listFiles(filter);
		for (int i = 0; i < filename.length; i++) {
			if(filename[i].isFile()){
				readFile2(filename[i],nMails);
				++nMails;
				if(nMails>=nTrainingCorpora) return;
			}
			if(filename[i].isDirectory()) readDir(filename[i].getPath());
			
		}
	}

	private static void readFile2(File name, int n) {
		boolean isSpam=false;
    	treemap treeMessage=new treemap();
		try {
			file = new Scanner (name);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		if(name.getName().contains("spmsg")){
			nSpam++;
			labels[n]=-1;
			isSpam=true;
		}
		else{
			nNoSpam++;
			labels[n]=1;
		}
		while(file.hasNext()) {
        	String line = file.nextLine();
        	int startIndex=0;
        	if(line.isEmpty()) continue;
        	if(line.startsWith("Subject:")) startIndex=1;
        	String[] email = line.split (" ");
        	if (email.length > 0) {	
        		for(int j=startIndex; j<email.length; j++){
        			if(!isSpam){
        				noSpam.totalWords++;
        				noSpam.put(Integer.parseInt(email[j]), 1);
        				general.totalWords++;
        				general.put(Integer.parseInt(email[j]), 1);
        				treeMessage.put(Integer.parseInt(email[j]), 1);
        			}else{
        				spam.totalWords++;
        				spam.put(Integer.parseInt(email[j]), 1);
        				general.totalWords++;
        				general.put(Integer.parseInt(email[j]), 1);
        				treeMessage.put(Integer.parseInt(email[j]), 1);
        			}
    			    
        		}
        			
        	}
        		
        }
		messages[n]=treeMessage;
		
	}
}
