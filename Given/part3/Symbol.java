import java.util.*;
import java.io.*;

public class Symbol {
    public int dec_line;
    public int nesting_depth;
    public int assign_line;
    public String name;
    public Vector<Integer> used_on;

    public Symbol() {
        this.dec_line = 0;
        this.nesting_depth = 0;
        this.name = "";
        this.used_on = new Vector<Integer>(10, 2);
    }
}