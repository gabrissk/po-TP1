package po;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        Scanner scan = new Scanner(new File(args[0]));
        int vars = Integer.parseInt(scan.nextLine());
        int num_restr = Integer.parseInt(scan.nextLine());
        Pl pl = new Pl(vars, num_restr);
        String[] var_types = scan.nextLine().split(" ");
        for(int i= 0; i< pl.var_type.length; i++) {
            pl.setVar_type(i, Integer.parseInt(var_types[i]));
        }
        String func[] = scan.nextLine().split(" ");
        pl.lp.add(new ArrayList<>());
        for(int i=0; i<vars;i++) {
            pl.getFunc().add(Double.parseDouble(func[i]));
            pl.setFunc(i, Double.parseDouble(func[i]));
        }
        pl.getFunc().add(pl.getFunc().size(), 0.0);
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
        System.out.println();
        pl.printPl();
        pl.nNegativity();
        System.out.println();
        pl.printPl();
        System.out.println();
        pl.FPI();
        pl.printPl();

        /*System.out.println(pl.checkB());
        System.out.println(pl.choose());
        System.out.println(pl.checkC());*/

        int i = pl.solve();
        pl.printPl();
        System.out.println(i);


    }
}
