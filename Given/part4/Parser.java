import java.util.*;


/* *** This file is given as part of the programming assignment. *** */

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
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
    }

    private void program() {
        perm_table.sym_push();
        block();
        //print_st();
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
        while( is(TK.ID) ) {
            newSym = new Symbol(tok.lineNumber, table.depth, tok.string);
            table.addSym(newSym);
            perm_table.addSym(newSym);
            scan();
        }
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
        table.assign(newSym);
        scan();
        mustbe(TK.ASSIGN);
        expression();

    }

    private void print() {
        // you'll need to add some code here
        scan();
        expression();
    }

    private void if_fn() {
        // you'll need to add some code here
        scan();
        guarded_commands();
        mustbe(TK.FI);
    }

    private void do_fn() {
        // you'll need to add some code here
        scan();//skip do
        guarded_commands();
        mustbe(TK.OD);
    }

    private void fa() {
        // you'll need to add some code here
        scan();// skip fa
        if(is(TK.ID)){
        newSym = new Symbol(tok.lineNumber, table.depth, tok.string);
        table.assign(newSym);
        }
        mustbe(TK.ID);
        mustbe(TK.ASSIGN);
        expression();
        mustbe(TK.TO);
        expression();
        if (is(TK.ST)){
            scan();//skip ST
            expression();
        }
        commands();
        mustbe(TK.AF);
    }

    private void guarded_commands() {
        // you'll need to add some code here
        guarded_command();
        while(is(TK.BOX)){
            scan();
            guarded_command();
        }
        if (is(TK.ELSE)){
            scan();
            commands();
        }
    }

    private void guarded_command() {
        // you'll need to add some code here
        expression();
        commands();
    }

    private void commands() {
        // you'll need to add some code here
        mustbe(TK.ARROW);
        table.depth++;
        block();
    }

    private void expression() {
        // you'll need to add some code here
        simple();
        if(is(TK.NE) || is(TK.GE) || is(TK.LE) || is(TK.GT) || is(TK.LT) || is(TK.EQ)){
            relop();
            simple();
        }
    }

    private void simple() {
        // you'll need to add some code here
        term();
        while(is(TK.PLUS) || is(TK.MINUS)){
            addop();
            term();
        }
    }

    private void term() {
        // you'll need to add some code here
        factor();
        while(is(TK.TIMES) || is(TK.DIVIDE)){
            multop();
            factor();
        }
    }

    private void factor() {
        // you'll need to add some code here
        if(is(TK.LPAREN)){
            scan();
            expression();
            mustbe(TK.RPAREN);
        }
        else if(is(TK.ID)){ 
            newSym = new Symbol(tok.lineNumber, table.depth, tok.string);
            table.checkSym(newSym);
            scan();
        }
        else if(is(TK.NUM)){ scan();}
        else{parse_error("factor");}
    }

    private void relop() {
        // you'll need to add some code here
        scan();
    }

    private void addop() {
        // you'll need to add some code here
        scan();
    }

    private void multop() {
        // you'll need to add some code here
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
    /*private void print_st(){
        for(int i=0; i < perm_table.sym_top().size(); i++){
            System.err.print(perm_table.sym_top().get(i).name + "\n declared on line " + perm_table.sym_top().get(i).dec_line
                + " at nesting depth " + perm_table.sym_top().get(i).nesting_depth + "\n");
            if (!(perm_table.sym_top().get(i).assign_on.isEmpty())){
                System.err.print(" assigned to on: ");
                Vector<Integer> dup = new Vector<Integer>();
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
                    if (count > 1 && !dup.contains(perm_table.sym_top().get(i).assign_on.get(j))){
                        System.err.print(perm_table.sym_top().get(i).assign_on.get(j) + "(" + count+ ") ");
                    }
                    else {System.err.print(perm_table.sym_top().get(i).assign_on.get(j) + " ");}
                }// for j
                System.err.print("\n");
            }//if assign_on not empty
            else{
                System.err.print(" never assigned\n");
            }// else assign_on is empty

            if (!(perm_table.sym_top().get(i).used_on.isEmpty())){
                System.err.print(" used on: ");
                Vector<Integer> dup = new Vector<Integer>();
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

                    if (count > 1 && !dup.contains(perm_table.sym_top().get(i).used_on.get(j))){
                        System.err.print(perm_table.sym_top().get(i).used_on.get(j) + "(" + count+ ") ");
                    }
                    else {System.err.print(perm_table.sym_top().get(i).used_on.get(j) + " ");}
                }// for j
                System.err.print("\n");
            }//if used_on not empty
            else{
                System.err.print(" never used\n");
            }// else used_on is empty
        }// for print the symbols       
    }*/

}
