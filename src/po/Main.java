package po;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        Scanner scan = new Scanner(new File(args[0]));
        File arq = new File(args[1]);
        int vars = Integer.parseInt(scan.nextLine());
        int num_restr = Integer.parseInt(scan.nextLine());
        Pl pl = new Pl(vars, num_restr, arq);
        String[] var_types = scan.nextLine().split(" ");
        for(int i= 0; i< pl.var_type.length; i++) {
            pl.setVar_type(i, Integer.parseInt(var_types[i]));
        }
        String func[] = scan.nextLine().split(" ");
        for(int i=0; i<vars;i++) {
            pl.setFunc(i, Double.parseDouble(func[i]));
        }
        pl.getFunc().add(pl.getFunc().size(), 0.0);
        pl.lp.add(0, pl.getFunc());
        for(int i=0; i< num_restr; i++) {
            pl.lp.add(new ArrayList<>());
            String restr = scan.nextLine();
            String[] split = restr.split(" ");
            int j;
            for (j=0; j< vars; j++) {
                double var = Double.parseDouble(split[j]);
                pl.getRestriction(i).add(var);
            }
            pl.setRestr_type(i, split[j++].charAt(0));
            pl.getRestriction(i).add(Double.parseDouble(split[j]));
        }
        pl.FPI();
        int r;
        if(!pl.hasBasicSol()) {
            pl.lp = pl.auxLp();
            pl.lp.set(0, pl.getFunc());
            for(int j=0; j< pl.num_restr-pl.basic; j++) {
                pl.lp.get(0).add(0, 0.0);
            }
            for(int i:pl.bases.keySet()) {
                pl.pivot(pl.lp, i, pl.bases.get(i));
            }

            r = pl.solve(pl.lp, false);
        }
        else {
            r = pl.solve(pl.lp, true);
        }
        if (r == 2) {
            pl.out.println("Status: otimo");
            pl.out.println("Objetivo: " +pl.lp.get(0).get(pl.lp.get(0).size()-1));
            pl.out.println("Solucao:");
            pl.out.println(Arrays.toString(pl.getSolution()));
            pl.out.println("Certificado:");
            pl.out.println(pl.getCertificate(pl.lp));
            pl.out.flush();
        }
        else {
            pl.out.println("Status: ilimitado");
            pl.out.println("Certificado:");
            pl.out.println(pl.getUnboundedCertificate(r / 1000000));
            pl.out.flush();
        }
    }
}
