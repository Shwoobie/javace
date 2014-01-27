import java.util.*;
import java.io.*;

public class Symbol_table {
   private Stack st;
	private Sym sym;
	private Token tok;
	public static depth = 0;

   static void sym_push() {
      st.push(new Vector());
      return;
   }

   static void sym_pop() {
      st.pop();
      return;
   }

   static void sym_top() {
      return st.peek();
   
   }

   public void checkSym(Symbol sym) {
      if (!compare(sym))){
         System.err.println( "undeclared variable " + sym.name + " on line " + tok.lineNumber);
         System.exit(1);
         }
      else 
         return;
   }

   public void addSym(Symbol newSym) {
      
      if (compare(newSym)){
         System.err.println( "variable " + sym.name + " is redeclared on line " + tok.lineNumber);
      }
      else{
         sym_top().addElement(newSym);
         return;
      }
   }


 
   public boolean compare(Symbol newSym) {//compares names in the vector 
      for(int i = 0; i < sym_top().size(); i++){
         if(newSym.name == (sym_top().get(i)).name)
            return 1;
      }
      return 0;
   }