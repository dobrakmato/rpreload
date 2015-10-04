/**
 * rpreload - Resource pack management made easy.
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.matejkormuth.rpreload.resourcepack;

import javax.annotation.Nonnull;

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
    public void setNext(@Nonnull Chain<I, O> next) {
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
    public static <I, O> Builder<I, O> first(@Nonnull Chain<I, O> first) {
        return new Builder<>(first);
    }

    /**
     * Provides fluent way to build responsibility chains.
     *
     * @param <I> input parameter
     * @param <O> output parameter
     */
    public static class Builder<I, O> {
        private final Chain<I, O> first;
        private Chain<I, O> last;

        /**
         * Creates new Chain builder with specified first element in responsibility chain.
         *
         * @param first first element in chain
         */
        public Builder(@Nonnull Chain<I, O> first) {
            this.first = last = first;
        }

        /**
         * Specifies next element in this responsibility chain.
         *
         * @param next next element
         * @return this builder
         */
        public Builder<I, O> then(@Nonnull Chain<I, O> next) {
            last.setNext(next);
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
