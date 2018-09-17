package po;

import java.util.ArrayList;
import java.util.Arrays;

public class Pl {
    protected int vars;
    protected int[] var_type;
    protected int num_restr;
    protected double[] func;
    protected ArrayList<ArrayList<Double>> restrictions;
    protected char[] restr_type;

    public Pl(int vars, int num_restr) {
        this.vars = vars;
        this.var_type = new int[vars];
        this.num_restr = num_restr;
        this.func = new double[vars+1];
        this.restrictions = new ArrayList<>();
        this.restr_type= new char[num_restr];
    }

    public int getVars() {
        return vars;
    }

    public int getNum_restr() {
        return num_restr;
    }

    public ArrayList<Double> getRestriction(int i) {
        return restrictions.get(i);
    }

    public int[] getVar_type() {
        return var_type;
    }

    public char[] getRestr_type() {
        return restr_type;
    }

    public void setVars(int vars) {
        this.vars = vars;
    }

    public void setVar_type(int i, int type) {
        this.var_type[i] = type;
    }

    public void setNum_restr(int num_restr) {
        this.num_restr = num_restr;
    }

    public void setFunc(int i, double val) {
        this.func[i] = val;
    }

    public double[] getFunc() {
        return func;
    }

    void printPl() {
        System.out.println(Arrays.toString(this.func));
        for(ArrayList<Double> restr:this.restrictions) System.out.println(restr);
    }

    public void setRestr_type(int i, char c) {
        this.restr_type[i] = c;
    }
}
