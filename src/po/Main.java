package po;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        Scanner scan = new Scanner(new File(args[0]));
        int vars = Integer.parseInt(scan.nextLine());
        System.out.println(vars);
        int num_restr = Integer.parseInt(scan.nextLine());
        System.out.println(num_restr);
        Pl pl = new Pl(vars, num_restr);
        String var_types = scan.nextLine();
        System.out.println(var_types);
        // ...
        String func = scan.nextLine();
        System.out.println(func);
        for(int i=0; i< num_restr; i++) {
            pl.restrictions.add(new ArrayList<>());
            String restr = scan.nextLine();
            System.out.println(restr);
            String[] split = restr.split(" ");
            int j;
            for (j=0; j< vars; j++) {
                double var = Double.parseDouble(split[j]);
                System.out.println(var);
                pl.getRestriction(i).add(var);
            }
            pl.setRestr_type(i, split[j++].charAt(0));
            pl.getRestriction(i).add(Double.parseDouble(split[j]));
            System.out.println(pl.restrictions.get(i));

        }


    }
}
