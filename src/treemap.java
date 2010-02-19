import static java.lang.System.*;

class treemap {

   int totalWords=0;
   double prior;
	
   class tree {
      int key;
      int value;
      double probability;
      tree left;
      tree right;
   }
   tree root = null;

   public double get (int key) {
          return found(root, key);  
   }
   
   private double found(tree node, int key) {
          if(node==null) return 0;
          if(key<node.key) 
                  return found(node.left,key);
          else if (key>node.key) 
                  return found(node.right,key);
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


	public void inorder(tree node){
           if(node==null) return;
           inorder(node.left);
           out.println(node.key);
           inorder(node.right);
   }
   public void postorder(tree node){
           if(node==null) return;
           postorder(node.left);
           postorder(node.right);
           out.println(node.key);
   }
   public void preorder(tree node){
           if(node==null) return;
           out.println(node.key);
           preorder(node.left);
           preorder(node.right);
   }
   

   public void debug_tree () {
      debug_tree_recur (root, 0);
   }
   

	private void debug_tree_recur (tree node, int depth) {
           if(node==null) return;
           debug_tree_recur(node.left, depth+1);
           String left="NULL";
           String right="NULL";
           if(node.left!=null) left=node.left.toString();
           if(node.right!=null) right=node.right.toString();
           out.printf("%3d \"%s\" \"%s\" %s %s%n",
               depth, node.key, node.value, left, right);
           debug_tree_recur(node.right, depth+1);
   }
   
   @SuppressWarnings("unused")
   private void debug_tree_recur_pre (tree node, int depth) {
           if(node==null) return;
           String left="NULL";
           String right="NULL";
           if(node.left!=null) left=node.left.toString();
           if(node.right!=null) right=node.right.toString();
           out.printf("%3d \"%s\" \"%s\" %s %s%n",
                depth, node.key, node.value, left, right);
           debug_tree_recur_pre(node.left, depth+1);
           debug_tree_recur_pre(node.right, depth+1);
   }
   
   @SuppressWarnings("unused")
   private void debug_tree_recur_post (tree node, int depth) {
           if(node==null) return;
           debug_tree_recur_post(node.left, depth+1);
           debug_tree_recur_post(node.right, depth+1);
           String left="NULL";
           String right="NULL";
           if(node.left!=null) left=node.left.toString();
           if(node.right!=null) right=node.right.toString();
           out.printf("%3d \"%s\" \"%s\" %s %s%n",
               depth, node.key, node.value, left, right);
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

}
