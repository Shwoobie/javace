import java.util.*;
import java.io.*;

public class Symbol {
    public int dec_line;
    public int nesting_depth;
    public int assign_line;
    public String name = "";
    public Vector<Integer> used_on;

    public Symbol(int lineNumber, int depth, String string) {
        this.dec_line = lineNumber;
        this.nesting_depth = depth;
        this.name = string;
        this.used_on = new Vector<Integer>(10, 2);
    }
}