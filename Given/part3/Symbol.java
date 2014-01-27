import java.util.*;
import java.io.*;

public class Symbol {
    public int dec_line;
    public int nesting_depth;
    public int assign_line;
    public String name;
    public Vector used_on;

    public Symbol(int dec_line, int nesting_depth, String name) {
        this.dec_line = dec_line;
        this.nesting_depth = nesting_depth;
        this.name = name;
        this.used_on = new Vector<int>(10, 2);
    }
}