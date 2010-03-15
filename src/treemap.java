

public class treemap {

   int totalWords=0;
   double prior;
	
   public class tree {
      int key;
      int value;
      double probability;
      tree left;
      tree right;
   }
   tree root = null;

   public double get (int key) {
          return find(root, key);  
   }
   
   private double find(tree node, int key) {
          if(node==null) return 0;
          if(key<node.key) 
                  return find(node.left,key);
          if (key>node.key) 
                  return find(node.right,key);
          if (key==node.key){
        	  return node.probability;
          }
          return 0;
   }
   
   public int getValue (int key) {
       return findValue(root, key);  
   }

   private int findValue(tree node, int key) {
       if(node==null) return 0;
       if(key<node.key) 
               return findValue(node.left,key);
       else if (key>node.key) 
               return findValue(node.right,key);
       else{
     	  return node.value;
       }
}

   public void put (int key, int value) {
          tree newNode=new tree();
          newNode.key=key;
          newNode.value=value;
          newNode.right=null;
          newNode.left=null;
          if(root==null){
                  this.root=newNode;
                  return;
          }
          insert(this.root,newNode); 
   }
   
   private void insert(tree node, tree newNode){
       if (newNode.key<node.key) {
    	   if (node.left != null) {
    		   insert(node.left,newNode);
    	   } else {
    		   node.left=newNode;
    	   }
       } else if (newNode.key>node.key) {
    	   if (node.right != null) {
    		   insert(node.right,newNode);
    	   } else {
    		   node.right = newNode;
    	   }
       } else{
    	   node.value=node.value+newNode.value;
       }

   }
   
   public void addProbability(tree node) {
	   if(node==null) return;
	   addProbability(node.left);
       node.probability=(double)node.value/(double)this.totalWords;
       addProbability(node.right);	
   }

   public void setPrior(double prior) {
	   this.prior=prior;
   }
   
   public int findMaxIndex(tree node){
	   tree curr=new tree();
	   int max=0;
	   for(curr=node;curr!=null;curr=curr.right){
		   if(max<curr.key) max=curr.key;
		   
	   }
	   return max;
	   
   }
   
   public int findMaxFrequency(tree node,int max, int[] top){
	   if(node!=null){
	   
	       if(node.value>max && !checkMax(max, top)){
	    	   max=node.key;
	    	   node.value=node.value*-1;
	       }
	       max=findMaxFrequency(node.left, max, top);
		   max=findMaxFrequency(node.right,max, top);
	   }
	   return max;
   }

   private boolean checkMax(int max, int[] top) {
	   boolean found=false;
	   for(int i=0; i<top.length; i++){
		   if(top[i]==max && top[i]!=0) found=true;
	   }
	   return found;
   }
   
   
   public void printTree() {
	   printTreeRecursive(this.root);	
   }
   private void printTreeRecursive(tree node){
	   if(node==null) return;
	   printTreeRecursive(node.left);
       System.out.println("Key: "+node.key+" -> Value: "+ node.value);
       printTreeRecursive(node.right);
   }
   
   public double euclidianDistance(treemap trainingMessage){
	   return Math.sqrt(euclidianDistanceRec(this.root,trainingMessage));
	   
   }

   private double euclidianDistanceRec(tree node, treemap training) {
	   if(node==null) return 0;
	   double distance=Math.pow(node.value-training.getValue(node.key),2);
	   return (double)euclidianDistanceRec(node.left,training)+
	          (double)euclidianDistanceRec(node.right,training)+
	   		  distance;
   }
   
   

}
