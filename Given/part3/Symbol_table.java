import java.util.*;
import java.io.*;

public class Symbol_table {
   private Stack<Vector<Symbol>> st = new Stack<Vector<Symbol>>();
	private Symbol sym;
	private Token tok;
	public int depth = 0;

   public void sym_push() {
      Vector<Symbol> v = new Vector<Symbol>();
      st.push(v);
      return;
   }

   public void sym_pop() {
      if(!st.empty()){
         st.pop();
         return;
      }
   }

   public Vector<Symbol> sym_top() {
      if(!st.empty()){
         return st.peek();
      }
      else  
         return new Vector<Symbol>();
   }

   public void checkSym(Symbol sym) {
      if (compare(sym) == false){
         System.err.println( "undeclared variable " + sym.name + " on line " + sym.dec_line);
         System.exit(1);
         }
      else {
         return;
      }
   }

   public void addSym(Symbol newSym) {
      if (compare(newSym)){
         System.err.println( "variable " + newSym.name + " is redeclared on line " + newSym.dec_line);
      }
      else{
         if(!st.empty()){
            sym_top().addElement(newSym);
            return;
         }
      }
   }

   public boolean compare(Symbol newSym) {//compares names in the vector 
      for(int i = 0; i < sym_top().size(); i++){
         System.err.println( "current name " + newSym.name + " itt name " + (sym_top().get(i)).name);
         if((newSym.name).equals((sym_top().get(i)).name)){
            System.err.println( "RETURN TRUE");
            return true;
         }
      }

      if (newSym.nesting_depth > 0){
         Symbol tempSym = new Symbol(newSym.dec_line, newSym.nesting_depth, newSym.name);
         Stack<Vector<Symbol>> tempSt = new Stack<Vector<Symbol>>();
         tempSt = st;
         if(!tempSt.empty()){
            do{
               for(int i = 0; i < (tempSt.peek()).size(); i++){
               System.err.println( "CURRENT NAME " + newSym.name + " ITT NAME " + (sym_top().get(i)).name);
               if((tempSym.name).equals((tempSt.peek()).get(i).name)){
                  System.err.println( "RETURN TRUE2");
                  return true;
               }
               }
               tempSt.pop();
               tempSym.nesting_depth--;
            }while(tempSym.nesting_depth > 0);
      }

      }
      System.err.println( "RETURN FALSE");
      return false;

   }
}
   