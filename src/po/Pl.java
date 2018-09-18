package po;

import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Pl {
    protected int vars;
    protected int[] var_type;
    protected int num_restr;
    protected double[] func;
    protected ArrayList<ArrayList<Double>> restrictions;
    protected char[] restr_type;
    protected ArrayList<ArrayList<Double>> lp;
    protected ArrayList<Integer> canon;

    public Pl(int vars, int num_restr) {
        this.vars = vars;
        this.var_type = new int[vars];
        this.num_restr = num_restr;
        this.func = new double[vars+1];
        this.restrictions = new ArrayList<>();
        this.restr_type= new char[num_restr];
        this.lp = new ArrayList<>();
        this.canon = new ArrayList<>(num_restr);
    }

    public int getVars() {
        return vars;
    }

    public int getNum_restr() {
        return num_restr;
    }

    public ArrayList<Double> getRestriction(int i) {
        return lp.get(i+1);
    }

    public ArrayList<Double> getFunc() {return lp.get(0);}

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

    void printPl() {
        //System.out.println(Arrays.toString(this.func));
        for(ArrayList<Double> line:this.lp) System.out.println(line);
    }

    public void setRestr_type(int i, char c) {
        this.restr_type[i] = c;
    }

    public void nNegativity() {
        for(int i=0; i< this.vars; i++) {
            if(this.var_type[i] == 0) {
                insertNonNegative(i);
            }
        }
    }

    private void insertNonNegative(int i) {
        for(ArrayList<Double> line: this.lp) {
            line.add(i+1, -line.get(i));
        }
    }

    public void FPI() {
        for(int i=0; i< this.num_restr; i++) {
            if(this.restr_type[i] != '=') {
                addColumn(i+1, this.restr_type[i]);
                if(this.restr_type[i] == '<') {
                    this.canon.ensureCapacity(i+1);
                    this.canon.add(i, i);
                    //this.lp.set(i + 1, (ArrayList<Double>) this.lp.get(i + 1).stream().map(x -> x *= -1).collect(Collectors.toList()));
                }
            }
        }
        int x = isCanon();
        while(x != 333) {
            addColumn(x+1);
            this.canon.add(x,x);
            x = isCanon();
        }
    }

    private void addColumn(int i) {
        for(int j = 0;j < this.lp.size(); j++) {
            if(j==i) {
                this.lp.get(j).add(this.lp.get(j).size()-1, (double)1);
                continue;
            }
            this.lp.get(j).add(this.lp.get(j).size()-1, (double)0);
        }
    }

    private void addColumn(int i, char c) {
        double sign = c == '<' ? 1 : -1;
        for(int j = 0;j < this.lp.size(); j++) {
            if(j==i) {
                this.lp.get(j).add(this.lp.get(j).size()-1, sign);
                continue;
            }
            this.lp.get(j).add(this.lp.get(j).size()-1, (double)0);
        }
    }

    public int isCanon() {
        if(this.canon.size() != this.num_restr) {
            for(int i=0; i < this.num_restr; i++) {
                if(!this.canon.contains(i)) return i;
            }
        }
        return 333;
    }

    public boolean checkB() {
        for(ArrayList<Double> arr:this .lp) {
            if(arr.get(arr.size()-1) < 0) return false;
        }
        return true;
    }

    public Map.Entry<Integer, Integer> choose() {
        Map.Entry<Integer, Integer> p = null;
        boolean unb = true;
        for(int j=1; j< this.lp.get(0).size()-1; j++) {
            if(this.lp.get(0).get(j) < 0) {
                double choice = 9999999;
                for (int i=1; i <= this.num_restr; i++) {
                    double d = this.lp.get(i).get(j);
                    if(d > 0 && (this.lp.get(i).get(this.lp.get(i).size()-1)/d < choice)) {
                        p = Map.entry(i, j);
                        unb = false;
                    }
                }
                if(unb) return Map.entry(-11,-11);
                return p;
            }
        }
        return null;
    }

    public boolean checkC() {
        return !this.lp.get(0).stream().anyMatch(aDouble -> aDouble < 0);
    }

    public int solve() {
        this.lp.set(0, (ArrayList<Double>) this.lp.get(0).stream().map(x -> x *= -1).collect(Collectors.toList()));
        while(!checkC()) {
            Map.Entry<Integer, Integer> entry = choose();
            if(entry.getKey() == -11 && entry.getValue() == -11) return 1;
            pivot(entry.getKey(), entry.getValue());
        }
        return 2;
    }

    public void pivot(int i, int j) {
        double piv = this.lp.get(i).get(j);
        ArrayList<Double> arr = this.lp.get(i);
        for(double x: arr) {
            x /= piv;
        }
        piv = arr.get(j);
        for(int k=0; k< this.num_restr+1; k++) {
            double mult = -(this.lp.get(k).get(j));
            for(int m= 0; m< arr.size(); m++) {
                if(k==i || this.lp.get(i).get(m) == 0) continue;
                this.lp.get(k).set(m, this.lp.get(k).get(m) + (this.lp.get(i).get(m) * mult));
            }
        }
    }

}
