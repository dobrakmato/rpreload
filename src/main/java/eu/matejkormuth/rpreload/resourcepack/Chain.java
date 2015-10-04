package eu.matejkormuth.rpreload.resourcepack;

/**
 * Provides simple interface for responsibility chain.
 *
 * @param <I> input parameter
 * @param <O> output parameter
 */
public abstract class Chain<I, O> {
    /**
     * Next element in responsibility chain.
     */
    protected Chain<I, O> next;

    /**
     * Sets next element in responsibility chain.
     *
     * @param next next element
     */
    public void setNextChain(Chain<I, O> next) {
        this.next = next;
    }

    /**
     * Processes specified input parameter using this responsibility chain.
     *
     * @param input parameter to process
     * @return result of this operation
     */
    public abstract O process(I input);

    /**
     * Creates new Chain builder with specified first element in responsibility chain.
     *
     * @param first first element in chain
     * @param <I>   input parameter
     * @param <O>   output parameter
     * @return chain builder
     */
    public static <I, O> Builder<I, O> first(Chain<I, O> first) {
        return new Builder<>(first);
    }

    /**
     * Provides fluent way to build responsibility chains.
     *
     * @param <I> input parameter
     * @param <O> output parameter
     */
    public static class Builder<I, O> {
        private Chain<I, O> last;
        private Chain<I, O> first;

        /**
         * Creates new Chain builder with specified first element in responsibility chain.
         *
         * @param first first element in chain
         */
        public Builder(Chain<I, O> first) {
            this.first = last = first;
        }

        /**
         * Specifies next element in this responsibility chain.
         *
         * @param next next element
         * @return this builder
         */
        public Builder<I, O> then(Chain<I, O> next) {
            last.setNextChain(next);
            last = next;

            return this;
        }

        /**
         * Builds the chain (returns first element of this chain).
         *
         * @return first element of this responsibility chain
         */
        public Chain<I, O> build() {
            return first;
        }
    }
}
