package pp.s1483870.q2_5;


import pp.s1483870.iloc.Assembler;
import pp.s1483870.iloc.Simulator;
import pp.s1483870.iloc.eval.Machine;
import pp.s1483870.iloc.model.Program;
import pp.s1483870.iloc.parse.FormatException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Rick on 11-6-2015.
 */
public class IlocConvert {
    private final static String FILE = "src/main/java/pp/s1483870/q2_5/test.iloc";

    public static void main(String[] args) {
        new IlocConvert().run();
    }

    public void run() {
        try {
            File file = new File(FILE);
            Program program = Assembler.instance().assemble(file);
            Machine machine = new Machine();
            new Simulator(program, machine).run();
        } catch (FormatException | IOException e) {
            e.printStackTrace();
        }
    }
}
