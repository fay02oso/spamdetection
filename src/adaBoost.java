
public class adaBoost {

	public static int T=500;											//Number of rounds
	public static double et;
	public static int m;
	int[] ht= new int[T];
	double[] alphat= new double[T];
	int k=0;
	
	public void run(treemap[] messages, short[] labels, int nMails){
		double[][] D=new double [T+1][messages.length]; 					//Distribution array (array of weights)
		m=nMails; 	
		
		
		int[] weakLearners = spam.getWeekLearners();
		
		for(int i=0; i<m; i++) D[0][i]=(float)1/m;
		
		for(int t=0;t<T;t++){
			ht[t] = weakLearner(messages,labels,D[t], weakLearners);
			alphat[t]=(float) Math.log((float)((1-et)/et));
			double Zt=0;
			for(int i=0;i<m;i++){
				int target;
				if(messages[i].getValue(weakLearners[ht[t]])>k) target=-1;
				else target=1;
				D[t+1][i]=D[t][i]*Math.exp((double)((-alphat[t])*labels[i]*target));
				
				Zt=Zt+D[t+1][i];
			}
			for(int i=0;i<m;i++){
				D[t+1][i]=(double)D[t+1][i]/Zt;
			}
		}
		for(int t=0;t<T;t++){
			ht[t]=weakLearners[ht[t]];
		}
	}

	private int weakLearner(treemap[] messages,short[] labels, double[] d, int[] weakLearners) {
		double[] results=new double[weakLearners.length];
		for(int j=0; j<weakLearners.length; j++){
			for(int i=0; i<m; i++){
				int target;
				if(messages[i].getValue(weakLearners[j])>k) target=-1;
				else target=1;
				if(labels[i]!=target){
					results[j]+=d[i];
				}
			}
			
		}
		int index=minError(results);
		et=results[index];
		return index;
	}

	private int minError(double[] results) {
		double min=results[0];
		int index=0;
		for (int i=0; i<results.length; i++) {
			if (results[i]<min) {
					min = results[i];
					index=i;
			}
		}		
		return index;
	}

	public int classify(treemap newMessage) {
		double sum=0;
		for(int t=0; t<T; t++){
			int target;
			if(newMessage.getValue(ht[t])>k) target=-1;
			else target=1;
			sum=sum+(alphat[t]*target);
		}
		if(sum>0) return 1;
		else return -1;
	}
}
