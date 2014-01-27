/* *** This file is given as part of the programming assignment. *** */

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    Symbol_table table;
    Symbol newSym = new Symbol(tok.lineNumber , table.depth, tok.string);//symbol object for making IDs to put in the symbol table after declaration
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
        block();
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
        table.sym_pop();
    }

    private void declarations() {
        mustbe(TK.VAR);
        while( is(TK.ID) ) {
            newSym.dec_line = tok.lineNumber;
            newSym.nesting_depth = table.depth;
            newSym.name = tok.string;
            (table.sym_top()).addSym(newSym);
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
        mustbe(TK.ID);
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
            newSym.dec_line = tok.lineNumber;
            newSym.nesting_depth = table.depth;
            newSym.name = tok.string;
            (table.sym_top()).checkSym(newSym);
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
}
