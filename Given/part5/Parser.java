import java.util.*;


/* *** This file is given as part of the programming assignment. *** */

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    Vector<String> vec = new Vector<String>(); // forsuch that
    Symbol_table table = new Symbol_table();
    Symbol_table perm_table = new Symbol_table(); 
    Symbol newSym;//symbol object for making IDs to put in the symbol table after declaration
    private void scan() {
        tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
        this.scanner = scanner;
        scan();
        program();
        if( tok.kind != TK.EOF )
            parse_error("junk after logical end of program");
        print_st();//********** TEMPORARILY TAKING THIS OUT FOR TESTING
    }

    private void program() {
        perm_table.sym_push();
        System.err.println("#include<stdio.h>\nmain(){");//**********
        block();
        System.err.println("}");//**********
    }

    private void block() {
        // you'll need to add some code here
        table.sym_push();
        
        if (is(TK.VAR)){
            declarations();
        }
        if (is(TK.ID) || is(TK.PRINT) || is(TK.IF) || is(TK.DO) || is(TK.FA)){
            statement_list();
        }
        table.depth--;
        table.sym_pop();
    }

    private void declarations() {
        mustbe(TK.VAR);
        //System.err.println("int");//********** 
        while( is(TK.ID) ) {
            newSym = new Symbol(tok.lineNumber, table.depth, tok.string);
            if(table.addSym(newSym)){
                System.err.println("int x_"+ table.sym_top().lastElement().name+" = -12345;");//********** 
                perm_table.sym_top().addElement(newSym);
            }
            scan();
        }
        //System.err.println(";");//********** 
        mustbe(TK.RAV);

    }

    private void statement_list() {
        // you'll need to add some code here
        while(is(TK.ID)|| is(TK.PRINT) || is(TK.IF) || is(TK.DO) || is(TK.FA)){
           statement();  
        }//end while
    }

    private void statement() {
        // you'll need to add some code here
        if (is(TK.ID)){ assignment();}
        else if(is(TK.PRINT)){ print(); }
        else if (is(TK.IF)){if_fn();}
        else if (is(TK.DO)){do_fn();}
        else if (is(TK.FA)){fa();}  
        else parse_error("expected a statement");
    }

    private void assignment() {
        // you'll need to add some code here
        newSym = new Symbol(tok.lineNumber, table.depth, tok.string);
       // System.err.println( "variable " + newSym.name + " linenumber: " + newSym.dec_line + "address:" + newSym);
        table.assign_check(newSym); //print in the assign
        //table.checkSym(newSym);
        scan();
        mustbe(TK.ASSIGN);
        expression();
        System.err.println(";");//********** 

    }

    private void print() {
        // you'll need to add some code here
        System.err.print("printf(\"%d\\n\",");//********** 
        scan();
        expression();
        System.err.println(");");//********** 
    }

    private void if_fn() {
        // you'll need to add some code here
        scan();//skip if
        System.err.print("if(");//********** 
        guarded_commands();
        mustbe(TK.FI);
        //System.err.println("\n}");//********** 
    }

    private void do_fn() { // while loop
        // you'll need to add some code here
        scan();//skip do
        System.err.print("while(");//********** 
        guarded_commands_do();
        mustbe(TK.OD);
    }

    private void fa() {
        // you'll need to add some code here
        scan();// skip fa
        System.err.print("for(");//********** 
        if(is(TK.ID)){
        newSym = new Symbol(tok.lineNumber, table.depth, tok.string);
        table.assign_check(newSym);
        //table.checkSym(newSym);
        }
        String id_name = new String(tok.string);//******************
        mustbe(TK.ID); 
        //System.err.print(" =");//********** 
        mustbe(TK.ASSIGN);
        expression();
        mustbe(TK.TO);
        System.err.print("; x_"+id_name + " <=");//********** 
        expression();
        vec.clear();//***************
        if (is(TK.ST)){
           // System.err.print(" ||");//********** 
            scan();//skip ST

            System.err.print("&& (1 ||");//********** 
            expression();
            System.err.print(")");//********** 
        }
        System.err.print("; x_"+ id_name +"++)");//********** 
        command_for();//***************was prev commands()
        mustbe(TK.AF);
    }

    private void guarded_commands() {
        // you'll need to add some code here
        guarded_command();
        while(is(TK.BOX)){ // else if
            scan();
            System.err.print("else if(");//********** 
            guarded_command();
        }
        if (is(TK.ELSE)){
            scan();
            System.err.println("else");//********** 
            commands();
        }
    }
    private void guarded_commands_do() { //******************
        // you'll need to add some code here
        guarded_command();
        while(is(TK.BOX)){ // else if
            scan(); // skip box
            System.err.print("while(");//********** 
            guarded_command();
        }
        if (is(TK.ELSE)){
            scan();
            System.err.println("while(true)");//********** 
            commands();
        }
    }

    private void guarded_command() {
        // you'll need to add some code here
        
        expression();
        System.err.println(")");//********** 
        commands();
    }

    private void commands() {
        // you'll need to add some code here
        mustbe(TK.ARROW);
        System.err.println("{");//********** 
        table.depth++;
        block();
        System.err.println("}");//********** 
    }

    private void command_for() {
        // you'll need to add some code here
        mustbe(TK.ARROW);
        System.err.println("{");//********** 
        System.err.print("if(0 ==");//***********
        for(int i=0; i < vec.size(); i++){//*************
            System.err.print(vec.get(i));//**********
        }//**********
        System.err.print(") continue;");//**********
        vec.clear();//**********     
        table.depth++;
        block();
        System.err.println("}");//********** 
    }

    private void expression() {
        // you'll need to add some code here
        //System.err.print(" (");//********** 
        simple();
        if(is(TK.NE) || is(TK.GE) || is(TK.LE) || is(TK.GT) || is(TK.LT) || is(TK.EQ)){
            relop();
            simple();
        }
        //System.err.print(" )");//********** 
    }

    private void simple() {
        // you'll need to add some code here
        System.err.print(" (");//******************
        vec.addElement(" (");//*************************
        term();
        while(is(TK.PLUS) || is(TK.MINUS)){
            addop();
            term();
        }
        System.err.print(" )");//******************
        vec.addElement(")");//*************************
    }

    private void term() {
        // you'll need to add some code here
        System.err.print(" (");//********** 
        vec.addElement("(");//*************************
        factor();
        while(is(TK.TIMES) || is(TK.DIVIDE)){
            multop();
            factor();
        }
        System.err.print(" )");//********** 
        vec.addElement(")");//*************************
    }

    private void factor() {
        // you'll need to add some code here
        if(is(TK.LPAREN)){
            System.err.print(" (");//********** 
            vec.addElement("(");//*************************
            scan();
            expression();
            System.err.print(" )");//********** 
            vec.addElement(")");//*************************
            mustbe(TK.RPAREN);
        }
        else if(is(TK.ID)){ 
            newSym = new Symbol(tok.lineNumber, table.depth, tok.string);
            table.checkSym(newSym); //prints in the compare
            vec.addElement(tok.string);//*************************
            scan();
        }
        else if(is(TK.NUM)){ 
            System.err.print(" "+tok.string);//**********
            vec.addElement(tok.string);//*************************
            scan();
             
        }
        else{parse_error("factor");}
    }

    private void relop() {
        // you'll need to add some code here
        if(is(TK.NE)) {System.err.print(" !=");
            vec.addElement("!=");//*************************
        }//********** 
        else if(is(TK.EQ)) {System.err.print(" ==");
            vec.addElement(" ==");//*************************
        }//&************
        else if(is(TK.GT)) {System.err.print(" >");
            vec.addElement(">");//*************************
        }//********** 
        else if(is(TK.LT)) {System.err.print(" <");
            vec.addElement("<");//*************************
        }//********** 
        else if(is(TK.GE)) {System.err.print(" >=");
            vec.addElement(" >=");//*************************
        }//**********
        else if(is(TK.LE)) {System.err.print(" <=");
            vec.addElement("<=");//*************************
        }//**********  
        scan();
    }

    private void addop() {
        // you'll need to add some code here
        if(is(TK.MINUS)) {System.err.print(" -");
            vec.addElement(" -");//*************************
        }//**********
        if(is(TK.PLUS)) {System.err.print(" +");
            vec.addElement(" +");//*************************
        }//**********  
        scan();
    }

    private void multop() {
        // you'll need to add some code here
        if(is(TK.TIMES)) {System.err.print(" *");
            vec.addElement(" *");//*************************
        }//********** 
        if(is(TK.DIVIDE)) {System.err.print(" /");
            vec.addElement(" /");//*************************
        }//********** 
        scan();
    }
    // you'll need to add a bunch of methods here

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
        if( ! is(tk) ) {
            System.err.println( "mustbe: want " + tk + ", got " +
                                    tok);
            parse_error( "missing token (mustbe)" );
        }
        scan();
    }

    private void parse_error(String msg) {
        System.err.println( "can't parse: line "
                            + tok.lineNumber + " " + msg );
        System.exit(1);
    }
    private void print_st(){    
        for(int i=0; i < perm_table.sym_top().size(); i++){
            System.err.println(perm_table.sym_top().get(i).name) ;
            System.err.println("  declared on line " + perm_table.sym_top().get(i).dec_line
                + " at nesting depth " + perm_table.sym_top().get(i).nesting_depth);
            if (!(perm_table.sym_top().get(i).assign_on.isEmpty())){
                System.err.print("  assigned to on:");
                Vector<Integer> dup = new Vector<Integer>();
                Vector<Integer> printed = new Vector<Integer>();
                for (int j = 0; j < perm_table.sym_top().get(i).assign_on.size(); j++ ){
                    int count = 0;
                    for(int k = 0; k < perm_table.sym_top().get(i).assign_on.size(); k++){
                        if(perm_table.sym_top().get(i).assign_on.get(j) == perm_table.sym_top().get(i).assign_on.get(k)){
                            count++;
                        }
                        if (count > 1){
                            dup.addElement(perm_table.sym_top().get(i).assign_on.get(j));
                        }
                    }// for k
                    if (count > 1 && dup.contains(perm_table.sym_top().get(i).assign_on.get(j)) &&
                            !printed.contains(perm_table.sym_top().get(i).assign_on.get(j))){
                        printed.addElement(perm_table.sym_top().get(i).assign_on.get(j));
                        System.err.print(" "+perm_table.sym_top().get(i).assign_on.get(j) + "(" + count+ ")");
                    }
                    if(count == 1) {
                        System.err.print(" " + perm_table.sym_top().get(i).assign_on.get(j));
                        printed.addElement(perm_table.sym_top().get(i).assign_on.get(j));
                    }
                }// for j
                System.err.println();
            }//if assign_on not empty
            else{
                System.err.println("  never assigned");
            }// else assign_on is empty

            if (!(perm_table.sym_top().get(i).used_on.isEmpty())){
                System.err.print("  used on:");
                Vector<Integer> dup = new Vector<Integer>();
                Vector<Integer> printed = new Vector<Integer>();
                for (int j = 0; j < perm_table.sym_top().get(i).used_on.size(); j++ ){
                    int count = 0;
                    for(int k = 0; k < perm_table.sym_top().get(i).used_on.size(); k++){
                        if(perm_table.sym_top().get(i).used_on.get(j) == perm_table.sym_top().get(i).used_on.get(k)){
                            count++;
                        }
                        if (count > 1){
                            dup.addElement(perm_table.sym_top().get(i).used_on.get(j));
                        }
                    }// for k

                    if (count > 1 && dup.contains(perm_table.sym_top().get(i).used_on.get(j)) &&
                            !printed.contains(perm_table.sym_top().get(i).used_on.get(j))){
                        printed.addElement(perm_table.sym_top().get(i).used_on.get(j));
                        System.err.print(" "+perm_table.sym_top().get(i).used_on.get(j) + "(" + count+ ")");
                    }
                    if(count == 1) {
                        System.err.print(" " + perm_table.sym_top().get(i).used_on.get(j));
                        printed.addElement(perm_table.sym_top().get(i).used_on.get(j));
                    }
                }// for j
                System.err.println();
            }//if used_on not empty
            else{
                System.err.println("  never used");
            }// else used_on is empty
        }// for print the symbols       
    }//*/

}
