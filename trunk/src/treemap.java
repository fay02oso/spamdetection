

class treemap {

   int totalWords=0;
   double prior;
	
   class tree {
      int key;
      int value;
      double probability;
      tree left;
      tree right;
      tree parent;
   }
   tree root = null;

   public double get (int key) {
          return find(root, key);  
   }
   
   private double find(tree node, int key) {
          if(node==null) return 0;
          if(key<node.key) 
                  return find(node.left,key);
          else if (key>node.key) 
                  return find(node.right,key);
          else{
        	  return node.probability;
          }
   }

   public void put (int key, int value) {
          tree newNode=new tree();
          newNode.key=key;
          newNode.value=value;
          newNode.right=null;
          newNode.left=null;
          if(this.root==null){
                  this.root=newNode;
                  return;
          }
          insert(this.root,newNode); 
   }
   
   public void insert(tree node, tree newNode){
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
   
   public void findMaxFrequency(){
	   
   
   }
   
   

}
