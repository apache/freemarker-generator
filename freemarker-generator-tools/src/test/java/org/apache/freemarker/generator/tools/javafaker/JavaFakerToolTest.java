/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.freemarker.generator.tools.javafaker;

import com.github.javafaker.Faker;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class JavaFakerToolTest {

    private final Faker faker = javaFakerTool().getFaker();

    @Test
    public void shouldCreateFakerFromLocale() {
        assertNotNull(new JavaFakerTool().getFaker(Locale.ENGLISH));
    }

    @Test
    public void shouldCreateFakerFromLocaleString() {
        assertNotNull(new JavaFakerTool().getFaker("de-DE"));
    }

    @Test
    public void shouldCreateFakeData() {
        assertFalse(faker.name().fullName().isEmpty());
        assertFalse(faker.internet().emailAddress().isEmpty());
        assertNotNull(faker.date().past(12, TimeUnit.DAYS));
        assertTrue(faker.finance().iban("AT").startsWith("AT"));
    }

    @Test
    public void shouldGetTimeUnits() {
        assertEquals(6, javaFakerTool().getTimeUnits().size());
    }

    @Test
    public void shouldShowGeneralUsage() {
        final String language = "de";
        final String country = "DE";
        final Locale locale = new Locale(language, country);
        final Faker faker = javaFakerTool().getFaker(locale);
        final String iban = faker.finance().iban(country);
        final String firstName = faker.name().firstName();
        final String lastName = faker.name().lastName();
        final String email = String.format("%s.%s@gmail.com", firstName, lastName).toLowerCase();
        final Date birthday = faker.date().birthday(21, 65);
        final String streetAddress = faker.address().streetAddress();
        final String state = faker.address().stateAbbr();
        final String zipCode = faker.address().zipCode();
        final int numberBetween = faker.number().numberBetween(10, 99);
        final List<String> words = faker.lorem().words(10);

        assertTrue(iban.startsWith("DE"));
        assertFalse(firstName.isEmpty());
        assertFalse(lastName.isEmpty());
        assertFalse(email.isEmpty());
        assertNotNull(birthday);
        assertFalse(streetAddress.isEmpty());
        assertFalse(state.isEmpty());
        assertFalse(zipCode.isEmpty());
        assertTrue(numberBetween >= 10 && numberBetween <= 99);
        assertFalse(words.isEmpty());

        /*
        System.out.println("iban: " + iban);
        System.out.println("name: " + firstName + " " + lastName);
        System.out.println("email: " + email);
        System.out.println("birthday: " + birthday);
        System.out.println("streetName: " + streetAddress);
        System.out.println("state: " + state);
        System.out.println("zipCode: " + zipCode);
        System.out.println("numberBetween: " + numberBetween);
        System.out.println("words: " + words);
        */
    }

    private static JavaFakerTool javaFakerTool() {
        return new JavaFakerTool();
    }
}
