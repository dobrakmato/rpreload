package eu.matejkormuth.rpreload.commands;

public class CommandArgs {
    private final String[] args;
    private int pos;

    public CommandArgs(String[] args) {
        this.args = args;
        this.pos = -1;
    }

    public String peekNext() {
        return args[pos + 1];
    }

    public String next() {
        return args[++pos];
    }

    public int nextInt() {
        return Integer.valueOf(next());
    }

    public long nextLong() {
        return Long.valueOf(next());
    }

    public float nextFloat() {
        return Float.valueOf(next());
    }

    public double nextDouble() {
        return Double.valueOf(next());
    }

    public boolean nextBoolean() {
        return Boolean.valueOf(next());
    }

    public int length() {
        return args.length;
    }
}
