/*
 * Copyright 2009 Michael Burton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.astroboy.bean;

import java.util.Date;

import roboguice.inject.ContextScoped;
import roboguice.inject.InjectExtra;

import com.google.inject.Provider;

@ContextScoped
public class PersonFromNameExtraProvider implements Provider<Person> {

    @InjectExtra("nameExtra")
    protected String nameExtra;

    @InjectExtra(value = "ageExtra", optional = true)
    protected Date   ageExtra;

    public Person get() {
        if (ageExtra == null) {
            return new Person(nameExtra);
        } else {
            return new Person(nameExtra, ageExtra);
        }
    }

}
