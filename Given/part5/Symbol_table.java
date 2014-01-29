import java.util.*;
import java.io.*;

public class Symbol_table {
   private Stack<Vector<Symbol>> st = new Stack<Vector<Symbol>>();
   private Stack<Vector<Symbol>> tempTable = new Stack<Vector<Symbol>>();
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
       //  System.err.println(st.empty());
         return;
      }

   }

   public Vector<Symbol> sym_top() {
      if(!st.empty()){
         return st.peek();
      }
      else  
      //   System.err.println("did you go into the else loop? you shouldnt fuck you");
         return new Vector<Symbol>();
   }

   public void checkSym(Symbol sym) {
     // System.err.println( "variable " + sym.name + " linenumber: " + sym.dec_line + "address:" + sym);
      if (compare(sym) == false){
         System.err.println( "undeclared variable " + sym.name + " on line " + sym.dec_line);
         System.exit(1);
         }
      else {
         return;
      }
   }


    public void assign_check(Symbol sym) {
     // System.err.println( "variable " + sym.name + " linenumber: " + sym.dec_line + "address:" + sym);
      if (assign(sym) == false){
         System.err.println( "undeclared variable " + sym.name + " on line " + sym.dec_line);
         System.exit(1);
         }
      else {

         return;
      }
   }

   public boolean addSym(Symbol newSym) {
      if (shallow_compare(newSym)){ //check if it is redeclared
        System.err.println( "variable " + newSym.name + " is redeclared on line " + newSym.dec_line);
        return false;
      }
      else{
         if(!st.empty()){
            sym_top().addElement(newSym);
            
         }
      }

      return true;
   }

   public boolean shallow_compare(Symbol newSym) {
      for(int i = 0; (i < sym_top().size()); i++){
       //  System.err.println( "current name " + newSym.name + " itt name " + (sym_top().get(i)).name);
         if((newSym.name).equals((sym_top().get(i)).name)){
        //    System.err.println( "RETURN TRUE");
            return true;
         }
      }
      return false;
   }


   public boolean compare(Symbol newSym) {//compares names in the vector 
   //   System.err.println(sym_top().size());
      for(int i = 0; (i < sym_top().size()); i++){
       //  System.err.println( "current name " + newSym.name + " itt name " + (sym_top().get(i)).name);
         if((newSym.name).equals((sym_top().get(i)).name)){
          //  System.err.println( "RETURN TRUE");
            (sym_top().get(i)).used_on.addElement(newSym.dec_line);
            System.err.print(" x_"+sym_top().get(i).name);//******************
            return true;
         }
       //  System.err.println( "CURRFALSE NAME " + newSym.name + " ITTFALSE NAME " + (sym_top().get(i)).name);
      }

      if (newSym.nesting_depth > 0){

         Symbol tempSym = new Symbol(newSym.dec_line, newSym.nesting_depth, newSym.name);
         //Stack<Vector<Symbol>> st = new Stack<Vector<Symbol>>(st);
         //st = st;
         if(!st.empty()){
            do{
               for(int i = 0; i < (st.peek()).size(); i++){
           //    System.err.println( "CURRENT NAME " + newSym.name + " ITT NAME " + (sym_top().get(i)).name);
                  if((tempSym.name).equals((st.peek()).get(i).name)){
           //       System.err.println( "RETURN TRUE2");
                    (st.peek()).get(i).used_on.addElement(newSym.dec_line);
                     while(!tempTable.empty()){
                        st.push(tempTable.pop());
                //     System.err.println(tempTable.empty());
                     } //repopulate the original stack
                     
                     return true;

                  }
          //     System.err.println( "CURRFALSE2 NAME " + newSym.name + " ITTFALSE2 NAME " + (sym_top().get(i)).name);
               }//for
               
              // System.err.println(tempTable.empty());
               if(tempSym.nesting_depth != 0){
                  tempTable.push(st.pop()); // transfer the stack contents to tempTable
               }
               tempSym.nesting_depth--;
                  
            }while(tempSym.nesting_depth >= 0);

            while(!tempTable.empty()){
               st.push(tempTable.pop());
              // System.err.println(tempTable.empty());
             } //repopulate the original stack
      }
      }
    //  System.err.println( "RETURN FALSE");
      return false;

   }

   public boolean assign(Symbol newSym) {//compares names in the vector used by assign
   //   System.err.println(sym_top().size());
      for(int i = 0; (i < sym_top().size()); i++){
       //  System.err.println( "current name " + newSym.name + " itt name " + (sym_top().get(i)).name);
         if((newSym.name).equals((sym_top().get(i)).name)){
          //  System.err.println( "RETURN TRUE");
            (sym_top().get(i)).assign_on.addElement(newSym.dec_line);
            System.err.print("x_"+sym_top().get(i).name + " = ");//********** 
            //add the line number to the used_on vector 
            return true;
         }
       //  System.err.println( "CURRFALSE NAME " + newSym.name + " ITTFALSE NAME " + (sym_top().get(i)).name);
      }

      if (newSym.nesting_depth > 0){

         Symbol tempSym = new Symbol(newSym.dec_line, newSym.nesting_depth, newSym.name);
         //Stack<Vector<Symbol>> st = new Stack<Vector<Symbol>>(st);
         //st = st;
         if(!st.empty()){
            do{
               for(int i = 0; i < (st.peek()).size(); i++){
           //    System.err.println( "CURRENT NAME " + newSym.name + " ITT NAME " + (sym_top().get(i)).name);
                  if((tempSym.name).equals((st.peek()).get(i).name)){
           //       System.err.println( "RETURN TRUE2");
                    st.peek().get(i).assign_on.addElement(newSym.dec_line);
                    while(!tempTable.empty()){
                      st.push(tempTable.pop());
                //     System.err.println(tempTable.empty());
                    } //repopulate the original stack
                     
                    return true;

                  }
          //     System.err.println( "CURRFALSE2 NAME " + newSym.name + " ITTFALSE2 NAME " + (sym_top().get(i)).name);
               }
               
              // System.err.println(tempTable.empty());
               if(tempSym.nesting_depth != 0){
                  tempTable.push(st.pop()); // transfer the stack contents to tempTable
               }
               tempSym.nesting_depth--;
                  
            }while(tempSym.nesting_depth >= 0);

            while(!tempTable.empty()){
               st.push(tempTable.pop());
              // System.err.println(tempTable.empty());
             } //repopulate the original stack
      }
      }
    //  System.err.println( "RETURN FALSE");
      return false;

   }
}
   