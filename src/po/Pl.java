package po;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
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
    protected int basic;
    protected TreeMap<Integer,Integer> bases;
    protected int[] negs;
    protected PrintWriter out;

    public Pl(int vars, int num_restr, File arq) throws FileNotFoundException {
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
        this.bases = new TreeMap<>();
        this.negs = new int[this.num_restr+1];
        out = new PrintWriter(arq);
    }


    public ArrayList<Double> getRestriction(int i) {
        return lp.get(i+1);
    }

    public ArrayList<Double> getFunc() {return this.func;}

    public void setVar_type(int i, int type) {
        this.var_type[i] = type;
    }

    public void setFunc(int i, double val) {
        this.func.add(i, val);
    }

    void printPl(ArrayList<ArrayList<Double>> lp) {
        for(ArrayList<Double> line:lp) System.out.println(line);
        System.out.println();
    }

    public void setRestr_type(int i, char c) {
        this.restr_type[i] = c;
    }

    public void nNegativity() {
        final int v = this.vars;
        int change = 0;
        for(int i=0; i< v; i++) {
            if(this.var_type[i] == 0) {
                insertNonNegative(i+change);
                this.vars++;
                change++;
            }
        }
    }

    private void insertNonNegative(int i) {
        for(ArrayList<Double> line: this.lp) {
            line.add(i+1, -line.get(i));
        }
    }

    public void FPI() {
        this.nNegativity();
        for(int i=1; i<= this.num_restr; i++) {
            if(this.lp.get(i).get(this.lp.get(0).size()-1) < 0) {
                this.negs[i] = 1;
                this.lp.set(i, (ArrayList<Double>) this.lp.get(i).stream().map(x -> x *= -1).collect(Collectors.toList()));
                if(this.restr_type[i-1] == '<') this.restr_type[i-1] = '>';
                else if(this.restr_type[i-1] == '>') this.restr_type[i-1] = '<';
            }
            if(this.restr_type[i-1] != '=') {
                addColumn(i, this.restr_type[i-1]);
                this.vars++;
                if(this.restr_type[i-1] == '<') {
                    this.basic++;
                    this.canon[i] = i;
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


    public Map.Entry<Integer, Integer> choose(ArrayList<ArrayList<Double>> lp) {
        Map.Entry<Integer, Integer> p = null;
        boolean unb = true;
        for(int j=this.num_restr; j< lp.get(0).size()-1; j++) {
            if(lp.get(0).get(j) < 0) {
                double choice = 9999999;
                for (int i=1; i <= this.num_restr; i++) {
                    double d = lp.get(i).get(j);
                    if(d > 0 && (lp.get(i).get(lp.get(i).size()-1)/d < choice)) {
                        choice = lp.get(i).get(lp.get(i).size()-1)/d;
                        p = Map.entry(i, j);
                        unb = false;
                    }
                }
                if(unb) return Map.entry(j,-11);
                this.bases.put(p.getKey(), p.getValue());
                return p;
            }
        }
        return null;
    }

    public boolean checkC(ArrayList<ArrayList<Double>> lp) {
        for(int j = this.num_restr; j < lp.get(0).size()-1; j++)
            if(lp.get(0).get(j) < 0) return false;
        return true;
    }

    @SuppressWarnings("unchecked")
    public int solve(ArrayList<ArrayList<Double>> lp, boolean flag) {
        if(flag) addAux(lp);
        lp.set(0, (ArrayList<Double>) lp.get(0).stream().map(x -> x *= -1).collect(Collectors.toList()));
        while(!checkC(lp)) {
            Map.Entry<Integer, Integer> entry = choose(lp);
            if(entry.getValue() == -11) return entry.getKey() * 1000000;
            pivot(lp, entry.getKey(), entry.getValue());
        }
        return 2;
    }

    public void addAux(ArrayList<ArrayList<Double>> lp) {
        for(int i= 0; i<= this.num_restr; i++) {
            for(int j= 0; j < this.num_restr; j++) {
                lp.get(i).add(0, 0.0);
            }
        }
        for(int i= 1; i<= this.num_restr; i++) {
            for(int j= 0; j < this.num_restr; j++) {
                if(i==j+1) {
                    if(this.negs[i] == 1) lp.get(i).set(j, -1.0);
                    else lp.get(i).set(j, 1.0);
                }
            }
        }

    }

    public void pivot(ArrayList<ArrayList<Double>> lp, int i, int j) {
        double piv = lp.get(i).get(j);
        ArrayList<Double> arr = lp.get(i);
        for(int x=0; x < arr.size();x++) {
            arr.set(x, arr.get(x) / piv);
            if(Math.abs(arr.get(x)) < 0.000000001) arr.set(x, 0.0);
        }
        piv = arr.get(j);
        for(int k=0; k<= this.num_restr; k++) {
            double mult = -(lp.get(k).get(j))/piv;
            for(int m= 0; m< arr.size(); m++) {
                if(k==i || lp.get(i).get(m) == 0) continue;
                lp.get(k).set(m, lp.get(k).get(m) +(lp.get(i).get(m) * mult));
                if(Math.abs(lp.get(k).get(m)) < 0.000000001) lp.get(k).set(m, 0.0);
            }
        }
    }

    public boolean hasBasicSol() {
        return this.basic == this.num_restr;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ArrayList<Double>> auxLp() {
        ArrayList<ArrayList<Double>> aux = (ArrayList<ArrayList<Double>>) this.lp.clone();
        aux.set(0, (ArrayList<Double>) aux.get(0).stream().map(x -> x = 0.0).collect(Collectors.toList()));
        for(int i=0; i < this.num_restr; i++) {
            if(this.canon[i+1] != -1) {
                continue;
            }
            addColumn(aux, i+1);
            aux.get(0).set(aux.get(0).size()-2, 1.0);
            this.canon[i] = i;

        }
        addAux(aux);
        for(int i = 1; i<= this.num_restr; i++) {
            for(int j = 0; j< aux.get(i).size(); j++) {
                aux.get(0).set(j, aux.get(0).get(j) -(aux.get(i).get(j)));
            }
        }

        aux.set(0, (ArrayList<Double>) aux.get(0).stream().map(x -> x *= -1).collect(Collectors.toList()));
        solve(aux, false);
        if(aux.get(0).get(aux.get(0).size()-1) != 0) {
            this.out.println("Status: inviavel");
            this.out.println("Certificado:");
            this.out.println(getCertificate(aux));
            this.out.flush();
            System.exit(-1);
        }

        for(int i=0; i<= this.num_restr; i++) {
            final int size = aux.get(aux.size()-1).size();
            for(int j=size-2; j>=  size-this.num_restr-1; j--) {
                aux.get(i).remove(j);
            }
        }
        return aux;
    }


    public ArrayList<Double> getCertificate(ArrayList<ArrayList<Double>> lp) {
        return new  ArrayList<>(lp.get(0).subList(0, this.num_restr));
    }

    public double[] getSolution() {
        double sol[] = new double[this.vars];
        for(int i=1; i< this.vars+1; i++) {
            if(this.bases.containsKey(i)) {
                sol[this.bases.get(i)-this.num_restr] = this.lp.get(i).get(this.lp.get(i).size()-1);
            }
        }
        return sol;
    }

    public ArrayList<Double> getUnboundedCertificate(int x) {
        ArrayList<Double> certificate = new ArrayList<>();
        for(int j=this.num_restr; j < this.lp.get(0).size()-1; j++) {
            boolean flag =false;
            if(j == x) {
                certificate.add(1.0);
                continue;
            }
            for (int i=1; i<= this.num_restr; i++) {
                if(this.lp.get(i).get(j) == 1.0 && this.lp.get(0).get(j)==0) {
                    certificate.add(-this.lp.get(i).get(x));
                    flag = true;
                    break;
                }
            }
            if(!flag) certificate.add(0.0);
        }
        return certificate;
    }


}
