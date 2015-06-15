package pp.s1483870.q2_2;

import pp.s1483870.iloc.Assembler;
import pp.s1483870.iloc.model.*;
import pp.s1483870.iloc.parse.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ILOC2CFG {
    /**
     * The singleton instance of this class.
     */
    private static final ILOC2CFG instance = new ILOC2CFG();
    private static final String BASE_DIR = "src/main/java/pp/s1483870/q2_2/";

    /**
     * Private constructor for the singleton instance.
     */
    private ILOC2CFG() {
        // empty by design
    }

    /**
     * Returns the singleton instance of this class.
     */
    public static ILOC2CFG instance() {
        return instance;
    }

    /**
     * Converts an ILOC file given as parameter and prints out the
     * resulting CFG.
     */
    public static void main(String[] args) {
        File file = null;
        if (args.length >= 1) {
            file = new File(args[0]);
        } else {
            file = new File(BASE_DIR + "test3.iloc");
            System.out.println(file.toPath());
        }
        try {
            Program program = Assembler.instance().assemble(file);
            System.out.println(instance().convert(program));
        } catch (FormatException | IOException exc) {
            exc.printStackTrace();
        }
    }

    public Graph convert(Program program) {
        Graph graph = new Graph();
        List<IlocEdge> ilocEdges = new ArrayList<IlocEdge>();
        List<String> ilocNodes = new ArrayList<String>();
        List<Instr> instrs = program.getInstr();
        String labelName = null;
        String finalLabel = null;
        for (Instr instr : instrs) {
            if (instr.hasLabel())
                finalLabel = instr.getLabel().getValue();

            if (labelName == null) {
                Label label = instr.getLabel();
                if (label != null) {
                    labelName = label.getValue();
                } else {
                    continue;
                }
            }
            Op op = program.getOpAt(instr.getLine());
            OpCode opCode = op.getOpCode();
            if (opCode.equals(OpCode.jumpI) || opCode.equals(OpCode.cbr)) {
                if (opCode.equals(OpCode.jumpI)) {
                    ilocEdges.add(new IlocEdge(labelName, op.getArgs().get(0).toString()));
                } else {
                    ilocEdges.add(new IlocEdge(labelName, op.getArgs().get(1).toString()));
                    ilocEdges.add(new IlocEdge(labelName, op.getArgs().get(2).toString()));
                }
                ilocNodes.add(labelName);
                labelName = null;
            }
        }

        if (labelName != null) {
            ilocNodes.add(labelName);
        }


        //create list of receiving nodes
        List<String> receivers = new ArrayList<String>();
        receivers.add(instrs.get(0).getLabel().toString());        ilocNodes.add(finalLabel);

        for (IlocEdge ilocEdge : ilocEdges) {
            receivers.add(ilocEdge.getTo());
        }

        //remove unreachable nodes
        for (Iterator<String> iter = ilocNodes.listIterator(); iter.hasNext(); ) {
            String a = iter.next();
            if (!receivers.contains(a)) {
                iter.remove();
            }
        }

        //remove their respective edges.
        for (Iterator<IlocEdge> iter = ilocEdges.listIterator(); iter.hasNext(); ) {
            String a = iter.next().getTo();
            if (!receivers.contains(a)) {
                iter.remove();
            }
        }

        for (String ilocNode : ilocNodes) {
            graph.addNode(ilocNode);
        }

        for (IlocEdge ilocEdge : ilocEdges) {
            Node from = graph.getNodeById(ilocEdge.getFrom());
            Node to = graph.getNodeById(ilocEdge.getTo());
            from.addEdge(to);
        }


        return graph;
    }

    private class IlocEdge {
        private String from;
        private String to;

        IlocEdge(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }

    /*
        test2.iloc proper output:
        Graph with 3 nodes
        Node label1: edges to [label4, label5]
        Node label4: edges to [label1, label5]
        Node label5: no outgoing edges
     */
}