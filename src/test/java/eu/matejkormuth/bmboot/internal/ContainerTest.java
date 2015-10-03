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
package eu.matejkormuth.bmboot.internal;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ContainerTest {

    @Test
    public void testGet() throws Exception {
        AppContainer container = new AppContainer();

        container.put(String.class, "testString");
        Assert.assertEquals(container.get(String.class), "testString");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExistingGet() throws Exception {
        AppContainer container = new AppContainer();

        container.get(Double.class);
    }

    @Test
    public void testOptional() throws Exception {
        AppContainer container = new AppContainer();

        container.put(Integer.class, 42);
        Optional<Float> floatOptional = container.optional(Float.class);
        Optional<Integer> integerOptional = container.optional(Integer.class);

        Assert.assertFalse(floatOptional.isPresent());
        Assert.assertTrue(integerOptional.isPresent());

        Assert.assertEquals(integerOptional.get(), Integer.valueOf(42));
    }
}