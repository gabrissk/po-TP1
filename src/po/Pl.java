package po;

import java.util.Arrays;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Pl {
    protected int vars;
    protected int[] var_type;
    protected int num_restr;
    protected ArrayList<Double> func;
    protected ArrayList<ArrayList<Double>> restrictions;
    protected char[] restr_type;
    protected ArrayList<ArrayList<Double>> lp;
    protected int[] canon;
    protected  int basic;

    public Pl(int vars, int num_restr) {
        this.vars = vars;
        this.var_type = new int[vars];
        this.num_restr = num_restr;
        this.func = new ArrayList<>(vars+1);
        this.restrictions = new ArrayList<>();
        this.restr_type= new char[num_restr];
        this.lp = new ArrayList<>();
        this.canon = new int[num_restr+1];
        Arrays.fill(this.canon, -1);
        this.basic = 0;
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
        this.func.add(i, val);
    }

    void printPl(ArrayList<ArrayList<Double>> lp) {
        //System.out.println(Arrays.toString(this.func));
        for(ArrayList<Double> line:lp) System.out.println(line);
        System.out.println();
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
        for(int i=1; i<= this.num_restr; i++) {
            if(this.lp.get(i).get(this.lp.get(0).size()-1) < 0) {
                this.lp.set(i, (ArrayList<Double>) this.lp.get(i).stream().map(x -> x *= -1).collect(Collectors.toList()));
                if(this.restr_type[i-1] == '<') this.restr_type[i-1] = '>';
                else if(this.restr_type[i-1] == '>') this.restr_type[i-1] = '<';
            }
            if(this.restr_type[i-1] != '=') {
                addColumn(i, this.restr_type[i-1]);
                if(this.restr_type[i-1] == '<') {
                    this.basic++;
                    this.canon[i] = i;
                    //this.lp.set(i + 1, (ArrayList<Double>) this.lp.get(i + 1).stream().map(x -> x *= -1).collect(Collectors.toList()));
                }
            }
        }
        /*int x = isCanon();
        while(x != 333) {
            //addColumn(x+1);
            chooseBaseVar();
            this.canon.add(x,x);
            x = isCanon();
        }*/
    }

    private void chooseBaseVar() {
        for(int j=0; j< this.lp.get(0).size()-1; j++) {
            for(int i=1; i < this.num_restr+1; i++) {
                if(this.lp.get(i).get(j) != 0) {
                    pivot(this.lp, i, j);
                    break;
                }
            }
        }
    }

    private void addColumn(ArrayList<ArrayList<Double>>lp, int i) {
        for(int j = 0;j < lp.size(); j++) {
            if(j==i) {
                lp.get(j).add(lp.get(j).size()-1, (double)1);
                continue;
            }
            lp.get(j).add(lp.get(j).size()-1, (double)0);
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

    /*public int isCanon() {
        if(this.canon.size() != this.num_restr) {
            for(int i=0; i < this.num_restr; i++) {
                if(!this.canon.contains(i)) return i;
            }
        }
        return 333;
    }*/

    public boolean checkB() {
        for(ArrayList<Double> arr:this .lp) {
            if(arr.get(arr.size()-1) < 0) return false;
        }
        return true;
    }

    public Map.Entry<Integer, Integer> choose(ArrayList<ArrayList<Double>> lp) {
        Map.Entry<Integer, Integer> p = null;
        boolean unb = true;
        for(int j=0; j< lp.get(0).size()-1; j++) {
            if(lp.get(0).get(j) < 0) {
                double choice = 9999999;
                for (int i=1; i <= this.num_restr; i++) {
                    double d = lp.get(i).get(j);
                    if(d > 0 && (lp.get(i).get(lp.get(i).size()-1)/d < choice)) {
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

    public boolean checkC(ArrayList<ArrayList<Double>> lp) {
        for(int j = 0; j < lp.get(0).size()-1; j++)
            if(lp.get(0).get(j) < 0) return false;
        //return lp.get(0).stream().noneMatch(aDouble -> aDouble < 0);
        return true;
    }

    public int solve(ArrayList<ArrayList<Double>> lp) {
        lp.set(0, (ArrayList<Double>) lp.get(0).stream().map(x -> x *= -1).collect(Collectors.toList()));
        while(!checkC(lp)) {
            Map.Entry<Integer, Integer> entry = choose(lp);
            if(entry.getKey() == -11 && entry.getValue() == -11) return 1;
            System.out.println(entry);
            pivot(lp, entry.getKey(), entry.getValue());
        }
        return 2;
    }

    public void pivot(ArrayList<ArrayList<Double>> lp, int i, int j) {
        System.out.println("antes");
        printPl(lp);
        double piv = lp.get(i).get(j);
        ArrayList<Double> arr = lp.get(i);
        for(int x=0; x < arr.size();x++) {
            arr.set(x, arr.get(x) / piv);
        }
        piv = arr.get(j);
        for(int k=0; k< this.num_restr+1; k++) {
            double mult = -(lp.get(k).get(j))/piv;
            for(int m= 0; m< arr.size(); m++) {
                if(k==i || lp.get(i).get(m) == 0) continue;
                lp.get(k).set(m, lp.get(k).get(m) + (lp.get(i).get(m) * mult));
            }
        }
        System.out.println("dps");
        printPl(lp);
    }

    public boolean hasBasicSol() {
        return this.basic == this.num_restr;
    }

    @SuppressWarnings("unchecked")
    public void auxLp() {
        ArrayList<ArrayList<Double>> aux = (ArrayList<ArrayList<Double>>) this.lp.clone();
        aux.set(0, (ArrayList<Double>) aux.get(0).stream().map(x -> x = 0.0).collect(Collectors.toList()));
        int cans = this.num_restr - this.basic;
        for(int i=0; i < this.num_restr; i++) {
            if(this.canon[i] != -1) {
                //this.canon[i] = i;
                continue;
            }
            addColumn(aux, i+1);
            aux.get(0).set(aux.get(0).size()-2, 1.0);
            this.canon[i] = i;

        }
        printPl(aux);
        for(int i = 1; i<= this.num_restr; i++) {
            for(int j = 0; j< aux.get(i).size(); j++) {
                //aux.set(0, (ArrayList<Double>) this.lp.get(0).stream().map(x -> x = 0.0).collect(Collectors.toList()));
                aux.get(0).set(j, aux.get(0).get(j) -(aux.get(i).get(j)));
            }
        }

        aux.set(0, (ArrayList<Double>) aux.get(0).stream().map(x -> x *= -1).collect(Collectors.toList()));
        printPl(aux);
        solve(aux);
        System.out.println(aux.get(0).get(aux.get(0).size()-1) == 0);

    }
}
