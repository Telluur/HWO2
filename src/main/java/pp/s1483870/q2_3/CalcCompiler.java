package pp.s1483870.q2_3;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pp.s1483870.iloc.Simulator;
import pp.s1483870.iloc.model.*;
import pp.s1483870.iloc.parse.ErrorListener;

/**
 * Compiler from Calc.g4 to stack-based ILOC.
 */
public class CalcCompiler extends CalcBaseListener {
    /**
     * First of the two registers required.
     */
    private final Reg reg1 = new Reg("r_1");
    /**
     * Second of the two registers required.
     */
    private final Reg reg2 = new Reg("r_2");
    /**
     * Program under construction.
     */
    private Program program;

    /**
     * Calls the compiler, and simulates and prints the compiled program.
     */
    public static void main(String[] args) {
        String[] exprs = {"(1 + -3) * 4"};
        if (args.length != 0) {
            exprs = args;
        }
        CalcCompiler compiler = new CalcCompiler();
        for (String expr : exprs) {
            System.out.println("Processing " + expr);
            Program program = compiler.compile(expr);
            new Simulator(program).run();
            System.out.println(program.prettyPrint());
        }
    }

    /**
     * Compiles a given expression string into an ILOC program.
     */
    public Program compile(String text) {
        Program result = null;
        ErrorListener listener = new ErrorListener();
        CharStream chars = new ANTLRInputStream(text);
        Lexer lexer = new CalcLexer(chars);
        lexer.removeErrorListeners();
        lexer.addErrorListener(listener);
        TokenStream tokens = new CommonTokenStream(lexer);
        CalcParser parser = new CalcParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(listener);
        ParseTree tree = parser.complete();
        if (listener.hasErrors()) {
            System.out.printf("Parse errors in %s:%n", text);
            for (String error : listener.getErrors()) {
                System.err.println(error);
            }
        } else {
            result = compile(tree);
        }
        return result;
    }

    /**
     * Compiles a given Calc-parse tree into an ILOC program.
     */
    public Program compile(ParseTree tree) {
        program = new Program();
        new ParseTreeWalker().walk(this, tree);
        emit(OpCode.out, new Str("Outcome: "), reg2);
        return program;
    }

    // Override the listener methods
    @Override
    public void exitMinus(@NotNull CalcParser.MinusContext ctx){
        emit(OpCode.pop, reg1);
        emit(OpCode.rsubI, reg1, new Num(0), reg2);
        emit(OpCode.push, reg2);
    }
    @Override
    public void exitNumber(@NotNull CalcParser.NumberContext ctx) {
        emit(OpCode.loadI, new Num(Integer.parseInt(ctx.getText())), reg1);
        emit(OpCode.push, reg1);
    }
    @Override
    public void exitTimes(@NotNull CalcParser.TimesContext ctx){
        emit(OpCode.pop, reg2);
        emit(OpCode.pop, reg1);
        emit(OpCode.mult, reg1, reg2, reg2);
        emit(OpCode.push, reg2);
    }

    @Override
    public void exitPlus(@NotNull CalcParser.PlusContext ctx){
        emit(OpCode.pop, reg2);
        emit(OpCode.pop, reg1);
        emit(OpCode.add, reg1, reg2, reg2);
        emit(OpCode.push, reg2);
    }

    /**
     * Constructs an operation from the parameters
     * and adds it to the program under construction.
     */
    private Op emit(OpCode opCode, Operand... args) {
        Op result = new Op(opCode, args);
        program.addInstr(result);
        return result;
    }
}
